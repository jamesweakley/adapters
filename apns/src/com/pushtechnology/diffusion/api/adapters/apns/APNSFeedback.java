package com.pushtechnology.diffusion.api.adapters.apns;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.pushtechnology.diffusion.api.internal.adapters.apns.APNSCommon;
import com.pushtechnology.diffusion.api.json.Dictionary;

/**
 * Feedback from APNS that an app has been uninstalled from a device, and
 * push-notifications should not be sent to it hereafter.
 * 
 * @author martincowie
 * @since 4.1
 */
@Deprecated
public final class APNSFeedback {
    private Date theTimestamp;
    private byte[] theDeviceToken;

    /**
     * Construct an APNSFeedback with the given properties
     * 
     * @param timestamp the timestamp
     * @param deviceToken the device token
     */
    private APNSFeedback(Date timestamp,byte[] deviceToken) {
        theTimestamp = timestamp;
        theDeviceToken = deviceToken;
    }

    /**
     * Read and construct an APNSFeedback object
     * 
     * @param inputStream InputStream from which to read
     * @return a newly constructed APNSFeedback object
     * @throws IOException if the InputStream encounters any problems or if the
     * APNSFeedback message is invalid
     */
    public static APNSFeedback read(InputStream inputStream) throws IOException {
        byte apnsToken[] = new byte[APNSCommon.TOKEN_SIZE];
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int seconds = dataInputStream.readInt();
        int tokenLength = dataInputStream.readShort();
        if (tokenLength!=apnsToken.length)
            throw new IOException(String.format("Unexpected token length %d, not %d",tokenLength,apnsToken.length));
        dataInputStream.readFully(apnsToken);

        APNSFeedback result = new APNSFeedback(new Date(1000L*seconds),apnsToken);
        return result;
    }

    /**
     * Return the date stamp of this feedback message.
     * <P>
     * @return The date-stamp
     */
    public Date getTimestamp() {
        return theTimestamp;
    }

    /**
     * Return the device token.
     * <P>
     * @return The now unavailable APNS device token.
     */
    public byte[] getDeviceToken() {
        return theDeviceToken;
    }

    /**
     * Serialise this object
     * 
     * @return A JSON dictionary string
     */
    public String toString() {
        return new Dictionary(
            "timestamp",theTimestamp.toString(),
            "deviceToken",APNSCommon.toString(theDeviceToken)).toString();
    }

}
