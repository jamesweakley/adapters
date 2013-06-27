package com.pushtechnology.diffusion.api.internal.adapters.twitter;

import java.util.Date;

public interface TwitterServerConnectionImplMBean {
    /**
     * @return True if there is currently a connection in place
     */
    boolean isConnected();

    /**
     * @return The java.util.Date of last successful connection placed.
     */
    Date getConnectedDatestamp();

    /**
     * @return The symbolic name used to identify this connection in the
     * configuration files.
     */
    String getDefinitionName();

    /**
     * @return the list of IDs to follow as a comma-separated list
     */
    String getFollowIds();

    /**
     * @return the list of keywords to track as a comma-separated list
     */
    String getTrackKeywords();

    /**
     * @return Twitter user name
     */
    String getUsername();

    /**
     * @return Twitter password
     */
    String getPassword();

    /**
     * @return The maximum number of follow Ids to follow with one credential
     * set
     */
    Integer getMaxIdsPerCredentials();

    /**
     * @return The maximum number of keywords to track with one credential set
     */
    Integer getMaxKeywordsPerCredentials();

    /**
     * @return The amount of time (in millis) to wait between parallel threads
     * connecting
     */
    Integer getProcessForMillis();

    /**
     * @return The amount of time (in millis) to wait before a reconnecting to
     * the server upon disconnection
     */
    Integer getTcpInitialBackoff();

    /**
     * @return The maximum interval of time (in millis) to wait before a
     * reconnecting to the server upon disconnection
     */
    Integer getHttpBackoffCap();

    /**
     * @return The amount of time (in millis) to wait before a reconnecting to
     * the server upon disconnection
     */
    Integer getHttpInitialBackoff();

    /**
     * @return The maximum interval of time (in millis) to wait before a
     * reconnecting to the server upon disconnection
     */
    Integer getTcpBackoffCap();

    /**
     * Forces the connection to be placed.
     */
    void connect();

    /**
     * Forces the connection to be torn down.
     */
    void disconnect();
}
