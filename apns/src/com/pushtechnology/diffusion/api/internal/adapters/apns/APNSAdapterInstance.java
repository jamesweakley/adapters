package com.pushtechnology.diffusion.api.internal.adapters.apns;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.xmlproperties.XmlPushNotification;
import com.pushtechnology.diffusion.xmlproperties.pushnotification.APNSDefinition;

/**
 * Factory class, allowing access to APNSServerConnection and
 * APNSFeedbackConneciton objects.
 * 
 * @author pwalsh
 * @since 4.1
 */
@Deprecated
public final class APNSAdapterInstance {

    /**
     * A collection of connections, indexed by name
     */
    protected Map<String,APNSServerConnectionImpl> theConnectionsByName = new HashMap<String,APNSServerConnectionImpl>();

    /**
     * A collection of feedback-connections, indexed by name
     */
    protected Map<String,APNSFeedbackConnectionImpl> theFeedbackConnectionsByName = new HashMap<String,APNSFeedbackConnectionImpl>();

    /**
     * Get or create & cache an APNSServerConnectionImpl
     * 
     * @param connectionName Symbolic name used to search in the configuration
     * @throws APIException if the object cannot be found or created
     */
    public APNSServerConnectionImpl getNamedConnection(String connectionName) throws APIException {
        if (!theConnectionsByName.containsKey(connectionName)) {
            APNSDefinition definition = findDefinition(connectionName);
            if (definition==null)
                throw new APIException(String.format( "Unknown APNS definition with name \"%s\"",connectionName));
            APNSServerConnectionImpl result = new APNSServerConnectionImpl(definition);
            theConnectionsByName.put(connectionName,result);
            return result;
        }
        APNSServerConnectionImpl result = theConnectionsByName.get(connectionName);
        result.connect();
        return result;
    }

    /**
     * Get or create & cache an APNSFeedbackConnectionImpl
     * 
     * @param apnsConnectionName Symbolic name used to search in the
     * configuration
     * @throws APIException if the object cannot be found or created
     */
    public APNSFeedbackConnectionImpl getFeedbackConnection(String apnsConnectionName) throws APIException {
        if (!theFeedbackConnectionsByName.containsKey(apnsConnectionName)) {
            try {
                APNSDefinition definition = findDefinition(apnsConnectionName);
                if (definition==null)
                    throw new APIException(String.format( "Unknown APNS definition with name \"%s\"", apnsConnectionName));

                APNSFeedbackConnectionImpl result = new APNSFeedbackConnectionImpl(definition);
                theFeedbackConnectionsByName.put(apnsConnectionName,result);
                return result;

            }
            catch (FileNotFoundException ex) {
                throw new APIException(String.format( "Cannot find file for APNS definition with name \"%s\"", apnsConnectionName),ex);
            }
        }
        APNSFeedbackConnectionImpl result = theFeedbackConnectionsByName.get(apnsConnectionName);
        result.connect();
        return result;
    }

    /**
     * Find an APNSDefinition from within the configuration
     * 
     * @return an APNSDefinition with the matching name, or null, if it's not
     * found.
     */
    private APNSDefinition findDefinition(String defName) {
        List<APNSDefinition> apnsDefinitions = XmlPushNotification.getPushNotification().getAPNS().getAPNSDefinitions();

        for (APNSDefinition definition:apnsDefinitions)
            if (definition.getName().equals(defName))
                return definition;
        return null;
    }

}
