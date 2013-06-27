package com.pushtechnology.diffusion.api.adapters.twitter;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.internal.adapters.twitter.TwitterAdapterInstance;

/**
 * Factory class, allowing access to {@link TwitterServerConnection} object.
 * 
 * @author Antonio Di Ferdinando - created 20 Dec 2011
 * @since 4.1
 */
public final class TwitterAdapter {

    /**
     * Class singleton
     */
    private static TwitterAdapterInstance theInstance =
        new TwitterAdapterInstance();

    /**
     * Fetch the Twitter server connection with the given name
     * <P>
     * 
     * @param connectionName Symbolic name matching one in TwitterAdapter.xml
     * 
     * @param messageLevel specifies the type of messages to display.
     * 
     * @return A TwitterServerConnection ready to use.
     * 
     * @throws APIException
     */
    public static TwitterServerConnection getNamedConnection(
    String connectionName,TweetMessageLevel messageLevel) throws APIException {
        return theInstance.getNamedConnection(connectionName,messageLevel);
    }
}
