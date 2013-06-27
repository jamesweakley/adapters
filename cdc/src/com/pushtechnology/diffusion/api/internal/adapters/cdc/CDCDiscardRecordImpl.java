/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:45:48
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCDiscardRecord;

@SuppressWarnings("deprecation")
final class CDCDiscardRecordImpl 
extends CDCRecordImpl 
implements CDCDiscardRecord  {

    private long theSequenceNumber;
    private int theTransactionID;
    
    /**
     * Constructor 
     * @param header
     */
    CDCDiscardRecordImpl(CDCHeader header) {
        super(header);
    }
    
   
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isDiscardRecord()
     */
    public boolean isDiscardRecord() {
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
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCDiscardRecord#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return theSequenceNumber;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCDiscardRecord#getTransactionID()
     */
    public int getTransactionID() {
        return theTransactionID;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCDiscardRecord: SequenceNumber ["
        +theSequenceNumber
        +"] Transaction ID ["
        +theTransactionID
        +"]";
    }

}
