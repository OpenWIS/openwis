package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.exceptions.BadInputEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * It filters out the user which have not been notified.
 */
public class NotifiedUserFilter  implements AccountFilter{

    private final Dbms dbms;

    public NotifiedUserFilter(Dbms dbms) {
        this.dbms = dbms;
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        try {
            LogFilter logFilter = new LogFilter();
            List<UserLogDTO> logs = logFilter.getLogs(dbms);
            for (User user : users) {
                UserLogDTO lastNotificationEntry = logFilter.getLastLogEntry(logs, user, UserAction.INACTIVITY_NOTIFICATION_MAIL);
                if (lastNotificationEntry != null) {
                    if (user.getLastLogin().isBefore(lastNotificationEntry.getDate())) {
                        Log.debug(Log.SCHEDULER, String.format("%s: Found notified user: %s. Last notification was: %s. User is passing the filter.",
                                NotifiedUserFilter.class.getSimpleName(),
                                user.getUsername(),
                                lastNotificationEntry.getDate().toString()));
                        filteredUsers.add(user);
                    }
                }
            }
        } catch (SQLException | BadInputEx ex) {
            Log.error(Log.SCHEDULER, ex);
        }
        return filteredUsers;
    }
}
