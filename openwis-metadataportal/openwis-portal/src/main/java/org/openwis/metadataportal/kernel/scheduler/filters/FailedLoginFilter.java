package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.model.user.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FailedLoginFilter implements AccountFilter {

    private final Dbms dbms;
    private final String datePattern = "YYYY-MM-dd HH:mm:ss.SSS";

    public FailedLoginFilter(Dbms dbms) {
        this.dbms = dbms;
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
            } catch (SQLException | NullPointerException e) {
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
    private LocalDateTime getAccountLockedDate(User user) throws SQLException {
        String query = String.format("select * from openwis_alarms where message like '%%Account locked%%%s%%' and source = 'Security Service' ORDER BY date DESC LIMIT 1;", user.getUsername());

        List<Element> result = this.dbms.select(query).getChildren();
        if (result.size() == 0) {
            return null;
        }

        List<Element> elements = (List<Element>) result.get(0).getContent();
        for (Element e: elements) {
            if (e.getName().equals("date")) {
                return LocalDateTime.parse(e.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }
        return null;
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
