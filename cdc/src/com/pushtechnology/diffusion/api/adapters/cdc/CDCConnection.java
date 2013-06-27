/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 12:10:49
 */

package com.pushtechnology.diffusion.api.adapters.cdc;

import java.io.PrintWriter;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.internal.adapters.cdc.CDCCaptureEngine;

/**
 * This controls a connection to an Informix CDC instance.
 * <P>
 * One CDC instance can listen to many tables at once, but it is also possible to have many CDCConnections as well.
 * 
 * <pre>
 *     CDCConnectionDetails connectionDetails =
 *         new CDCConnectionDetails("//192.168.52.12:9088/syscdcv1");
 *     connectionDetails.setUsername("informix");
 *     connectionDetails.setPassword("********");
 *     connectionDetails.setInformixServer("cdctest_net");
 * 
 *     CDCConnection cdcConnection = new CDCConnection(connectionDetails);
 *     cdcConnection.connect();
 * 
 *     int outcomeID =
 *         cdcConnection.enableCapture(
 *             "test:informix.Outcome",
 *             "uuid,price");
 * 
 *     int marketID =
 *         cdcConnection.enableCapture(
 *             "test:informix.market",
 *             "ev_mkt_id,name,status");
 * 
 *     cdcConnection.addCDCRecordListener(
 *         new CDCRecordListener() {
 *             public void onCDCRecord(CDCRecord record) {
 *                 if (record.isMetadataRecord()) {
 *                     byte [] data = new byte[record.getPayload().capacity()];
 *                     ByteBuffer buffer = record.getPayload();
 *                     buffer.get(data);
 *                     System.out.println(new String(data));
 *                 }
 *                  
 *                 if (record.isOperationalRecord()) {
 * 
 *                     CDCOperationRecord operationRecord =
 *                         (CDCOperationRecord)record;
 *                         
 *                     if (operationRecord.getUserData()==outcomeID) {                        
 *                         CDCOutcomeUpdate outcomeUpdate =
 *                             new CDCOutcomeUpdate(record);
 *                         System.out.println(
 *                             "UUID = "+outcomeUpdate.getUUID());
 *                         System.out.println(
 *                             "Updated price = "+outcomeUpdate.getPrice());
 *                      }
 *                      else if (operationRecord.getUserData()==marketID) {
 *                          CDCMarketUpdate marketUpdate =
 *                              new CDCMarketUpdate(record);
 *                          System.out.println(
 *                              "UUID = "+marketUpdate.getUUID());
 *                          System.out.println(
 *                              "Status = "+marketUpdate.getStatus());
 *                          System.out.println(
 *                              "Name = " + marketUpdate.getName());*
 *                      }
 *                  }
 *              }
 *          });
 * 
 *    cdcConnection.startCapture();
 * 
 * </pre>
 * 
 * @author dhudson
 * 
 */
public final class CDCConnection implements CDCRecordListener {

	private CDCConnectionDetails theConnectionDetails = null;

	private Connection theConnection;

	private int theSessionID;

	private int theNextUserData = 0;

	private List<CDCRecordListener> theListeners;

	private CDCCaptureEngine theCaptureEngine;

	private static final String INFORMIX_DRIVER_CLASS = "com.informix.jdbc.IfxDriver";

	/**
	 * Create a new CDC connection.
	 * <P>
	 * If this constructor is used then connection details must be explictly supplied using {@link #setConnectionDetails(CDCConnectionDetails)} before
	 * connecting.
	 */
	public CDCConnection() {
		this(null);
	}

	/**
	 * Create a new CDC Connection with supplied connection details.
	 * 
	 * @param connectionDetails
	 *            the connection details
	 */
	public CDCConnection(CDCConnectionDetails connectionDetails) {
		theConnectionDetails = connectionDetails;
		theListeners = new ArrayList<CDCRecordListener>();
		theCaptureEngine = new CDCCaptureEngine(this);
	}

	/**
	 * Set the connection details
	 * <p>
	 * 
	 * @param connectionDetails
	 *            the connection details to set
	 */
	public void setConnectionDetails(CDCConnectionDetails connectionDetails) {
		theConnectionDetails = connectionDetails;
	}

	/**
	 * Returns the connection details
	 * <p>
	 * 
	 * @return the connection details or null if not set
	 */
	public CDCConnectionDetails getConnectionDetails() {
		return theConnectionDetails;
	}

