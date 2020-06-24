package org.openwis.metadataportal.services.login;


import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.user.TwoFactorAuthenticationUtils;
import org.openwis.metadataportal.kernel.user.UserAlreadyExistsException;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.Address;
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
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Account Request
 *
 * @author gibaultr
 */
public class OpenWisRequestAccount extends HttpServlet {

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
            boolean captchaPassed = GoogleCaptchaVerificator.verify(request.getParameter(GOOGLE_CAPTCHA_PARAMETER_RESPONSE));

            //If captcha passed, send mail to end user
            if (captchaPassed) {
                processRequest(context, request, response);
            } else {
                String errorMessage = OpenWISMessages.format("AccountRequest.captchaFailed", context.getLanguage());
                forwardError(request, response, errorMessage);
            }
        } catch (UserAlreadyExistsException e) {
            String errorMessage = OpenWISMessages.format("AccountRequest.userAlreadyExists", context.getLanguage());
            forwardError(request, response, errorMessage);
        } catch (Exception e) {
            Log.error(LoginConstants.LOG, "Error processing Account Request  : " + e.getMessage());
            forwardError(request, response, "Error during acccount request - " + e.getMessage());
        }
    }


    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              String message) throws ServletException, IOException {

        String baseUrl = this.getBaseUrl(request.getRequestURI());
        String redirect = baseUrl + "/srv/en/user.accountRequest.get?errorMessage=" + URLEncoder.encode(message, "UTF-8");

        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.setHeader("Location", redirect);
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
        String redirect = baseUrl + "/jsp/requestAccountAck.jsp";
        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.addHeader("Location", redirect);

        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String organisation = request.getParameter("organisation");
        String country = request.getParameter("country");
        String email = request.getParameter("email");

        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        // Create User
        UserManager um = new UserManager(dbms);
        User user = new User();
        user.setUsername(email);
        user.setName(lastname);
        user.setSurname(firstname);
        user.setPassword("openwis");
        Address address = new Address();
        address.setAddress("");
        address.setCity("");
        address.setCountry(country);
        address.setState("");
        address.setZip("");
        user.setAddress(address);
        user.setEmailContact(email);
        user.setProfile("Candidate");
        user.setPassword(generateRandomPassword());
        user.setSecretKey(TwoFactorAuthenticationUtils.generateKey());
        um.createUser(user);
        Log.debug(Geonet.SELF_REGISTER, "User created on Security Server");

        //Send Mail To User
        Log.debug(Geonet.SELF_REGISTER, "Sending an email to the user");
        sendEmailToUser(context, email, firstname, lastname);

        //Send Mail To Openwis Administrator
        Log.debug(Geonet.SELF_REGISTER, "Sending an email to the administrator");
        sendEmailToAdministrator(context, email, firstname, lastname, organisation, country);

        // create action log entry
        UserLogDTO userLogDTO = new UserLogDTO();
        userLogDTO.setActioner(user.getUsername());
        userLogDTO.setAction(UserAction.REQUEST);
        userLogDTO.setUsername(user.getUsername());
        userLogDTO.setDate(LocalDateTime.now());
        UserLogUtils.save(dbms, userLogDTO);
    }

    /**
     * Sending email notification to the end user just after he has requested an account
     *
     * @param context   context
     * @param email     user email address
     * @param firstname firstname of the user
     * @param lastname  last name of the user
     */
    private void sendEmailToUser(ServiceContext context, String email, String firstname, String lastname) {

        MailUtilities mail = new MailUtilities();

        Map<String, Object> content = new HashMap<>();
        content.put("firstname", firstname);
        content.put("lastname", lastname);
        content.put("username", email);


        OpenWISMail openWISMail = OpenWISMailFactory.buildRequestAccountUserMail(context, "AccountRequest.subject1",new String[]{email}, content);
        boolean result = mail.send(openWISMail);
        if (!result) {
            Log.error(Geonet.SELF_REGISTER, "Error during Account Request : error while sending email to the administrator ("+openWISMail.getAdministratorAddress()+") about account request of user "+email);
        } else {
            Log.info(Geonet.SELF_REGISTER, "Email sent successfully to the user ("+openWISMail.getAdministratorAddress()+") about account request of user "+email);
        }
    }

    /**
     * Sending email notification to the administrator after the end user has requested an account
     *
     * @param context   context
     * @param email     user email address
     * @param firstname firstname of the user
     * @param lastname  last name of the user
     * @param organisation organisation
     * @param country country
     */
    private void sendEmailToAdministrator(ServiceContext context, String email, String firstname, String lastname, String organisation, String country) {

        MailUtilities mail = new MailUtilities();

        Map<String, Object> content = new HashMap<>();
        content.put("firstname", firstname);
        content.put("lastname", lastname);
        content.put("username", email);
        content.put("organization", organisation);
        content.put("country", country);

        OpenWISMail openWISMail = OpenWISMailFactory.buildRequestAccountAdminMail(context, "AccountRequest.subject2", content);
        boolean result = mail.send(openWISMail);
        if (!result) {
            Log.error(Geonet.SELF_REGISTER, "Error during Account Request : error while sending email to the administrator ("+openWISMail.getAdministratorAddress()+") about account request of user "+email);
        } else {
            Log.info(Geonet.SELF_REGISTER, "Email sent successfully to the administrator ("+openWISMail.getAdministratorAddress()+") about account request of user "+email);
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
        String[] uris=url.split("/");
        StringBuilder baseUrl = new StringBuilder("/");
        for (int i = 1; i<uris.length;i++ ) {

            boolean done = false;
            if ( uris[i].contains("user-portal") || uris[i].contains("admin-portal") ) {
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
