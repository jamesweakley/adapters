package com.pushtechnology.diffusion.api.adapters.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.xmlproperties.XmlMailProperties;
import com.pushtechnology.diffusion.xmlproperties.mail.MailServer;

/**
 * Encapsulates the details of an SMTP server.
 * <P>
 * These details can be used to specify the details of an SMTP server when
 * creating an {@link SMTPMailSession}.
 */
public final class SMTPServerDetails {

    private MailServer theMailServer;

    /**
     * Constructor for empty programmatic mail server
     */
    public SMTPServerDetails() {
        theMailServer = new MailServer();
    }

    /**
     * Constructor for named server in the Mail.xml file.
     * <P>
     * 
     * @param server the name of a server with details declared in the Mail.xml
     * file.
     * 
     * @throws APIException if the named server is not declared or the details
     * declared for it are invalid.
     */
    public SMTPServerDetails(String server) throws APIException {
        setFromProperties(server);
    }

    /**
     * Constructor.
     * 
     * @param host the SMTP server host name or IP address.
     * 
     * @param port the SMTP server port. May be supplied as -1 to assume the
     * default SMTP server port.
     * 
     * @param username the SMTP user name - may be null if no authentication
     * required.
     * 
     * @param password the SMTP password - ignored if username is null
     */
    public SMTPServerDetails(
    String host,
    int port,
    String username,
    String password) {
        this(host,port,username,password,false,(String)null);
    }

    /**
     * Constructor for secure connections.
     * <P>
     * 
     * @param host the SMTP server host name or IP address.
     * 
     * @param port the SMTP server port.
     * 
     * @param username the SMTP user name - may be null if no authentication
     * required.
     * 
     * @param password the SMTP password - ignored if username is null
     * 
     * @param tls If true, enables the use of the STARTTLS command (if supported
     * by the server) to switch the connection to a TLS-protected connection
     * before issuing any login commands.
     * 
     * @param sslProtocols Specifies the SSL protocols that will be enabled for
     * SSL connections. If null is specified then no SSL protocols will be
     * enabled. Valid values are TLSv1, SSLv2, SSLv3, SSLv2Hello. A space
     * delimited list is required.
     * 
     */
    public SMTPServerDetails(
    String host,
    int port,
    String username,
    String password,
    boolean tls,
    String sslProtocols) {
        theMailServer = new MailServer();
        setHost(host);
        setPort(port);
        setUserNameAndPassword(username,password);
        setTLSEnabled(tls);
        setSSLProtocols(sslProtocols);
    }

    /**
     * Sets up the details from a named server in the properties file
     * 
     * @param serverName may be null for default
     * @throws APIException if named server not defined.
     */
    private void setFromProperties(String serverName) throws APIException {

        MailServer server = XmlMailProperties.getServerByName(serverName);
        if (server==null) {
            throw new APIException("Unable to find ["
                +serverName
                +"] SMTP server in Mail.xml");
        }

        theMailServer = server;
    }

    /**
     * Returns SMTP server host.
     * <P>
     * 
     * @return the host
     */
    public String getHost() {
        return theMailServer.getHost();
    }

    /**
     * Sets SMTP server host.
     * <P>
     * 
     * @param host the host to set
     */
    public void setHost(String host) {
        theMailServer.setHost(host);
    }

    /**
     * Returns SMTP server port.
     * <P>
     * 
     * @return the port number.
     */
    public int getPort() {
        return theMailServer.getPort();
    }

    /**
     * Sets SMTP server port number.
     * <P>
     * 
     * @param port the port number or -1 to indicate default port.
     */
    public void setPort(int port) {
        theMailServer.setPort(port);
    }

    /**
     * Indicates whether authenticating
     * 
     * @return true if user name supplied
     */
    boolean authenticates() {
        return (theMailServer.getUsername()!=null);
    }

    /**
     * Returns the user name.
     * 
     * @return the userName or null if none supplied (authentication not
     * required).
     */
    public String getUserName() {
        return theMailServer.getUsername();
    }

    /**
     * Returns password.
     * <P>
     * 
     * @return the password which can be null if user name not supplied.
     */
    public String getPassword() {
        return theMailServer.getPassword();
    }

    /**
     * Returns true if the Debugging property set.
     * <P>
     * 
     * @return true if the Debugging property set.
     */
    public boolean isDebugging() {
        return theMailServer.isDebug();
    }

    /**
     * Set the debugging attribute on the Mail Server
     * <P>
     * 
     * @param value
     */
    public void setDebugging(boolean value) {
        theMailServer.setDebug(value);
    }

    /**
     * Set user name and password.
     * <P>
     * 
     * @param userName the user name or null if no authentication.
     * 
     * @param password the password - ignored when user name is null.
     */
    public void setUserNameAndPassword(String userName,String password) {
        if (userName!=null) {
            theMailServer.setUsername(userName);
            theMailServer.setPassword(password);
        }
        else {
            theMailServer.setUsername(null);
            theMailServer.setPassword(null);
        }
    }

    /**
     * Indicates whether TLS (Transport Level Security) is enabled.
     * <P>
     * 
     * @return true if TLS enabled.
     */
    public boolean isTLSEnabled() {
        return theMailServer.isTls();
    }

    /**
     * Sets TLS enabled.
     * <P>
     * 
     * @param enabled true to set TLS enabled, false to disable.
     */
    public void setTLSEnabled(boolean enabled) {
        theMailServer.setTls(enabled);
    }

    /**
     * Returns the SSL protocols.
     * <P>
     * 
     * @return the SSL protocols or null if none set.
     */
    public String getSSLProtocols() {
        return theMailServer.getSslProtocols();
    }

    /**
     * Indicates whether SSL protocols set.
     * <P>
     * 
     * @return true if SSL protocols set.
     */
    public boolean hasSSLProtocols() {
        return ((theMailServer.getSslProtocols()!=null)&&(!theMailServer
            .getSslProtocols()
            .isEmpty()));
    }

    /**
     * Sets SSL protocols.
     * <P>
     * 
     * @param protocols the SSLProtocols to set.
     */
    public void setSSLProtocols(String protocols) {
        theMailServer.setSslProtocols(protocols);
    }

    /**
     * Get SMTP authenticator.
     * 
     * @return the SMTP authenticator
     */
    Authenticator getAuthenticator() {
        return new SMTPAuthenticator(getUserName(),getPassword());
    }

    /**
     * SMTP callback authenticator. Used if the SMTP server requests
     * authentication.
     * 
     * @author pwalsh
     */
    private class SMTPAuthenticator extends Authenticator {

        private String theUsername;
        private String thePassword;

        /**
         * Constructor.
         * 
         * @param username
         * @param password
         */
        public SMTPAuthenticator(String username,String password) {
            theUsername = username;
            thePassword = password;
        }

        /**
         * @see javax.mail.Authenticator#getPasswordAuthentication()
         */
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(theUsername,thePassword);
        }
    }
}
