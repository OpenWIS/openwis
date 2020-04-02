package org.openwis.metadataportal.kernel.scheduler;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import org.openwis.management.alert.AlertService;
import org.openwis.metadataportal.kernel.scheduler.filters.*;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;
import org.openwis.metadataportal.services.util.mail.OpenWISMailFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccountTaskFactory {

    public static AccountTask buildAccountLockTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer duration, TimeUnit timeUnit) {
        AccountFilter[] filters = new AccountFilter[]{
                new ProfileFilter("user"),
                new ActiveAccountFilter(),
                new NotifiedUserFilter(dbms),
                new LastLoginFilter(duration, timeUnit)
        };

        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("duration",duration);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());

        // Create a partial mail without destination. Destination address will be set be the task for each user.
        IOpenWISMail mail = OpenWISMailFactory.buildAccountTerminationMail(context, "subject", null,mailContent);

        AccountAction accountAction = new AccountLockAction(dbms, alertService, filters, mail);
        return new AccountTask(accountAction);
    }

    public static AccountTask buildAccountActivityNotificationTask(ServiceContext context, Dbms dbms, AlertService alertService, Integer duration, TimeUnit timeUnit) {
        AccountFilter[] filters = new AccountFilter[]{
                new ProfileFilter("user"),
                new ActiveAccountFilter(),
                new UnnotifiedUserFilter(dbms),
                new LastLoginFilter(duration, timeUnit)
        };

        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("duration",duration);
        mailContent.put("timeUnit", timeUnit.toString().toLowerCase());

        // Create a partial mail without destination. Destination address will be set be the task for each user.
        IOpenWISMail mail = OpenWISMailFactory.buildAccountDisabledMail(context, "subject", null,mailContent);

        AccountAction accountAction = new AccountActivityNotificationAction(dbms, alertService, filters, mail);
        return new AccountTask(accountAction);
    }
}
