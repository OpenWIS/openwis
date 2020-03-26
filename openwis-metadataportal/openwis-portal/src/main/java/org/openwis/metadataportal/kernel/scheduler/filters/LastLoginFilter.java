package org.openwis.metadataportal.kernel.scheduler.filters;

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
 * All date time are computed in UTC. A user is passing the filter if the last login is before the threshold (meaning is older).
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
        Log.debug(Log.SCHEDULER, String.format("%s : Threshold is: %s", LastLoginFilter.class.getSimpleName(), threshold.format(DateTimeFormatter.ISO_DATE_TIME)));

        // filtered users
        for (User user : users) {
            Log.debug(Log.SCHEDULER, String.format("%s : User %s. Last login: %s",
                    LastLoginFilter.class.toString(),
                    user.getUsername(),
                    user.getLastLogin().toLocalDateTime().atOffset(ZoneOffset.UTC)));

            if (user.getLastLogin().toLocalDateTime().atOffset(ZoneOffset.UTC).isBefore(threshold)) {
                Log.debug(Log.SCHEDULER, String.format("%s : User: %s. Last login: %s. Threshold: %s",
                        LastLoginFilter.class.getSimpleName(),
                        user.getUsername(),
                        user.getLastLoginAsString(),
                        threshold.toString()));
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}
