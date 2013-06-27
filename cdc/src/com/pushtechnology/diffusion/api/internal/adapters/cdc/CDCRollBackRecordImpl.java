/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:18:21
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCRollbackRecord;

@SuppressWarnings("deprecation")
final class CDCRollBackRecordImpl 
extends CDCRecordImpl 
implements CDCRollbackRecord {

    private long theSequenceNumber;
    private int theTransactionID;

    /**
     * Constructor
     * 
     * @param header
     */
    CDCRollBackRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isRollbackRecord()
     */
    public boolean isRollbackRecord() {
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
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRollbackRecord#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return theSequenceNumber;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRollbackRecord#getTransactionID()
     */
    public int getTransactionID() {
        return theTransactionID;
    }

   
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCRollbackRecord: SequenceNumber ["
            +theSequenceNumber
            +"] Transaction ID ["
            +theTransactionID
            +"]";
    }

}
