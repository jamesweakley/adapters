/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 14:48:13
 */

package com.pushtechnology.diffusion.api.adapters.cdc;

/**
 * The interface for receiving events from a {@link CDCConnection}.
 * <P>
 * Such a listener may be added using the 
 * {@link CDCConnection#addCDCRecordListener(CDCRecordListener)} method.
 * @author pwalsh
 *
 */
@Deprecated
public interface CDCRecordListener {

    /**
     * This method will be called when a CDC record has arrived.
     * <p>
     * @param record a CDC Record
     */
    public void onCDCRecord(CDCRecord record);
    
}
