package org.openwis.metadataportal.kernel.scheduler;

import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class to filter a list of users based on the date of their last login.
 * A user is passing the filter if the timestamp of their last login is greater than timestamp now - duration * tileUnit
 */
public class LastLoginFilter implements AccountFilter {

    // Represents the duration in timeUnits from the last login date for which the user passe the filter
    private final Integer duration;

    // time unit
    private final TimeUnit timeUnit;

    public LastLoginFilter(Integer duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();

        // compute threshold
        OffsetDateTime now = LocalDateTime.now().atOffset(ZoneOffset.UTC);
        OffsetDateTime threshold = now.minus(duration, ChronoUnit.valueOf(timeUnit.toString()));
        Log.debug(Log.SCHEDULER, String.format("Threshold is: %s", threshold.format(DateTimeFormatter.ISO_DATE_TIME)));

        // filtered users
        for (User user : users) {
            if (user.getProfile().toLowerCase().equals("admin") || user.getProfile().toLowerCase().equals("administrator")) {
                continue;
            }

            Log.debug(Log.SCHEDULER, String.format("User %s. Last login: %s", user.getUsername(), user.getLastLogin().toLocalDateTime().atOffset(ZoneOffset.UTC)));

            if (user.getLastLogin().toLocalDateTime().atOffset(ZoneOffset.UTC).isBefore(threshold)) {
                Log.debug(Log.SCHEDULER, String.format("User: %s. Last login: %s. Threshold: %s",
                        user.getUsername(),
                        user.getLastLoginAsString(),
                        threshold.toString()));
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}
