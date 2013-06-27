CDC adapter
===========

CDC (Change data capture) is a an API exposed by Informix (IBM) which provides immediate
notification of database table changes. The Change Data Capture API starts capturing transactions
from the current logical log and processes all transactions sequentially. The first time you
start capturing data for a particular table, data capture starts at the current log position. If
you later stop capture and then restart it, you can restart at the point in the logical logs where
data capture was stopped. You cannot go backwards in time through the logical logs to capture the
history of the table or perform random seeking in the logical logs.

Using the CDC adapter classes
-----------------------------

A CDC adpater is created from the CDCConnectionDetails class :

    CDCConnectionDetails connectionDetails = new
  	    CDCConnectionDetails("//192.168.52.12:9088/syscdcv1");
    connectionDetails.setUsername("informix");
		connectionDetails.setPassword("********");
		connectionDetails.setInformixServer("cdctest_net");
		theCDCConnection = new CDCConnection(connectionDetails);
		theCDCConnection.connect();

CDC table and column configuration
----------------------------------

Specify the table and columns for CDC capture

    final int marketID = theCDCConnection.enableCapture(
        "test:informix.market",
  	    "ev_mkt_id,name,status"); 
    final int eventID = theCDCConnection.enableCapture(
        "test:informix.event",
				"ev_id,ev_name,ev_desc,ev_status");

CDC capture is enabled (Note: CDC Listeners must be configured first)

Listenting to capture events
----------------------------

After a enabling table capture but before starting the capture you will need to register
a listener so that the CDC messages can be passed to the listener.

    theCDCConnection.addCDCRecordListener(new CDCRecordListener() {
        public void onCDCRecord(CDCRecord record) { 
		        if(record.isMetadataRecord()) {
			          byte[] data = new byte[record.getPayload().remaining()]; 
			          ByteBuffer buffer = record.getPayload(); 
			          buffer.get(data); 
			          // Display table schema info
			          System.out.println(new String(data)); 
			          return;
		        } 
				
		        if(record.isOperationalRecord()) {
			          CDCOperationRecord operationRecord = (CDCOperationRecord)record; 

			          if(operationRecord.getUserData()==outcomeID) { 
				            CDCOutcomeUpdate outcomeUpdate = new CDCOutcomeUpdate(record);
				            System.out.println("UUID = " +outcomeUpdate.getUUID());
				            System.out.println("Updated price = " +outcomeUpdate.getPrice());
						            ...
			          }
            }
        }
    }
		

In the example above an anonymous listener is used, but it doesn't have to be. Once all
of the tables are set up then you need to start the data capture.


Starting the capture of CDC events
----------------------------------

Once all of the tables are set up then you need to start the data capture. This is done by
the following:

    theCDCConnection.startCapture();

Starting data capture spawns another thread and as soon as any data arrives via the CDC API
the listener interface will be called. It is important to note here that the Informix database
logging must be switched to unbuffered if you require minimal latency. If the database log
mode is buffered the this Adapter will only be notified every database checkpoint.

Parsing the results from a CDC event
------------------------------------

Once you have the `CDCRecord` then you may parse the payload of the `CDCRecord` to correspond
with the columns that you require. Please note that the order in which columns are listed
for capture in the enableCapture method might not be the same as they are returned via CDC.
It is wise to look at the metadata CDC record, as this will detail the order in which the
columns will be returned. Once you have the order the order remains the same so this only
needs to be done once at development time. Once you have the order, parsing the payload
becomes simple. In the example below we are going to be parsing the event columns:

    theUUID = payload.getInt();
    theStatus = CDCConnection.getChar(payload);
    theName = CDCConnection.getVarchar(payload);
    theDescription = CDCConnection.getVarchar(payload);
		

As you can see from the example above there are some helper methods to extract data from
the `ByteBuffer` payload on the `CDCConnection` class.

Stopping the capture of CDC events
----------------------------------

To turn off row level capture use the following method on the `CDCConnection`,
The following example shows how to turn off CDC capture:

    theCDCConnection.disableCapture("test:informix.market");
		theCDCConnection.disableCapture("test:informix.event");
		theCDCConnection.stopCapture();
		
Different CDC records
---------------------

For each type of transaction there are different CDC records that may contain information:

<table>
  <tr>
    <th>Operation</th>
    <th>Java type</th>
  </tr>
  <tr>
    <td>Begin Transaction</td>
    <td>CDCBeginTransactionRecord</td>
  </tr>
  <tr>
    <td>Commit Transaction</td>
    <td>CDCCommitRecord</td>
  </tr>
  <tr>
    <td>Delete Record</td>
    <td>CDCDeleteRecord</td>
  </tr>
  <tr>
    <td>Discard Record</td>
    <td>CDCDiscardRecord</td>
  </tr>
  <tr>
    <td>Error Record</td>
    <td>CDCErorRecord</td>
  </tr>
  <tr>
    <td>Insert Record</td>
    <td>CDCInsertRecord</td>
  </tr>
  <tr>
    <td>Table Schema Record</td>
    <td>CDCMetadataRecord</td>
  </tr>
  <tr>
    <td>Rollback Transaction</td>
    <td>CDCRollbackRecord</td>
  </tr>
  <tr>
    <td>Truncate Record</td>
    <td>CDCTruncateRecord</td>
  </tr>
  <tr>
    <td>Update Before Record</td>
    <td>CDCUpdateBeforeRecord</td>
  </tr>
  <tr>
    <td>Update After Record</td>
    <td>CDCUpdateAfterRecord</td>
  </tr>
</table>
