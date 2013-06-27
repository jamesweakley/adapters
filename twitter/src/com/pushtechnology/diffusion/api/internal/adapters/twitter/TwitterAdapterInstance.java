package com.pushtechnology.diffusion.api.internal.adapters.twitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.adapters.twitter.TweetMessageLevel;
import com.pushtechnology.diffusion.xmlproperties.XmlTwitter;
import com.pushtechnology.diffusion.xmlproperties.twitter.TwitterDefinition;

/**
 * Instance of Twitter adapter.
 * 
 * @author Antonio Di Ferdinando - created 20 Dec 2011
 */
@SuppressWarnings("deprecation")
public final class TwitterAdapterInstance {

    /**
     * A collection of connections, indexed by name
     */
    protected Map<String,TwitterServerConnectionImpl> theConnectionsByName =
        new HashMap<String,TwitterServerConnectionImpl>();

    /**
     * Get or create & cache a TwitterServerConnectionImpl
     * 
     * @param connectionName Symbolic name used to search in the configuration
     * @throws APIException if the object cannot be found or created
     */
    public TwitterServerConnectionImpl getNamedConnection(
    String connectionName,TweetMessageLevel messageLevel) throws APIException {
        if (!theConnectionsByName.containsKey(connectionName)) {
            TwitterDefinition definition = findConnection(connectionName);
            if (definition==null)
                throw new APIException(String.format(
                    "Unknown definition with name \"%s\"",connectionName));
            TwitterServerConnectionImpl result =
                new TwitterServerConnectionImpl(definition,messageLevel);
            theConnectionsByName.put(connectionName,result);
            // result.connect();
            return result;
        }
        TwitterServerConnectionImpl result =
            theConnectionsByName.get(connectionName);
        // result.connect();
        return result;
    }

    /**
     * Find an TwitterDefinition from within the configuration
     * 
     * @return an TwitterConnection with the matching name, or null, if it's not
     * found.
     */
    private TwitterDefinition findConnection(String defName) {
        // List<TwitterConnection> t =
        // XmlPushNotification.getPushNotification().getAPNS().getAPNSDefinitions();
        List<TwitterDefinition> twitterConnections =
            XmlTwitter.getTwitter().getTwitterDefinitions();

        for (TwitterDefinition definition:twitterConnections)
            if (definition.getName().equals(defName))
                return definition;
        return null;
    }
}
