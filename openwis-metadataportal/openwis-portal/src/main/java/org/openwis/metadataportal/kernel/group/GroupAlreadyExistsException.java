/**
 * 
 */
package org.openwis.metadataportal.kernel.group;

/**
 * Group Already Exists Exception. <P>
 * 
 */
public class GroupAlreadyExistsException extends Exception {
    
    /**
     * Comment for <code>name</code>
     */
    private String name;
    
    /**
     * Default constructor.
     * Builds a InvalidDataPolicyNameException.
     * @param name The group name.
     */
    public GroupAlreadyExistsException(String name) {
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
