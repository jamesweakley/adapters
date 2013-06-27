/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:58:52
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;
import java.util.Date;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCCommitRecord;

final class CDCCommitRecordImpl 
extends CDCRecordImpl
implements CDCCommitRecord {

    private long theSequenceNumber;
    private int theTransactionID;
    private long theCommitTime;
    private Date theCommitDate = null;

    /**
     * Constructor 
     * @param header
     */
    CDCCommitRecordImpl(CDCHeader header) {
        super(header);
    }
    
   
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isCommitRecord()
     */
    public boolean isCommitRecord() {
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
        theCommitTime = buffer.getLong();
    }
    
  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCCommitRecord#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return theSequenceNumber;
    }

 
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCCommitRecord#getTransactionID()
     */
    public int getTransactionID() {
        return theTransactionID;
    }

 
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCCommitRecord#getCommitTime()
     */
    public long getCommitTime() {
        return theCommitTime;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCCommitRecord#getCommitDate()
     */
    public Date getCommitDate() {
        if (theCommitDate==null) {
            theCommitDate = new Date(theCommitTime);
        }

        return theCommitDate;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCCommitRecord: Sequence Number ["
            +theSequenceNumber
            +"] Transaction ID ["
            +theTransactionID
            +"] Time ["
            +theCommitTime
            +"]";
    }

}
