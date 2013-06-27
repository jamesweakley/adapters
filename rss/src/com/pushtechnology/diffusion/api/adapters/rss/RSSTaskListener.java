package com.pushtechnology.diffusion.api.adapters.rss;

import com.pushtechnology.diffusion.api.APIException;

/**
 * The interface for an {@link RSSTask RSS Task} listener.
 * <P>
 * The listener is declared when an RSS Task is created and is called whenever
 * the task fetches a new update or when the task has closed due to a failure.
 * 
 * @author pwalsh
 *
 */
public interface RSSTaskListener {
    
    /**
     * This is called each time the task fetches an update.
     * <P>
     * @param document the document that encapsulates the fetched data.
     * 
     * @throws APIException thrown if an exception occurs. If this is thrown
     * back to the task then the task will stop and notify via the
     * {@link #rssTaskFailed(Throwable)} method. 
     */
    void rssTaskUpdate(RSSDocument document) throws APIException;
    
    /**
     * Notifies a failure within the task that has caused it to stop.
     * @param cause
     */
    void rssTaskFailed(Throwable cause);
}
