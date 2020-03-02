package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.ActionLog;
import org.openwis.metadataportal.services.user.dto.UserActionLogDTO;
import org.openwis.metadataportal.services.user.dto.UserDTO;
import org.openwis.metadataportal.services.util.DateTimeUtils;
import org.openwis.metadataportal.services.util.UserActionLogUtils;
import org.openwis.securityservice.InetUserStatus;

import java.sql.Timestamp;

public class Lock implements Service {
    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        UserDTO userDTO = JeevesJsonWrapper.read(params, UserDTO.class);
        String username = getUsernameFromRequest(context, userDTO);

        AcknowledgementDTO acknowledgementDTO = null;
        UserActionLogDTO userActionLogDTO = null;
        if (StringUtils.isEmpty(username)) {
            acknowledgementDTO = new AcknowledgementDTO(false, "Username is not provided");
            return JeevesJsonWrapper.send(acknowledgementDTO);
        }

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        UserManager um = new UserManager(dbms);
        User user = um.getUserByUserName(username);

        // Cannot lock the administrator
        if (isAdministrator(user.getProfile())) {
            acknowledgementDTO = new AcknowledgementDTO(false, "Cannot lock administrator");
            return JeevesJsonWrapper.send(acknowledgementDTO);
        }

        ActionLog lockAction = user.getInetUserStatus() == InetUserStatus.ACTIVE ? ActionLog.LOCK : ActionLog.UNLOCK;
        um.lockUser(user.getUsername(), user.getInetUserStatus() == InetUserStatus.ACTIVE);
        acknowledgementDTO = new AcknowledgementDTO(true, lockAction.name());

        // save log
        userActionLogDTO = new UserActionLogDTO();
        userActionLogDTO.setAction(lockAction);
        userActionLogDTO.setDate(Timestamp.from(DateTimeUtils.getUTCInstant()));
        userActionLogDTO.setUsername(user.getUsername());
        userActionLogDTO.setActionerUsername(context.getUserSession().getUsername());
        UserActionLogUtils.saveLog(dbms, userActionLogDTO);

        return JeevesJsonWrapper.send(acknowledgementDTO);
    }

    private Boolean isAdministrator(String profile) {
        return profile.equals("Administrator");
    }

    /**
     * Extract the username of the user to retrieve from the request information.  This
     * can be overridden by subclasses to restrict the usernames that can be retrieved.
     *
     * @param context
     * @param userDTO
     * @param username
     * @return
     */
    private String getUsernameFromRequest(ServiceContext context, UserDTO userDTO) {
        if (userDTO != null && (userDTO.getUser() != null || userDTO.isEditingPersoInfo())) {
            if (userDTO.isEditingPersoInfo()) {
                return context.getUserSession().getUsername();
            } else if (userDTO.getUser().getUsername() != null) {
                return userDTO.getUser().getUsername();
            }
        }
        return null;
    }
}
