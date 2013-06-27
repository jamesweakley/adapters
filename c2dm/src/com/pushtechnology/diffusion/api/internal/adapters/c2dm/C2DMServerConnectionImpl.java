package com.pushtechnology.diffusion.api.internal.adapters.c2dm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.IOUtils;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.adapters.c2dm.C2DMException;
import com.pushtechnology.diffusion.api.adapters.c2dm.C2DMServerConnection;
import com.pushtechnology.diffusion.api.adapters.c2dm.C2DMUnavailableException;
import com.pushtechnology.diffusion.xmlproperties.pushnotification.C2DMDefinition;

/**
 * Implementation of {@link C2DMServerConnection}.
 * 
 * @author martincowie - created Nov 30, 2011
 * @since 4.1.0
 */
public class C2DMServerConnectionImpl implements C2DMServerConnectionImplMBean,
C2DMServerConnection {

    private final C2DMDefinition theDefinition;
    private final AtomicInteger theMessageCount = new AtomicInteger();
    private final URL theCDMServerURL;

    private long theLastMessageSentMillis;
    private HttpsURLConnection theUrlConnection;
    private String theGoogleAuthToken;
    private long theGoogleAuthenticated;

    /**
     * Constructor.
     * 
     * @param definition {@link C2DMDefinition} configuration object
     * @throws APIException If it is not possible to register an MBean for this
     * adapter
     */
    C2DMServerConnectionImpl(C2DMDefinition definition) throws APIException {

        try {
            theCDMServerURL = new URL(C2DMCommon.C2DM_MESSAGES_SERVER);
        }
        catch (Exception ex) {
            throw new APIException("Cannot compose C2DM server URL",ex);
        }

        theDefinition = definition;

        // Register with MBean server
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName =
                new ObjectName(
                    String.format(
                        "%s:type=%s,name=%s",
                        C2DMCommon.JMX_DOMAIN,
                        "C2DMServerConnection",
                        definition.getName()));
            mbs.registerMBean(this,objectName);
        }
        catch (Exception ex) {
            throw new APIException(
                String.format(
                    "Cannot construct MBean for \"%s\"",definition.getName()),
                ex);
        }
    }

    /**
     * @see C2DMServerConnection#send(String, Map)
     */
    @Override
    public String send(String clientRegistrationID,Map<String,String> params)
    throws C2DMException, C2DMUnavailableException, APIException {
        return send(clientRegistrationID,params,null);
    }

    /**
     * @see C2DMServerConnection#send(String, Map, String)
     */
    @Override
    public String send(
    String clientRegistrationID,
    Map<String,String> params,
    String collapseKey)
    throws C2DMException, C2DMUnavailableException, APIException {
        // First, fetch/reuse an existing Google Auth-token for the given
        // connection
        String authToken = getGoogleAuthToken();

        // Compose the data to POST
        HashMap<String,String> postDict = new HashMap<String,String>();
        postDict.put(
            C2DMCommon.PARAM_REGISTRATION_ID,
            clientRegistrationID);
        postDict.put(
            C2DMCommon.PARAM_COLLAPSE_KEY,
            collapseKey!=null ? collapseKey : getDefCollapseKey());
        if (params!=null) {
            postDict.putAll(params);
        }

        String postString = C2DMCommon.buildURI(postDict);
        byte[] postBytes = postString.getBytes();

        OutputStream out = null;

        // Hit the URL.
        try {
            theMessageCount.incrementAndGet();
            theLastMessageSentMillis = System.currentTimeMillis();

            HttpsURLConnection connection =
                (HttpsURLConnection)theCDMServerURL.openConnection();
            connection.setHostnameVerifier(new CustomizedHostnameVerifier());
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",""+postBytes.length);
            connection.setRequestProperty(
                "Authorization",
                "GoogleLogin auth="+authToken);

            out = connection.getOutputStream();
            out.write(postBytes);
            IOUtils.close(out);

            // Check for a renewed
            checkForAuthUpdate(connection,authToken);

            // Check the response Code
            int responseCode = connection.getResponseCode();
            switch (responseCode) {
            case 200:
                // Success, but check for errors in the body
                BufferedReader responseReader = null;
                String responseLine = null;
                try {
                    responseReader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                    responseLine = responseReader.readLine();
                }
                catch (IOException ex) {
                    throw new C2DMException("Failed to read response",ex);
                }
                finally {
                    IOUtils.close(responseReader);
                }
                if (responseLine==null||responseLine.equals("")) {
                    throw new C2DMException(
                        "Got empty response from Google datamessaging endpoint.");
                }

                String[] responseParts = responseLine.split("=",2);
                if (responseParts.length!=2) {
                    // Invalid message from Google
                    throw new C2DMException(
                        "Invalid response from Google "+
                            responseCode+" "+responseLine);
                }
                String key = responseParts[0],
                value = responseParts[1];

                if (key.equals("id")) {
                    // Successfully sent data message to device
                    return value;
                }

                if (key.equals("Error")) {
                    // Got error response from Google datamessaging endpoint
                    throw new C2DMException(value);
                }

                // Invalid content in response
                throw new C2DMException(
                    String.format(
                        "Unexpected content in C2DM response \"%s\"",
                        responseLine));

                // Invalid auth-token
            case 401:
            case 403:
                // Unauthorised: The token is wrong
                throw new C2DMException(
                    String.format(
                        "Server error: %d, server-side authentication invalid",
                        responseCode));

                // Service Unavailable
            case 503:
                throw new C2DMUnavailableException(
                    String.format(
                        "C2DM servers currently unavailable (%d)",
                        responseCode),
                    connection);

            default:
                throw new C2DMException(
                    String.format(
                        "Unexpected HTTP response code %d",
                        responseCode));
            }
        }
        catch (IOException ex) {
            IOUtils.close(out);
            throw new APIException(
                "Exception interacting with Google C2DM servers",
                ex);
        }
    }

    /**
     * @return The defined collapse-key if there is one, otherwise the name of
     * the definition
     */
    private String getDefCollapseKey() {
        return theDefinition.getCollapse()!=null ? theDefinition.getCollapse()
            : theDefinition.getName();
    }

    /**
     * Check for an updated Google auth-token in a C2DM response
     * 
     * @param conn
     */
    private void checkForAuthUpdate(HttpURLConnection conn,String authToken) {
        String updatedAuthToken =
            conn.getHeaderField(C2DMCommon.UPDATE_CLIENT_AUTH);
        if (updatedAuthToken!=null&&!authToken.equals(updatedAuthToken))
            setGoogleAuthToken(updatedAuthToken);

    }

    /**
     * Explicitly fetch a server-side auth token
     * 
     * @throws APIException
     */
    @SuppressWarnings("resource")
    private synchronized void fetchGoogleAuthToken() throws APIException {
        // Create the post data
        String request =
            C2DMCommon.buildURI(
                "Email",theDefinition.getEmail(),
                "Passwd",theDefinition.getPassword(),
                "accountType","GOOGLE",
                "service","ac2dm");

        OutputStream output = null;
        BufferedReader reader = null;

        try {
            // Setup the HTTP Post
            byte[] data = request.getBytes();
            URL url = new URL(C2DMCommon.C2DM_LOGGIN_SERVER);
            HttpURLConnection connection =
                (HttpURLConnection)url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded");
            connection.setRequestProperty(
                "Content-Length",
                Integer.toString(data.length));

            // Issue the HTTP POST request
            output = connection.getOutputStream();
            output.write(data);
            output.close();

            // Read the response
            reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            String line = null;

            while ((line = reader.readLine())!=null) {
                if (line.startsWith("Auth=")) {
                    setGoogleAuthToken(line.substring(5));
                    return;
                }
            }
            setGoogleAuthToken(null);
        }
        catch (Exception ex) {
            throw new APIException("Unable to fetch auth token",ex);
        }
        finally {
            IOUtils.close(output);
            IOUtils.close(reader);
        }
    }

    /**
     * @see C2DMServerConnection#getDefinition()
     */
    @Override
    public C2DMDefinition getDefinition() {
        return theDefinition;
    }

    /**
     * Fetch the auth-token if already authenticated. If not, authenticate with
     * Google and return that.
     * 
     * @return the auth-token, or null if authentication was not possible.
     * @since 4.1
     */
    public String getGoogleAuthToken() {
        try {
            if (theGoogleAuthToken==null) {
                fetchGoogleAuthToken();
            }
            return theGoogleAuthToken;
        }
        catch (Throwable ex) {
            Logs.severe(
                String.format(
                    "Cannot authenticate C2DM definition \"%s\"",
                    theDefinition.getName()),
                ex);
            return null;
        }
    }

    /**
     * Set the Google auth-token, and record the time of authentication
     * 
     * @param googleAuthToken returned from {@link #getGoogleAuthToken()}
     * @since 4.1
     */
    public void setGoogleAuthToken(String googleAuthToken) {
        theGoogleAuthToken = googleAuthToken;
        if (null!=googleAuthToken) {
            theGoogleAuthenticated = System.currentTimeMillis();
        }
    }

    /**
     * @see C2DMServerConnection#isGoogleAuthenticated()
     */
    @Override
    public boolean isGoogleAuthenticated() {
        return theGoogleAuthToken!=null;
    }

    /**
     * @see C2DMServerConnectionImplMBean#getMessagesSent()
     */
    @Override
    public int getMessagesSent() {
        return theMessageCount.get();
    }

    /**
     * @see C2DMServerConnectionImplMBean#getLastMessageSent()
     */
    @Override
    public Date getLastMessageSent() {
        return theLastMessageSentMillis>0 ? new Date(theLastMessageSentMillis)
            : null;
    }

    /**
     * @see C2DMServerConnectionImplMBean#getDefinitionName()
     */
    @Override
    public String getDefinitionName() {
        return theDefinition.getName();
    }

    /**
     * @see C2DMServerConnectionImplMBean#getDefinitionEmailAddress()
     */
    @Override
    public String getDefinitionEmailAddress() {
        return theDefinition.getEmail();
    }

    /**
     * @see C2DMServerConnectionImplMBean#getCollapseKey()
     */
    @Override
    public String getCollapseKey() {
        return theDefinition.getCollapse();
    }

    /**
     * @see C2DMServerConnectionImplMBean#getWhenGoogleAuthenticated()
     */
    @Override
    public Date getWhenGoogleAuthenticated() {
        return theGoogleAuthenticated>0 ? new Date(theGoogleAuthenticated)
            : null;
    }

    /**
     * @see C2DMServerConnectionImplMBean#disconnect()
     */
    @Override
    public void disconnect() {
        if (theUrlConnection!=null)
            theUrlConnection.disconnect();
    }

    /**
     * Hack to overcome the fact that C2DM doesn't used properly signed certs
     * 
     * @author martincowie
     */
    private static class CustomizedHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname,SSLSession session) {
            return true;
        }
    }

}
