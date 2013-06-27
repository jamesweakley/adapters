/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 17:08:55
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCInsertRecord;

@SuppressWarnings("deprecation")
final class CDCInsertRecordImpl 
extends CDCOperationRecordImpl 
implements CDCInsertRecord {

    /**
     * Constructor
     * 
     * @param header
     */
    CDCInsertRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isInsertRecord()
     */
    public boolean isInsertRecord() {
        return true;
    }

    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCOperationRecordImpl#toString()
     */
    @Override
    public String toString() {
        return "CDCInsertRecord: "+super.toString();
    }

}
