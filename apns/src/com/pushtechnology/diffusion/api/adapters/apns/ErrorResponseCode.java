package com.pushtechnology.diffusion.api.adapters.apns;

/**
 * Enumeration of error codes returned from APNS.
 * 
 * @author martincowie
 * @since 4.1
 */
public enum ErrorResponseCode {

    NO_ERROR(0, "No errors encountered"),
    PROCESSING_ERROR(1, "Processing error"),
    MISSING_DEVICE_TOKEN(2, "Missing device token "),
    MISSING_TOPIC(3, "Missing topic"),
    MISSING_PAYLOAD(4, "Missing payload"),
    INVALID_TOKEN_SIZE(5, "Invalid token size"),
    INVALID_TOPIC_SIZE(6, "Invalid topic size"),
    INVALID_PAYLOAD_SIZE(7, "Invalid payload size"),
    INVALID_TOKEN(8, "Invalid token"),
    UNKNOWN(255, "None (unknown)");

    private int theCode;
    private String theDescription;

    /**
     * Construct an ErrorResponseCode with the given properties
     * 
     * @param code
     * @param description
     */
    private ErrorResponseCode(int code,String description) {
        theCode = code;
        theDescription = description;
    }

    /**
     * Serialise this enum as a String
     * 
     * @since 4.1
     */
    public String toString() {
        return String.format("%s(%d)",theDescription,theCode);
    }

    /**
     * Parse from code
     * 
     * @param code to search with
     * @return the matching enum constant
     * @since 4.1
     */
    public static ErrorResponseCode fromCode(byte code) {
        for (ErrorResponseCode value:ErrorResponseCode.class.getEnumConstants())
            if (value.theCode==code)
                return value;

        return null;
    }
}
