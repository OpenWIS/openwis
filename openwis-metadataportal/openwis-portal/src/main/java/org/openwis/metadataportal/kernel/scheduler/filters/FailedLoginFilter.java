package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.management.alert.AlarmEvent;
import org.openwis.management.alert.AlertService;
import org.openwis.metadataportal.model.user.User;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FailedLoginFilter implements AccountFilter {

    private final Dbms dbms;
    private final String datePattern = "YYYY-MM-dd HH:mm:ss.SSS";

    /** Date/time format used in OpenAM logs */
    private final String ALARM_DATE_TIME_FORMAT = new String("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final AlertService alertService;

    public FailedLoginFilter(Dbms dbms, AlertService alertService) {
        this.dbms = dbms;
        this.alertService = alertService;
    }

    @Override
    public List<User> filter(List<User> users) {

        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            try {
                LocalDateTime lastLockedDate = this.getAccountLockedDate(user);
                if (lastLockedDate != null) {
                    if (!this.isAccountLockedLastNotification(user, lastLockedDate)) {
                        filteredUsers.add(user);
                    }
                }
            } catch (Exception e) {
                Log.error(Geonet.ADMIN, String.format("%s. User %s: %s",
                        FailedLoginFilter.class.getSimpleName(),
                        user.getUsername(),
                        e.getMessage()));
            }

        }

        return filteredUsers;
    }

    /**
     * Return the date when the last lock out of account occured
     *
     * @param user user which account has been locked out
     * @return Element
     */
    @SuppressWarnings("unchecked case")
    private LocalDateTime getAccountLockedDate(User user) {
        String filterExp = String.format("LOWER(openwis_alarms.severity) = 'warn' and LOWER(openwis_alarms.module) like '%%log timer service%%' and LOWER(openwis_alarms.source) like '%%security service%%' and LOWER(openwis_alarms.message) like '%%account locked %%%s%%'", user.getUsername());
        String sortColumn = "date";
        String sortOrder = "DESC";
        int index = 0;
        int limit = 1;

        List<AlarmEvent> events = this.alertService.getFilteredEventsSorted(filterExp, sortColumn, sortOrder, index, limit);
        if (events.size() == 0) {
            return null;
        }

         AlarmEvent event = events.get(0);
         return LocalDateTime.parse(event.getDate().toString(), DateTimeFormatter.ofPattern(ALARM_DATE_TIME_FORMAT));
    }

    private boolean isAccountLockedLastNotification(User user, LocalDateTime lockedDate) throws SQLException {
        String query = String.format("select * from user_log where username = '%s' and action = 'ACCOUNT_LOCKED_NOTIFICATION_MAIL' and date >= '%s';",
                user.getUsername(),
                lockedDate.format(DateTimeFormatter.ofPattern(datePattern))
        );

        List<Element> result = null;
        result = dbms.select(query).getChildren();

        return result.size() != 0;
    }
}
