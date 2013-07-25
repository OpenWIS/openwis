/**
 * 
 */
package org.openwis.metadataportal.kernel.datapolicy;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class InvalidDataPolicyNameException extends Exception {
    
    /**
     * Comment for <code>name</code>
     */
    private String name;
    
    /**
     * Default constructor.
     * Builds a InvalidDataPolicyNameException.
     */
    public InvalidDataPolicyNameException(String name) {
        super();
        this.name = name;
    }

    /**
     * Gets the name.
     * @return the name.
     */
    public String getName() {
        return name;
    }

}
