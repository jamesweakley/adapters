package com.pushtechnology.diffusion.api.adapters.twitter;

import java.io.Serializable;
import java.util.Set;

/**
 * Interface of a wrapped tweet data structure.
 * 
 * @author Antonio Di Ferdinando - created 22 Dec 2011
 * @since 4.1
 */
public interface Tweet extends Serializable {

    /**
     * Adds a field in the data structure for this tweet.
     * 
     * @param name name for the field.
     * @param value value for the field.
     */
    void add(String name,Object value);

    /**
     * Gets the value of a named field.
     * 
     * @param name the name of the field for which a value is wanted.
     * @return the value for the named field in this tweet.
     */
    Object get(String name);

    /**
     * Returns the list of field names contained in this tweet.
     * 
     * @return a set of strings containing the names of fields present in this
     * tweet
     */
    Set<String> getNames();

    /**
     * Checks whether a named field is present in this tweet
     * 
     * @param name the name of the field to check presence for.
     * @return true if this the named a field with the same name as name is
     * present in this tweet. False otherwise
     */
    boolean has(String name);

    /**
     * Gets the length of the data structure for this tweet.
     * 
     * @return the length of the data structure for this tweet.
     */
    int length();

    /**
     * Removed a named field from this tweet.
     * 
     * @param name the name of the field to remove.
     * @return the value for the field name just removed.
     */
    Object remove(String name);

    /**
     * Provides a string representation of this tweet.
     * 
     * @return a representation of this tweet as a comma-separated list
     * of name=value pairs.
     */
    String toString();

    /**
     * Gets the text field of this tweet.
     * 
     * @return the text for this tweet in form of string.
     */
    String getText();

    /**
     * Gets the twitter name for the sender of this tweet.
     * 
     * @return the name of the sender for this tweet in form of string.
     */
    String getSenderName();
}
