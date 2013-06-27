/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 12:11:07
 */

package com.pushtechnology.diffusion.api.adapters.cdc;

/**
 * Used to specify the Connection Details for a CDC Connection.
 * <P>
 * @author pwalsh
 *
 */
@Deprecated
public final class CDCConnectionDetails {

    private final String theConnectionString;

    private String theUsername = null;
    private String thePassword = null;
    private String theInformixServer = null;

    private boolean thisIsDebugging = false;

    /**
     * Constructor.
     * <P> 
     * The connection String should look like the following..
     * <pre>
     * //192.168.52.12:9088/syscdcv1
     * </pre>
     * 
     * 
     * @param connectionString
     */
    public CDCConnectionDetails(String connectionString) {
      
        if (!connectionString.endsWith(":")) {
            theConnectionString = connectionString+":";
        }
        else {
            theConnectionString = connectionString;
        }

    }

  
    /**
     * Sets the name of the Informix server.
     * <p>
     * 
     * @param informixServer the Informix Server name.
     */
    public void setInformixServer(String informixServer) {
        theInformixServer = informixServer;
    }
    
    /**
     * Return the name of the Informix Server.
     * <p>
     * 
     * @return the Informix server
     */
    public String getInformixServer() {
        return theInformixServer;
    }

    /**
     * Sets the username for the connection
     * <p>
     * 
     * @param username the user name.
     */
    public void setUsername(String username) {
        theUsername = username;
    }
  
    /**
     * Returns the user name.
     * <P>
     * @return the username
     */
    public String getUsername() {
        return theUsername;
    }


    /**
     * Sets the password for the connection
     * <p>
     * 
     * @param password
     */
    public void setPassword(String password) {
        thePassword = password;
    }

    /**
     * Indicates whether debugging is set on for the connection.
     * <p>
     * 
     * @return true if the connection is to have debugging enabled
     */
    public boolean isDebugging() {
        return thisIsDebugging;
    }

    /**
     * Sets debugging flag for the connection.
     * <p>
     * 
     * @param value true to debug
     */
    public void setDebugging(boolean value) {
        thisIsDebugging = value;
    }

    /**
     * Get the JDBC URL
     * 
     * @return the qualified JDBC URL String
     */
    String getJDBCUrl() {
        StringBuilder builder = new StringBuilder("jdbc:informix-sqli:");
        builder.append(theConnectionString);
        builder.append("informixserver=");
        builder.append(theInformixServer);
        builder.append(";user=");
        builder.append(theUsername);
        builder.append(";password=");
        builder.append(thePassword);

        return builder.toString();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getJDBCUrl()+" debug ["+thisIsDebugging+"]";
    }
}
