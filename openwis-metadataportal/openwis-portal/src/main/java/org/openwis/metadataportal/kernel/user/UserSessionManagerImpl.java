package org.openwis.metadataportal.kernel.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of
 */
public class UserSessionManagerImpl implements UserSessionManager {
    private Map<String,String> userSessions = new HashMap<>();

    public UserSessionManagerImpl() {}

    @Override
    public synchronized String getUserSessionId(String username) {
        if (userSessions.containsKey(username)) {
            return userSessions.get(username);
        }
        return "";
    }

    @Override
    public String registerUser(String username, String sessionId) {
        if (userSessions.containsKey(username)) {
            return userSessions.get(username);
        }
        userSessions.put(username, sessionId);
        return sessionId;
    }

    @Override
    public synchronized void unregisterUser(String username) {
            userSessions.remove(username);
    }
}
