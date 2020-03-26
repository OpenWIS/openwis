package org.openwis.metadataportal.kernel.scheduler;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import org.openwis.metadataportal.kernel.scheduler.filters.AccountFilter;
import org.openwis.metadataportal.kernel.scheduler.filters.ActiveAccountFilter;
import org.openwis.metadataportal.kernel.scheduler.filters.LastLoginFilter;
import org.openwis.metadataportal.kernel.scheduler.filters.ProfileFilter;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;
import org.openwis.metadataportal.services.util.mail.OpenWISMailFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccountTaskFactory {

    public static AccountTask buildAccountLockTask(ServiceContext context, Dbms dbms, Integer duration, TimeUnit timeUnit) {
        AccountFilter[] filters = new AccountFilter[]{
                new ProfileFilter("user"),
                new ActiveAccountFilter(),
                new LastLoginFilter(duration, timeUnit)
        };

        Map<String, Object> mailContent = new HashMap<>();
        mailContent.put("duration",duration);
        mailContent.put("timeUnit", timeUnit.toString());

        // Create a partial mail without destination. Destination address will be set be the task for each user.
        IOpenWISMail mail = OpenWISMailFactory.buildAccountTerminationMail(context, "subject", null,mailContent);

        AccountAction accountAction = new AccountLockAction(dbms, filters, mail);
        return new AccountTask(accountAction);

    }
}
