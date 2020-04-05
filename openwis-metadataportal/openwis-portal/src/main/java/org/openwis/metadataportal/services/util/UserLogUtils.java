package org.openwis.metadataportal.services.util;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import org.bouncycastle.util.Strings;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openwis.securityservice.OpenWISUserUpdateLog;

public class UserLogUtils {

    public static void save(Dbms dbms, UserLogDTO log) throws SQLException {
        if (log != null) {
            String query = "INSERT INTO user_log(date, username, action, actioner, attribute) Values(?,?,?,?,?)";
            dbms.execute(query, log.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), log.getUsername(), log.getAction().name(), log.getActioner(), log.getAttribute());
            Log.debug(LoginConstants.LOG, "Insert into user_log " + log.getAction().name() + " for " + log.getUsername());
        }
    }

    public static UserLogDTO buildLog(OpenWISUserUpdateLog openwisLog) {
        UserLogDTO userActionLogDTO = new UserLogDTO();
        userActionLogDTO.setAction(UserAction.valueOf(Strings.toUpperCase(openwisLog.getAction())));
        userActionLogDTO.setUsername(openwisLog.getUsername());
        userActionLogDTO.setAttribute(openwisLog.getAttribute());
        userActionLogDTO.setDate(LocalDateTime.now());
        return userActionLogDTO;
    }
}
