package org.openwis.metadataportal.kernel.scheduler.filters;

import jeeves.utils.Log;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.management.RecentEvents;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FailedLoginFilter implements AccountFilter {

    private final LocalDateTime from;
    private String datePattern = "YYYY-MM-dd HH:mm";

    public FailedLoginFilter(LocalDateTime from) {
        this.from = from;
    }

    @Override
    public List<User> filter(List<User> users) {

        List<User> filteredUsers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // create a RecentEvents service and request the last events
        Element requestElement = this.createRequestElement(this.from, now);
        Log.debug(Geonet.ADMIN, String.format("%s: Request element: %s",FailedLoginFilter.class.getSimpleName(), requestElement.toString()));
        RecentEvents recentEvents = new RecentEvents();
        Element response = null;
        try {
            response = recentEvents.exec(requestElement, null);
        } catch (Exception e) {
            Log.error(Geonet.ADMIN, e.getMessage());
            return new ArrayList<>();
        }

        for (User user : users) {
            if (!response.toString().contains(user.getUsername())) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    private Element createRequestElement(LocalDateTime from, LocalDateTime to) {
        Element element = new Element("request");
        Element child = new Element("start");
        child.setText("0");
        element.addContent(child);

        child = new Element("limit").setText("100");
        element.addContent(child);

        child = new Element("date_from").setText(from.format(DateTimeFormatter.ofPattern(datePattern)));
        element.addContent(child);

        child = new Element("date_to").setText(to.format(DateTimeFormatter.ofPattern(datePattern)));
        element.addContent(child);

        child = new Element("severity");
        element.addContent(child);

        child = new Element("module");
        element.addContent(child);

        child = new Element("source").setText("Security Service");
        element.addContent(child);

        child = new Element("message").setText("Account locked");
        element.addContent(child);

        child = new Element("sort").setText("date");
        element.addContent(child);
        return element;
    }
}
