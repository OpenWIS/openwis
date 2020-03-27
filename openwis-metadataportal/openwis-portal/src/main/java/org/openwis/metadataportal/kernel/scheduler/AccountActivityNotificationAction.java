package org.openwis.metadataportal.kernel.scheduler;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.kernel.scheduler.filters.AccountFilter;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.user.dto.UserActions;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Send an email to users warning them about inactivity.
 */
public class AccountActivityNotificationAction implements AccountAction {

    private final Dbms dbms;
    private final IOpenWISMail mail;
    private final AccountFilter[] filters;

    public AccountActivityNotificationAction(Dbms dbms, AccountFilter[] filters, IOpenWISMail mail) {
        this.dbms = dbms;
        this.filters = filters;
        this.mail = mail;
    }

    @Override
    public void doAction() {

        Log.info(Log.SCHEDULER, "Start account activity notification action");
        MailUtilities mailUtilities = new MailUtilities();
        UserManager um = new UserManager(this.dbms);
        try {
            List<User> filteredUsers = um.getAllUsers();
            for (AccountFilter filter : filters) {
                filteredUsers = filter.filter(filteredUsers);
            }

            Log.info(Log.SCHEDULER, String.format("Found %d users which will be notified about their inactivity.", filteredUsers.size()));
            for (User user : filteredUsers) {
                Log.info(Log.SCHEDULER, "Activity notification mail sent to " + user.getUsername());

                this.mail.setDestinations(new String[]{user.getEmailContact()});
                mailUtilities.send(this.mail);
                this.saveActionToLog(dbms, user);
            }
        } catch (Exception e) {
            Log.error(Log.SCHEDULER, e.getMessage());
        }
        Log.info(Log.SCHEDULER, "Finished account activity notification action");
    }

    private void saveActionToLog(Dbms dbms, User user) throws SQLException {
        String query = "INSERT INTO user_log(date, username, action, actioner, attribute) Values(?,?,?,?,?)";
        dbms.execute(query,
                Timestamp.valueOf(LocalDateTime.now()),
                user.getUsername(),
                UserActions.INACTIVITY_NOTIFICATION_MAIL.name(),
                "administrator",
                "");
        Log.debug(LoginConstants.LOG, "Insert into user_log " + UserActions.INACTIVITY_NOTIFICATION_MAIL.name() + " for " + user.getUsername() );
    }
}
