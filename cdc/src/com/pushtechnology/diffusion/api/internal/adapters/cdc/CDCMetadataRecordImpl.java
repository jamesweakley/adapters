/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 15:23:48
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;
import java.util.Vector;

import com.pushtechnology.diffusion.api.adapters.cdc.CDCMetadataRecord;

final class CDCMetadataRecordImpl
extends CDCRecordImpl 
implements CDCMetadataRecord {

    private int theUserData;
    private int theFlags;
    private int theFixedLengthSize;
    private int theFixedLengthCols;
    private int theVarLengthCols;
    private Vector<String> theColNames;

    /**
     * Constructor
     * 
     * @param header
     */
    public CDCMetadataRecordImpl(CDCHeader header) {
        super(header);
        parsePayload();
    }
    
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#isMetadataRecord()
     */
    public boolean isMetadataRecord() {
        return true;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#parseHeader()
     */
    void parseHeader() {
        ByteBuffer buffer = getHeader();

        theUserData = buffer.getInt();

        // Must be 0
        theFlags = buffer.getInt();

        theFixedLengthSize = buffer.getInt();

        theFixedLengthCols = buffer.getInt();

        theVarLengthCols = buffer.getInt();
    }

   
    /**
     * Parse the payload
     */
    private void parsePayload() {
        ByteBuffer payload = getPayload();

        byte[] tmp = new byte[payload.capacity()];
        payload.get(tmp);

        String[] names = new String(tmp).split(",");

        theColNames = new Vector<String>();

        for (String name:names) {
            theColNames.add(name);
        }
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#getUserData()
     */
    public int getUserData() {
        return theUserData;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCMetadataRecord#getFlags()
     */
    public int getFlags() {
        return theFlags;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCMetadataRecord#getFixedLengthSize()
     */
    public int getFixedLengthSize() {
        return theFixedLengthSize;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCMetadataRecord#getFixedLengthCols()
     */
    public int getFixedLengthCols() {
        return theFixedLengthCols;
    }

   
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCMetadataRecord#getVarLengthCols()
     */
    public int getVarLengthCols() {
        return theVarLengthCols;
    }

  
    /**
     * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCMetadataRecord#getColNames()
     */
    public Vector<String> getColNames() {
        return theColNames;
    }

 
    /**
     * @see com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCRecordImpl#hasUserData()
     */
    @Override
    public boolean hasUserData() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CDCMetadataRecord : "+theColNames;
    }
}
