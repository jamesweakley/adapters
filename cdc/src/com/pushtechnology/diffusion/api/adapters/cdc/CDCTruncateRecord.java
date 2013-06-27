/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:12:38
 */

package com.pushtechnology.diffusion.api.adapters.cdc;


/**
 * 'Truncate' Record.
 * <P>
 * 
 * @author pwalsh
 *
 */
public interface CDCTruncateRecord extends CDCRecord {

    /**
     * The sequence number of the record.
     * <p>
     * 
     * @return sequence number
     */
    long getSequenceNumber();

    /**
     * Get database transaction ID
     * <p>
     * 
     * @return Database transaction ID
     */
    int getTransactionID() ;


}
