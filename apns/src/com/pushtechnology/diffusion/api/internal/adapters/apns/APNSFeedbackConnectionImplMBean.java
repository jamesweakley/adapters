package com.pushtechnology.diffusion.api.internal.adapters.apns;

import java.util.Date;

/**
 * JMX interface to the APNSFeedbackConnection object when appearing as an MBean
 * 
 * @author martincowie
 * 
 */
@Deprecated
public interface APNSFeedbackConnectionImplMBean {
    /**
     * @return True if there is currently a connection in place
     */
    public boolean isConnected();

    /**
     * @return The java.util.Date of last successful connection placed.
     */
    public Date getConnectedDatestamp();

    /**
     * @return The number of messages received via this object
     */
    public int getMessagesReceived();

    /**
     * @return java.util.Date of the last message received
     */
    public Date getLastMessageReceived();

    /**
     * @return The symbolic name used to identify this connection in the
     * configuration files.
     */
    public String getDefinitionName();

    /**
     * @return Filename of the P12 certificate used to place this connection
     */
    public String getDefinitionCertificate();

    /**
     * @return Returns true if configured to true in the configuration file
     */
    public boolean getDefinitionIsProduction();

    /**
     * Forces the connection to be placed.
     */
    public void connect();

    /**
     * Forces the connection to be torn down.
     */
    public void disconnect();

}
