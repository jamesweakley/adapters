Twitter adapter
===============

The Twitter adapter provides an easy way of integrating a flow of tweets. The adapter is easy to use. Once configured correctly with Twitter credentials, all that is necessary is to implement the Twitter listener, instantiate the adapter and handle the objects received.

Using the Twitter adapter
-------------------------

1. Instantiate the adapter as follows:

    TwitterServerConnection tc =
        TwitterAdapter.getNamedConnection("myTwitterConnection",
                                         TweetMessageLevel.ALL);
    if(tc.isConnected()) {
        tc.addListener(this);
    } 

2. Print the tweet text within a received object:

    public void handleTweet(Tweet tweet) { 
        System.out.println(tweet.get("text")); 
    }
