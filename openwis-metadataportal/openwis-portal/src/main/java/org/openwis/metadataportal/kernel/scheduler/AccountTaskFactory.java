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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.openwis.securityservice.InetUserStatus;

public class AccountTaskFactory {

    /**
     * Creates an account task which locks out the user due to inactivity
     *
     * @param context
     * @param dbms
     * @param alertService
     * @param period       period between the last login the date when their account will be locked out.
     * @param timeUnit     unit of time for {@param period}
     * @return
     */
    public static AccountTask buildAccountInactivityLockTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        // Create filters
        List<AccountFilter> filters = Arrays.asList(
                new ProfileFilter(Profile.User.name()),
                new AccountStatusFilter(InetUserStatus.ACTIVE),
                new ValidPasswordFilter(),
                new LastLoginFilter(period, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        // lock action
        actions.add(new AccountLockAction(userManager));

        // create user mail action
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("period", period);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildAccountSuspensionMail(context, "Account.lock.subject", null, mailContent);
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

    /**
     * Creates an account task which notifies user about their inactivity
     *
     * @param context
     * @param dbms
     * @param alertService
     * @param period       period remains to account lock out due to inactivity
     * @param threshold    period between the last login the date when their account will be locked out.
     * @param timeUnit     unit of time for {@param period} {@param threshold}
     * @return
     */
    public static AccountTask buildAccountInactivityNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, Integer threshold, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        List<AccountFilter> filters = Arrays.asList(
                // users only
                new ProfileFilter(Profile.User.name()),
                // active accounts only
                new AccountStatusFilter(InetUserStatus.ACTIVE),
                // users with password not expired
                new ValidPasswordFilter(),
                // user who have not been notified yet
                new InactivityNotificationFilter(dbms, period - threshold, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("period", threshold);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildAccountInactivityNotificationMail(context, "Account.notification.subject", null, mailContent);
        actions.add(new MailAction(mail));

        // log action
        actions.add(new LogAction(dbms, UserAction.INACTIVITY_NOTIFICATION_MAIL));

        //alert action
        actions.add(new AlertAction(alertService, UserAction.INACTIVITY_NOTIFICATION_MAIL));

        return new AccountTask(String.format("AccountInactivity: T - %d %s", threshold, timeUnit.toString()), userManager, filters, actions);
    }

    /**
     * Creates a task which will notify users that their password is about to expire.
     *
     * @param context
     * @param dbms
     * @param alertService
     * @param period       period of time to password expiration
     * @param timeUnit     unit of time of {@param period}
     * @return Account task
     */
    public static AccountTask buildPasswordExpireNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer period, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        List<AccountFilter> filters = Arrays.asList(
                // only users
                new ProfileFilter("user"),
                // users with an active account
                new AccountStatusFilter(InetUserStatus.ACTIVE),
                // user without the password expired
                new ValidPasswordFilter(),
                // users who have not been notified yet
                new PasswordNotificationFilter(dbms, period, timeUnit)
        );

        List<AccountAction> actions = new ArrayList<>();
        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("duration", period);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());
        IOpenWISMail mail = OpenWISMailFactory.buildPasswordExpireNotificationMail(context, "Password.notification.subject", null, mailContent);
        actions.add(new PasswordNotificationMailAction(mail));

        // log action
        actions.add(new LogAction(dbms, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL));

        //alert action
        actions.add(new AlertAction(alertService, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL));

        return new AccountTask(String.format("Password expire notification task: Expire date - %d %s", period, timeUnit.toString()), userManager, filters, actions);
    }

    /**
     * Return a task which sends notification mail if the user has been locked out due to multiple failed logins
     *
     * @param context      context
     * @param dbms         dbms
     * @param alertService alert service
     * @param execPeriod   period between two executions of the task. Used to compute date_from filter
     * @param timeUnit     time unit of {@param execPeriod}
     * @return AccountTask
     */
    public static AccountTask buildLockedAccountNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer execPeriod, TimeUnit timeUnit) {
        UserManager userManager = new UserManager(dbms);
        List<AccountFilter> filters = Arrays.asList(
                // Only users
                new ProfileFilter("user"),
                // Only inactive accounts
                new AccountStatusFilter(InetUserStatus.INACTIVE),
                // only users who have not been notified yet
                new AccountLockedNotificationFilter(dbms),
                // only users with the account locked out due to multiple failed logins
                new FailedLoginFilter(LocalDateTime.now().minus(execPeriod, ChronoUnit.valueOf(timeUnit.toString())))
        );

        List<AccountAction> actions = new ArrayList<>();
        IOpenWISMail mail = OpenWISMailFactory.buildAccountSuspensionFailedLoginMail(context, "Account.locked.notification.subject", null, new HashMap<>());
        // user mail action
        actions.add(new MailAction(mail));

        // admin mail action
        IOpenWISMail adminMail = OpenWISMailFactory.buildAccountSuspensionAdminFailedLoginMail(context, "Account.locked.notification.subject", new HashMap<>());
        actions.add(new MailAction(adminMail));

        // log action
        actions.add(new LogAction(dbms, UserAction.ACCOUNT_LOCKED_NOTIFICATION_MAIL));

        return new AccountTask("Multiple failed login account task", userManager, filters, actions);
    }
}
