package org.openwis.metadataportal.kernel.user;

import jeeves.utils.Log;
import org.fao.geonet.constants.Geonet;

import java.util.HashMap;
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
        Log.info(Geonet.OPENWIS, String.format("Register user: %s with session ID: %s",username, sessionId));
        if (userSessions.containsKey(username)) {
            return userSessions.get(username);
        }
        userSessions.put(username, sessionId);
        return sessionId;
    }

    @Override
    public synchronized void unregisterUser(String username) {
            Log.info(Geonet.OPENWIS, "Unregister user: " + username);
            userSessions.remove(username);
    }

    @Override
    public void unregisterSession(String sessionId) {
        String username = "";
        for(Map.Entry<String,String> entry: userSessions.entrySet()) {
            if (entry.getValue().equals(sessionId)) {
                username = entry.getKey();
                break;
            }
        }

        if (!username.isEmpty()) {
            userSessions.remove(username);
        }
    }
}
