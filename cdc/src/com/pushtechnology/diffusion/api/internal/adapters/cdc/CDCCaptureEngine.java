/* 
 * @author dhudson - 
 * Created 29 Jul 2010 : 15:09:18
 */

package com.pushtechnology.diffusion.api.internal.adapters.cdc;

import java.sql.SQLException;

import com.informix.jdbc.IfxSmartBlob;
import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.adapters.cdc.CDCConnection;
import com.pushtechnology.diffusion.api.adapters.cdc.CDCRecord;
import com.pushtechnology.diffusion.api.threads.RunnableTask;
import com.pushtechnology.diffusion.api.threads.ThreadService;

@SuppressWarnings("deprecation")
public final class CDCCaptureEngine implements RunnableTask {

    private boolean thisIsRunning = false;
    private CDCConnection theConnection;
    private IfxSmartBlob theSmartBlob = null;
    
    /**
     * Constructor
     * 
     * @param connection
     */
    public CDCCaptureEngine(CDCConnection connection) {
        theConnection = connection;
    }

    /**
     * start
     * 
     * @throws APIException
     */
    public void start() throws APIException {
        thisIsRunning = true;
        ThreadService.newThread(
            this,
            "CDCCaptureEngine",
            Thread.NORM_PRIORITY,
            true).start();
    }

    /**
     * stop 
     */
    public void stop() {
        thisIsRunning = false;
        
        if(theSmartBlob != null) {
            theSmartBlob.notify();
        }
    }
    
  
    /**
     * @see com.pushtechnology.diffusion.api.threads.RunnableTask#run()
     */
    public void run() {

        try {
            theSmartBlob = new IfxSmartBlob(theConnection.getSQLConnection());
        }
        catch (SQLException ex) {
            thisIsRunning = false;
            Logs.warning(
                "CDCCaptureEngine: Unable to create smart blob",ex);
        }

        // TODO: This needs to be tunable..
        byte[] buffer = new byte[10240];

        while (thisIsRunning) {

            // Blocking..
            int availableDataLength;
            try {
                availableDataLength =
                    theSmartBlob.IfxLoRead(
                        theConnection.getCDCSessionID(),
                        buffer,
                        buffer.length);
                CDCRecord record =
                    CDCMessageFactory.createCDCRecord(
                        buffer,
                        availableDataLength);

                if (record!=null) {
                    theConnection.onCDCRecord(record);
                }
            }
            catch (SQLException ex) {
                thisIsRunning = false;
                Logs.warning(
                    "CDCCaptureEngine: problem with CDC capture",ex);
            }
        }
    }

}
