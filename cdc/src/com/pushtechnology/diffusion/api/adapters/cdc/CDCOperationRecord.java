/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 08:56:40
 */

package com.pushtechnology.diffusion.api.adapters.cdc;


/**
 * Operational Record.
 * <P>
 * This is the base interface for Delete, Insert and Update Records.
 * <P>
 * The User Data returned is the return value from the 
 * {@link CDCConnection#enableCapture(String, String) enableCapture} method.
 * @author pwalsh
 *
 */
@Deprecated
public interface CDCOperationRecord extends CDCRecord {


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


    /**
     * Returns the CDC flags.
     * <p>
     * @return flags associated with this operation (normally meaningless)
     */
    int getFlags();


}
