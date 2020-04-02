package org.openwis.metadataportal.kernel.scheduler;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.kernel.scheduler.filters.AccountFilter;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserActions;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.UserLogUtils;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class lock the accounts.
 */
public class AccountLockAction implements AccountAction {

    private final IOpenWISMail mail;
    private final Dbms dbms;
    private final AccountFilter[] filters;

    /**
     * Default constructor
     * @param dbms db
     * @param filters list of filters to be applied on the list of users from db
     * @param mail partial mail. It does not contain the destination address. It will be set for each filtered user.
     */
    public AccountLockAction(Dbms dbms, AccountFilter[] filters, IOpenWISMail mail) {
        this.filters = filters;
        this.dbms = dbms;
        this.mail = mail;
    }

    @Override
    public void doAction() {
        UserManager um = new UserManager(dbms);
        MailUtilities mailUtilities = new MailUtilities();

        Log.info(Log.SCHEDULER, "Start account lock action");
        try {
            List<User> filteredUsers = um.getAllUsers();
            for (AccountFilter filter: filters) {
                filteredUsers = filter.filter(filteredUsers);
            }

            Log.info(Log.SCHEDULER, String.format("Found %d users which their account will be locked due to inactivity.", filteredUsers.size()));
            for (User user: filteredUsers) {
                um.lockUser(user.getUsername(), true);
                Log.info(Log.SCHEDULER, "Account locked due to inactivity for user " + user.getUsername());
                this.saveLog(user);

                // send email to user
                this.mail.setDestinations(new String[]{user.getEmailContact()});
                mailUtilities.send(this.mail);
            }
        } catch (Exception e) {
            Log.error(Log.SCHEDULER, e.getMessage());
        }
        Log.info(Log.SCHEDULER, "Account lock action finished");
    }

    private void saveLog(User user) {

        // save log
        UserLogDTO userActionLogDTO = new UserLogDTO();
        userActionLogDTO.setAction(UserActions.LOCK);
        userActionLogDTO.setDate(LocalDateTime.now());
        userActionLogDTO.setUsername(user.getUsername());
        userActionLogDTO.setActioner("admin");
        try {
            UserLogUtils.saveLog(dbms, userActionLogDTO);
        } catch (SQLException e) {
            Log.warning(Log.SCHEDULER, e);
        }
    }
}
