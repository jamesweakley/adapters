package com.pushtechnology.diffusion.api.internal.adapters.twitter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.adapters.twitter.Tweet;

/**
 * JSON implementation of a {@link Tweet}.
 * 
 * @author Antonio Di Ferdinando - created 22 Dec 2011
 * @since 4.1
 * 
 */
@SuppressWarnings("deprecation")
public final class JsonTweet implements Tweet {
    
    private static final long serialVersionUID = 1L;

    
    private final Map<String,Object> theObjectMap;

    /**
     * Constructor.
     */
    public JsonTweet() {
        theObjectMap = new HashMap<String,Object>();
    }

    /**
     * Constructor.
     * 
     * @param object the JSONObject to convert
     */
    public JsonTweet(JSONObject object) {
        theObjectMap = new HashMap<String,Object>();

        try {

            final String[] names = JSONObject.getNames(object);

            if (names!=null) {
                for (String name:names) {
                    if (object.get(name) instanceof JSONObject) {
                        theObjectMap.put(name,
                            new JsonTweet((JSONObject)object.get(name)));
                    }
                    else {
                        theObjectMap.put(name,object.get(name));
                    }
                }
            }
            else {
                Logs.info("null JSON received.");
                Logs.info("["+object.get("text")+"]");
            }
        }
        catch (JSONException jsonEx) {
            jsonEx.getMessage();
        }
    }

    /**
     * @see Tweet#add(String,Object)
     */
    public void add(String name,Object value) {
        theObjectMap.put(name,value);
    }

    /**
     * @see Tweet#get(String)
     */
    public Object get(String name) {
        Object res = theObjectMap.get(name);
        return res;
    }

    /**
     * @see Tweet#getNames()
     */
    public Set<String> getNames() {
        return theObjectMap.keySet();
    }

    /**
     * @see Tweet#has(java.lang.String)
     */
    public boolean has(String name) {
        return theObjectMap.containsKey(name);
    }

    /**
     * @see Tweet#length()
     */
    public int length() {
        return theObjectMap.size();
    }

    /**
     * @see Tweet#remove(java.lang.String)
     */
    public Object remove(String name) {
        if (theObjectMap.containsKey(name)) {
            return theObjectMap.remove(name);
        }
        else {
            return null;
        }
    }

    /**
     * @see Tweet#getText()
     */
    public String getText() {
        if (theObjectMap.containsKey("text")) {
            return (String)theObjectMap.get("text");
        }
        else {
            return null;
        }
    }

    /**
     * @see Tweet#getSenderName()
     */
    public String getSenderName() {
        if (theObjectMap.containsKey("user")) {
            return (String)((JsonTweet)theObjectMap.get("user")).get("name");
        }
        else {
            return null;
        }
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        String result = null;
        Set<String> names = theObjectMap.keySet();
        Iterator<String> i = names.iterator();

        while (i.hasNext()) {
            String name = i.next();
            String token = name+":"+theObjectMap.get(name);
            if (result==null) {
                result = token+",";
            }
            else {
                result.concat(token);
                result.concat(",");
            }
        }
        return result;
    }
}
