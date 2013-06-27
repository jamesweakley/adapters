package com.pushtechnology.diffusion.api.internal.adapters.c2dm;

import java.net.URLEncoder;
import java.util.HashMap;

import com.pushtechnology.diffusion.api.APIException;
import com.pushtechnology.diffusion.api.APIProperties;

/**
 * A store of commonly used constants and functions.
 * 
 * @author martincowie
 */
final class C2DMCommon {

    // C2DM Servers
    static final String C2DM_MESSAGES_SERVER =
        "https://android.clients.google.com/c2dm/send";
    static final String C2DM_LOGGIN_SERVER =
        "https://www.google.com/accounts/ClientLogin";
    static final String UPDATE_CLIENT_AUTH = "Update-Client-Auth";
//    private static final String UTF8 = "UTF-8";
    static final String PARAM_COLLAPSE_KEY = "collapse_key";
    //static final String PARAM_DELAY_WHILE_IDLE = "delay_while_idle";
    
    // C2DM Values
    static final String PARAM_REGISTRATION_ID = "registration_id";

    static final String JMX_DOMAIN = "com.pushtechnology.adapters.c2dm";

    private C2DMCommon() {
    }

    /**
     * Build a URI formatted name=value query string
     * 
     * @param strings An even number of strings, where each pair composes a
     * (name,value) pair.
     * @return A correctly formatted URI, where values are URL escaped
     */
    static String buildURI(String... strings) throws APIException {
        if (strings.length%2!=0)
            throw new APIException(
                String.format(
                    "Non-even number of arguments %d",strings.length));

        try {
            StringBuilder result = new StringBuilder();
            for (int i = 0;i<strings.length;i += 2) {
                String key = strings[i], value =
                    URLEncoder.encode(strings[i+1],APIProperties.UTF8);

                if (result.length()>0)
                    result.append('&');
                result.append(key).append('=').append(value);
            }

            return result.toString();
        }
        catch (Throwable ex) {
            // This shouldn't ever happen
            throw new APIException("Build URI fails",ex);
        }
    }

    /**
     * Build a URI formatted name=value query string
     * 
     * @param map A map of name,value pairs
     * @return A correctly formatted URI, where values are URL escaped
     */
    static String buildURI(HashMap<String,String> map) 
    throws APIException {
        StringBuilder result = new StringBuilder();

        try {
            for (String key:map.keySet()) {
                String value = URLEncoder.encode(map.get(key),APIProperties.UTF8);
                if (result.length()>0)
                    result.append('&');
                result.append(key).append('=').append(value);
            }
            return result.toString();
        }
        catch (Throwable ex) {
            // This shouldn't ever happen
            throw new APIException("Build URI fails",ex);
        }
    }

}
