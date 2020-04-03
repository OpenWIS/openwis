package org.openwis.metadataportal.kernel.scheduler.actions;

import org.openwis.metadataportal.model.user.User;

public interface AccountAction {

    /**
     * Do some action on a list of users.
     */
    public void doAction(User user);
}
