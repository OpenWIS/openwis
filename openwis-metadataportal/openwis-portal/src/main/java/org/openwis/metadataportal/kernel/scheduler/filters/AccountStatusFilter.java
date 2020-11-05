package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;

import java.util.ArrayList;
import java.util.List;
import org.openwis.securityservice.InetUserStatus;

/**
 * This class filters the account which are locked.
 */
public class AccountStatusFilter implements AccountFilter{

    private final InetUserStatus accountStatus;

    public AccountStatusFilter(InetUserStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user: users) {
            if (user.getInetUserStatus() == this.accountStatus) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }
}
