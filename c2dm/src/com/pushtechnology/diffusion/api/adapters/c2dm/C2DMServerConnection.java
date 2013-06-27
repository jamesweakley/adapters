package com.pushtechnology.diffusion.api.adapters.c2dm;

import java.util.Map;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.xmlproperties.pushnotification.C2DMDefinition;

/**
 * C2DMServerConnection
 * 
 * An interface that facilitates Google C2DM 'push-notification' messaging
 * 
 * @author martincowie - created Nov 30, 2011
 * @since 4.1
 */
public interface C2DMServerConnection {

    /**
     * Convenience method to send a C2DM message
     * <P>
     * Equivalent to {@link #send(String, Map, String)} with null collapseKey.
     * 
     * @param clientRegistrationID C2DM registration-key obtained from the
     * Android handset
     * 
     * @param params Dictionary of values to pass to the handset
     * 
     * @return The message identifier returned from the C2DM servers
     * 
     * @throws APIException if communications with C2DM do not adhere to
     * specifications
     * 
     * @throws C2DMException if the C2DM server relay that the message is
     * exceptional in some way
     * 
     * @throws C2DMUnavailableException if the C2DM servers are overloaded
     */
    public abstract String send(
    String clientRegistrationID,
    Map<String,String> params) 
    throws C2DMException,C2DMUnavailableException, APIException;

    /**
     * Send a C2DM message
     * <P>
     * Passes a dictionary of named values to the C2DM servers for relay to an
     * Android handset.
     * 
     * @param clientRegistrationID C2DM registration-key obtained from the
     * Android handset
     * 
     * @param params Dictionary of values to pass to the handset
     * 
     * @param collapseKey Collapse-key used by C2DM to conflate messages. If
     * null, the collapse-key in the configuration is used, or failing that the
     * symbolic name of the configuration
     * 
     * @return The message identifier returned from the C2DM servers
     * 
     * @throws APIException if communications with C2DM do not adhere to
     * specifications
     * 
     * @throws C2DMException if the C2DM server relay that the message is
     * exceptional in some way
     * 
     * @throws C2DMUnavailableException if the C2DM servers are overloaded
     */
    public abstract String send(
    String clientRegistrationID,
    Map<String,String> params,
    String collapseKey) 
    throws C2DMException,C2DMUnavailableException, APIException;

    /**
     * Check if connector authenticated.
     * <P>
     * @return true if this connector has correctly authenticated with the C2DM
     * servers
     */
    public abstract boolean isGoogleAuthenticated();

    /**
     * Get the {@link C2DMDefinition} configuration from within this object
     * 
     * @return the related {@link C2DMDefinition}
     */
    public C2DMDefinition getDefinition();
}