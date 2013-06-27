package com.pushtechnology.diffusion.api.internal.adapters.twitter;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.Collection;

import org.apache.commons.httpclient.HttpException;

public interface TwitterProcessor {

    /**
     * Connects to twitter and handles tweets until it gets an exception or is
     * interrupted.
     */
    void connectAndProcess()
    throws HttpException, InterruptedException, IOException;

    /**
     * Disconnects the the current processor from the Twitter server.
     */
    void disconnect();

    /**
     * Checks whether the current twitter processor is connected to the server.
     * 
     * @return true if this processor is connected to the server. False
     * otherwise.
     */
    boolean isConnected();

    /**
     * Gets the current state for the processor. Borrowed from Thread state.
     * 
     * @return The state of this processor
     */
    State getState();

    /**
     * 
     * Gets the processor name. Borrowed from its thread name.
     * 
     * @return The state of this processor
     */
    String getProcessorName();
    
    /**
     * 
     * Gets the processor twitter targets. 
     * 
     * @return The list of targets.
     */
    Collection<String> getTarget();
    
    /**
     * Gets the processor HTTP action.
     * 
     * @return The string action.
     */
    String getAction();
    
    /**
     * Add an exception listener
     *
     * @param listener
     * @return
     */
    boolean addExceptionListener(TwitterProcessorExceptionListener listener);
    
    /**
     * Remove an exception listener
     *
     * @param listener
     * @return
     */
    boolean removeExceptionListener(TwitterProcessorExceptionListener listener);
    
    /**
     * Get TCP backoff
     *
     * @return
     */
    long getCurrentTcpBackOffDelay();
    
    /**
     * Get HTTP backoff
     *
     * @return
     */
    long getCurrentHttpBackOffDelay();

}