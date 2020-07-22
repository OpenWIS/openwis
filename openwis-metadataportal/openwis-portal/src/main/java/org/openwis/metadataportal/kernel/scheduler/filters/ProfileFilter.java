package org.openwis.metadataportal.kernel.scheduler.filters;

import org.openwis.metadataportal.model.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileFilter implements AccountFilter {

    private final List<String> profiles = new ArrayList<>();

    public ProfileFilter(String profile) {
        this.profiles.add(profile);
    }

    public ProfileFilter(String[] profiles) {
        this.profiles.addAll(Arrays.asList(profiles));
    }

    @Override
    public List<User> filter(List<User> users) {
        return users.stream()
                .filter(u -> hasProfile(u.getProfile()))
                .collect(Collectors.toList());
    }

    private boolean hasProfile(String profile) {
        return this.profiles.stream().anyMatch(p -> p.toLowerCase().equals(profile.toLowerCase()));
    }
}
