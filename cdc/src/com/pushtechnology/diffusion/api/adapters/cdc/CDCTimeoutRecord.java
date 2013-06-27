/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 16:29:12
 */

package com.pushtechnology.diffusion.api.adapters.cdc;


/**
 * 'Timeout' Record.
 * @author pwalsh
 *
 */
@Deprecated
public interface CDCTimeoutRecord extends CDCRecord {

    /**
     * Returns the sequence number of the record.
     * <p>
     * @return sequence number
     */
    long getSequenceNumber();
 
}
