package org.openwis.metadataportal.kernel.scheduler;

import jeeves.utils.Log;
import org.openwis.metadataportal.kernel.scheduler.actions.AccountAction;
import org.openwis.metadataportal.kernel.scheduler.filters.AccountFilter;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.securityservice.UserManagementException_Exception;
import java.util.List;

/**
 * This task do some action on user accounts
 */
public class AccountTask implements Runnable {

    private final String name;
    private final UserManager userManager;
    private final List<AccountFilter> filters;
    private final List<AccountAction> actions;

    public AccountTask(String name, UserManager userManager, List<AccountFilter> filters, List<AccountAction> actions) {
        this.name = name;
        this.userManager = userManager;
        this.filters = filters;
        this.actions = actions;
    }

    @Override
    public void run() {
        Log.info(Log.SCHEDULER, String.format("=============== %s ===============", this.name));

        try {
            List<User> filteredUsers = userManager.getAllUsers();
            for (AccountFilter filter : filters) {
                Log.debug(Log.SCHEDULER, "Applying filter: " + filter.getClass().getSimpleName());
                filteredUsers = filter.filter(filteredUsers);
            }

            Log.info(Log.SCHEDULER, String.format("Found %d users", filteredUsers.size()));
            for (User user : filteredUsers) {
                for (AccountAction action : actions) {
                    Log.debug(Log.SCHEDULER, "Performing action: " + action.getClass().getSimpleName());
                    action.doAction(user);
                }
            }
            Log.info(Log.SCHEDULER, String.format("=============== Finished %s ===============", this.name));
        } catch (UserManagementException_Exception ex) {
            Log.error(Log.SCHEDULER, ex);
        }
    }
}
