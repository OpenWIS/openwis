package org.openwis.metadataportal.services.util;

import jeeves.exceptions.BadInputEx;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.Strings;
import org.jdom.Element;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openwis.securityservice.OpenWISUserUpdateLog;

public class UserLogUtils {

    public static void save(Dbms dbms, UserLogDTO log) throws SQLException {
        if (log != null) {
            String query = "INSERT INTO user_log(date, username, action, actioner, attribute) Values(?,?,?,?,?)";
            dbms.execute(query, log.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), log.getUsername(), log.getAction().name(), log.getActioner(), log.getAttribute());
            dbms.commit();
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

    public static List<UserLogDTO> getLogs(Dbms dbms) throws SQLException, BadInputEx {
        String query = "SELECT * from user_log;";
        List<Element> elements = dbms.select(query).getChildren();
        dbms.commit();
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
