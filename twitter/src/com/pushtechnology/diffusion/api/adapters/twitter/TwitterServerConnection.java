package com.pushtechnology.diffusion.api.adapters.twitter;

import com.pushtechnology.diffusion.xmlproperties.twitter.TwitterDefinition;

/**
 * Interface to the Twitter service
 * 
 * @author Antonio Di Ferdinando - created 24 Dec 2011
 * @since 4.1
 */
@Deprecated
public interface TwitterServerConnection {
    
    /**
     * Get the notification model.
     * 
     * @return the configuration associated with this object
     */
    TwitterDefinition getConnection();

    /**
     * Add an {@link TwitterListener} object to the <em>set</em> of handler
     * objects.
     * <P>
     * All handlers are notified of any TwitterObject objects received from the
     * Twitter servers.
     * 
     * @param handler Handler object to add to the set.
     * @return true if the handler was not yet present in the underlying set
     */
    boolean addListener(TwitterListener handler);

    /**
     * Remove the given {@link TwitterListener} object from the set of handlers
     * <P>
     * 
     * @param handler the handler to remove
     * @return true if the handler was present in the underlying set
     */
    boolean removeListener(TwitterListener handler);

    /**
     * @return True if there is currently a connection in place
     */
    boolean isConnected();

}
