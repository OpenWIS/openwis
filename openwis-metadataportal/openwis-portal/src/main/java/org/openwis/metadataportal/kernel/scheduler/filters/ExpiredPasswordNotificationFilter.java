package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.resources.dbms.Dbms;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * This filter filters out the users who have been already notified about the expiration of their password
 * <p>The user is passing the filter if there is <b>not</b> a notification <b>after</b> the pwd expiration date</p>
 */
public class ExpiredPasswordNotificationFilter extends BaseNotificationFilter implements AccountFilter {
    public ExpiredPasswordNotificationFilter(Dbms dbms) {
        super(dbms, UserAction.PASSWORD_EXPIRED_NOTIFICATION_MAIL, 0, TimeUnit.DAYS);
    }

    @Override
    public LocalDateTime shiftDate(User user, int period, TimeUnit timeUnit) {
        // do not shift the pwd expiration date.
        return user.getPwdExpireTime();
    }
}
