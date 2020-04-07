package org.openwis.metadataportal.kernel.scheduler.filters;

import org.openwis.metadataportal.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * This class filters the user with a valid password (not expired)
 */
public class ValidPasswordFilter implements AccountFilter {

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user: users) {
            if (user.getPwdChangedTime().isBefore(user.getPwdExpireTime())) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}
