/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 08:56:40
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCOperationRecord;

abstract class CDCOperationRecordImpl 
extends CDCRecordImpl 
implements CDCOperationRecord {

    private long theSequenceNumber;
    private int theTransactionID;
    private int theUserData;
    private int theFlags;

    /**
     * Constructor
     * 
     * @param header
     */
    CDCOperationRecordImpl(CDCHeader header) {
        super(header);
    }

    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#parseHeader()
     */
    @Override
    final void parseHeader() {
        ByteBuffer buffer = getHeader();
        theSequenceNumber = buffer.getLong();
        theTransactionID = buffer.getInt();
        theUserData = buffer.getInt();
        theFlags = buffer.getInt();
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCOperationRecord#getSequenceNumber()
     */
    public final long getSequenceNumber() {
        return theSequenceNumber;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCOperationRecord#getTransactionID()
     */
    public final int getTransactionID() {
        return theTransactionID;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#getUserData()
     */
    public final int getUserData() {
        return theUserData;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCOperationRecord#getFlags()
     */
    public final int getFlags() {
        return theFlags;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#hasUserData()
     */
    @Override
    public final boolean hasUserData() {
        return true;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isOperationalRecord()
     */
    public final boolean isOperationalRecord() {
        return true;
    }

   
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " Sequence Number ["
            +theSequenceNumber
            +"] Transaction ID ["
            +theTransactionID
            +"] User Data ["
            +theUserData
            +"]";
    }

}
