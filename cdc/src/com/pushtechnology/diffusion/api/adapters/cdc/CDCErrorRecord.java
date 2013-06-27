/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:24:52
 */

package com.pushtechnology.diffusion.api.adapters.cdc;


/**
 * 'Error' Record.
 * @author pwalsh
 *
 */
@Deprecated
public interface CDCErrorRecord extends CDCRecord {


    /**
     * Checks if the CDC session is still valid
     * <p>
     * @return true if the session is still valid
     */
    boolean isSessionValid();


    /**
     * Returns the CDC error flag.
     *  
     * @return the error flag from CDC
     */
    int getFlag();
    
    /**
     * Returns the CDC error code. 
     * 
     * @return the error code from CDC
     */
    int getErrorCode();
    
   

}
