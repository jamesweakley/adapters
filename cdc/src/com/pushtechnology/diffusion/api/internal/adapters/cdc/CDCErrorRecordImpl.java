/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:24:52
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCErrorRecord;

final class CDCErrorRecordImpl 
extends CDCRecordImpl 
implements CDCErrorRecord {

    private int theFlag;
    private int theErrorCode;

    /**
     * Constructor
     * 
     * @param header
     */
    public CDCErrorRecordImpl(CDCHeader header) {
        super(header);
    }
    
   
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isErrorRecord()
     */
    public boolean isErrorRecord() {
        return true;
    }

 
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#parseHeader()
     */
    @Override
    void parseHeader() {
        ByteBuffer buffer = getHeader();
        theFlag = buffer.getInt();
        theErrorCode = buffer.getInt();
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCErrorRecord#isSessionValid()
     */
    public boolean isSessionValid() {
        return (theFlag!=1);
    }


   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCErrorRecord#getFlag()
     */
    public int getFlag() {
        return theFlag;
    }
    
  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCErrorRecord#getErrorCode()
     */
    public int getErrorCode() {
        return theErrorCode;
    }
    
 
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCErrorRecord: Flag ["
            +theFlag
            +"] Error Code ["
            +theErrorCode
            +"]";
    }

}
