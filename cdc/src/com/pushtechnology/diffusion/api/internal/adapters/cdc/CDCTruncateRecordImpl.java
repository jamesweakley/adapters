/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:12:38
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCTruncateRecord;

@SuppressWarnings("deprecation")
final class CDCTruncateRecordImpl 
extends CDCRecordImpl
implements CDCTruncateRecord {

    private long theSequenceNumber;
    private int theTransactionID;
    private int theUserData;

    /**
     * Constructor
     * 
     * @param header
     */
    CDCTruncateRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isTruncateRecord()
     */
    public boolean isTruncateRecord() {
        return false;
    }

    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#parseHeader()
     */
    @Override
    void parseHeader() {
        ByteBuffer buffer = getHeader();
        theSequenceNumber = buffer.getLong();
        theTransactionID = buffer.getInt();
        theUserData = buffer.getInt();
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCTruncateRecord#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return theSequenceNumber;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCTruncateRecord#getTransactionID()
     */
    public int getTransactionID() {
        return theTransactionID;
    }

  
  
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#getUserData()
     */
    public int getUserData() {
        return theUserData;
    }

    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#hasUserData()
     */
    @Override
    public boolean hasUserData() {
        return true;
    }

   
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCTruncateRecord: Sequence Number ["
            +theSequenceNumber
            +"] Transaction ID ["
            +theTransactionID
            +"] User Data ["
            +theUserData
            +"]";
    }

}
