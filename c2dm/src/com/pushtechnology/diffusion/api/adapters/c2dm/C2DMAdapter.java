package com.pushtechnology.diffusion.api.adapters.c2dm;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.internal.adapters.c2dm.C2DMAdapterInstance;

/**
 * Factory class, providing access to {@link C2DMServerConnection} objects by
 * their symbolic name. Objects are pooled within this adapter
 * 
 * @author martincowie - created Nov 30, 2011
 * @since 4.1
 */
public final class C2DMAdapter {
    
    /**
     * Singleton instance
     */
    private static C2DMAdapterInstance theInstance = new C2DMAdapterInstance();
 
    /**
     * Constructor (prevents instantiation)
     */
    private C2DMAdapter() {
    }

    /**
     * Fetch the C2DM server connection with the given name
     * 
     * @param connectionName Symbolic name matching one in PushNotification.xml
     * C2DM/C2DM-definition/@name
     * 
     * @return a {@link C2DMServerConnection} for the given configuration name
     * 
     * @throws APIException if no connection found with the given name.
     */
    public static C2DMServerConnection getNamedConnection(String connectionName)
    throws APIException {
        return theInstance.getNamedConnection(connectionName);
    }

}
