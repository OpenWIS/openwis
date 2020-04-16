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
 * This class filtered the users whom password will expire soon and they have not been notified about it.
 */
public class PasswordNotificationFilter implements AccountFilter {

    private Dbms dbms;

    public PasswordNotificationFilter(Dbms dbms) {
        this.dbms = dbms;
    }

    @Override
    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        LogFilter logFilter = new LogFilter();
        try {
            List<UserLogDTO> logs = logFilter.getLogs(this.dbms);
            for (User user : users) {
                UserLogDTO lastNotificationLog = logFilter.getLastLogEntry(logs,user, UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL);
                if (lastNotificationLog == null) {
                    Log.debug(Log.SCHEDULER, String.format("%s: Found user not notified: %s. User is passing the filter.",
                            PasswordNotificationFilter.class.getSimpleName(),
                            user.getUsername()));
                    filteredUsers.add(user);
                } else {
                    if (user.getPwdChangedTime().isAfter(lastNotificationLog.getDate())) {
                        Log.debug(Log.SCHEDULER, String.format("%s: Found user not notified: %s. Last notification was: %s.",
                                PasswordNotificationFilter.class.getSimpleName(),
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
