/**
 * 
 */
package org.openwis.metadataportal.kernel.common;

import java.text.SimpleDateFormat;

import jeeves.resources.dbms.Dbms;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public abstract class AbstractManager {

    /**
     * Comment for <code>dbms</code>
     */
    private Dbms dbms;
    
    private SimpleDateFormat sdf; 
    
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Default constructor.
     * Builds a AbstractManager.
     * @param dbms
     */
    public AbstractManager(Dbms dbms) {
        super();
        this.dbms = dbms;
    }

    /**
     * Gets the dbms.
     * @return the dbms.
     */
    public Dbms getDbms() {
        return dbms;
    }

    /**
     * Sets the dbms.
     * @param dbms the dbms to set.
     */
    public void setDbms(Dbms dbms) {
        this.dbms = dbms;
    }

    /**
     * Gets the sdf.
     * @return the sdf.
     */
    public SimpleDateFormat getSdf() {
        if(sdf == null) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        }
        return sdf;
    }
    
    
}
