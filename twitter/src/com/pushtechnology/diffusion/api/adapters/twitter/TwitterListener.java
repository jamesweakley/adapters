package com.pushtechnology.diffusion.api.adapters.twitter;

/**
 * Interface defining a class to asynchronously receive Tweet objects from
 * the Twitter servers.
 * 
 * @author Antonio Di Ferdinando - created Jan 4, 2012
 * @since 4.1
 */
@Deprecated
public interface TwitterListener {

    /**
     * Handle Tweet objects from Twitter
     * 
     * @param tweet the tweet object
     */
    void handleTweet(Tweet tweet);
}
