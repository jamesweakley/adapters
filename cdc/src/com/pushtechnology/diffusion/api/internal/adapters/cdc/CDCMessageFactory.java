/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 16:16:42
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord;

public final class CDCMessageFactory {

    /**
     * createCDCRecord
     * 
     * @param data
     * @param length
     * @return
     */
    public static CDCRecord createCDCRecord(byte[] data,int length) {
        CDCHeader header = new CDCHeader(data, length);
        
        switch(header.getLogRecordType()) {
        case CDCConstants.CDC_REC_TABSCHEMA:
            return new CDCMetadataRecordImpl(header);
            
        case CDCConstants.CDC_REC_UPDBEF:
            return new CDCUpdateBeforeRecordImpl(header);
            
        case CDCConstants.CDC_REC_UPDAFT:
            return new CDCUpdateAfterRecordImpl(header);
            
        case CDCConstants.CDC_REC_TIMEOUT:
            return new CDCTimeoutRecordImpl(header);
            
        case CDCConstants.CDC_REC_INSERT:
            return new CDCInsertRecordImpl(header);
            
        case CDCConstants.CDC_REC_DELETE:
            return new CDCDeleteRecordImpl(header);
            
        case CDCConstants.CDC_REC_BEGINTX:
            return new CDCBeginTransactionRecordImpl(header);
            
        case CDCConstants.CDC_REC_COMMTX:
            return new CDCCommitRecordImpl(header);
            
        case CDCConstants.CDC_REC_DISCARD:
            return new CDCDiscardRecordImpl(header);
            
        case CDCConstants.CDC_REC_ERROR:
            return new CDCErrorRecordImpl(header);
            
        case CDCConstants.CDC_REC_RBTX:
            return new CDCRollBackRecordImpl(header);
            
        case CDCConstants.CDC_REC_TRUNCATE:
            return new CDCTruncateRecordImpl(header);
            
        }
        
        return null;
    }
}
