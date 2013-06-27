/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:18:21
 */

package com.pushtechnology.diffusion.api.adapters.cdc;


/**
 * 'Rollback' Record.
 * @author pwalsh
 *
 */
@Deprecated
public interface CDCRollbackRecord extends CDCRecord {

 
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