	/**
	 * Add a CDC record listener
	 * <p>
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addCDCRecordListener(CDCRecordListener listener) {
		theListeners.add(listener);
	}

	/**
	 * Remove CDC record listener
	 * <p>
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeCDCRecordListener(CDCRecordListener listener) {
		theListeners.remove(listener);
	}

	/**
	 * This opens a connection to the database and creates a CDC Session
	 * 
	 * @throws APIException
	 *             if unable to connect
	 */
	public void connect() throws APIException {

		if (theConnectionDetails == null) {
			throw new APIException("CDCConnection: no connection details supplied");
		}

		try {
			Class.forName(INFORMIX_DRIVER_CLASS);
		} catch (ClassNotFoundException ex) {
			throw new APIException("CDCConnection: Unable to find class " + INFORMIX_DRIVER_CLASS, ex);
		}

		try {
			if (theConnectionDetails.isDebugging()) {
				DriverManager.setLogWriter(new PrintWriter(System.out));
			}

			theConnection = DriverManager.getConnection(theConnectionDetails.getJDBCUrl());
		} catch (SQLException ex) {
			throw new APIException("CDCConnection: unable to connect to the database : " + ex.getLocalizedMessage(), ex);
		}

		// TODO: Look at this, as it might make it faster
		// theConnection.setReadOnly(true);

		try {
			CallableStatement cstmt = theConnection.prepareCall("execute function informix.cdc_opensess(?,?,?,?,?,?)");

			cstmt.setString(1, theConnectionDetails.getInformixServer());

			// Assign Session ID : Must be zero
			cstmt.setInt(2, 0);

			// Timeout... < 0 wait for ever.. 0 return immediately if no data >
			// 0 number of seconds to wait
			cstmt.setInt(3, 500);

			// Max records per return..
			cstmt.setInt(4, 1);

			// Interface behaviour Major version Must be 1
			cstmt.setInt(5, 1);

			// Interface behaviour Minor version Must be 1
			cstmt.setInt(6, 1);

			ResultSet rs = cstmt.executeQuery();

			rs.next();

			// If positive int all OK...
			theSessionID = rs.getInt(1);

			if (theSessionID < 0) {
				throw new APIException("CDCConnection: Unable to get CDC session error code " + rs.getInt(2));
			}
		} catch (SQLException ex) {
			throw new APIException("CDCConnection: unable to open cdc session " + ex.getLocalizedMessage(), ex);
		}
	}

	/**
	 * Specify table and columns to capture.
	 * <P>
	 * Specifies a table and columns within that table from which to start capturing data. You cannot include columns with simple large objects, user-defined
	 * data types, or collection data types.
	 * <p>
	 * 
	 * @param qualifiedTableName
	 *            For example database:owner.table (i.e. test:informix.Outcome)
	 * 
	 * @param cols
	 *            A comma separated list of column names.
	 * 
	 * @return an ID to reference with this table name, for operational records this will be user data
	 * 
	 * @throws APIException
	 *             if unable to enable capture using specified table and columns
	 */
	public int enableCapture(String qualifiedTableName, String cols) throws APIException {

		int userData = 0;
		int resultCode;
		CallableStatement cstmt;
		ResultSet rs;

		try {
			cstmt = theConnection.prepareCall("execute function informix.cdc_set_fullrowlogging(?,?)");

			// Table to capture
			cstmt.setString(1, qualifiedTableName);

			// mode=1 - Start. mode=0 - Stop
			cstmt.setInt(2, 1);

			rs = cstmt.executeQuery();
			rs.next();

			resultCode = rs.getInt(1);

			if (resultCode != 0) {
				throw new APIException("CDCConnection: Unable to set full row logging " + resultCode);
			}

		} catch (SQLException ex) {
			throw new APIException("CDCConnection: Unable to set full row logging ", ex);
		}

		try {

			cstmt = theConnection.prepareCall("execute function informix.cdc_startcapture(?,?,?,?,?)");
			cstmt.setInt(1, theSessionID);

			// Must be zero
			cstmt.setLong(2, 0);

			cstmt.setString(3, qualifiedTableName);
			cstmt.setString(4, cols);

			// Thread safe
			userData = theNextUserData++;

			// My user data...
			cstmt.setInt(5, userData);

			rs = cstmt.executeQuery();
			rs.next();

			resultCode = rs.getInt(1);

			if (resultCode != 0) {
				throw new APIException("CDCConnection: Unable to start cdc capture " + resultCode);
			}
		} catch (SQLException ex) {
			throw new APIException("CDCConnection: Unable to start cdc capture ", ex);
		}

		return userData;
	}

