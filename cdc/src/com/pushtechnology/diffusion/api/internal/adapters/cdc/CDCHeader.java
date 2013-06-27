/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 09:34:11
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.nio.ByteBuffer;

import com.pushtechnology.diffusion.api.Utils;

public final class CDCHeader {

  
    /**
     * The number of bytes in the common and CDC record-specific headers.
     */
    private int theHeaderSize;
  
    /**
     * The number of bytes of data in the record after the common and CDC
     * record-specific headers.
     */
    private int thePayloadSize;
    
    /**
     * The packetization scheme number of one of the packetization schemes
     * contained in the syscdcpacketschemes table. The only packetization scheme
     *is 66, CDC_PKTSCHEME_LRECBINARY.
     */
    private int thePacketType;
    // 
    /**
     * The record number of one of the CDC records contained in the
     * syscdcrectypes table.
     */
    private int theLogRecordType;

    private ByteBuffer theHeader;
    private ByteBuffer thePayload;

    /**
     * Constructor.
     * @param data
     * @param length
     */
    CDCHeader(byte[] data,int length) {

        ByteBuffer buffer = ByteBuffer.allocate(length); // CDCConstants.CDC_HEADER_SIZE);
        buffer.put(data,0,length);
        buffer.flip();

        theHeaderSize = buffer.getInt()-CDCConstants.CDC_HEADER_SIZE;
        thePayloadSize = buffer.getInt();

        // TODO: This must be 66
        thePacketType = buffer.getInt();

        // Log record type
        theLogRecordType = buffer.getInt();

        byte[] header = new byte[theHeaderSize];
        byte[] payload = new byte[thePayloadSize];

        buffer.get(header);
        buffer.get(payload);

        theHeader = ByteBuffer.wrap(header);
        thePayload = ByteBuffer.wrap(payload);
    }

    /**
     * getHeaderSize
     * 
     * @return The number of bytes in the common and CDC record-specific
     * headers.
     */
    public int getHeaderSize() {
        return theHeaderSize;
    }

    /**
     * getPayloadSize
     * 
     * @return The number of bytes of data in the record after the common and
     * CDC record-specific headers.
     */
    public int getPayloadSize() {
        return thePayloadSize;
    }

    /**
     * getPacketType
     * 
     * @return The packetization scheme number of one of the packetization
     * schemes contained in the syscdcpacketschemes table. The only
     * packetization scheme is 66, CDC_PKTSCHEME_LRECBINARY.
     */
    public int getPacketType() {
        return thePacketType;
    }

    /**
     * theRecordNumber
     * 
     * @return The record number of one of the CDC records contained in the
     * syscdcrectypes table.
     */
    public int getLogRecordType() {
        return theLogRecordType;
    }

    /**
     * getPayload
     * 
     * @return
     */
    public ByteBuffer getPayload() {
        return thePayload;
    }

    /**
     * getHeader
     * 
     * @return
     */
    public ByteBuffer getHeader() {
        return theHeader;
    }

    @Override
    public String toString() {
        return "CDCRecord: Header ["
            +theHeaderSize
            +"] Payload["
            +thePayloadSize
            +"] Packet Type["
            +thePacketType
            +"] Log Type ["
            +theLogRecordType
            +"] header["
            +Utils.byteBufferToHex(theHeader)
            +"]: payload ["
            +Utils.byteBufferToHex(thePayload)
            +"]";
    }

}
