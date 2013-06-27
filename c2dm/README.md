# Google Cloud 2 Device Messaging Services

Provides a simple means of integrating with Google Cloud 2 Device Messaging Services.

## Using the C2DM adapter

### Obtain the C2DM connection
The connection to the APNS servers is over an HTTPS connection. The connection configuration
is held in the Diffusion™ configuration file PushNotification.xml.
   
The configuration is constucted as follows:

<table>
  <tr>
    <td>name</td>
    <td>Symbolic name used to locate this connection from the adapter.</td>
  </tr>
  <tr>
    <td>email</td>
    <td>An email address registered with Google for C2DM</td>
  </tr>
  <tr>
    <td>password</td>
    <td>The password related to the above email address</td>
  </tr>
  <tr>
    <td>collapse</td>
    <td>Optional collapse-key used by C2DM servers to conflate C2DM messages</td>
  </tr>
</table>

Java developers wishing to send C2DM messages via the adapter must first obtain a connection
to the C2DM servers with the following code:

    C2DMServerConnection cnx = C2DMAdapter.getNamedConnection( definitionName );

This will place a connection (or reuse an existing connection).
  
### Writing apps to receive C2DM messages

Google hosts [documentation on writing C2DM aware applications](http://code.google.com/android/c2dm/#writing_apps)
   
### Composing the message

Messages sent via C2DM comprise a dictionary of named values. Some values are reserved for
C2DM transmission purposes and are provided automatically by the C2DM adapter. Google
recommends prefixing solution specific values with data. to disambiguate.
   
<table>
  <tr>
    <th>Reserved key values</th>
  </tr>
  <tr>
    <td>registration_id</td>
    <td>The registration ID retrieved from the Android application on the phone. Required.</td>
  </tr>
  <tr>
    <td>collapse_key</td>
    <td>An arbitrary string that is used to collapse a group of like messages when the device is
    offline, so that only the last message gets sent to the client. This is intended to avoid
    sending too many messages to the phone when it comes back online. Note that since there is no
    guarantee of the order in which messages get sent, the "last" message may not actually be the
    last message sent by the application server. Required.</td>
  </tr>
  <tr>
    <td>delay_while_idle</td>
    <td>If included, indicates that the message should not be sent immediately if the device is
    idle. The server will wait for the device to become active, and then only the last message
    for each collapse_key value will be sent. Optional.</td>
  </tr>
  <tr>
    <td>Authorization</td>
    <td>Header with a ClientLogin Auth token. The cookie must be associated with the c2dm
    service. Required.</td>
  </tr>
</table>

Developers compose a message for transmission using a `Map<String,String`:
  
    Map<String,String> params = new HashMap<String,String>();
    params.put("data.payload", "A test message for the handset" );

### Dispatch the message

Once a connection is in place and a message is ready, the message can be transmitted:

    String messageID = cnx.send(clientRegistrationID, params);
    
The arguments are the handset registration-id and the parameters, respectively. A
message identifier string is returned if the message is successfully queued by C2DM.
In case of failure (e.g. message too large, invalid registration-id supplied, C2DM servers
overloaded) an exception will be thrown.

### JMX monitoring

On instantiation, the C2DMServerConnection object registers an MBean in the standard MBean server.

C2DMServerConnection exposes the following:

<table>
  <tr>
    <th>Attributes</th>
  </tr>
  <tr>
    <td>MessagesSent</td>
    <td>The number of messages sent via this object</td>
  </tr>
  <tr>
    <td>LastMessageSent</td>
    <td>java.util.Date of the last message sent</td>
  </tr>
  <tr>
    <td>DefinitionName</td>
    <td>The symbolic name used to identify this connection in the configuration files</td>
  </tr>
  <tr>
    <td>DefinitionEmailAddress</td>
    <td>The email address used to authenticate with the Google C2DM servers</td>
  </tr>
  <tr>
    <td>CollapseKey</td>
    <td>The conflation/collapse key used by this connection</td>
  </tr>
  <tr>
    <td>WhenGoogleAuthenticated</td>
    <td>The timestamp of the last authentication exchange</td>
  </tr>
  <tr>
    <td>GoogleAuthenticated</td>
    <td>true if this connector has correctly authenticated with the C2DM servers</td>
  </tr>
  <tr>
   <th>Actions</th>
  </tr>
  <tr>
    <td>disconnect</td>
    <td>Explicitly disconnect from the C2DM servers</td>
  </tr>
</table>
