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
     * @see Service#exec(Element, ServiceContext)
     */
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        UserDTO userDTO = JeevesJsonWrapper.read(params, UserDTO.class);
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        UserManager um = new UserManager(dbms);
        AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);
        UserLogDTO userLogDTO = null;

        try {
            User user = userDTO.getUser();
            if (userDTO.isCreationMode()) {
                user.setSecretKey(TwoFactorAuthenticationUtils.generateKey());
                um.createUser(user);

                if (!user.getProfile().equals(Profile.Candidate.toString())) {
                    Map<String, Object> vars = new HashMap<>();
                    vars.put("firstname", user.getName());
                    vars.put("lastname", user.getSurname());
                    vars.put("username", user.getUsername());
                    vars.put("password", user.getPassword());

                    String decodedKey = TwoFactorAuthenticationUtils.decodeBase16(user.getSecretKey());
                    vars.put("secretKey", TwoFactorAuthenticationUtils.getGoogleAuthenticatorBarCode(user.getUsername(), TwoFactorAuthenticationUtils.encodeBase32(decodedKey)));
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
                User storedUser = um.getUserByUserName(user.getUsername());

                // set secret key from storedUser
                user.setSecretKey(storedUser.getSecretKey());
                if (user.getPassword().compareTo(user.getUsername()) == 0)
                {
                    Log.info(Geonet.PRIVILEGES, "##### Password = <" + user.getPassword() + "> User name= <"+user.getUsername() + " - " + user.getName()+ " - " + user.getId());
                    throw new Exception("Password must be different from user identifier");

                }
                else
                {
                    Log.info(Geonet.PRIVILEGES, "!##### Password = <" + user.getPassword() + "> User name= <"+user.getUsername() + " - " + user.getName()+ " - " + user.getId());

                }

                // When profile changes from Candidate to another value
                // an email is sent to the end user
                if (storedUser.getProfile().equals(Profile.Candidate.toString()) && !user.getProfile().equals(Profile.Candidate.toString()) ) {
                    boolean result = sendEmailToUser(context,user);
                    if (!result) {
                        Log.info(Geonet.PRIVILEGES, "Error sending mail to " + user.getEmailContact());
                        throw new Exception("Error sending mail to " + user.getEmailContact());
                    }
                }

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
        } catch (Exception ex) {
            acknowledgementDTO = new AcknowledgementDTO(false, ex.getMessage());
        }

        return JeevesJsonWrapper.send(acknowledgementDTO);
    }

    private boolean sendEmailToUser(ServiceContext context, User user) {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("firstname", user.getName());
        bodyData.put("lastname", user.getSurname());
        bodyData.put("username", user.getUsername());
        bodyData.put("password", user.getPassword());

        String decodedKey = TwoFactorAuthenticationUtils.decodeBase16(user.getSecretKey());
        bodyData.put("secretKey", TwoFactorAuthenticationUtils.getGoogleAuthenticatorBarCode(user.getUsername(),TwoFactorAuthenticationUtils.encodeBase32(decodedKey)));

        IOpenWISMail mail = OpenWISMailFactory.buildAccountCreationMail(context, "UserCreation.subject", new String[]{user.getEmailContact()}, bodyData);
        return new MailUtilities().send(mail);
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
