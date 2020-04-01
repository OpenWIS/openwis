package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.exceptions.BadInputEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends {@link AbstractNotificationFilter}.
 * It filters out the user which have not been notified.
 */
public class NotifiedUserFilter extends AbstractNotificationFilter implements AccountFilter{

    public NotifiedUserFilter(Dbms dbms) {
        super(dbms);
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        try {
            List<UserLogDTO> logs = this.getUserLogs(this.getDbms());
            for (User user : users) {
                UserLogDTO lastNotificationLog = this.getLastNotification(logs, user);
                if (lastNotificationLog != null) {
                    if (user.getLastLogin().isBefore(fromTimestamp(lastNotificationLog.getDate()))) {
                        Log.debug(Log.SCHEDULER, String.format("%s: Found notified user: %s. Last notification was: %s. User is passing the filter.",
                                NotifiedUserFilter.class.getSimpleName(),
                                user.getUsername(),
                                lastNotificationLog.getDate().toString()));
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
