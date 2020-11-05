package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.exceptions.BadInputEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.util.UserLogUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class BaseNotificationFilter {

    private final Dbms dbms;
    private final UserAction action;
    private final int period;
    private final TimeUnit timeUnit;

    public BaseNotificationFilter(Dbms dbms, UserAction action, int period, TimeUnit timeUnit) {
        this.dbms = dbms;
        this.action = action;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    public abstract LocalDateTime shiftDate(User user, int period, TimeUnit timeUnit);

    public List<User> filter(List<User> users) {
        List<User> filteredUsers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        try {
            List<UserLogDTO> logs = UserLogUtils.getLogs(dbms);
            for (User user : users) {
                // shift last login
                LocalDateTime shiftedDate = this.shiftDate(user, this.period, this.timeUnit);
                if (shiftedDate == null) {
                    Log.warning(Log.SCHEDULER, "Shift date null for user: %s. Skip it" + user.getUsername());
                    continue;
                }
                // if now < shifted date continue
                if (shiftedDate.isAfter(now)) {
                    continue;
                }

                // look for a inactivity notification log after shiftedDate
                List<UserLogDTO> notificationLog = logs.stream()
                        .filter(l -> l.getAction().equals(this.action))
                        .filter(l -> l.getUsername().equals(user.getUsername()))
                        .filter(l -> l.getDate().isAfter(shiftedDate))
                        .collect(Collectors.toList());

                if (notificationLog.size() == 0) {
                    Log.debug(Log.SCHEDULER, String.format("%s: Found user not notified: %s. User is passing the filter.",
                            BaseNotificationFilter.class.getSimpleName(),
                            user.getUsername()));
                    filteredUsers.add(user);
                }
            }
        } catch (SQLException | BadInputEx ex) {
            Log.error(Log.SCHEDULER, ex);
        }
        return filteredUsers;
    }
}
