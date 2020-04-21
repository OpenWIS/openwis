package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.resources.dbms.Dbms;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This filter filters out the users who have been already notified.
 *
 * For each user, an account locked notification is searched between
 * <p> (now - period * timeUnit, now)</p>.
 * If such a notification do not exists, meaning that the user has not been notified yet about locking out of his account,
 * the user is passing the filter.
 */
public class AccountLockedNotificationFilter implements AccountFilter {

    private final Dbms dbms;

    public AccountLockedNotificationFilter(Dbms dbms) {
        this.dbms = dbms;
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user: users) {
            if (countNotifications(user) == 0) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    private int countNotifications(User user) {
        String query = new StringBuilder().append("select * from user_log ")
                .append(String.format("where username = '%s' and action = '%s';", user.getUsername(), UserAction.ACCOUNT_LOCKED_NOTIFICATION_MAIL)).toString();

        try {
            return this.dbms.select(query).getChildren().size();
        } catch (SQLException e) {
           return 0;
        }
    }
}
