package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.exceptions.BadInputEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides a filter to extract the last log entry of a certain type for a given user.
 */
public class LogFilter {

    /**
     * Return the last mail notification of a user
     *
     * @param user user for which the notification is filtered
     * @return last notification of type {@param action} for user {@param user}
     */
    public UserLogDTO getLastLogEntry(List<UserLogDTO> logs, User user, UserAction action) {

        List<UserLogDTO> userLog = logs.stream()
                .filter(l -> l.getUsername().equals(user.getUsername()))
                .filter(l -> l.getAction().equals(action))
                .sorted((l1, l2) -> dateComparator.compare(l1.getDate(), l2.getDate()))
                .collect(Collectors.toList());

        if (userLog.size() == 0) {
            return null;
        }

        return userLog.get(userLog.size() - 1);
    }

    public List<UserLogDTO> getLogs(Dbms dbms) throws SQLException, BadInputEx {
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

    private Timestamp toTimestamp(String value) {
        ZonedDateTime zdt = LocalDateTime.parse(value).atZone(ZoneId.of("UTC"));
        return Timestamp.valueOf(zdt.toLocalDateTime());
    }

    // Class to compare timestamps
    private Comparator<LocalDateTime> dateComparator = (t2, t1) -> {
        if (t2.isAfter(t1)) {
            return 1;
        } else if (t2.equals(t1)) {
            return 0;
        }

        return -1;
    };
}
