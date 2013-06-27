package com.pushtechnology.diffusion.api.adapters.c2dm;

import javax.net.ssl.HttpsURLConnection;

/**
 * Exception to represent circumstances where C2DM is unavailable and the
 * implementor should back-off.
 * 
 * @author martincowie
 * @since 4.1
 */
public final class C2DMUnavailableException extends C2DMException {
    
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4039543343601762595L;
    
    /**
     * Retry after header field id
     */
    private static final String RETRY_AFTER = "Retry-After";

    /**
     * Retry after value
     */
    private String theRetryAfter;

    /**
     * Construct a {@link C2DMUnavailableException}
     * 
     * @param message Message to give to the exception
     * @param conn {@link HttpsURLConnection} to examine for the header
     * 'Retry-After'
     */
    public C2DMUnavailableException(String message,HttpsURLConnection conn) {
        super(message);
        theRetryAfter = conn.getHeaderField(RETRY_AFTER);
    }

    /**
     * Returns the 'Retry-After' header of the POST result, if any.
     * 
     * @return If non-null holds either an RFC-1123 date, or the number of
     * seconds for which a client should back off. <b>Failure to pay heed to
     * this can result in blacklisting</b>. Example value <cite>Mon, 28 Nov 2011
     * 15:57:22 GMT</cite>
     */
    public String getRetryAfter() {
        return theRetryAfter;
    }

}
