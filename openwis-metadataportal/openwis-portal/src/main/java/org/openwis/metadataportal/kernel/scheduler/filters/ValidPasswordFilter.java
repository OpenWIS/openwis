package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.utils.Log;
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
                Log.debug(Log.SCHEDULER,String.format("User [%s] has a valid password", user.getUsername()));
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}
