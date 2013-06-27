package com.pushtechnology.diffusion.api.adapters.apns;

/**
 * Interface to the APNS feedback service
 * 
 * @author martincowie
 * @since 4.1
 */
public interface APNSFeedbackConnection {

    /**
     * Add a handler to this connection.
     * 
     * @param listener Added to an underlying Set<APNSFeedbackHandler> so can be
     * called many times w/o consequence
     * @return true if the handler was not yet present in the underlying set
     */
    boolean addListener(APNSFeedbackListener listener);

    /**
     * Remove a handler from this connection
     * 
     * @param listener the handler
     * @return true if the handler was present in the underlying set
     */
    boolean removeListener(APNSFeedbackListener listener);

    /**
     * Forces the connection to be placed.
     */
    void connect();

}