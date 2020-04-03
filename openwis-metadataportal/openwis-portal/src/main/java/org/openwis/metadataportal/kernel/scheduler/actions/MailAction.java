package org.openwis.metadataportal.kernel.scheduler.actions;

import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;

public class MailAction implements AccountAction {

    private final IOpenWISMail mail;

    public MailAction(IOpenWISMail mail) {
        this.mail = mail;
    }

    @Override
    public void doAction(User user) {
        MailUtilities mailUtilities = new MailUtilities();
        this.mail.setDestinations(new String[]{user.getEmailContact()});
        this.mail.addContentVariable("firstname", user.getName());
        this.mail.addContentVariable("lastname", user.getSurname());
        mailUtilities.send(this.mail);
        Log.debug(Log.SCHEDULER, String.format("Mail sent to [%s]", user.getEmailContact()));
    }
}