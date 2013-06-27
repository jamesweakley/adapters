/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 09:10:18
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCUpdateBeforeRecord;

@SuppressWarnings("deprecation")
final class CDCUpdateBeforeRecordImpl 
extends CDCOperationRecordImpl 
implements CDCUpdateBeforeRecord {

    /**
     * Constructor 
     * @param header
     */
    CDCUpdateBeforeRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isUpdateBeforeRecord()
     */
    public boolean isUpdateBeforeRecord() {
        return true;
    }

 
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCOperationRecordImpl#toString()
     */
    @Override
    public String toString() {
        return "CDCUpdateBeforeRecord: "+super.toString();
    }
}
