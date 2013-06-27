package com.pushtechnology.diffusion.api.adapters.mail;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.pushtechnology.diffusion.api.APIException;

/**
 * SMTP Mail Session.
 * <P>
 * This allows a connection to an SMTP server to be defined and used to send
 * email messages.
 * <P>
 * SMTP servers may have their details defined in mail.properties in which case
 * they can simply be referred to by name or the server can be explicitly
 * defined using an {@link SMTPServerDetails} object.
 * <P>
 * A single session may be used to send many messages if required.
 */
@Deprecated
public final class SMTPMailSession {

    private SMTPServerDetails theServerDetails = null;

    /**
     * The mail session
     */
    private Session theMailSession;

    /**
     * Constructor for use with a named SMTP server.
     * <P>
     * The details are as defined for the named server in the mail.properties
     * file.
     * 
     * @param server names a server with details defined in mail.properties.
     * 
     * @throws APIException if no such server is defined or is incorrectly
     * defined.
     */
    public SMTPMailSession(String server) throws APIException {
        theServerDetails = new SMTPServerDetails(server);
        theMailSession = createMailSession(theServerDetails);
    }

    /**
     * Constructor for used with explicitly defined server details.
     * <P>
     * 
     * @param serverDetails the server details.
     */
    public SMTPMailSession(SMTPServerDetails serverDetails) {
        theServerDetails = serverDetails;
        theMailSession = createMailSession(theServerDetails);
    }

    /**
     * Creates a mail session from the currently set values.
     */
    private Session createMailSession(SMTPServerDetails serverDetails) {
        // Set the host
        Properties props = new Properties();
        props.put("mail.smtp.host",serverDetails.getHost());
        props.put("mail.smtp.port",serverDetails.getPort());
        
        // Enable TLS
        if (serverDetails.isTLSEnabled()) {

            props.put("mail.smtp.starttls.enable","true");

            // Set SSL protocols
            if (serverDetails.hasSSLProtocols()) {
                props.put("mail.smtp.ssl.protocols",serverDetails
                    .getSSLProtocols());
            }

            if (serverDetails.isDebugging()) {
                // And set ssl debugging
                System.setProperty("javax.net.debug","ssl,handshake");
            }
        }

        // Create a session (authenticating if necessary)
        Session session = null;
        if (serverDetails.authenticates()) {
            props.put("mail.smtp.auth","true");
            session =
                Session.getInstance(props,serverDetails.getAuthenticator());
        }
        else {
            props.put("mail.smtp.auth","false");
            session = Session.getInstance(props);
        }

        session.setDebug(serverDetails.isDebugging());

        return session;
    }

    /**
     * Send a message.
     * <P>
     * 
     * @param subject the message subject.
     * 
     * @param body the message body.
     * 
     * @param from the email address of the sender.
     * 
     * @param to the email address(es) of the recipients.
     * 
     * @throws APIException if unable to send the message
     */
    public void send(String subject,String body,String from,String... to)
    throws APIException {

        // create a message
        Message msg = new MimeMessage(theMailSession);

        // set the from address
        InternetAddress addressFrom;
        try {
            addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);
        }
        catch (Exception ex) {
            throw new APIException("Invalid from address "+from,ex);
        }

        // Set the message recipients
        ArrayList<InternetAddress> addressesTo =
            new ArrayList<InternetAddress>();
        for (String recipient:to) {
            try {
                addressesTo.add(new InternetAddress(recipient));
            }
            catch (Exception ex) {
                throw new APIException("Invalid recipient "+recipient,ex);
            }
        }

        if (addressesTo.isEmpty()) {
            throw new APIException("No recipients");
        }

        InternetAddress[] toList = new InternetAddress[addressesTo.size()];
        toList = addressesTo.toArray(toList);

        try {
            msg.setRecipients(Message.RecipientType.TO,toList);
        }
        catch (Exception ex) {
            throw new APIException("Error setting message recipients",ex);
        }

        // Set Subject
        try {
            msg.setSubject(subject);
        }
        catch (Exception ex) {
            throw new APIException("Error setting message subject",ex);
        }

        // Set content
        try {
            msg.setContent(body,"text/plain");
        }
        catch (MessagingException ex) {
            throw new APIException("Error setting message content",ex);
        }

        try {
            Transport.send(msg);
        }
        catch (MessagingException ex) {
            throw new APIException("Error sending message",ex);
        }
    }

}
