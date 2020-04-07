package org.openwis.metadataportal.kernel.scheduler.actions;

import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;

import java.time.format.DateTimeFormatter;

/**
 * This class sends a mail to user following a password notification action.
 */
public class PasswordNotificationMailAction implements AccountAction {

    private final IOpenWISMail mail;

    public PasswordNotificationMailAction(IOpenWISMail mail) {
        this.mail = mail;
    }

    @Override
    public void doAction(User user) {
        MailUtilities mailUtilities = new MailUtilities();
        this.mail.setDestinations(new String[]{user.getEmailContact()});
        this.mail.addContentVariable("firstname", user.getName());
        this.mail.addContentVariable("lastname", user.getSurname());
        this.mail.addContentVariable("lastPasswordChange", user.getPwdChangedTime().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")));
        this.mail.addContentVariable("username", user.getUsername());
        mailUtilities.send(this.mail);
        Log.debug(Log.SCHEDULER, String.format("Mail sent to [%s]", user.getEmailContact()));
    }
}
