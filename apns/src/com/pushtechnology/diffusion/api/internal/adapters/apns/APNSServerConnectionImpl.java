package com.pushtechnology.diffusion.api.internal.adapters.apns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.net.ssl.SSLContext;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.APIProperties;
import com.pushtechnology.diffusion.api.IOUtils;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.adapters.apns.APNSResponse;
import com.pushtechnology.diffusion.api.adapters.apns.APNSResponseListener;
import com.pushtechnology.diffusion.api.adapters.apns.APNSServerConnection;
import com.pushtechnology.diffusion.api.threads.ThreadService;
import com.pushtechnology.diffusion.xmlproperties.pushnotification.APNSDefinition;

/**
 * Concrete implementation of APNSServerConnection
 * 
 * @author martincowie
 * 
 */
@Deprecated
public final class APNSServerConnectionImpl implements Runnable,
APNSServerConnectionImplMBean,APNSServerConnection {
    private static final int ADVANCED_MESSAGE = 1;

    private APNSDefinition theDefinition;
    private SSLContext theSSLContext;
    private Socket theSocket;
    private static AtomicInteger theMessageID = new AtomicInteger();
    private Thread theListenerThread;
    private Set<APNSResponseListener> theListeners =
        new HashSet<APNSResponseListener>();

    private long theLastMessageSentMillis;
    private Date theConnectedDatestamp;

    public APNSServerConnectionImpl(APNSDefinition definition)
    throws APIException {
        theDefinition = definition;

        FileInputStream certStream = null;
        try {
            // Build an SSLContext from the given crypto artefacts
            certStream =
                new FileInputStream(definition.getCertificate());
            theSSLContext =
                APNSCommon.newSSLContext(
                    certStream,definition.getPassphrase(),
                    APNSCommon.KEYSTORE_TYPE,
                    APNSCommon.KEY_ALGORITHM);

            // Place a connection
            connect();

            // Register with MBean server
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName =
                new ObjectName(
                    String.format(
                        "%s:type=%s,name=%s",
                        APNSCommon.JMX_DOMAIN,
                        "APNSServerConnection",
                        definition.getName()));
            mbs.registerMBean(this,objectName);
        }
        catch (Exception ex) {
            throw new APIException(
                String.format(
                    "Cannot construct MBean for \"%s\"",definition.getName()),
                ex);
        }
        finally {
            IOUtils.close(certStream);
        }
    }

    /**
     * getSocket
     * 
     * @return Gets the (SSL) socket from within this object
     */
    Socket getSocket() {
        return theSocket;
    }

    /**
     * @see APNSServerConnectionImplMBean#connect()
     */
    @Override
    public void connect() {
        if (isConnected())
            return;

        try {
            String apnsHost =
                theDefinition.isProduction() ? APNSCommon.PROD_PUSH_HOST
                    : APNSCommon.DEV_PUSH_HOST;
            Logs.fine(
                String.format(
                    "Placing SSL connection to %s:%d",
                    apnsHost,
                    APNSCommon.PUSH_PORT));
            theSocket =
                theSSLContext.getSocketFactory().createSocket(
                    apnsHost,
                    APNSCommon.PUSH_PORT);

            startListenerThread();

            // Record connection-timestamp for monitoring
            theConnectedDatestamp = new Date();
        }
        catch (Throwable ex) {
            Logs.severe("Cannot connect to APNS",ex);
            theSocket = null;
        }
    }

    /**
     * @see APNSServerConnectionImplMBean#disconnect()
     */
    @Override
    public void disconnect() {
        try {
            theSocket.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @see APNSServerConnectionImplMBean#isConnected()
     */
    @Override
    public boolean isConnected() {
        return theSocket!=null&&theSocket.isConnected()&&!theSocket.isClosed();
    }

    /**
     * @see APNSServerConnection#send(byte[], java.lang.String, java.util.Date)
     */
    @Override
    public synchronized int send(byte[] apnsToken,String payload,Date expiry)
    throws APIException {
        if (payload.length()>APNSCommon.PAYLOAD_MAX)
            throw new APIException(
                String.format(
                    "Payload length %d exceeds APNS maximum of %d bytes",
                    payload.length(),
                    APNSCommon.PAYLOAD_MAX));

        try {
            // Frame the message for sending
            int result = theMessageID.getAndIncrement();
            byte framedMessage[] =
                marshallAdvanced(
                    apnsToken,
                    payload.getBytes(APIProperties.UTF8),
                    expiry,
                    result);

            // Send it
            OutputStream apnsStream = theSocket.getOutputStream();
            APNSCommon.spool(
                new ByteArrayInputStream(framedMessage),
                apnsStream);
            apnsStream.flush();

            // Record the event
            theLastMessageSentMillis = System.currentTimeMillis();

            return result;
        }
        catch (Throwable ex) {
            throw new APIException(
                String.format(
                    "Exception sending message via APNS connection \"%s\"",
                    theDefinition.getName()),
                ex);
        }
    }

    /**
     * @see APNSServerConnection#send(byte[], String)
     */
    @Override
    public synchronized int send(byte[] apnsToken,String payload)
    throws APIException {
        return send(apnsToken,payload,APNSCommon.MAX_DATE);
    }

    /**
     * @see APNSServerConnection#getDefinition()
     */
    @Override
    public APNSDefinition getDefinition() {
        return theDefinition;
    }

    /**
     * Assemble framed message as a byte-array
     * 
     * @param deviceToken the token retrieved from a iOS device
     * @param payload JSON formated string holding
     * @param expiry Date after which the message should not be sent.
     * @param messageID Message identifier
     * @return A byte array holding a framed message 'ready for the wire'
     */
    private static byte[] marshallAdvanced(byte[] deviceToken,byte[] payload,
    Date expiry,int messageID)
    throws APIException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(result);

        try {
            dos.writeByte(ADVANCED_MESSAGE);
            dos.writeInt(messageID);
            dos.writeInt((int)(expiry.getTime()/1000));

            dos.writeShort(deviceToken.length);
            dos.write(deviceToken);
            dos.writeShort(payload.length);
            dos.write(payload);
            return result.toByteArray();
        }
        catch (Throwable ex) {
            throw new APIException("Exception assembling APNS message",ex);
        }
    }

    /**
     * Start the socket listening thread
     */
    private void startListenerThread() throws APIException {

        theListenerThread =
            ThreadService.newThread(this,
                "APNS Listener: "+theDefinition.getName(),Thread.NORM_PRIORITY,
                true);
        theListenerThread.start();

    }

    /**
     * @see Runnable#run()
     */
    @Override
    public void run() {
        // Entry point for the socket listening thread
        try {
            InputStream inputStream = theSocket.getInputStream();
            while (theSocket.isClosed()==false) {
                // Listen to framed responses from APNS
                APNSResponse resp = APNSResponse.read(inputStream);

                for (APNSResponseListener handler:theListeners)
                    handler.handleResponse(resp);
            }
        }
        catch (EOFException ex) {
            Logs.fine("APNS socket closed.");
            disconnect();
        }
        catch (SocketException ex) {
            if (ex.getMessage().equals("Socket closed")) {
                Logs.fine("APNS socket closed.");
            }
            else {
                Logs.severe("APNS socket exception",ex);
            }
            disconnect();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            Logs.fine(
                String.format(
                    "Thread \"%s\" concluded",
                    Thread.currentThread().getName()));
        }
    }

    /**
     * @see APNSServerConnection#addListener(APNSResponseListener)
     */
    @Override
    public boolean addListener(APNSResponseListener handler) {
        return theListeners.add(handler);
    }

    /**
     * @see APNSServerConnection#removeListener(APNSResponseListener)
     */
    @Override
    public boolean removeListener(APNSResponseListener handler) {
        return theListeners.remove(handler);
    }

    /**
     * @see APNSServerConnectionImplMBean#getMessagesSent()
     */
    @Override
    public int getMessagesSent() {
        return theMessageID.get();
    }

    /**
     * @see APNSServerConnectionImplMBean#getLastMessageSent()
     */
    @Override
    public Date getLastMessageSent() {
        return theLastMessageSentMillis>0 ? new Date(theLastMessageSentMillis)
            : null;
    }

    /**
     * @see APNSServerConnectionImplMBean#getDefinitionName()
     */
    @Override
    public String getDefinitionName() {
        return theDefinition.getName();
    }

    /**
     * @see APNSServerConnectionImplMBean#getDefinitionCertificate()
     */
    @Override
    public String getDefinitionCertificate() {
        return theDefinition.getCertificate();
    }

    /**
     * @see APNSServerConnectionImplMBean#getDefinitionIsProduction()
     */
    @Override
    public boolean getDefinitionIsProduction() {
        return theDefinition.isProduction();
    }

    /**
     * @see APNSServerConnectionImplMBean#getConnectedDatestamp()
     */
    @Override
    public Date getConnectedDatestamp() {
        return theConnectedDatestamp;
    }

}
