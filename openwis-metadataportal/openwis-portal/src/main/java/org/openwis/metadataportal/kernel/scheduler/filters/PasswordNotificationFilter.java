package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.resources.dbms.Dbms;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * This filter filters out the users who have been already notified.
 * <p>
 * Consider the following time line:
 *
 *         -7d      Now     -5d   -2d   -1d   pwdExpire
 * ------- | ------- x ----- | --- | --- | --- | --- |
 * </p>
 *
 * For each user the date of the password expiration is shifted with the {@param period}.
 * The filter checks if there is a notification between the present time and the shifted date.
 * <p>For example if {@param period} is 7d the filter will check if there are notification between
 * the present date and pwdExpire - 7d. If there is a notification the user is filtered out.</p>
 *
 * <p> If the present day is before the shifted date the user is filter out. </p>
 */
public class PasswordNotificationFilter extends BaseNotificationFilter implements AccountFilter {
    public PasswordNotificationFilter(Dbms dbms, int period, TimeUnit timeUnit) {
        super(dbms, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL, period, timeUnit);
    }

    @Override
    public LocalDateTime shiftDate(User user, int period, TimeUnit timeUnit) {
        ChronoUnit chronoUnit;
        switch (timeUnit) {
            case MINUTES:
                chronoUnit = ChronoUnit.MINUTES;
                break;
            case HOURS:
                chronoUnit = ChronoUnit.HOURS;
                break;
            default:
                chronoUnit = ChronoUnit.DAYS;
        }

        return user.getPwdExpireTime().minus(period, chronoUnit);
    }
}
