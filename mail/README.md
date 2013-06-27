Mail adapter
============

The purpose of the Mail adapter is to provide a very simple interface for sending
emails via a mail server that supports SMTP (Simple Mail Transfer Protocol).

The adapter is very easy to use. All that is required is to specify the details
(e.g. address information) of the mail server and create a session to connect to it.
That session may then be used to send one or more email messages.

Using the mail adapter
----------------------

Define the server in the Mail.xml configuration file. It is only necessary to specify
the logical name of the server as defined in the properties file. Alternatively the server
details may all be supplied programatically. See the javadoc for full details. 
