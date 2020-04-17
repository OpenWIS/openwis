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
 *        Last login         +83d      Now     +85d  +88d  +89d  +90d
 * ---------- X ------------- | ------- x ----- | --- | --- | --- | --- |
 * </p>
 *
 * For each user the date of the last login is shifted with the {@param period}.
 * The filter checks if there is a notification between the present time and the shifted date.
 * <p>For example if {@param period} is 83 the filter will check if there are notification between
 * the present date and last_login + 83d. If there is a notification the user is filtered out.</p>
 *
 * <p> If the present day is before the shifted date the user is filter out. </p>
 */
public class InactivityNotificationFilter extends BaseNotificationFilter implements AccountFilter {

    public InactivityNotificationFilter(Dbms dbms, UserAction action, int period, TimeUnit timeUnit) {
        super(dbms, action, period, timeUnit);
    }

    @Override
    public LocalDateTime shiftDate(User user, int period, TimeUnit timeUnit) {
        return user.getLastLogin().plus(period, ChronoUnit.valueOf(timeUnit.toString()));
    }


}
