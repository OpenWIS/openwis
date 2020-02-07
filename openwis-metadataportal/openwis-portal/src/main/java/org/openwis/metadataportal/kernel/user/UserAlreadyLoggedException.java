package org.openwis.metadataportal.kernel.user;

/**
 * Exception thrown when a user is trying to logged more than one
 */
public class UserAlreadyLoggedException extends Exception {

    /**
     * Comment for <code>userName</code>
     */
    private String userName;

    /**
     * Default constructor.
     */
    public UserAlreadyLoggedException(String userName) {
        super(String.format("User %s is already logged in from another session.", userName));
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
