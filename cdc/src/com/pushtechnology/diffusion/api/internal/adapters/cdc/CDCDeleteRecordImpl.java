/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 17:14:59
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCDeleteRecord;

final class CDCDeleteRecordImpl 
extends CDCOperationRecordImpl 
implements CDCDeleteRecord {

    /**
     * Constructor 
     * @param header
     */
    CDCDeleteRecordImpl(CDCHeader header) {
        super(header);
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isDeleteRecord()
     */
    public boolean isDeleteRecord() {
        return true;
    }

    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCOperationRecordImpl#toString()
     */
    @Override
    public String toString() {
        return "CDCDeleteRecord: " + super.toString();
    }

}
