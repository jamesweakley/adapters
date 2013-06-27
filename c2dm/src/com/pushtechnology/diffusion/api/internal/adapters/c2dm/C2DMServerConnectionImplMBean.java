package com.pushtechnology.diffusion.api.internal.adapters.c2dm;

import java.util.Date;

/**
 * 
 * C2DMServerConnectionImplMBean
 * <P>
 * JMX MBean interface, exposing parts of {@link C2DMServerConnectionImpl} for
 * monitoring
 * 
 * @author martincowie - created Nov 30, 2011
 * @since 4.1
 */
public interface C2DMServerConnectionImplMBean {
  
    /**
     * @return The number of messages sent via this object
     */
    public int getMessagesSent();

    /**
     * @return java.util.Date of the last message sent
     */
    public Date getLastMessageSent();

    /**
     * @return The symbolic name used to identify this connection in the
     * configuration files.
     */
    public String getDefinitionName();

    /**
     * @return The email address used to authenticate with the Google C2DM
     * servers
     */
    public String getDefinitionEmailAddress();

    /**
     * @return The conflation/collapse key used by this connection
     */
    public String getCollapseKey();

    /**
     * @return The timestamp of the last authentication exchange
     */
    public Date getWhenGoogleAuthenticated();

    /**
     * @return true if this connector has correctly authenticated with the C2DM
     * servers
     */
    public boolean isGoogleAuthenticated();

    /**
     * Explicitly disconnect from the C2DM servers
     */
    public void disconnect();
}
