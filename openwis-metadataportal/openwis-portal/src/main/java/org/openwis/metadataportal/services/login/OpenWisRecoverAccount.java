package org.openwis.metadataportal.services.login;


import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Account Request
 * @author gibaultr
 *
 */
public class OpenWisRecoverAccount extends HttpServlet{

    private final String GOOGLE_CAPTCHA_PARAMETER_RESPONSE="g-recaptcha-response";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        handleRequest(request,response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        handleRequest(request,response);

    }
    /**
     * Method called once the end user has submitted his account request
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException
     * @throws IOException
     */
    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {

        try {
            //Check whether the captcha passed or not
            boolean captchaPassed = GoogleCaptchaVerificator.verify(request.getParameter(GOOGLE_CAPTCHA_PARAMETER_RESPONSE));
            ServiceContext context = (ServiceContext) request.getSession().getAttribute("context");


            //If captcha passed, send mail to end user
            if (captchaPassed) {
                processRequest(context, request, response);
            }
            else {
                String errorMessage= OpenWISMessages.format("AccountRequest.captchaFailed", context.getLanguage());
                forwardError(request, response, errorMessage);
            }

        } catch (Exception e) {
            Log.error(LoginConstants.LOG, "Error processing Account Recovery  : " + e.getMessage());
            forwardError(request, response, "Error during acccount recovery - " + e.getMessage());
        }
    }


    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              String message) throws ServletException, IOException {

        String[] uris=request.getRequestURI().split("/");
        String redirect = "/"+uris[1]+"/srv/en/user.accountRecovery.get?errorMessage="+message;
        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.addHeader("Location", redirect);
    }
    /**
     * Process account request when captcha passed
     * @param context context
     * @param request HTTP request
     * @param response HTTP response
     * @throws Exception
     */
    private void processRequest(ServiceContext context, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String[] uris=request.getRequestURI().split("/");
        String redirect = "/"+uris[1]+"/jsp/recoverAccountAck.jsp";
        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.addHeader("Location", redirect);

        String email = request.getParameter("email");

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        // Get User
        UserManager um = new UserManager(dbms);
        User user = um.getUserByUserName(email);

        //Generate new Password
        String newPassword = generatePassword();
        //Change User Password
        um.changePassword(user.getUsername(), newPassword);
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
     * @param context context
     * @param user user
     */
    private void sendEmailToUser(ServiceContext context, User user) {

        MailUtilities mail = new MailUtilities();

        Map<String, Object> content = new HashMap<>();
        content.put("firstname", user.getName());
        content.put("lastname", user.getSurname());
        content.put("username", user.getEmailContact());
        content.put("password", user.getPassword());
        String decodedKey = TwoFactorAuthenticationUtils.decodeBase16(user.getSecretKey());
        content.put("secretKey", TwoFactorAuthenticationUtils.getGoogleAuthenticatorBarCode(user.getEmailContact(),TwoFactorAuthenticationUtils.encodeBase32(decodedKey)));


        OpenWISMail openWISMail = OpenWISMailFactory.buildRecoverAccountUserMail(context, "AccountRecovery.subject1",new String[]{user.getEmailContact()}, content);
        boolean result = mail.send(openWISMail);
        if (!result) {
            // To be confirmed: Set ack dto if error message is requested
            //acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("SelfRegister.errorSendingMail", context.getLanguage()));
            Log.error(Geonet.SELF_REGISTER, "Error during Account Recovery : error while sending email to the end user("+user.getEmailContact()+")");
        } else {
            Log.info(Geonet.SELF_REGISTER, "Account recovery email sent successfully to the end user("+user.getEmailContact()+") from "+openWISMail.getAdministratorAddress());
        }
    }
    /**
     * Sending email notification to the administrator after the end user has requested an account
     * @param context context
     * @param email user email address
     * @param firstname firstname of the user
     * @param lastname last name of the user
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
            Log.error(Geonet.SELF_REGISTER, "Error during Account Recovery : error while sending email to the administrator ("+openWISMail.getAdministratorAddress()+") about account recovery of user "+email);
        } else {
            Log.info(Geonet.SELF_REGISTER, "Email sent successfully to the administrator ("+openWISMail.getAdministratorAddress()+") about account recovery of user "+email);
        }
    }

    private String generatePassword() {

        String[] symbols = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
        int length = 10;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int indexRandom = getRandomNumberInRange( 0,symbols.length );
            sb.append( symbols[indexRandom] );
        }
        String password = sb.toString();
        return password;
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
