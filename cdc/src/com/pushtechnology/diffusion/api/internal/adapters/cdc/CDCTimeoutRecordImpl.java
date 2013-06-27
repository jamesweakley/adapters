/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 16:29:12
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCTimeoutRecord;

final class CDCTimeoutRecordImpl extends CDCRecordImpl 
implements CDCTimeoutRecord {

    private long theSequenceNumber;
    
    /**
     * Constructor 
     * @param header
     */
    CDCTimeoutRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isTimeoutRecord()
     */
    public boolean isTimeoutRecord() {
        return true;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#parseHeader()
     */
    @Override
    void parseHeader() {
        ByteBuffer buffer = getHeader();
        theSequenceNumber = buffer.getLong();
    }

 
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCTimeoutRecord#getSequenceNumber()
     */
    public long getSequenceNumber() {
        return theSequenceNumber;
    }
    
  
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCTimoutRecord: " +theSequenceNumber;
    }

}
