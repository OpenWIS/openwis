package org.openwis.metadataportal.kernel.user;

import jeeves.server.UserSession;

/**
 * Handle multiple log in tentative of the user into portal.
 */
public interface UserSessionManager {

    /**
     * Return true if the user is already logged in portal
     * @param username
     * @return sessionId if the username is logged. Return empty string otherwise
     */
    public String getUserSessionId(String username);

    /**
     * Add user to logged users when is logged for the first time
     * @param username
     * @param sessionId
     * @return sessionId. If the user is already logged in return the old sessionId
     */
    public String registerUser(String username, String sessionId);

    /**
     * Log out the user. Remove it from the logged users.
     * @param username
     */
    public void unregisterUser(String username);

    /**
     * Remove entry when session is destroyed
     * @param sessionId
     */
    public void unregisterSession(String sessionId);


}
