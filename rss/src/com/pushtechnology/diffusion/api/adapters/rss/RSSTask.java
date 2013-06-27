package com.pushtechnology.diffusion.api.adapters.rss;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.threads.RunnableTask;
import com.pushtechnology.diffusion.api.threads.ThreadService;

/**
 * This is a task which runs periodically to fetch from an RSS Feed and 
 * notify a listener.
 * <P>
 * The background thread pool is used for running.
 * <P>
 * The task is started using the {@link #start(int, TimeUnit) start} method 
 * which specifies a frequency of running.
 * 
 * @author pwalsh
 */
@Deprecated
public class RSSTask implements RunnableTask {
    
    private RSSDocument theRSSDocument;
    private RSSTaskListener theListener;
    private ScheduledFuture<?> theFuture;
    private int theExceptionTolerance = 3;
    private int theNumberOfExceptions = 0;
   

    /**
     * Constructor.
     * @param url the RSS URL
     * @param listener the listener to call back on when the task fetches an
     * update or when it fails.
     * @throws APIException if unable to create the task.
     */
    public RSSTask(String url,RSSTaskListener listener) 
    throws APIException {        
        theRSSDocument=new RSSDocument(url);
        theListener=listener;
    }
    
    /**
     * Returns the RSS Document associated with this task.
     * 
     * @return the RSS document.
     */
    public RSSDocument getDocument() {
        return theRSSDocument;
    }

    /**
     * Starts the task.
     * <P>
     * If the task is already running then it will be stopped and restarted 
     * with the specified frequency.
     * 
     * @param frequency the interval after which the task should be first run 
     * then periodically run until stopped.
     * 
     * @param timeUnit the time unit of the frequency.
     * 
     * @throws APIException if unable to start the task.
     */
    public synchronized void start(int frequency,TimeUnit timeUnit) 
    throws APIException {
        if (theFuture!=null) {
            theFuture.cancel(false);
        }
        
        theFuture =
            ThreadService.schedule(   
                this,
                frequency,
                frequency,
                timeUnit,
                false);
    }
    
    /**
     * Perform a fetch from the RSS feed.
     * <P>
     * This may be called independently of the periodic task if required. For
     * example to perform an initial load.
     * 
     * @return the document that encapsulates the fetched data.
     * 
     * @throws APIException if the fetch fails.
     */
    public synchronized RSSDocument fetch() throws APIException {
        theRSSDocument.fetch();
        return theRSSDocument;
    }
    
    /**
     * Stop the task.
     */
    public synchronized void stop() {
        if (theFuture!=null) {
            theFuture.cancel(false);
            theFuture=null;
        }
    }
    
    /**
     * Indicates whether the task is running.
     * <P>
     * @return true if running.
     */
    public boolean isRunning() {
        return (theFuture!=null);
    }


    /**
     * @see com.pushtechnology.diffusion.api.threads.RunnableTask#run()
     */
    public void run() {
        synchronized (this) {
            if (!isRunning()) {
                return;
            }
            try {
                RSSDocument document = fetch();
                try {
                    theListener.rssTaskUpdate(document);
                    theNumberOfExceptions=0;
                }
                catch (Throwable ex) {
                    Logs.severe(
                        "Exception returned from RSS Task processing - " +
                        "Stopping : "+
                        ex.getLocalizedMessage());
                    stop();
                    theListener.rssTaskFailed(ex);                 
                }
            }
            catch (Exception ex) {
                theNumberOfExceptions++;
                if (theNumberOfExceptions>theExceptionTolerance) {
                    Logs.severe(
                        "RSS Task - Too Many Exceptions - Stopping");
                    stop();
                    theListener.rssTaskFailed(ex);
                }
                else {
                    Logs.warning(
                        "RSS Task Exception - Continuing : "+
                        ex.getLocalizedMessage());
                }          
            }   
        }
    }

}
