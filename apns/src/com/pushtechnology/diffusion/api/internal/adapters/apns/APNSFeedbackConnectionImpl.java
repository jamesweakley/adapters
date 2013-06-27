package com.pushtechnology.diffusion.api.internal.adapters.apns;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.net.ssl.SSLContext;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.IOUtils;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.adapters.apns.APNSFeedback;
import com.pushtechnology.diffusion.api.adapters.apns.APNSFeedbackConnection;
import com.pushtechnology.diffusion.api.adapters.apns.APNSFeedbackListener;
import com.pushtechnology.diffusion.api.threads.ThreadService;
import com.pushtechnology.diffusion.xmlproperties.pushnotification.APNSDefinition;

/**
 * Implementation of {@link APNSFeedbackConnection}
 * 
 * @author mcowie - created 29 Nov 2011
 * @since 4.1.0
 */
public final class APNSFeedbackConnectionImpl
implements Runnable,APNSFeedbackConnectionImplMBean,APNSFeedbackConnection {

    private final HashSet<APNSFeedbackListener> theListeners =
        new HashSet<APNSFeedbackListener>();
    private final SSLContext theSSLContext;
    private final APNSDefinition theDefinition;
    private Socket theSocket;
    private Thread theListenerThread;

    // For monitoring
    private Date theConnectedDatestamp;
    private AtomicInteger theMessagesReceived = new AtomicInteger();
    private long theLastMessageReceivedMillis;

    /**
     * Construct an APNSFeedbackConnectionImpl object. Register it as an MBean
     * in the MBean server.
     * 
     * @param definition Configuration associated
     * @throws FileNotFoundException If the P12 certificate given in the
     * configuration cannot be found.
     * @throws APIException If it is not possible to register this object as an
     * MBean
     */
    public APNSFeedbackConnectionImpl(APNSDefinition definition)
    throws FileNotFoundException, APIException {
        theDefinition = definition;
        // Build an SSLContext from the given crypto artifacts

        FileInputStream certStream = null;

        try {
            certStream =
                new FileInputStream(definition.getCertificate());
            theSSLContext =
                APNSCommon.newSSLContext(
                    certStream,definition.getPassphrase(),
                    APNSCommon.KEYSTORE_TYPE,
                    APNSCommon.KEY_ALGORITHM);
            IOUtils.close(certStream);
        }
        catch (IOException ex) {
            IOUtils.close(certStream);
            throw new APIException("Unable to load certificate", ex);
        }
        
        // Place a connection
        connect();

        try {
            // Register with MBean server
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName =
                new ObjectName(
                    String.format(
                        "%s:type=%s,name=%s",
                        APNSCommon.JMX_DOMAIN,"APNSFeedbackConnection",
                        definition.getName()));
            mbs.registerMBean(this,objectName);
        }
        catch (Exception ex) {
            throw new APIException(
                String.format(
                    "Cannot construct MBean for \"%s\"",
                    definition.getName()),
                ex);
        }
    }

    /**
     * @see APNSFeedbackConnection#addListener(APNSFeedbackListener)
     */
    @Override
    public synchronized boolean addListener(APNSFeedbackListener handler) {
        return theListeners.add(handler);
    }

    /**
     * @see APNSFeedbackConnection#removeListener(APNSFeedbackListener)
     */
    @Override
    public synchronized boolean removeListener(APNSFeedbackListener handler) {
        return theListeners.remove(handler);
    }

    /**
     * @see APNSFeedbackConnectionMBean#connect()
     */
    @Override
    public void connect() {
        if (isConnected())
            return;

        try {
            String apnsHost =
                theDefinition.isProduction() ? APNSCommon.PROD_FEEDBACK_HOST
                    : APNSCommon.DEV_FEEDBACK_HOST;

            Logs.fine(
                String.format(
                    "Placing SSL connection to %s:%d",
                    apnsHost,
                    APNSCommon.FEEDBACK_PORT));

            theSocket =
                theSSLContext.getSocketFactory().createSocket(
                    apnsHost,
                    APNSCommon.FEEDBACK_PORT);

            startListenerThread();

            theConnectedDatestamp = new Date();
        }
        catch (Throwable ex) {
            Logs.severe("Cannot connect to APNS",ex);
            theSocket = null;
        }
    }

    /**
     * @see APNSFeedbackConnectionMBean#isConnected()
     */
    @Override
    public boolean isConnected() {
        return theSocket!=null&&theSocket.isConnected()&&!theSocket.isClosed();
    }

    /**
     * @see APNSFeedbackConnectionMBean#disconnect()
     */
    @Override
    public void disconnect() {
        try {
            theSocket.close();
        }
        catch (IOException ex) {
            Logs.warning("Close error",ex);
        }
    }

    /**
     * Start a listener thread and listen to incoming data from APNS
     */
    private void startListenerThread()
    throws APIException {

        final String name = "APNS Feedback listener: "+theDefinition.getName();
        theListenerThread =
            ThreadService.newThread(this,name,Thread.NORM_PRIORITY,true);
        theListenerThread.start();

    }

    /**
     * @see Runnable#run()
     */
    @Override
    public void run() {
        try {
            final InputStream inputStream = theSocket.getInputStream();
            while (theSocket.isClosed()==false) {
                // Listen to framed responses from APNS
                APNSFeedback feedback = APNSFeedback.read(inputStream);

                theMessagesReceived.incrementAndGet();

                for (APNSFeedbackListener handler:theListeners) {
                    handler.handleFeedback(feedback);
                }
            }
        }
        catch (java.io.EOFException ex) {
            Logs.fine("APNS feedback socket closed.");
            disconnect();
        }
        catch (SocketException ex) {
            if (ex.getMessage().equals("Socket closed")) {
                Logs.fine("APNS feedback socket closed.");
            }
            else {
                Logs.severe("APNS feedback socket exception",ex);
            }
            disconnect();
        }
        catch (Exception ex) {
            Logs.severe("APNS Run error",ex);
        }
        finally {
            Logs.fine(
                String.format(
                    "Thread \"%s\" concluded",
                    Thread.currentThread().getName()));
        }
    }

    /**
     * @see APNSFeedbackConnectionMBean#getConnectedDatestamp()
     */
    @Override
    public Date getConnectedDatestamp() {
        return theConnectedDatestamp;
    }

    /**
     * @see APNSFeedbackConnectionMBean#getMessagesReceived()
     */
    @Override
    public int getMessagesReceived() {
        return theMessagesReceived.get();
    }

    /**
     * @see APNSFeedbackConnectionMBean#getLastMessageReceived()
     */
    @Override
    public Date getLastMessageReceived() {
        return new Date(theLastMessageReceivedMillis);
    }

    /**
     * @see APNSFeedbackConnectionMBean#getDefinitionName()
     */
    @Override
    public String getDefinitionName() {
        return theDefinition.getName();
    }

    /**
     * @see APNSFeedbackConnectionMBean#getDefinitionCertificate()
     */
    @Override
    public String getDefinitionCertificate() {
        return theDefinition.getCertificate();
    }

    /**
     * @see APNSFeedbackConnectionMBean#getDefinitionIsProduction()
     */
    @Override
    public boolean getDefinitionIsProduction() {
        return theDefinition.isProduction();
    }

}
