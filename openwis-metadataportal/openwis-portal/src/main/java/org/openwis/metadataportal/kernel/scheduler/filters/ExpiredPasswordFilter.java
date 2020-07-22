package org.openwis.metadataportal.kernel.scheduler.filters;

import org.openwis.metadataportal.model.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to filter the users with an expired password
 */
public class ExpiredPasswordFilter implements AccountFilter{

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user: users) {
            if (LocalDateTime.now().isAfter(user.getPwdExpireTime())) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}
