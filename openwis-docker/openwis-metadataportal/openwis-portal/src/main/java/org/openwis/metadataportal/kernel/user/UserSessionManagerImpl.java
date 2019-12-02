package org.openwis.metadataportal.kernel.user;

import org.openwis.metadataportal.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of
 */
public class UserSessionManagerImpl implements UserSessionManager {
    private List<String> loggedUsers = new ArrayList<>();

    public UserSessionManagerImpl() {}

    @Override
    public Boolean isUserAlreadyLogged(String userName) {
        return findUser(userName) != null;
    }

    @Override
    public synchronized void registerUser(String username) throws UserAlreadyLoggedException {

        if (this.findUser(username) != null) {
            throw new UserAlreadyLoggedException(username);
        }

        loggedUsers.add(username);
    }

    @Override
    public synchronized void unregisterUser(String username) {
        String loggedUser = this.findUser(username); // should be != null
        loggedUsers.remove(loggedUser);
    }

    /**
     * return the user or null
     * @param username
     * @return user or null
     */
    private String findUser(String username) {
        for(String u: loggedUsers) {
            if (u.equals(username)) {
                return u;
            }
        }

        return null;
    }
}
