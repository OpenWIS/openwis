package org.openwis.metadataportal.services.login;


import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.user.TwoFactorAuthenticationUtils;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.OpenWISMessages;
import org.openwis.metadataportal.services.util.UserLogUtils;
import org.openwis.metadataportal.services.util.mail.OpenWISMail;
import org.openwis.metadataportal.services.util.mail.OpenWISMailFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Account Request
 *
 * @author gibaultr
 */
public class OpenWisRecoverAccount extends HttpServlet {

    private final String GOOGLE_CAPTCHA_PARAMETER_RESPONSE = "g-recaptcha-response";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);

    }

    /**
     * Method called once the end user has submitted his account request
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @throws ServletException
     * @throws IOException
     */
    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServiceContext context = (ServiceContext) request.getSession().getAttribute("context");
        try {
            //Check whether the captcha passed or not
            boolean captchaPassed = GoogleCaptchaVerificator.verify(request.getParameter(GOOGLE_CAPTCHA_PARAMETER_RESPONSE), context);


            //If captcha passed, send mail to end user
            if (captchaPassed) {
                processRequest(context, request, response);
            } else {
                String errorMessage = OpenWISMessages.format("AccountRequest.captchaFailed", context.getLanguage());
                forwardError(request, response, errorMessage);
            }
            context.getResourceManager().close();
        } catch (Exception e) {
            Log.error(LoginConstants.LOG, "Error processing Account Recovery  : " + e.getMessage(),e);
            try {
                context.getResourceManager().abort();
            } catch (Exception exception) {
                Log.error(LoginConstants.LOG, "Cannot abort resource: " + e.getMessage(),e);
            }
            forwardError(request, response, "Error during account recovery - " + e.getMessage());
        }
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              String message) throws ServletException, IOException {

        String baseUrl = this.getBaseUrl(request.getRequestURI());
        String redirect = baseUrl + "/srv/en/user.accountRecovery.get?errorMessage=" + message;
        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.addHeader("Location", redirect);
    }

    /**
     * Process account request when captcha passed
     *
     * @param context  context
     * @param request  HTTP request
     * @param response HTTP response
     * @throws Exception
     */
    private void processRequest(ServiceContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String baseUrl = this.getBaseUrl(request.getRequestURI());
        String redirect = baseUrl + "/jsp/recoverAccountAck.jsp";
        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.addHeader("Location", redirect);

        String email = request.getParameter("email");

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        // Get User
        UserManager um = new UserManager(dbms);
        User user = um.getUserByUserName(email);

        // Bug fixed 13/10/2020
        /*  *
         *  When they requested for an account, they receive a notification email which is correct. Then their account need to be elevated from candidate to user by the admin.
         *  But when their privilege has not been elevated from candidate to user,
         *  some of them tried to request for lost account recovery and they could receive a login credential this way.
         */
        if (user.getProfile().equals("Candidate")) {
            return;
        }

        //Generate new Password
        String newPassword = generateRandomPassword();
        //Change User Password
        um.changePassword(user.getUsername(), newPassword, true);
        user.setPassword(newPassword);

        //Send Mail To User
        Log.debug(Geonet.SELF_REGISTER, "Sending an email to the user");
        sendEmailToUser(context, user);

        //Send Mail To Openwis Administrator
        Log.debug(Geonet.SELF_REGISTER, "Sending an email to the administrator");
        sendEmailToAdministrator(context, email, user.getSurname(), user.getName(), newPassword);

        // create action log entry
        UserLogDTO userLogDTO = new UserLogDTO();
        userLogDTO.setActioner(user.getUsername());
        userLogDTO.setAction(UserAction.RECOVER);
        userLogDTO.setUsername(user.getUsername());
        userLogDTO.setDate(LocalDateTime.now());
        UserLogUtils.save(dbms, userLogDTO);


    }

    /**
     * Sending email notification to the end user just after he has requested an account
     *
     * @param context context
     * @param user    user
     */
    private void sendEmailToUser(ServiceContext context, User user) {

        MailUtilities mail = new MailUtilities();

        Map<String, Object> content = new HashMap<>();
        content.put("firstname", user.getName());
        content.put("lastname", user.getSurname());
        content.put("username", user.getEmailContact());
        content.put("password", user.getPassword());
        String decodedKey = TwoFactorAuthenticationUtils.decodeBase16(user.getSecretKey());
        content.put("secretKey", TwoFactorAuthenticationUtils.getGoogleAuthenticatorBarCode(user.getEmailContact(), TwoFactorAuthenticationUtils.encodeBase32(decodedKey)));


        OpenWISMail openWISMail = OpenWISMailFactory.buildRecoverAccountUserMail(context, "AccountRecovery.subject1", new String[]{user.getEmailContact()}, content);
        boolean result = mail.send(openWISMail);
        if (!result) {
            // To be confirmed: Set ack dto if error message is requested
            //acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("SelfRegister.errorSendingMail", context.getLanguage()));
            Log.error(Geonet.SELF_REGISTER, "Error during Account Recovery : error while sending email to the end user(" + user.getEmailContact() + ")");
        } else {
            Log.info(Geonet.SELF_REGISTER, "Account recovery email sent successfully to the end user(" + user.getEmailContact() + ") from " + openWISMail.getAdministratorAddress());
        }
    }

    /**
     * Sending email notification to the administrator after the end user has requested an account
     *
     * @param context   context
     * @param email     user email address
     * @param firstname firstname of the user
     * @param lastname  last name of the user
     */
    private void sendEmailToAdministrator(ServiceContext context, String email, String firstname, String lastname, String password) {

        MailUtilities mail = new MailUtilities();

        Map<String, Object> content = new HashMap<>();
        content.put("firstname", firstname);
        content.put("lastname", lastname);
        content.put("username", email);
        content.put("password", password);

        OpenWISMail openWISMail = OpenWISMailFactory.buildRecoverAccountAdminMail(context, "AccountRecovery.subject2", content);
        boolean result = mail.send(openWISMail);
        if (!result) {
            // To be confirmed: Set ack dto if error message is requested
            //acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("SelfRegister.errorSendingMail", context.getLanguage()));
            Log.error(Geonet.SELF_REGISTER, "Error during Account Recovery : error while sending email to the administrator (" + openWISMail.getAdministratorAddress() + ") about account recovery of user " + email);
        } else {
            Log.info(Geonet.SELF_REGISTER, "Email sent successfully to the administrator (" + openWISMail.getAdministratorAddress() + ") about account recovery of user " + email);
        }
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

    private String getBaseUrl(String url) {
        String[] uris = url.split("/");
        StringBuilder baseUrl = new StringBuilder("/");
        for (int i = 1; i < uris.length; i++) {

            boolean done = false;
            if (uris[i].contains("user-portal") || uris[i].contains("admin-portal")) {
                done = true;
            }
            baseUrl.append(uris[i]).append("/");
            if (done) {
                break;
            }
        }

        return baseUrl.toString();
    }

}
