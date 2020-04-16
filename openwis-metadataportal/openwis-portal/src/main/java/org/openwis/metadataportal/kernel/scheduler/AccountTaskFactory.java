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

    public static AccountTask buildAccountInactivityLockTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        // Create filters
        List<AccountFilter> filters = Arrays.asList(
                new ProfileFilter(Profile.User.name()),
                new ActiveAccountFilter(),
                new NotifiedUserFilter(dbms),
                new ValidPasswordFilter(),
                new LastLoginFilter(period, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        // lock action
        actions.add(new AccountLockAction(userManager));
        // create mail action
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("period",period);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildAccountTerminationMail(context, "Account.lock.subject", null,mailContent);
        actions.add(new MailAction(mail));

        //log action
        actions.add(new LogAction(dbms, UserAction.LOCK));

        // alert action
        actions.add(new AlertAction(alertService, UserAction.LOCK));
        return new AccountTask("Account Lock Task", userManager, filters, actions);
    }

    public static AccountTask buildAccountInactivityNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        List<AccountFilter> filters = Arrays.asList(
                new ProfileFilter(Profile.User.name()),
                new ActiveAccountFilter(),
                new ValidPasswordFilter(),
                new NotificationFilter(dbms),
                new LastLoginFilter(period, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("period",period);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildAccountDisabledMail(context, "Account.notification.subject", null,mailContent);
        actions.add(new MailAction(mail));

        // log action
        actions.add(new LogAction(dbms, UserAction.INACTIVITY_NOTIFICATION_MAIL));

        //alert action
        actions.add(new AlertAction(alertService, UserAction.INACTIVITY_NOTIFICATION_MAIL));

        return new AccountTask("Account Inactivity Task", userManager, filters, actions);
    }

    public static AccountTask buildPasswordExpireNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer duration, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        List<AccountFilter> filters = Arrays.asList(
                new ProfileFilter("user"),
                new ActiveAccountFilter(),
                new ValidPasswordFilter(),
                new PasswordNotificationFilter(dbms)
        );

        List<AccountAction> actions = new ArrayList<>();
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("duration",duration);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildPasswordExpireNotificationMail(context, "Password.notification.subject", null,mailContent);
        actions.add(new PasswordNotificationMailAction(mail));

        // log action
        actions.add(new LogAction(dbms, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL));

        //alert action
        actions.add(new AlertAction(alertService, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL));

        return new AccountTask("Password expire notification task", userManager, filters, actions);
    }
}
