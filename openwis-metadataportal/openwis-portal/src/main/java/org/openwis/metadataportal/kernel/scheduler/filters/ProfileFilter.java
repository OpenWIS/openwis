package org.openwis.metadataportal.kernel.scheduler.filters;

import org.openwis.metadataportal.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileFilter implements AccountFilter {

    private final String profile;

    public ProfileFilter(String profile) {
        this.profile = profile;
    }

    @Override
    public List<User> filter(List<User> users) {
        return users.stream()
                .filter(u -> u.getProfile().toLowerCase().equals(profile))
                .collect(Collectors.toList());
    }
}
