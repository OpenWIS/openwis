package org.openwis.metadataportal.kernel.scheduler;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;

import java.util.List;

/**
 * This class lock the accounts.
 */
public class AccountLockAction implements AccountAction {

    private final AccountFilter filter;
    private final IOpenWISMail mail;
    private final Dbms dbms;

    /**
     * Default constructor
     * @param dbms db
     * @param filter filter used to get a list of users which account will be locked
     * @param mail partial mail. It does not contain the destination address. It will be set for each filtered user.
     */
    public AccountLockAction(Dbms dbms, AccountFilter filter, IOpenWISMail mail) {
        this.filter = filter;
        this.dbms = dbms;
        this.mail = mail;
    }

    @Override
    public void doAction() {
        UserManager um = new UserManager(dbms);
        MailUtilities mailUtilities = new MailUtilities();

        Log.info(Log.SCHEDULER, "Start account lock action");
        try {
            List<User> users = um.getAllUsers();
            List<User> filteredUsers = this.filter.filter(users);
            for (User user: filteredUsers) {
//                um.lockUser(user.getUsername(), true);
                Log.info(Log.SCHEDULER, "Account locked due to inactivity for user " + user.getUsername());

                // send email to user
                this.mail.setDestinations(new String[]{user.getEmailContact()});
                //mailUtilities.send(this.mail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.info(Log.SCHEDULER, "Account lock action finished");
    }
}
