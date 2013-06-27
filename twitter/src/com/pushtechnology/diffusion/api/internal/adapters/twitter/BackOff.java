package com.pushtechnology.diffusion.api.internal.adapters.twitter;

/**
 * Handles backing off for an initial time, doubling until a cap is reached.
 * 
 * @author Antonio Di Ferdinando - created 18 Dec 2011
 * @since 4.1
 */
public final class BackOff {
    
    /**
     * True if initial backoff should be zero
     */
    private final boolean thisHasNoInitialBackoff;
    
    /**
     * The initial amount of time to back off, after an
     * optional zero-length initial backoff
     */
    private final long theInitialBackoff;
    
    /**
     * upper limit to the back off time
     */
    private final long theBackoffCap;
    
    /**
     * The current backoff value
     */
    private long theCurrentDelay;

    /**
     * @param noInitialBackoff true if the initial backoff should be zero
     * @param initialMillis the initial amount of time to back off, after an
     * optional zero-length initial backoff
     * @param capMillis upper limit to the back off time
     */
    public BackOff(
    boolean noInitialBackoff,long initialMillis,long capMillis) {
        thisHasNoInitialBackoff = noInitialBackoff;
        theInitialBackoff = initialMillis;
        theBackoffCap = capMillis;
        reset();
    }

    /**
     * @param initialMillis the initial amount of time to back off, after an
     * optional zero-length initial backoff
     * @param capMillis upper limit to the back off time
     */
    public BackOff(long initialMillis,long capMillis) {
        this(false,initialMillis,capMillis);
    }

    /**
     * Resets the current BackOff settings to defaults.
     */
    public void reset() {
        if (thisHasNoInitialBackoff) {
            theCurrentDelay = 0;
        }
        else {
            theCurrentDelay = theInitialBackoff;
        }
    }

    /**
     * Enacts the BackOff according to currently set timeouts
     */
    public void backOff() throws InterruptedException {
        if (theCurrentDelay==0) {
            theCurrentDelay = theInitialBackoff;
        }
        else {
            Thread.sleep(theCurrentDelay);
            theCurrentDelay *= 2;
            if (theCurrentDelay>theBackoffCap) {
                theCurrentDelay = theBackoffCap;
            }
        }
    }
    /**
     * Returns the current backoff value
     *
     * @return
     */
    public long getCurrentDelay() {
        return theCurrentDelay;
    }
}
