/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:58:52
 */

package com.pushtechnology.diffusion.api.adapters.cdc;

import java.util.Date;

/**
 * 'Commit' Record.
 * <P>
 * 
 * @author pwalsh
 * 
 */
@Deprecated
public interface CDCCommitRecord extends CDCRecord {

    /**
     * Returns the sequence number of the record.
     * <p>
     * 
     * @return sequence number
     */
    long getSequenceNumber();

    /**
     * Returns the database transaction ID
     * <p>
     * 
     * @return Database transaction ID
     */
    int getTransactionID();

    /**
     * Returns the transaction commit time
     * <p>
     * 
     * @return The UTC time at which the transaction committed, in time_t
     * format.
     */
    long getCommitTime();

    /**
     * Get transaction commit date
     * <p>
     * 
     * @return the commit date of the transaction
     */
    Date getCommitDate();

}
