package org.openwis.metadataportal.kernel.scheduler.filters;

import org.openwis.metadataportal.model.user.User;

import java.util.ArrayList;
import java.util.List;
import org.openwis.securityservice.InetUserStatus;

/**
 * This class filters the account which are locked.
 */
public class ActiveAccountFilter implements AccountFilter{

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user: users) {
            if (user.getInetUserStatus() == InetUserStatus.ACTIVE) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }
}
