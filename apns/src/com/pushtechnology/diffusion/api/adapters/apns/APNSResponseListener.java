package com.pushtechnology.diffusion.api.adapters.apns;

/**
 * Interface defining a class to asynchronously receive APNSResponseHandler
 * objects from the APNS servers.
 * 
 * @author martincowie
 * @since 4.1
 */
@Deprecated
public interface APNSResponseListener {
    
    /**
     * Handle APNSResponse objects from APNS
     * 
     * @param response the response
     */
    void handleResponse(APNSResponse response);
}
