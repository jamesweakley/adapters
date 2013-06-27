package com.pushtechnology.diffusion.api.adapters.cdc;

import java.util.Date;

/**
 * Begin Transaction' Record.
 * <P>
 * 
 * @author pwalsh
 * 
 */
@Deprecated
public interface CDCBeginTransactionRecord extends CDCRecord {

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
     * Returns the transaction start time
     * <p>
     * 
     * @return The UTC time at which the transaction began, in time_t format.
     */
    long getStartTime();

    /**
     * Returns the transaction start date
     * <p>
     * 
     * @return the start date of the transaction
     */
    Date getStartDate();

    /**
     * Returns the user ID
     * <p>
     * 
     * @return The operating system user ID of the user who started the
     * transaction.
     */
    int getUserID();

}
