package org.openwis.metadataportal.kernel.scheduler.actions;

import com.sun.istack.NotNull;
import jeeves.utils.Log;
import org.openwis.management.alert.AlertService;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;

import java.util.Collections;

public class AlertAction implements AccountAction {

    private final AlertService alertService;
    private final UserAction action;

    public AlertAction(@NotNull AlertService alertService, UserAction action) {
        this.alertService = alertService;
        this.action = action;
    }

    @Override
    public void doAction(User user) {
        Log.debug(Log.SCHEDULER, String.format("Create alert [%s] for user [%s]", this.getAlertName(action), user.getUsername()));
        alertService.raiseEvent(this.getAlertName(this.action),
                "Admin Portal",
                null,
                this.getDescription(user, this.action),
                Collections.singletonList(user.getUsername()));
    }

    private String getAlertName(UserAction action) {
        if (action.equals(UserAction.INACTIVITY_NOTIFICATION_MAIL)) {
            return "Account Activity Notification";
        } else if (action.equals(UserAction.LOCK)) {
            return "Account Lock Task";
        } else if (action.equals(UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL)) {
            return "Password expiry notification";
        }
        return "Unknown alert";
    }
    private String getDescription(User user, UserAction action) {
        if (action.equals(UserAction.INACTIVITY_NOTIFICATION_MAIL)) {
            return String.format("User [%s] notified due to inactivity", user.getUsername());
        } else if (action.equals(UserAction.LOCK)) {
            return String.format("Account locked for user: %s", user.getUsername());
        } else if (action.equals(UserAction.PASSWORD_EXPIRE_NOTIFICATION_MAIL)) {
            return String.format("User [%s] notified due to password age", user.getUsername());
        }
        return "Unknown action";
    }
}
