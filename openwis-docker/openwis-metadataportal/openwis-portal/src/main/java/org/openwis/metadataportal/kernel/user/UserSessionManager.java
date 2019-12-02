package org.openwis.metadataportal.kernel.user;

/**
 * Handle multiple log in tentative of the user into portal.
 */
public interface UserSessionManager {

    /**
     * Return true if the user {@username} is already logged in portal
     * @param user user
     * @return true if user already logged in portal from other page
     */
    public Boolean isUserAlreadyLogged(String username);

    /**
     * Add user to logged users when is logged for the first time
     * @param username user name
     */
    public void registerUser(String username) throws UserAlreadyLoggedException;

    /**
     * Log out the user. Silently remove it from the logged users.
     * @param username
     */
    public void unregisterUser(String username);


}
