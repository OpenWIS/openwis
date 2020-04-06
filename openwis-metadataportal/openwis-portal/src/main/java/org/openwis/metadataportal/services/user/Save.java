/**
 *
 */
package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import jeeves.utils.Log;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.TwoFactorAuthenticationUtils;
import org.openwis.metadataportal.kernel.user.UserAlreadyExistsException;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.Profile;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.user.dto.UserDTO;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.OpenWISMessages;
import org.openwis.metadataportal.services.util.UserLogUtils;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;
import org.openwis.metadataportal.services.util.mail.OpenWISMailFactory;
import org.openwis.securityservice.OpenWISUserUpdateLog;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class Save implements Service {

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        UserDTO userDTO = JeevesJsonWrapper.read(params, UserDTO.class);
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        UserManager um = new UserManager(dbms);
        AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);
        UserLogDTO userLogDTO = null;

        try {
            if (userDTO.isCreationMode()) {
                User user = userDTO.getUser();
                user.setSecretKey(TwoFactorAuthenticationUtils.encodeBase16(TwoFactorAuthenticationUtils.generateKey()));
                um.createUser(user);

                if (!user.getProfile().equals(Profile.Candidate.toString())) {
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("firstname", user.getName());
                    vars.put("lastname", user.getSurname());
                    vars.put("username", user.getUsername());
                    vars.put("password", user.getPassword());

                    String decodedKey = TwoFactorAuthenticationUtils.decodeBase16(user.getSecretKey());
                    vars.put("secretKey", TwoFactorAuthenticationUtils.getTOPTKeyUri(TwoFactorAuthenticationUtils.encodeBase32(decodedKey), user.getEmailContact()));
                    IOpenWISMail mail = OpenWISMailFactory.buildAccountCreationMail(context, "UserCreation.subject", new String[]{user.getEmailContact()}, vars);
                    MailUtilities mailUtilities = new MailUtilities();
                    boolean result =  mailUtilities.send(mail);
                    if (!result) {
                        acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("UserCreation.error", context.getLanguage()));
                        Log.error(Geonet.PRIVILEGES, "User Creation: Error while sending email to user email " + user.getEmailContact());
                    } else {
                        Log.info(Geonet.PRIVILEGES, "User Creation: successfully created. Notification email sent to " + user.getEmailContact());
                    }
                }
                // create action log entry
                userLogDTO = new UserLogDTO();
                userLogDTO.setActioner(this.getUsernameFromRequest(context));
                userLogDTO.setAction(UserAction.CREATE);
                userLogDTO.setUsername(userDTO.getUser().getUsername());
                userLogDTO.setDate(LocalDateTime.now());
                UserLogUtils.save(dbms, userLogDTO);

            } else {
                List<OpenWISUserUpdateLog> updateLogs = um.updateUser(userDTO.getUser());
                for (OpenWISUserUpdateLog updateLog : updateLogs) {
                    userLogDTO = UserLogUtils.buildLog(updateLog);
                    userLogDTO.setActioner(context.getUserSession().getUsername());
                    UserLogUtils.save(dbms, userLogDTO);
                }
                // call method checkSubscription on RequestManager service.
                RequestManager requestManager = new RequestManager();
                requestManager.checkUserSubscription(userDTO.getUser().getUsername(), dbms);
            }
        } catch (UserAlreadyExistsException e) {
            acknowledgementDTO = new AcknowledgementDTO(false, "The user " + e.getUserName() + " already exists");
        }

        return JeevesJsonWrapper.send(acknowledgementDTO);
    }

    /**
     * Extract the username of the user to retrieve from the request information.
     * @param context
     * @return
     */
    private String getUsernameFromRequest(ServiceContext context) {
        return context.getUserSession().getUsername();
    }
}
