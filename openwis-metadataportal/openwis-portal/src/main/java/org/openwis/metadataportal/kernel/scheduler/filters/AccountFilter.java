package org.openwis.metadataportal.kernel.scheduler.filters;

import org.openwis.metadataportal.model.user.User;

import java.util.List;

public interface AccountFilter {

    /**
     * Filter a list of users
     * @param users list of users
     * @return a filtered list of user
     */
    public List<User> filter(List<User> users);
}
