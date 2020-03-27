package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.exceptions.BadInputEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserActions;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements basic logic to filter user on the fact that they have been already notified by mail about
 * their account activity or not.
 *
 * A notified user is a user which last login is older than a notification entry in USER_LOG table and its account
 * is active. This means that the user has been notified by email about his/her account activity.
 *
 * <b>Remark</b> We considered that the list of users contains only active users.
 */
public abstract class AbstractNotificationFilter {

    private final Dbms dbms;

    public AbstractNotificationFilter(Dbms dbms) {
        this.dbms = dbms;
    }

    public Dbms getDbms() {
        return dbms;
    }
    /**
     * Return the last mail notification of a user
     *
     * @param logs list of action logs
     * @param user user for which the notification is searced
     * @return last inactivity mail notification
     */
    protected UserLogDTO getLastNotification(List<UserLogDTO> logs, User user) {
        List<UserLogDTO> userLog = logs.stream()
                .filter(l -> l.getUsername().equals(user.getUsername()))
                .filter(l -> l.getAction().equals(UserActions.INACTIVITY_NOTIFICATION_MAIL))
                .sorted((l1, l2) -> dateComparator.compare(l1.getDate(), l2.getDate()))
                .collect(Collectors.toList());

        if (userLog.size() == 0) {
            return null;
        }

        return userLog.get(userLog.size() - 1);
    }

    protected List<UserLogDTO> getUserLogs(Dbms dbms) throws SQLException, BadInputEx {
        String query = "SELECT * from user_log;";
        List<Element> elements = dbms.select(query).getChildren();
        if (elements.size() == 0) {
            return new ArrayList<>();
        }

        List<UserLogDTO> results = new ArrayList<>();
        for (Element element : elements) {
            UserLogDTO log = new UserLogDTO();
            log.setId(Util.getParamAsInt(element, "id"));
            log.setDate(toTimestamp(Util.getParam(element, "date")));
            log.setAction(UserActions.valueOf(StringUtils.upperCase(Util.getParam(element, "action"))));
            log.setAttribute(Util.getParam(element, "attribute", ""));
            log.setUsername(Util.getParam(element, "username"));
            log.setActioner(Util.getParam(element, "actioner"));
            results.add(log);
        }

        return results;
    }

    protected Timestamp toTimestamp(String value) {
        ZonedDateTime zdt = LocalDateTime.parse(value).atZone(ZoneId.of("UTC"));
        return Timestamp.valueOf(zdt.toLocalDateTime());
    }

    // Class to compare timestamps
    protected Comparator<Timestamp> dateComparator = (timestamp, t1) -> {
        if (timestamp.after(t1)) {
            return 1;
        } else if (timestamp.equals(t1)) {
            return 0;
        }

        return -1;
    };

}
