package org.openwis.metadataportal.kernel.scheduler;

import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.login.LoginConstants;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class to filter a list of users based on the date of their last login.
 * A user is passing the filter if the timestamp of their last login is greater than timestamp now - duration * tileUnit
 */
public class LastLoginFilter implements UserFilter {

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
        long now = System.currentTimeMillis();
        long threshold = now - this.timeUnit.toMillis(this.duration);

        // filtered users
        for (User user: users) {
            if (this.timestampToMillis(user.getLastLogin()) > threshold) {
                Log.debug(LoginConstants.LAST_LOGIN_FILTER, "Last login of user " + user.getUsername() + " is greater than threshold." );
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    private long timestampToMillis(Timestamp timestamp) {
        return TimeUnit.MILLISECONDS.convert(timestamp.getNanos(), TimeUnit.NANOSECONDS);
    }
}
