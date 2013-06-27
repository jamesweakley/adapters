package com.pushtechnology.diffusion.api.adapters.c2dm;

import com.pushtechnology.diffusion.api.APIException;

/**
 * Exceptions relating to interaction with C2DM
 * 
 * @author martincowie - created Nov 30, 2011
 * @since 4.1
 */
@Deprecated
public class C2DMException extends APIException {
    
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 8698474169184265309L;

    /**
     * Constructor.
     * 
     * @param message exception message
     */
    public C2DMException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message exception message
     * @param ex cause
     */
    public C2DMException(String message,Throwable ex) {
        super(message,ex);
    }

}
