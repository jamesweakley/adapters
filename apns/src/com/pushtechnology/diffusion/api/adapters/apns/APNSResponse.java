package com.pushtechnology.diffusion.api.adapters.apns;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pushtechnology.diffusion.api.internal.adapters.apns.JSONDictionary;

/**
 * A POJO holding an error response from an the APNS servers.
 * 
 * @author martincowie
 * @since 4.1
 */
public final class APNSResponse {
    
    private int theMessageIdentifier;
    private ErrorResponseCode theStatus;
    private static final byte COMMAND = 8;

    /**
     * Construct an APNSResponse
     * 
     * @param messageIdentifier Message identifier, returned when sending the
     * original message
     * @param code error-code supplied by APNS
     * @see APNSServerConnection#send(byte[],String)
     */
    private APNSResponse(int messageIdentifier,byte code) {
        theMessageIdentifier = messageIdentifier;
        theStatus = ErrorResponseCode.fromCode(code);
    }

    /**
     * Returns the message identifier.
     * <P>
     * @return Message identifier, returned when sending the original message
     * 
     * @see APNSServerConnection#send(byte[],String)
     */
    public int getMessageIdentifier() {
        return theMessageIdentifier;
    }

    /**
     * Returns the error code from APNS
     * <P>
     * @return Error code from APNS
     */
    public ErrorResponseCode getStatus() {
        return theStatus;
    }

    /**
     * Read an APNSResponse from the given InputStream
     *
     * @param inputStream
     * @return response
     * @throws IOException
     * @since n.n
     */
    public static APNSResponse read(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte command = dataInputStream.readByte();
        if (command!=COMMAND) {
            throw new IOException(
                String.format("Unexpected command-header: %d",command));
        }
        byte status = dataInputStream.readByte();
        int messageIdentifier = dataInputStream.readInt();

        return new APNSResponse(messageIdentifier,status);
    }

    /**
     * Serialise this APNSResponse as a JSON dictionary string
     * 
     * @return string representing a APNSResponse
     * @since 4.1
     */
    public String toString() {
        return new JSONDictionary(
            "status",theStatus,
            "messageIdentifier",theMessageIdentifier).toString();
    }

}