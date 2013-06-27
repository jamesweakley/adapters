/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 16:19:10
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord;

/**
 * Base class for all CDC Records.
 * @author pwalsh
 *
 */
abstract class CDCRecordImpl implements CDCRecord {

    private final CDCHeader theHeader;

    /**
     * Constructor
     * 
     * @param header
     */
    CDCRecordImpl(CDCHeader header) {
        theHeader = header;
        parseHeader();
    }

   
    /**
     * Get CDC record type
     * @return
     */
    final int getCDCRecordType() {
        return theHeader.getLogRecordType();
    }

   
   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#getPayload()
     */
    public final ByteBuffer getPayload() {
        return (ByteBuffer)theHeader.getPayload().rewind();
    }

   
   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#getHeader()
     */
    public final ByteBuffer getHeader() {
        return (ByteBuffer)theHeader.getHeader().rewind();
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isBeginTransactionRecord()
     */
    public boolean isBeginTransactionRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isCommitRecord()
     */
    public boolean isCommitRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isDeleteRecord()
     */
    public boolean isDeleteRecord() {
        return false;
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isDiscardRecord()
     */
    public boolean isDiscardRecord() {
        return false;
    }

    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isErrorRecord()
     */
    public boolean isErrorRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isInsertRecord()
     */
    public boolean isInsertRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isRollbackRecord()
     */
    public boolean isRollbackRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isMetadataRecord()
     */
    public boolean isMetadataRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isTimeoutRecord()
     */
    public boolean isTimeoutRecord() {
        return false;
    }


    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isTruncateRecord()
     */
    public boolean isTruncateRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isUpdateBeforeRecord()
     */
    public boolean isUpdateBeforeRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isUpdateAfterRecord()
     */
    public boolean isUpdateAfterRecord() {
        return false;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#hasUserData()
     */
    public boolean hasUserData() {
        return false;
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#getUserData()
     */
    public int getUserData() {
        return 0;
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord#isOperationalRecord()
     */
    public boolean isOperationalRecord() {
        return false;
    }
    
    /**
     * parse Header
     */
    abstract void parseHeader();

}
