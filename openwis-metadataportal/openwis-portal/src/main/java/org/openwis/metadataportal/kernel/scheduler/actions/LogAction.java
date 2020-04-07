package org.openwis.metadataportal.kernel.scheduler.actions;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.util.UserLogUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class LogAction implements AccountAction {

    private final Dbms dbms;
    private final UserAction action;

    public LogAction(Dbms dbms,  UserAction action) {
        this.dbms = dbms;
        this.action = action;
    }

    @Override
    public void doAction(User user) {
        UserLogDTO userActionLogDTO = new UserLogDTO();
        userActionLogDTO.setAction(action);
        userActionLogDTO.setDate(LocalDateTime.now());
        userActionLogDTO.setUsername(user.getUsername());
        userActionLogDTO.setActioner("admin");
        try {
            UserLogUtils.save(dbms, userActionLogDTO);
            Log.debug(Log.SCHEDULER, String.format("Log entry added for user [%s]", user.getUsername()));
        } catch (SQLException e) {
            Log.warning(Log.SCHEDULER, e);
        }
    }
}
