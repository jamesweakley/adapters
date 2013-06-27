/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:45:48
 */

package com.pushtechnology.diffusion.api.adapters.cdc;


/**
 * 'Discard' record.
 * @author pwalsh
 *
 */
public interface CDCDiscardRecord extends CDCRecord {
 
    /**
     * Returns the sequence number of the record.
     * <p>
     * @return sequence number
     */
    long getSequenceNumber();

    /**
     * Returns the database transaction ID
     * <p>
     * @return Database transaction ID
     */
    int getTransactionID();
 
}
