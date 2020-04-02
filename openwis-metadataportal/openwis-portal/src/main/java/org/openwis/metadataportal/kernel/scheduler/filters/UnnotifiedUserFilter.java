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
 * It filters out the user which have been notified.
 */
public class UnnotifiedUserFilter extends AbstractNotificationFilter implements AccountFilter{

    public UnnotifiedUserFilter(Dbms dbms) {
        super(dbms);
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        try {
            List<UserLogDTO> logs = getUserLogs(this.getDbms());
            for (User user : users) {
                UserLogDTO lastNotificationLog = getLastNotification(logs, user);
                if (lastNotificationLog == null) {
                    Log.debug(Log.SCHEDULER, String.format("%s: Found user not notified: %s. User is passing the filter.",
                            UnnotifiedUserFilter.class.getSimpleName(),
                            user.getUsername()));
                    filteredUsers.add(user);
                } else {
                    if (user.getLastLogin().isAfter(lastNotificationLog.getDate())) {
                        Log.debug(Log.SCHEDULER, String.format("%s: Found user not notified: %s. Last notification was: %s.",
                                UnnotifiedUserFilter.class.getSimpleName(),
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
