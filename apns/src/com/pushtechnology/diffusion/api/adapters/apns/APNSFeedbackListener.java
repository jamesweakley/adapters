package com.pushtechnology.diffusion.api.adapters.apns;

/**
 * Interface for objects required to handle delivery of {@link APNSFeedback} 
 * objects.
 * 
 * @author martincowie
 * @since 4.1
 */
public interface APNSFeedbackListener {
    /**
     * Handle feedback.
     * <P>
     * @param feedback Feedback received from APNS that an app has been
     * uninstalled from a handset.
     */
    void handleFeedback(APNSFeedback feedback);
}
