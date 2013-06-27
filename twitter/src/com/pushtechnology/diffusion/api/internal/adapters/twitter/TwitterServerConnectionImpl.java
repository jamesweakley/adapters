package com.pushtechnology.diffusion.api.internal.adapters.twitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONObject;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.adapters.twitter.TweetMessageLevel;
import com.pushtechnology.diffusion.api.adapters.twitter.TwitterListener;
import com.pushtechnology.diffusion.api.adapters.twitter.TwitterServerConnection;
import com.pushtechnology.diffusion.api.threads.ThreadService;
import com.pushtechnology.diffusion.xmlproperties.twitter.TwitterDefinition;

/**
 * 
 * Implementation object for a Twitter server connection
 * 
 * @author antonio - created Jan 5, 2012
 * @since 4.1
 */
@SuppressWarnings("deprecation")
public final class TwitterServerConnectionImpl 
implements 
Runnable,
TwitterServerConnection,
TwitterProcessorExceptionListener,
TwitterServerConnectionImplMBean {

    /**
     * Base URL for the Twitter server.
     */
    private static final String BASEURL =
        "https://stream.twitter.com/1/statuses/filter.json";


    private HashSet<TwitterListener> theListeners =
        new HashSet<TwitterListener>();

    private TwitterDefinition theDefinition;
    private String theCredentials;
    private Collection<String> theFollowIds = null;
    private Collection<String> theTrackKeywords = null;

    private long theProcessForMillis = 0;
    private int theMaxThreads = 0;

    private JsonTwitterProcessor[] theThreads;

    private Date theConnectDatestamp = null;

    private TweetMessageLevel theMessageLevel;

    private Queue<JSONObject> theQueue = 
        new ConcurrentLinkedQueue<JSONObject>();

    private boolean thisIsRunning;

    /**
     * Constructor.
     * 
     * @param connection the connection defined in the XML descriptor.
     * @param messageLevel the type of messages for which notifications need to
     * be sent.
     */
    public TwitterServerConnectionImpl(
    TwitterDefinition connection,
    TweetMessageLevel messageLevel) {
        theDefinition = connection;
        theMessageLevel = messageLevel;

        theCredentials = 
            new String(
                connection.getServerDefinition().getUsername()+
                ":"+
                connection.getServerDefinition().getPassword());

        if (connection.getServerDefinition().getFollowIds()!=null) {
            theFollowIds = new ArrayList<String>();
            theFollowIds.add(connection.getServerDefinition().getFollowIds());
        }

        if (connection.getServerDefinition().getTrackKeywords()!=null) {
            theTrackKeywords = new ArrayList<String>();
            theTrackKeywords.add(
                connection.getServerDefinition().getTrackKeywords());
        }

        theProcessForMillis =
            connection.getClientDefinition().getProcessForMillis();

        if (theFollowIds!=null) {
            theMaxThreads +=
                new Double(
                    Math.floor(
                        getIdsSize(theFollowIds)/
                        connection.getClientDefinition().getMaxIdsPerCredentials()))
                    .intValue();
        }

        if (theTrackKeywords!=null) {
            theMaxThreads +=
                new Double(
                    Math.floor(new Double(getIdsSize(theTrackKeywords))/
                    new Double(connection.getClientDefinition()
                        .getMaxKeywordsPerCredentials()))).intValue();
        }

        if (theMaxThreads==0) {
            theMaxThreads = 1;
        }
        theThreads = new JsonTwitterProcessor[theMaxThreads];

        connect();

        try {
            Thread t =
                ThreadService.newThread(
                    this,
                    "Tweet Client Collector",
                    Thread.NORM_PRIORITY,
                    true);
            t.start();
            Logs.info(t.getName()+" starting.");
        }
        catch (APIException apie) {
            apie.printStackTrace();
        }
    }


    /**
     * Returns the number of IDs currently being followed or tracked.
     * 
     * @param list the list of IDs or keywords being followed or tracked.
     * 
     * @return the total size of the list.
     * @since n.n
     */
    private int getIdsSize(Collection<String> list) {
        int result = 0;
        for (String string:list) {
            result += new StringTokenizer(string,",").countTokens();
        }
        return result;
    }

    /**
     * @see TwitterServerConnection#getConnection()
     */
    public TwitterDefinition getConnection() {
        return theDefinition;
    }

    /**
     * Stops the current thread.
     * 
     * @since n.n
     */
    public void stopMe() {
        disconnect();
        thisIsRunning = false;
    }

   
    /**
     * @see TwitterServerConnection#addListener(TwitterListener)
     */
    @Override
    public synchronized boolean addListener(TwitterListener listener) {
        return theListeners.add(listener);
    }

    /**
     * @see TwitterServerConnection#removeListener(TwitterListener)
     */
    @Override
    public synchronized boolean removeListener(TwitterListener listener) {
        return theListeners.remove(listener);
    }

 
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (thisIsRunning) {
            JSONObject tweet = theQueue.poll();

            if (tweet!=null) {
                for (TwitterListener listener:theListeners) {
                    listener.handleTweet(new JsonTweet(tweet));
                }
            }
        }
    }


    /**
     * @see TwitterProcessorExceptionListener#handleException(String, Exception)
     */
    @Override
    public void handleException(String name,Exception ex) {
        for (JsonTwitterProcessor processor:theThreads) {
            if (processor.getName().equals(name)) {
                Logs.warning(name+" terminated. Re-connecting.",ex);

                processor.removeExceptionListener(this);

                try {
                    processor =
                        new JsonTwitterProcessor(
                            BASEURL,
                            theCredentials,
                            processor.getTarget(),
                            processor.getAction(),
                            name,theProcessForMillis,
                            theDefinition.getClientDefinition().getBackoff(),
                            theQueue,
                            theMessageLevel);

                    processor.addExceptionListener(this);

                    processor.start();
                }
                catch (APIException apiEx) {
                    Logs.warning(apiEx.getMessage(),apiEx);
                }
            }
        }
    }

    /**
     * @see TwitterServerConnection#isConnected()
     */
    @Override
    public boolean isConnected() {
        // true if at least one twitter processor is connected
        boolean result = true;
        for (TwitterProcessor t:theThreads) {
            result = result||t.isConnected();
        }
        return true;
    }

    /**
     * @see witterServerConnectionImplMBean#getConnectedDatestamp()
     */
    @Override
    public Date getConnectedDatestamp() {
        return theConnectDatestamp;
    }

    /**
     * @see TwitterServerConnectionImplMBean#getDefinitionName()
     */
    @Override
    public String getDefinitionName() {
        return theDefinition.getName();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getFollowIds()
     */
    @Override
    public String getFollowIds() {
        String result = "";
        for (String id:theFollowIds) {
            if (result.equals("")) {
                result = id;
            }
            else {
                result = result+", "+id;
            }
        }

        return result;
    }

    @Override
    public String getTrackKeywords() {
        String result = "";
        for (String id:theTrackKeywords) {
            if (result.equals("")) {
                result = id;
            }
            else {
                result = result+", "+id;
            }
        }

        return result;
    }

    /**
     * @see TwitterServerConnectionImplMBean#getUsername()
     */
    @Override
    public String getUsername() {
        return theDefinition.getServerDefinition().getUsername();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getPassword()
     */
    @Override
    public String getPassword() {
        return theDefinition.getServerDefinition().getPassword();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getMaxIdsPerCredentials()
     */
    @Override
    public Integer getMaxIdsPerCredentials() {
        return theDefinition.getClientDefinition().getMaxIdsPerCredentials();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getMaxKeywordsPerCredentials()
     */
    @Override
    public Integer getMaxKeywordsPerCredentials() {
        return theDefinition.getClientDefinition().getMaxKeywordsPerCredentials();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getProcessForMillis()
     */
    @Override
    public Integer getProcessForMillis() {
        return theDefinition.getClientDefinition().getProcessForMillis();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getTcpInitialBackoff()
     */
    @Override
    public Integer getTcpInitialBackoff() {
        return theDefinition.getClientDefinition().getBackoff()
            .getTcpInitialBackoff();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getTcpBackoffCap()
     */
    @Override
    public Integer getTcpBackoffCap() {
        return theDefinition.getClientDefinition().getBackoff().getTcpBackoffCap();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getHttpInitialBackoff()
     */
    @Override
    public Integer getHttpInitialBackoff() {
        return theDefinition.getClientDefinition().getBackoff()
            .getHttpInitialBackoff();
    }

    /**
     * @see TwitterServerConnectionImplMBean#getHttpBackoffCap()
     */
    @Override
    public Integer getHttpBackoffCap() {
        return theDefinition.getClientDefinition().getBackoff()
            .getHttpBackoffCap();
    }

    
    /**
     * @see TwitterServerConnectionImplMBean#connect()
     */
    @Override
    public void connect() {
        thisIsRunning = true;

        if (theConnectDatestamp==null)
            theConnectDatestamp = new Date();

        for (int i = 0;i<theThreads.length;i++) {
            long delay = 0;

            Collection<String> target;
            String action;

            if (theFollowIds!=null) {
                target = theFollowIds;
                action = "follow";
            }
            else {
                target = theTrackKeywords;
                action = "track";
            }

            try {
                // this is to ensure that if we need multiple threads
                // they won't try to connect at the same time;
                if (i==theThreads.length) {
                    delay = theProcessForMillis;
                }

                JsonTwitterProcessor t =
                    new JsonTwitterProcessor(
                        BASEURL,
                        theCredentials,
                        target,
                        action,
                        "Twitter download as "+
                            theDefinition.getServerDefinition().getUsername()+
                            " ("+i+")",
                        delay,theDefinition.getClientDefinition().getBackoff(),
                        theQueue,
                        theMessageLevel);

                t.addExceptionListener(this);
                t.start();

                theThreads[i] = t;

            }
            catch (APIException apie) {
                apie.printStackTrace();
            }
        }
    }

    /**
     * @see TwitterServerConnectionImplMBean#disconnect()
     */
    @Override
    public void disconnect() {
        for (TwitterProcessor t:theThreads) {

            t.removeExceptionListener(this);
            t.disconnect();

        }
    }
}
