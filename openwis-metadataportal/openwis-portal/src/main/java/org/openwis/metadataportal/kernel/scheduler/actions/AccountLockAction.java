package org.openwis.metadataportal.kernel.scheduler.actions;

import jeeves.utils.Log;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;

/**
 * This class lock the accounts.
 */
public class AccountLockAction implements AccountAction {

    private UserManager userManager;

    /**
     * Default constructor
     */
    public AccountLockAction(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void doAction(User user) {
        try {
            userManager.lockUser(user.getUsername(), true);
            Log.info(Log.SCHEDULER, "Account locked due to inactivity for user " + user.getUsername());

        } catch (Exception e) {
            Log.error(Log.SCHEDULER, e.getMessage());
        }
    }
}
