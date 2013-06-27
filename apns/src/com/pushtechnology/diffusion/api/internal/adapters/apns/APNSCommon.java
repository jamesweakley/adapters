package com.pushtechnology.diffusion.api.internal.adapters.apns;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.Date;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.IOUtils;
import com.pushtechnology.diffusion.api.json.Dictionary;

/**
 * A store of commonly used constants and functions.
 * 
 * @author martincowie
 * 
 */
@Deprecated
public class APNSCommon {

    public static final String DEV_PUSH_HOST = "gateway.sandbox.push.apple.com";
    public static final String DEV_FEEDBACK_HOST = "feedback.sandbox.push.apple.com";
    public static final String PROD_PUSH_HOST = "gateway.push.apple.com";
    public static final String PROD_FEEDBACK_HOST = "feedback.push.apple.com";
    public static final int PUSH_PORT = 2195;
    public static final int FEEDBACK_PORT = 2196;
    public static final Date MAX_DATE = new Date(1000*Long.MAX_VALUE);
//    public static final String UTF8 = "UTF-8";
    public static final int TOKEN_SIZE = 32;
    public static final String JMX_DOMAIN = "com.pushtechnology.adapters.apns";

    // Crypto details
    public static final String KEYSTORE_TYPE = "PKCS12";
    public static final String KEY_ALGORITHM = "sunx509";

    /**
     * The maximum allowable message payload, in bytes.
     */
    public static final int PAYLOAD_MAX = 256;

    private final static String HEXABET = "0123456789abcdef";

    /**
     * Convenience method to compose the simplest push-notification messages.
     * All arguments can be null, and if so the property is not included in the
     * result.
     * 
     * @param alertString String to display by handset
     * @param soundName Symbolic name of sound to play by handset
     * @param badgeCount Numeric badge-count to display on app. Set to zero for
     * no badge-count.
     * @return a JSON formatted String
     * 
     * Users wishing to build more complex payloads should use JSON Dictionary
     * and Array classes
     * @see com.pushtechnology.diffusion.api.json.Array
     * @see com.pushtechnology.diffusion.api.json.Dictionary
     */
    public static String composePayload(String alertString,String soundName,Integer badgeCount) {
        Dictionary apsDict = new Dictionary();

        if (alertString!=null)
            apsDict.put("alert",alertString);
        if (soundName!=null)
            apsDict.put("sound",soundName);
        if (badgeCount!=null)
            apsDict.put("badge",badgeCount);

        return new Dictionary("aps",apsDict).toString();
    }

    /**
     * Render a hex-string from an arbitrary byte array. Useful for printing
     * APNS-tokens.
     * 
     * @param apnsToken
     * @return
     */
    public static String toString(byte[] apnsToken) {
        StringBuilder result = new StringBuilder(2*apnsToken.length);
        for (byte b:apnsToken) {
            int i = b&0xff;
            result.append(HEXABET.charAt(i>>4));
            result.append(HEXABET.charAt(i&0x0F));
        }
        return result.toString();
    }

    /**
     * Empty the contents of is into os.
     * 
     * @throws IOException
     */
    public static void spool(InputStream is,OutputStream os) throws IOException {
        byte buff[] = new byte[1024];
        int n;

        while (-1!=(n = is.read(buff)))
            os.write(buff,0,n);
    }

    /**
     * Get the named file
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] getFile(File file) throws FileNotFoundException, IOException {
        ByteArrayOutputStream result = null;
        FileInputStream in = null;
        byte[] contents = null;
        try {
            result = new ByteArrayOutputStream();
            in = new FileInputStream(file);
            spool(in,result);
            contents = result.toByteArray();
        }
        finally {
            IOUtils.close(in);
            IOUtils.close(result);
        }
        return contents;
    }

    /**
     * Create an object from which SSLSocketFactories can be made
     * 
     * @param cert InputStream for the P12 certificate file
     * @param password Passphrase for the P12 certificate
     * @param ksType Usually "PKCS12"
     * @param ksAlgorithm Usually "sunx509", may vary between JSSE providers
     * @return
     */
    public static SSLContext newSSLContext(InputStream cert,String password,String ksType,String ksAlgorithm) 
    throws APIException {
        try {
            KeyStore ks = KeyStore.getInstance(ksType);
            ks.load(cert,password.toCharArray());

            // Get a KeyManager and initialize it
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(ksAlgorithm);
            kmf.init(ks,password.toCharArray());

            // Get a TrustManagerFactory and init with KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(ksAlgorithm);
            tmf.init(ks);

            // Get the SSLContext to help create SSLSocketFactory
            SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(kmf.getKeyManagers(),null,null);
            return sslc;
        }
        catch (Throwable ex) {
            throw new APIException("Error creating SSL context",ex);
        }

    }

}
