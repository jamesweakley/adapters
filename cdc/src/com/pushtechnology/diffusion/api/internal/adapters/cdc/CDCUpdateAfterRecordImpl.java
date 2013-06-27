/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 14:45:11
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCUpdateAfterRecord;

final class CDCUpdateAfterRecordImpl 
extends CDCOperationRecordImpl 
implements CDCUpdateAfterRecord{

    /**
     * Constructor
     * 
     * @param header
     */
    CDCUpdateAfterRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isUpdateAfterRecord()
     */
    public boolean isUpdateAfterRecord() {
        return true;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCOperationRecordImpl#toString()
     */
    @Override
    public String toString() {
        return "CDCUpdateAfterRecord: "+super.toString();
    }

}
