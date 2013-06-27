package com.pushtechnology.diffusion.api.adapters.twitter;

/**
 * Defines the Tweet Message Level.
 *
 * @author pwalsh - created 25 Jan 2012
 * @since 4.1
 */
@Deprecated
public enum TweetMessageLevel {
    
    /**
     * Public tweets only.
     */
    TWEETS_ONLY,
    /**
     * public tweets and public replies (no retweets)
     */
    TWEETS_AND_REPLIES_ONLY,
    /**
     * public tweets and retweets only (no public replies)
     */
    TWEETS_AND_RETWEETS_ONLY,
    /**
     * All traffic (public tweets + public replies + retweets)
     */
    ALL;

}
