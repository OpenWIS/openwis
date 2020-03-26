package org.openwis.metadataportal.kernel.scheduler.filters;

import org.openwis.metadataportal.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileFilter implements AccountFilter {

    private final String profile;

    public ProfileFilter(String profile) {
        this.profile = profile;
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user: users) {
            if (user.getProfile().toLowerCase().equals(profile)) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}
