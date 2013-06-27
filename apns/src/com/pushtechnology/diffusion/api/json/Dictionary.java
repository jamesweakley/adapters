package com.pushtechnology.diffusion.api.json;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * A Dictionary that serialises as a JSON dictionary.
 * 
 * @author martincowie
 * @since 4.1
 */
@Deprecated
public final class Dictionary extends HashMap<String,Object> {

    /**
     * serial uid
     */
    private static final long serialVersionUID = 5528188465645020196L;

    /**
     * Construct a Dictionary object from the list of arguments
     * <P>
     * 
     * @param objects An even-number-sized (key,value, ... ) list of objects
     * 
     * @throws RuntimeException if a non-even sized list of arguments is given.
     * If any value object is not an Array, Dictionary, String or a (boxed) Java
     * primitive
     */
    public Dictionary(Object... objects) {
        if (objects.length%2!=0)
            throw new IllegalArgumentException(
                "Non-even number of arguments "+objects.length);

        for (int i = 0;i<objects.length;i += 2) {
            String keyName = objects[i].toString();
            Object value = objects[i+1];
            put(keyName,value);
        }
    }

    /**
     * @see HashMap#put(Object, Object)
     */
    @Override
    public Object put(String key,Object value) {
        if (JSONCommon.checkType(value)) {
            return super.put(key,value);
        }
        return null;
    }

    /**
     * @see HashMap#putAll(Map)
     */
    @Override
    public void putAll(Map<? extends String,? extends Object> m) {
        for (Object obj:m.values())
            if (!JSONCommon.checkType(obj)) {
                return;
            }

        super.putAll(m);
    }

  
    /**
     * Serialise this object as a JSON Dictionary.
     * @return object serialised as JSON dictionary.
     * @see AbstractMap#toString()
     * 
     */
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (String keyName:this.keySet()) {
            if (result.length()>0)
                result.append(',');
            String value = JSONCommon.toString(this.get(keyName));
            result.append(String.format("\"%s\":%s",keyName,value));
        }

        result.insert(0,'{').append('}');
        return result.toString();
    }
}
