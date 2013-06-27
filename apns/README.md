# Apple Push Notification Services

The APNS adapter provides a simple interface for sending Push Notification messages via the
Apple APNS servers.

The adapter is very easy to use. All that is required is to specify the message recipients'
details (e.g. APNS-token) and compose a message to send. An APNS connection is then selected
from the Diffusion™ configuration, a connection placed and the message sent.

## Using the adapter

### Obtain the APNS connection

The connection to the APNS servers is over an SSL/TLS secured TCP connection that is
authenticated both ways. The connection configuration is held in the Diffusion™ configuration
file `PushNotification.xml`.

The configuration is constructed as follows:

<table>
  <tr>
    <td>name</td>
    <td>Symbolic name used to locate this connection from the adapter.</td>
  </tr>
    <tr>
    <td>certificate</td>
    <td>Fully qualified filename of a PKCS12 certificate associated with the iOS app to which
    push-notifications are to be sent. Filenames are suffixed ".P12".</td>
  </tr>
  <tr>
    <td>passphrase</td>
    <td>Pass-phrase required to use the nominated certificate. It is not possible to use
    certificates that have no pass-phrase.</td>
  </tr>
  <tr>
    <td>production</td>
    <td>Choose production or sandbox APNS push servers.</td>
  </tr>
</table>

Java developers must first obtain a connection to the APNS servers with the following code:

    APNSServerConnection cnx = APNSAdapter.getNamedConnection( "APNS demo app (dev.)" );
    if( !cnx.isConnected() )
        throw new Exception( "Not possible to connect to APNS" );
        
### Obtain the P12 certificate

1. For an iOS app to receive push-notification messages from the APNS servers, it must be
configured to do so via the iOS Apple developer website. Subsequently the developer must
download an SSL certificate and load it into the user keychain with the keychain tool.
This will install both a certificate and a matching key in the user keychain. Users can
find it by searching for the App-ID.

2. Select **both** the certificate **and** the enclosed key and pick File => Export Items.
3. Select "Personal Information Exchange" from the File Format menu and save as a file.
4. Enter and record a passphrase.

### Compose the message
 
Messages sent via APNS use JSON formatted text.

Apple requires that the message be, at least, an anonymous dictionary holding a dictionary
named 'aps'. This, in turn, holds the text attribute 'alert'. For example:

    {"aps":{"alert":"This is the alert text"}}

The Diffusion™ APNS adapter includes a number of classes to ease creation of syntactically
correct message payloads. The simplest approach is to use the convenience method:

    String payload = APNSCommon.composePayload( String.format( "%ds countdown completed", seconds ), "gong.au", 0 );

where the arguments relate to the dictionary values 'alert', 'sound' and 'badge' respectively.

If the arguments are null they are omitted from the result.

The more flexible approach is to use classes
`com.pushtechnology.diffusion.api.internal.adapters.apns.JSONDictionary`
and `com.pushtechnology.diffusion.api.internal.adapters.apns.JSONArray` to compose your
own request and serialise it as JSON text, for example:

    String payload = new JSONDictionary("aps",
        new JSONDictionary("alert", "this is the alert text"),
	      "foo",
        new JSONArray(12, true, "some string value")).toString();

This will compose a message containing the miminum information required to send the text
"this is the alert text" and contains additional data to be processed by the receiving application.

### Dispatch the message

Once there is a connection in place and a mesage to send, the message can be transmitted:

    final long WEEK = 1000 * 60 * 60 * 24 * 7;

    Date weekLater = new Date( System.currentTimeMillis() + WEEK );
    int messageID = cnx.send( apnsToken, payload, weekLater );
    
The arguments to the `APNSServerConnection.send()` method are:

<table>
  <tr>
    <td>expiry</td>
    <td>Date after which the APNS servers will drop the message for delivery.</td>
  </tr>
  <tr>
    <td>apnsToken</td>
    <td>Device token identifying an app installed on a device.</td>
  </tr>
  <tr>
    <td>payload</td>
    <td>JSON formatted data describing the message.</td>
  </tr>
</table>

### Obtain an APNS token

Once an pplication is installed upon an iOS device it is able to request it's APNS token:

    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:( 
        UIRemoteNotificationTypeAlert |
	      UIRemoteNotificationTypeBadge |
	      UIRemoteNotificationTypeSound )];

This is delivered asynchronously to the app delegate object:

    - (void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken 
    { 
        NSLog( @"APNS registered, token: %@", deviceToken );
	      self.mainViewController.apnsToken = deviceToken;
    }

    - (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)err 
    { 
        NSLog( @"APNS Error: %@", err );
    }

The result of the call is an integer message id which can be used to identify the message
in any future error messages received.

###  Receiving error messages

In the event of error, the APNS service sends an error message before closing the connection.

The `APNSServerConnection.addListener( APNSResponseListener )` method can be used to listen
to `APNSResponse` objects fed back, should they occur.

The `APNSResponse` object is a simple bean object holding the messageID of the offending message
and an `ErrorResponseCode` enumeration describing the error.

### The APNS feedback service

The APNS token used by the `APNSServerConnection.send` method describes a unique combination of
an app installed on an iOS device. If the app is uninstalled from the device this is (eventually)
fed back to the APNS servers. In turn the APNS servers relay this information to the Diffusion™
APNS adapter via a APNSFeedbackConnection object. This is so that the token can be removed from
storage and no further messages are sent. For example, this code :

    APNSAdapter.getFeedbackConnection( "APNS demo app (dev.)" ).addListener( this );

will add the object this to the set of feedback listeners. When a feedback message is received
it is passed to the following method:

    public void handleFeedback(APNSFeedback feedback) 
    {
        Logs.info( String.format( "Recevied APNS feedback: %s", feedback ) );
    }

Apple advises that APNS message-senders periodically check for feedback messages. In practice the
servers close the connection shortly after dispatching any feedback messages.

### JMX Monitoring

On instantiation, both APNSServerConnection and APNSFeedbackConnection objects register MBeans
in the standard MBean server.

APNSServerConnection exposes the following:

<table>
  <tr>
    <th colspan="2">Attributes</th>
  </tr>
  <tr>
    <th>Name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>Connected</td>
    <td>True if there is currently a connection in place</td>
  </tr>
  <tr>
    <td>ConnectedDatestamp</td>
    <td>The timestamp of last successful connection placed</td>
  </tr>
  <tr>
    <td>MessagesSent</td>
    <td>The number of messages sent via this object</td>
  </tr>
  <tr>
    <td>LastMessageSent</td>
    <td>The timestamp of the last message sent</td>
  </tr>
  <tr>
    <td>DefinitionName</td>
    <td>The symbolic name used to identify this connection in the configuration files</td>
  </tr>
  <tr>
    <td>DefinitionCertificate</td>
    <td>Filename of the P12 certificate used to place this connection</td>
  </tr>
  <tr>
    <td>DefinitionIsProduction</td>
    <td>Returns true if configured to true in the configuration file</td>
  </tr>
  <tr>
    <th>Actions</th>
  </tr>
  <tr>
    <td>connect</td>
    <td>Explicitly connect to the APNS servers</td>
  </tr>
  <tr>
    <td>disconnect</td>
    <td>Explicitly disconnect from the APNS servers</td>
  </tr>
</table>
