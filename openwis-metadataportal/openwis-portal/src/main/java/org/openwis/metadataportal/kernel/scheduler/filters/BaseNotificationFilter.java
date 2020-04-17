package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.exceptions.BadInputEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            List<UserLogDTO> logs = this.getUserLogs(dbms);
            for (User user : users) {
                // shift last login
                LocalDateTime shiftedDate = this.shiftDate(user, this.period, this.timeUnit);

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

    private List<UserLogDTO> getUserLogs(Dbms dbms) throws SQLException, BadInputEx {
        String query = "SELECT * from user_log;";
        List<Element> elements = dbms.select(query).getChildren();
        if (elements.size() == 0) {
            return new ArrayList<>();
        }

        List<UserLogDTO> results = new ArrayList<>();
        for (Element element : elements) {
            UserLogDTO log = new UserLogDTO();
            log.setId(Util.getParamAsInt(element, "id"));
            log.setDate(LocalDateTime.parse(Util.getParam(element, "date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            log.setAction(UserAction.valueOf(StringUtils.upperCase(Util.getParam(element, "action"))));
            log.setAttribute(Util.getParam(element, "attribute", ""));
            log.setUsername(Util.getParam(element, "username"));
            log.setActioner(Util.getParam(element, "actioner"));
            results.add(log);
        }

        return results;
    }
}
