RSS adapter
===========

RSS (most commonly expanded as "Really Simple Syndication") is a family of web feed formats
used to publish frequently updated works such as blog entries, news headlines, audio, and video
in a standardised format. An RSS document (which is called a "feed", "web feed", or "channel")
includes full or summarised text, plus metadata such as publishing dates and authorship.

Using the RSS adapter
---------------------

1. The main class is the `RSSDocument` which is constructed with the URL of the RSS feed as follows :-

        RSSDocument rssDocument = new RSSDocument("http://feeds.reuters.com/reuters/topNews.rss");
        
2. Create an `RSSDocument` then fetch data from the feed as follows :-

        rssDocument.fetch();
        
3. After a fetch the feed data is available from the document as XML or more usefully in the form
   of an `RSSChannel` object which enables easy access to the subordinate data, for example :-
   
        RSSChannel channel = rssDocument.getChannel();
        for (RSSItem item : channel.getItems()) {
            System.out.println(item.getGuid());
        }
        
4. Poll an RSS Feed In order to use an RSS feed you would typically poll it at some regular interval.
   The `RSSTask` class is provided to simplify this type of processing. The following example shows a
   typical use of the `RSSTask` class. This initially connects to and loads from the feed and then when
   started will poll the feed every 30 seconds and update data as appropriate.
   
        public class RSSFeed implements RSSTaskListener {
   
            private final RSSTask theRSSTask;

            public RSSFeed() throws APIException {
                theRSSTask =
                    new RSSTask("http://feeds.reuters.com/reuters/topNews.rss",this);
                initialise(theRSSTask.fetch());
            }
   
            private void initialise(RSSDocument document) {
                // Initial load of news data
            }
   
            public void start() throws APIException {
                theRSSTask.start(30,TimeUnit.SECONDS);
            }
   
            public void rssTaskUpdate(RSSDocument document) throws APIException {
                // Process news from document as required
            }
 
            public void rssTaskFailed(Throwable cause) {
                Logs.severe("RSS Feed Failed",cause);
            }
   
            public void stop() {
                theRSSTask.stop();
            }
        }
