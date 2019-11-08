/**
 * 
 */
package org.openwis.metadataportal.kernel.user;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class UserAlreadyExistsException extends Exception {
    
    /**
     * Comment for <code>userName</code>
     */
    private String userName;
    
    /**
     * Default constructor.
     * Builds a InvalidDataPolicyNameException.
     */
    public UserAlreadyExistsException(String userName) {
        super();
        this.userName = userName;
    }

    /**
     * Gets the userName.
     * @return the userName.
     */
    public String getUserName() {
        return userName;
    }

}
