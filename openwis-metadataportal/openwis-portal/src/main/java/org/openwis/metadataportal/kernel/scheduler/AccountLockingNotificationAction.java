package org.openwis.metadataportal.kernel.scheduler;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;

import java.util.List;

/**
 * Send an email to users warning them about inactivity.
 */
public class AccountLockingNotificationAction implements AccountAction {

    private final Dbms dbms;
    private final AccountFilter filter;
    private final IOpenWISMail mail;

    public AccountLockingNotificationAction(Dbms dbms, AccountFilter filter, IOpenWISMail mail) {
        this.dbms = dbms;
        this.filter = filter;
        this.mail = mail;
    }
    @Override
    public void doAction() {

        MailUtilities mailUtilities = new MailUtilities();
        UserManager um = new UserManager(this.dbms);
        try {
            List<User> users = um.getAllUsers();
            List<User> filteredUsers = this.filter.filter(users);
            for (User user: filteredUsers) {
                um.lockUser(user.getUsername(), true);
                Log.debug(Log.SCHEDULER, "Account locked due to inactivity for user " + user.getUsername());

                this.mail.setDestinations(new String[]{user.getEmailContact()});
                mailUtilities.send(this.mail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