	/**
	 * Stop capturing data from a specific table.
	 * <P>
	 * This function does not affect the session status; the session remains open and active.
	 * 
	 * @param qualifiedTableName
	 *            should be in the form of database:owner.table
	 * @throws APIException
	 *             if unable to stop
	 */
	public void disableCapture(String qualifiedTableName) throws APIException {
		try {
			CallableStatement cstmt = theConnection.prepareCall("execute function informix.cdc_endcapture(?,?,?)");

			cstmt.setInt(1, theSessionID);
			cstmt.setLong(2, 0);
			cstmt.setString(3, qualifiedTableName);

			ResultSet rs = cstmt.executeQuery();
			rs.next();

			int resultCode = rs.getInt(1);

			if (resultCode != 0) {
				throw new APIException("CDCConnection: Unable to end cdc capture " + resultCode);
			}

		} catch (SQLException ex) {
			throw new APIException("CDCConnection: Unable to end cdc capture ", ex);
		}
	}

	/**
	 * Start the capture process.
	 * <p>
	 * 
	 * @throws APIException
	 *             if unable to start capture.
	 */
	public void startCapture() throws APIException {
		try {
			CallableStatement cstmt = theConnection.prepareCall("execute function informix.cdc_activatesess(?,?)");
			cstmt.setInt(1, theSessionID);
			cstmt.setLong(2, 0);

			ResultSet rs = cstmt.executeQuery();
			rs.next();
			int resultCode = rs.getInt(1);

			if (resultCode != 0) {
				throw new APIException("CDCConnection: Unable to activate session " + resultCode);
			}

			theCaptureEngine.start();

		} catch (SQLException ex) {
			throw new APIException("CDConnection: Unable to activate session" + ex);
		}
	}

	/**
	 * Stop capturing.
	 * <P>
	 * 
	 * @throws APIException
	 *             if unable to stop capture.
	 */
	public void stopCapture() throws APIException {
		theCaptureEngine.stop();
		try {
			CallableStatement cstmt = theConnection.prepareCall("execute function informix.cdc_deactivatesess(?,?)");
			cstmt.setInt(1, theSessionID);

			ResultSet rs = cstmt.executeQuery();
			rs.next();
			int resultCode = rs.getInt(1);

			if (resultCode != 0) {
				throw new APIException("CDCConnection: Unable to de activate session " + resultCode);
			}

			theCaptureEngine.start();

		} catch (SQLException ex) {
			throw new APIException("CDConnection: Unable to de activate session" + ex);
		}
	}

	/**
	 * Returns the CDC Session Identifier.
	 * <p>
	 * 
	 * @return the CDC session Identifier
	 */
	public int getCDCSessionID() {
		return theSessionID;
	}

	/**
	 * Returns the SQL Connection.
	 * <P>
	 * 
	 * @return the JDBC connection
	 */
	public Connection getSQLConnection() {
		return theConnection;
	}

	/**
	 * @see com.pushtechnology.diffusion.api.adapters.cdc.CDCRecordListener#onCDCRecord(com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord)
	 */
	public void onCDCRecord(CDCRecord record) {
		for (CDCRecordListener listeners : theListeners) {
			try {
				listeners.onCDCRecord(record);
			} catch (Throwable t) {
				Logs.warning("CDCConnection: Exception caugth in onCDCRecord", t);
			}
		}
	}

	/**
	 * Get a Varchar from a byte buffer.
	 * <P>
	 * The buffer needs to be positioned at the correct place. The buffer position will also be moved to the size of the Varchar.
	 * <p>
	 * 
	 * @param buffer
	 *            the buffer
	 * @return String representation of the VarChar
	 * @throws APIException
	 *             if unable to get Varchar
	 */
	public static String getVarchar(ByteBuffer buffer) throws APIException {
		try {
			int length = buffer.get();
			byte[] tmp = new byte[length];
			buffer.get(tmp);
			
			// Single char '\0' is equivalent to a NULL
			if(length == 1 && tmp[0] == '\0') {
				return null;
			}
			
			return new String(tmp);
		} catch (BufferUnderflowException ex) {
			throw new APIException("BufferUnderFlow exception", ex);
		}
	}

	/**
	 * Get a char from a byte buffer.
	 * <P>
	 * The buffer needs to be positioned at the correct point.
	 * 
	 * @param buffer
	 *            the buffer
	 * @return a single character
	 */
	public static char getChar(ByteBuffer buffer) {
		return (char) buffer.get();
	}

	/**
	 * Get an integer from a byte buffer
	 * <p>
	 * The buffer pointer will be advanced by the size of an integer (4 bytes)
	 * 
	 * @param buffer
	 * 			  the buffer
	 * @return int value
	 * @throws APIException
	 */
	public static int getInteger(ByteBuffer buffer) throws APIException {
		try {
			return buffer.getInt();
		} catch (BufferUnderflowException ex) {
			throw new APIException("BufferUnderflow exception", ex);
		}
	}
}
