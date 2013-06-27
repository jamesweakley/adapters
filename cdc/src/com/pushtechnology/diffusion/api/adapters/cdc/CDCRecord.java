package com.pushtechnology.diffusion.api.adapters.cdc;

import java.nio.ByteBuffer;

/**
 * Base interface for all CDC Records.
 * @author pwalsh
 *
 */
public interface CDCRecord {

    
    /**
     * Returns the payload of the Record.
     * <P>
     * @return the record payload
     */
    ByteBuffer getPayload();

    /**
     * Returns the record header.
     * <P>
     * @return the record header.
     */
    ByteBuffer getHeader();

    /**
     * Is this a 'begin transaction' record?
     * <P>
     * @return true if a 'begin transaction' record.
     */
    boolean isBeginTransactionRecord();

    /**
     * Is this a 'commit' record?
     * <P>
     * @return true if a 'commit' record.
     */
    boolean isCommitRecord();

    /**
     * Is this a 'delete' record?
     * <P>
     * @return true if a 'delete' record.
     */
    boolean isDeleteRecord();
    
    /**
     * Is this a 'discard' record?
     * <P>
     * @return true if a 'discard' record.
     */
    boolean isDiscardRecord();

    /**
     * Is this an 'error' record?
     * <P>
     * @return true if a 'error' record.
     */
    boolean isErrorRecord();

    /**
     * Is this a 'insert' record?
     * <P>
     * @return true if a 'insert' record.
     */
    boolean isInsertRecord();

    /**
     * Is this a 'rollback record?
     * <P>
     * @return true if a 'rollback' record.
     */
    boolean isRollbackRecord();

    /**
     * Is this a 'Metadata' record?
     * <P>
     * @return true if a 'Metadata' record.
     */
    boolean isMetadataRecord();

    /**
     * Is this a 'timeout' record?
     * <P>
     * @return true if a 'timeout record.
     */
    boolean isTimeoutRecord() ;

    /**
     * Is this a 'truncate' record?
     * <P>
     * @return true if a 'truncate' record.
     */
    boolean isTruncateRecord() ;

    /**
     * Is this a 'Update before' record?
     * <P>
     * @return true if a 'Update before' record.
     */
    boolean isUpdateBeforeRecord();

    /**
     * Is this a 'Update after' record?
     * <P>
     * @return true if a 'Update after' record.
     */
    boolean isUpdateAfterRecord();

    /**
     * Does the record have user data?
     * @return true if the record has user data
     */
    boolean hasUserData();
    
    /**
     * Returns User Data (if any).
     * <P>
     * @return the user data (or 0 for records that have no user data)
     */
    int getUserData();
    
    /**
     * Is this an 'operational' record, i.e. Delete, Insert or Update.
     * @return true if an operational record
     */
    boolean isOperationalRecord();
    
}
