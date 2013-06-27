package com.pushtechnology.diffusion.api.adapters.apns;

import java.util.Date;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.xmlproperties.pushnotification.APNSDefinition;

/**
 * A interface representing interaction with the APNS servers for iOS push
 * notification.
 * 
 * @author martincowie
 * @since 4.1
 */
@Deprecated
public interface APNSServerConnection {

    /**
     * Send an 'advanced' APNS push notification
     * <P>
     * @param apnsToken Device token identifying an app installed on a device
     * 
     * @param payload JSON formatted data describing the message
     * 
     * @param expiry Date after which the APNS servers will drop the message for
     * delivery
     * 
     * @return the ID of the message that is sent.
     * @throws APIException
     */
    int send(byte[] apnsToken,String payload,Date expiry) throws APIException;

    /**
     * Send an 'advanced' APNS push notification with no effective expiry date
     * 
     * @param apnsToken Device token identifying an app installed on a device
     * @param payload JSON formatted data describing th emessage
     * @return the ID of the message that is sent.
     * @throws APIException
     */
    int send(byte[] apnsToken,String payload) throws APIException;

    /**
     * Get the definition.
     * 
     * @return the configuration associated with this object
     * @since 4.1
     */
    APNSDefinition getDefinition();

    /**
     * Add an {@link APNSResponseListener} object to the <em>set</em> of handler
     * objects. 
     * <P>
     * All handlers are notified of any APNSResponse objects received
     * from the APNS servers.
     * 
     * @param handler Handler object to add to the set.
     * @return true if the handler was not yet present in the underlying set
     * @since 4.1
     */
    boolean addListener(APNSResponseListener handler);

    /**
     * Remove the given {@link APNSResponseListener} object from the set of 
     * handlers
     * <P>
     * @param handler the handler to remove
     * @return true if the handler was present in the underlying set
     * @since 4.1
     */
    boolean removeListener(APNSResponseListener handler);

    /**
     * Test the state to see if connected
     * 
     * @return True if there is currently a connection in place
     * @since 4.1
     */
    public boolean isConnected();

}
