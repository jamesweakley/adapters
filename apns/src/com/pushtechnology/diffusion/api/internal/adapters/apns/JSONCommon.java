package com.pushtechnology.diffusion.api.internal.adapters.apns;

import com.pushtechnology.diffusion.api.Logs;
import com.pushtechnology.diffusion.api.internal.adapters.apns.JSONArray;
import com.pushtechnology.diffusion.api.internal.adapters.apns.JSONDictionary;

/**
 * Functions common to both Array and Dictionary
 * @author martincowie
 *
 */
public class JSONCommon 
{
	
	/**
	 * Render a given JSON-friendly object as a JSON-ready string
	 * @param obj
	 * @return
	 */
	public static String toString( Object obj )
	{
		if(	obj instanceof Number ||
			obj instanceof Boolean ||
			obj instanceof JSONDictionary ||
			obj instanceof JSONArray )
		{
			// Return a 'naked' value literal
			return obj.toString();
		}

		// Otherwise .. handle it like a String
		return String.format( "\"%s\"", escapeString( obj.toString() ) );
	}

	private static final String JSON_CHARS = "\"\\/\b\f\n\r\t";
	private static final String[] JSON_ESCAPES = new String[] {
		"\\\"", "\\\\", "/", "\\b", "\\f", "\\n", "\\r", "\\t"
	};
	
	static {
		if( JSON_CHARS.length() != JSON_ESCAPES.length )
			Logs.severe( "jsonChars.length() != jsonEscapes.length" );
	}
	
	private static Object escapeString( String str ) 
	{
		StringBuilder result = new StringBuilder();
		for( int i=0; i< str.length(); i++ )
		{
			char chr = str.charAt( i );
			
			int n = JSON_CHARS.indexOf( chr );
			result.append( ( n != -1 ) ? JSON_ESCAPES[n] : chr );
		}
		return result.toString();
	}

	/**
	 * Check that the given object is suitable for JSON serialisation
	 * @param value Object to check
	 * @return true if value is a wrapped/boxed Java primitive, a String, a JSON Dictionary or JSON Array.
	 */
	public static boolean checkType(Object value) 
	{
		return ( 
			value instanceof Number || 
			value instanceof Boolean || 
			value instanceof Character || 
			value instanceof String || 
			value instanceof JSONDictionary || 
			value instanceof JSONArray 
		);
		
	}

}
