package com.pushtechnology.diffusion.api.internal.adapters.twitter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.APIProperties;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.adapters.twitter.TweetMessageLevel;
import com.pushtechnology.diffusion.xmlproperties.twitter.Backoff;

public final class JsonTwitterProcessor
extends Thread
implements TwitterProcessor {

    private final UsernamePasswordCredentials theCredentials;
    private final AuthScope theAuthScope;
    private final String theBaseURL;
    /**
     * Collection of follow ids or keywords
     */
    private final Collection<String> theTarget;
    private final String theAction;
    private final TweetMessageLevel theMessageLevel;

    private Queue<JSONObject> theQueue;

    private boolean thisIsRunning;
    private boolean thisIsConnected = false;

    // The backoff behavior conforms the specs.
    private final BackOff theTCPBackoff;
    private final BackOff theHTTPBackoff;

    // The objects listening for exceptions.
    private HashSet<TwitterProcessorExceptionListener> theListeners =
        new HashSet<TwitterProcessorExceptionListener>();

    /**
     * Constructor.
     * 
     * @param baseUrl
     * @param credentials
     * @param target
     * @param action
     * @param name
     * @param initialDelay
     * @param backoff
     * @param queue
     * @param messageLevel
     * @throws APIException
     */
    public JsonTwitterProcessor(
    String baseUrl,
    String credentials,
    Collection<String> target,
    String action,
    String name,
    long initialDelay,
    Backoff backoff,
    Queue<JSONObject> queue,
    TweetMessageLevel messageLevel)
    throws APIException {

        theTCPBackoff =
            new BackOff(
                true,
                backoff.getTcpInitialBackoff(),
                backoff.getTcpBackoffCap());
        theHTTPBackoff =
            new BackOff(
                backoff.getHttpInitialBackoff(),
                backoff.getHttpBackoffCap());

        if (initialDelay>0) {
            try {
                sleep(initialDelay);
            }
            catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

        theBaseURL = baseUrl;
        theCredentials = new UsernamePasswordCredentials(credentials);
        theTarget = target;
        theAction = action;
        theQueue = queue;
        theMessageLevel = messageLevel;

        setName(name);
        setPriority(Thread.NORM_PRIORITY);
        setDaemon(true);

        try {
            theAuthScope = createAuthScope(baseUrl);
        }
        catch (URIException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Invalid url: "+baseUrl,ex);
        }

        thisIsRunning = true;
    }

    /**
     * Extracts the host and post from the baseUrl and constructs an appropriate
     * AuthScope for them for use with HttpClient.
     */
    private AuthScope createAuthScope(String baseUrl) throws URIException {
        HttpsURL url = new HttpsURL(baseUrl);
        return new AuthScope(url.getHost(),url.getPort());
    }

   
    @Override
    public synchronized boolean addExceptionListener(
    TwitterProcessorExceptionListener listener) {
        return theListeners.add(listener);
    }

  
    @Override
    public synchronized boolean removeExceptionListener(
    TwitterProcessorExceptionListener listener) {
        return theListeners.remove(listener);
    }

    private void notifyException(Exception ex) {
        for (TwitterProcessorExceptionListener listener:theListeners) {
            listener.handleException(this.getName(),ex);
        }
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {
        Logs.info("Begin "+Thread.currentThread().getName());
        try {
            if (Thread.interrupted()) {
                return;
            }
            try {
                connectAndProcess();
            }
            catch (SocketTimeoutException ex) {
                // Handle like an IOException even though it's
                // an InterruptedIOException.
                Logs.warning(
                    theCredentials.getUserName()
                        +": (STE) Error fetching from "+theBaseURL,
                    ex);
                theTCPBackoff.backOff();
                notifyException(ex);
            }
            catch (InterruptedException ex) {
                Logs.warning("Thread Interrupted. Backing off.",ex);
                theHTTPBackoff.backOff();
                notifyException(ex);
                return;
            }
            catch (InterruptedIOException ex) {
                Logs.warning("I/O Interrupted. Backing off.",ex);
                theTCPBackoff.backOff();
                notifyException(ex);
                return;
            }
            catch (HttpException ex) {
                Logs.warning(
                    theCredentials.getUserName()
                        +": (HTTPE) Error fetching from "+theBaseURL+
                        ". Backing off.",
                    ex);

                theHTTPBackoff.backOff();
                notifyException(ex);
            }
            catch (IOException ex) {
                Logs.warning(
                    theCredentials.getUserName()
                        +": (IOE) Error fetching from "+theBaseURL+
                        ". Backing off",
                    ex);

                theTCPBackoff.backOff();
                notifyException(ex);
            }
            catch (Exception ex) {
                // This could be a NumberFormatException or
                // something.
                ex.printStackTrace();
                Logs.warning(
                    theCredentials.getUserName()
                        +": (EX) Error fetching from "+theBaseURL+
                        ". Backing off",
                    ex);
                theTCPBackoff.backOff();
                notifyException(ex);
            }
        }
        catch (InterruptedException ex) {
            Logs.warning("Thread Interrupted (2). Backing off",ex);
            notifyException(ex);
            return;
        }
        finally {
            Logs.info("End "+Thread.currentThread().getName());
        }
    }

  
    /**
     * @see TwitterProcessor#connectAndProcess()
     */
    @Override
    public void connectAndProcess()
    throws HttpException, InterruptedException, IOException {
        HttpClient httpClient = new HttpClient();

        // HttpClient has no way to set SO_KEEPALIVE on our
        // socket, and even if it did the TCP keepalive interval
        // may be too long, so we need to set a timeout at this
        // level. Twitter will send periodic newlines for
        // keepalive if there is no traffic, but they don't say
        // how often. Looking at the stream, it's every 30
        // seconds, so we use a read timeout of twice that.

        httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000);

        // Don't retry, we want to handle the backoff ourselves.
        httpClient.getParams().setParameter(
            HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler(0,false));

        httpClient.getState().setCredentials(theAuthScope,theCredentials);
        httpClient.getParams().setAuthenticationPreemptive(true);

        PostMethod postMethod = new PostMethod(theBaseURL);
        postMethod.setRequestBody(makeRequestBody());

        NameValuePair[] params = postMethod.getParameters();
        for (int i = 0;i<params.length;i++)
            Logs.info(params[i].toString());

        Logs.info(theCredentials.getUserName()
            +": Connecting to "+theBaseURL);
        httpClient.executeMethod(postMethod);
        try {
            if (postMethod.getStatusCode()!=HttpStatus.SC_OK) {
                throw new HttpException(
                    "Got status "+postMethod.getStatusCode());
            }
            Logs.info(theCredentials.getUserName()
                +": Processing from "+theBaseURL);

            // We've got a successful connection.
            thisIsConnected = true;

            // long startTime = System.currentTimeMillis();

            while (thisIsRunning) {
                // while(startTime+1000 > System.currentTimeMillis()) {

                InputStream is = postMethod.getResponseBodyAsStream();
                resetBackOff();

                InputStreamReader isr = new InputStreamReader(is, APIProperties.UTF8);
                JSONTokener jsonTokener = new JSONTokener(isr);
                JSONObject jsonObject = new JSONObject(jsonTokener);

                // text does not start with RT and in_reply_to_screen_name=null
                if (theMessageLevel==TweetMessageLevel.TWEETS_ONLY) { 
                    if (((String)jsonObject.get("text")).startsWith("RT")) {
                        Logs.info("Discarded retweet from "+
                            ((JSONObject)jsonObject.get("user")).get("name"));
                        Logs.info("\t"+jsonObject.getString("text"));
                        continue;
                    }

                    if (!jsonObject.isNull("in_reply_to_screen_name")) {
                        Logs.info("Discarded direct tweet from "+
                            ((JSONObject)jsonObject.get("user")).get("name"));
                        Logs.info("\t"+jsonObject.getString("text"));
                        continue;
                    }
                }

                // text does not start with RT
                if (theMessageLevel==TweetMessageLevel.TWEETS_AND_REPLIES_ONLY) { 
                    if (((String)jsonObject.get("text")).startsWith("RT")) {
                        Logs.info("Discarded retweet from "+
                            ((JSONObject)jsonObject.get("user")).get("name"));
                        continue;
                    }
                }

                // in_reply_to_screen_name=null
                if (theMessageLevel==TweetMessageLevel.TWEETS_AND_RETWEETS_ONLY) { 
                    if (jsonObject.get("in_reply_to_screen_name")==null) {
                        continue;
                    }
                }

                theQueue.offer(jsonObject);
            }

            Logs.info(
                theCredentials.getUserName()+": Completed processing from "
                +theBaseURL);
        }
        catch (JSONException jsonEx) {
            thisIsConnected = false;
        }
        finally {
            // Abort the method, otherwise releaseConnection() will
            // attempt to finish reading the never-ending response.
            // These methods do not throw exceptions.
            postMethod.abort();
            postMethod.releaseConnection();
            thisIsConnected = false;
        }
    }

    /**
     * Constructs the body for the HTTP POST method.
     * 
     * @return The request body as an array of (name, value) pairs
     * @since n.n
     */
    private NameValuePair[] makeRequestBody() {
        Collection<NameValuePair> params = new ArrayList<NameValuePair>();
        if (theAction.equals("follow")) {
            params.add(createNameValuePair("follow",theTarget));
        }
        else {
            params.add(createNameValuePair("track",theTarget));
        }

        return params.toArray(new NameValuePair[params.size()]);
    }

    private void resetBackOff() {
        theTCPBackoff.reset();
        theHTTPBackoff.reset();
    }

    /**
     * Creates a (name, value) pair parameter for the HTTP POST request body.
     * 
     * @param name name for the parameter.
     * @param items value for the parameter.
     * 
     * @return The parameter as a (name, value) pair
     */
    private NameValuePair createNameValuePair(
    String name,Collection<String> items) {
        StringBuilder sb = new StringBuilder();
        boolean needComma = false;
        for (String item:items) {
            if (needComma) {
                sb.append(',');
            }
            needComma = true;
            sb.append(item);
        }
        return new NameValuePair(name,sb.toString());
    }

  
    /**
     * @see TwitterProcessor#getProcessorName()
     */
    @Override
    public String getProcessorName() {
        return this.getName();
    }

    /**
     * @see TwitterProcessor#getTarget()
     */
    @Override
    public Collection<String> getTarget() {
        return theTarget;
    }

   
    /**
     * @see TwitterProcessor#getAction()
     */
    @Override
    public String getAction() {
        return theAction;
    }

    /**
     * @see TwitterProcessor#getCurrentTcpBackOffDelay()
     */
    @Override
    public long getCurrentTcpBackOffDelay() {
        return theTCPBackoff.getCurrentDelay();
    }

    /**
     * @see TwitterProcessor#getCurrentHttpBackOffDelay()
     */
    @Override
    public long getCurrentHttpBackOffDelay() {
        return theHTTPBackoff.getCurrentDelay();
    }

   
    /**
     * @see TwitterProcessor#disconnect()
     */
    @Override
    public void disconnect() {
        thisIsRunning = false;
        thisIsConnected = false;
    }

    /**
     * @see TwitterProcessor#isConnected()
     */
    @Override
    public boolean isConnected() {
        return thisIsConnected;
    }
}
