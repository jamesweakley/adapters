/* 
 * @author dhudson - 
 * Created 28 Jul 2010 : 15:23:48
 */

package com.pushtechnology.diffusion.api.adapters.cdc;

import java.util.Vector;

/**
 * 'Metadata' (Database Schema) Record.
 * <P>
 * The user data returned by this Record is the Table unique Id.
 * 
 * @author pwalsh
 * 
 */
public interface CDCMetadataRecord extends CDCRecord {

    /**
     * Return the flags.
     * 
     * @return the flags (constant 0)
     */
    int getFlags();

    /**
     * Returns the Fixed Length Size.
     * 
     * @return The number of bytes of data in fixed-length columns in the table.
     */
    int getFixedLengthSize();

    /**
     * Returns the number of fixed length columns.
     * 
     * @return The number of fixed-length columns in the table being captured. 0
     * indicates that there are no fixed-length columns.
     */
    int getFixedLengthCols();

    /**
     * Returns the number of variable length columns.
     * 
     * @return The number of variable-length columns in the table being
     * captured. 0 indicates that there are no variable-length columns.
     */
    int getVarLengthCols();

    /**
     * Returns a list of the column names.
     * 
     * @return A comma-separated list of column names and data types in UTF-8
     * format. The column list conforms to the syntax of the column list in a
     * CREATE TABLE statement. Names of any fixed-length columns appear before
     * names of any variable-length columns.
     * <p>
     * The number of columns equals the number of fixed-length columns plus the
     * number of variable-length columns.
     */
    Vector<String> getColNames();

}
