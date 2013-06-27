package com.pushtechnology.diffusion.api.internal.adapters.twitter;

public interface TwitterProcessorExceptionListener {

    void handleException(String name,Exception ex);

}
