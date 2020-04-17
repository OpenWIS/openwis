package org.openwis.metadataportal.kernel.scheduler;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import org.openwis.management.alert.AlertService;
import org.openwis.metadataportal.kernel.scheduler.actions.*;
import org.openwis.metadataportal.kernel.scheduler.filters.*;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.Profile;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;
import org.openwis.metadataportal.services.util.mail.OpenWISMailFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AccountTaskFactory {

    /**
     * Return a task for inactivity lock
     */
    public static AccountTask buildAccountInactivityLockTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        // Create filters
        List<AccountFilter> filters = Arrays.asList(
                new ProfileFilter(Profile.User.name()),
                new ActiveAccountFilter(),
                new ValidPasswordFilter(),
                new LastLoginFilter(period, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        // lock action
        actions.add(new AccountLockAction(userManager));

        // create user mail action
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("period",period);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildAccountSuspensionMail(context, "Account.lock.subject", null,mailContent);
        actions.add(new MailAction(mail));

        // create admin mail action
        IOpenWISMail adminMail = OpenWISMailFactory.buildAccountSuspensionAdminMail(context, "Account.lock.subject", mailContent);
        actions.add(new MailAction(adminMail));

        //log action
        actions.add(new LogAction(dbms, UserAction.LOCK));

        // alert action
        actions.add(new AlertAction(alertService, UserAction.LOCK));
        return new AccountTask("Account Lock Task", userManager, filters, actions);
    }

    public static AccountTask buildAccountInactivityNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, Integer threshold, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        List<AccountFilter> filters = Arrays.asList(
                new ProfileFilter(Profile.User.name()),
                new ActiveAccountFilter(),
                new ValidPasswordFilter(),
                new InactivityNotificationFilter(dbms, UserAction.INACTIVITY_NOTIFICATION_MAIL, period -threshold, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("period",threshold);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildAccountInactivityNotificationMail(context, "Account.notification.subject", null,mailContent);
        actions.add(new MailAction(mail));

        // log action
        actions.add(new LogAction(dbms, UserAction.INACTIVITY_NOTIFICATION_MAIL));

        //alert action
        actions.add(new AlertAction(alertService, UserAction.INACTIVITY_NOTIFICATION_MAIL));

        return new AccountTask(String.format("AccountInactivity: T - %d %s", threshold, timeUnit.toString()), userManager, filters, actions);
    }

    public static AccountTask buildPasswordExpireNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        List<AccountFilter> filters = Arrays.asList(
                new ProfileFilter("user"),
                new ActiveAccountFilter(),
                new ValidPasswordFilter(),
                new PasswordNotificationFilter(dbms, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL, period, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("duration",period);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildPasswordExpireNotificationMail(context, "Password.notification.subject", null,mailContent);
        actions.add(new PasswordNotificationMailAction(mail));

        // log action
        actions.add(new LogAction(dbms, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL));

        //alert action
        actions.add(new AlertAction(alertService, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL));

        return new AccountTask(String.format("Password expire notification task: Expire date - %d %s", period, timeUnit.toString()), userManager, filters, actions);
    }
}
