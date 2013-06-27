package com.pushtechnology.diffusion.api.adapters.apns;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.internal.adapters.apns.APNSAdapterInstance;

/**
 * Factory class, allowing access to {@link APNSServerConnection} and
 * {@link APNSFeedbackConnection} objects.
 * 
 * @author martincowie
 * @since 4.1
 */
public final class APNSAdapter {
    /**
     * Class singleton
     */
    private static APNSAdapterInstance theInstance = new APNSAdapterInstance();

    /**
     * Fetch the APNS server connection with the given name
     * <P>
     * 
     * @param connectionName Symbolic name matching one in PushNotification.xml
     * push-notification/APNS/APNS-definition/@name
     * 
     * @return An APNSServerConnection ready to use.
     * 
     * @throws APIException APNSServerConnection object are cached, so need not
     * be closed or returned to the pool.
     * @since 4.1
     */
    public static APNSServerConnection getNamedConnection(String connectionName) throws APIException {
        return theInstance.getNamedConnection(connectionName);
    }

    /**
     * Fetch the APNS feedback connection with the given name
     * <P>
     * 
     * @param connectionName Symbolic name matching one in PushNotification.xml
     * push-notification/APNS/APNS-definition/@name
     * 
     * @return An APNSFeedbackConnection ready to use 
     * 
     * @throws APIException APNSFeedbackConnection is used to get APNS feedback
     * of applications that have been uninstalled from the iOS device
     * @since 4.1
     */
    public static APNSFeedbackConnection getFeedbackConnection( String connectionName) throws APIException {
        return theInstance.getFeedbackConnection(connectionName);
    }

}
