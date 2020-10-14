package org.openwis.metadataportal.kernel.scheduler.actions;

import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;

/**
 * General mail action.
 * This class sends a mail to user.
 */
public class AdminMailAction implements AccountAction {

    private final IOpenWISMail mail;

    public AdminMailAction(IOpenWISMail mail) {
        this.mail = mail;
    }

    @Override
    public void doAction(User user) {
        MailUtilities mailUtilities = new MailUtilities();

        this.mail.addContentVariable("firstname", user.getName());
        this.mail.addContentVariable("lastname", user.getSurname());
        mailUtilities.send(this.mail);
        Log.debug(Log.SCHEDULER, String.format("Mail sent to [%s]", this.mail.getDestinations()[0]));
    }
}
