/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 10:17:05
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;
import java.util.Date;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCBeginTransactionRecord;

final class CDCBeginTransactionRecordImpl 
extends CDCRecordImpl 
implements CDCBeginTransactionRecord {

    private long theSequenceNumber;
    private int theTransactionID;
    private long theStartTime;
    private Date theStartDate = null;
    private int theUserID;

    /**
     * Constructor
     * 
     * @param header
     */
    CDCBeginTransactionRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isBeginTransactionRecord()
     */
    public boolean isBeginTransactionRecord() {
        return true;
    }

    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#parseHeader()
     */
    @Override
    void parseHeader() {
        ByteBuffer buffer = getHeader();
        theSequenceNumber = buffer.getLong();
        theTransactionID = buffer.getInt();
        theStartTime = buffer.getLong();
        theUserID = buffer.getInt();
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCBeginTransactionRecord#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return theSequenceNumber;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCBeginTransactionRecord#getTransactionID()
     */
    public int getTransactionID() {
        return theTransactionID;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCBeginTransactionRecord#getStartTime()
     */
    public long getStartTime() {
        return theStartTime;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCBeginTransactionRecord#getStartDate()
     */
    public Date getStartDate() {
        if (theStartDate==null) {
            theStartDate = new Date(theStartTime);
        }

        return theStartDate;
    }
    
  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCBeginTransactionRecord#getUserID()
     */
    public int getUserID() {
        return theUserID;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCBeginTransactionRecord: Sequence Number ["
            +theSequenceNumber
            +"] Transaction ID ["
            +theTransactionID
            +"] Start Time ["
            +theStartTime
            +"] User ID ["
            +theUserID
            +"]";
    }

}
