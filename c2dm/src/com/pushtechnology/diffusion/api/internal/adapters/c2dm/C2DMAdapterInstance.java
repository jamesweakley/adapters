package com.pushtechnology.diffusion.api.internal.adapters.c2dm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.adapters.c2dm.C2DMAdapter;
import com.pushtechnology.diffusion.xmlproperties.XmlPushNotification;
import com.pushtechnology.diffusion.xmlproperties.pushnotification.C2DMDefinition;

/**
 * Singleton instance of {@link C2DMAdapter}
 * 
 * Connections are pooled in here by their symbolic name
 * 
 * @author martincowie - created Nov 30, 2011
 * @since 4.1
 */
@SuppressWarnings("deprecation")
public final class C2DMAdapterInstance {
    
    /**
     * Collection of connection objects, indexed by name
     */
    private Map<String,C2DMServerConnectionImpl> theConnectionsByName =
        new HashMap<String,C2DMServerConnectionImpl>();

    /**
     * Find and decache, or create and en-cache the
     * {@link C2DMServerConnectionImpl} for the given name
     * 
     * @param connectionName Symbolic name of the configuration to search for
     * @return a {@link C2DMServerConnectionImpl} ready to use
     * @throws APIException If the connection-name cannot be found
     * @since 4.1
     */
    public C2DMServerConnectionImpl getNamedConnection(String connectionName)
    throws APIException {
        if (!theConnectionsByName.containsKey(connectionName)) {
            C2DMDefinition definition = findDefinition(connectionName);
            if (definition==null) {
                throw new APIException(
                    String.format(
                        "Unknown C2DM definition with name \"%s\"",
                        connectionName));
            }
            C2DMServerConnectionImpl result =
                new C2DMServerConnectionImpl(definition);
            theConnectionsByName.put(connectionName,result);
            return result;
        }
        return theConnectionsByName.get(connectionName);
    }

    /**
     * Find a C2DMDefinition with the given name, or null, if it's not found.
     * 
     * @param defName Symbolic name of the configuration to search for
     * @return {@link C2DMDefinition} if found, or null if not.
     */
    private C2DMDefinition findDefinition(String defName) {
        List<C2DMDefinition> c2dmDefinitions =
            XmlPushNotification.getPushNotification().getC2DM()
                .getC2DMDefinitions();

        for (C2DMDefinition definition:c2dmDefinitions)
            if (definition.getName().equals(defName)) {
                return definition;
            }
        return null;
    }

}
