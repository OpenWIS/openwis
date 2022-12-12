/**
 *
 */
package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import jeeves.utils.Log;
import org.apache.commons.lang3.RandomStringUtils;
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

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                //user.setPwdReset(true); // force user to change his password
                //user.setPassword(generateRandomPassword());
                user.setPassword(user.getPassword());
                um.createUser(user);

                if (!user.getProfile().equals(Profile.Candidate.toString())) {
                    boolean result = sendEmailToUser(context, user);
                    if (!result) {
                        acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("UserCreation.error", context.getLanguage()));
                        Log.error(Geonet.PRIVILEGES, "User Creation: Error while sending email to user email " + user.getEmailContact());
                    } else {
                        Log.info(Geonet.PRIVILEGES, "User Creation: successfully created. Notification email sent to " + user.getEmailContact());
                    }
                }
                // create action log entry
                saveLog(context, userDTO, dbms);

            } else {
                User storedUser = um.getUserByUserName(user.getUsername());

                // set secret key from storedUser
                user.setSecretKey(storedUser.getSecretKey());
                if ( !user.getPassword().isEmpty() ) {
                    if (user.getPassword().compareTo(user.getUsername()) == 0) {
                        throw new Exception("Password must be different from user identifier");
                    } else {
                        // password changed so reset it
                        user.setPwdReset(true);
                    }
                }

                // When profile changes from Candidate to another value
                // an email is sent to the end user
                if (storedUser.getProfile().equals(Profile.Candidate.toString()) && !user.getProfile().equals(Profile.Candidate.toString())) {
                    user.setPassword(generateRandomPassword());
                    boolean result = sendEmailToUser(context, user);
                    if (!result) {
                        Log.info(Geonet.PRIVILEGES, "Error sending mail to " + user.getEmailContact());
                        throw new Exception("Error sending mail to " + user.getEmailContact());
                    }
                }

                List<OpenWISUserUpdateLog> updateLogs = um.updateUser(user);
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

    private void saveLog(ServiceContext context, UserDTO userDTO, Dbms dbms) throws SQLException {
        UserLogDTO userLogDTO;
        userLogDTO = new UserLogDTO();
        userLogDTO.setActioner(this.getUsernameFromRequest(context));
        userLogDTO.setAction(UserAction.CREATE);
        userLogDTO.setUsername(userDTO.getUser().getUsername());
        userLogDTO.setDate(LocalDateTime.now());
        UserLogUtils.save(dbms, userLogDTO);
    }

    private boolean sendEmailToUser(ServiceContext context, User user) {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("firstname", user.getName());
        bodyData.put("lastname", user.getSurname());
        bodyData.put("username", user.getUsername());
        bodyData.put("password", user.getPassword());

        String decodedKey = TwoFactorAuthenticationUtils.decodeBase16(user.getSecretKey());
        bodyData.put("secretKey", TwoFactorAuthenticationUtils.getGoogleAuthenticatorBarCode(user.getUsername(), TwoFactorAuthenticationUtils.encodeBase32(decodedKey)));

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

    /**
     * According to WISMET password policy, the password must be 12 characters long.
     * 1 upper case
     * 2 special character
     * 1 number
     */
    private String generateRandomPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(10, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }
}
