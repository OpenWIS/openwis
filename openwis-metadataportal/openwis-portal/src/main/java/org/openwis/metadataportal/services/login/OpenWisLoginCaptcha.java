package org.openwis.metadataportal.services.login;

import jeeves.utils.Log;
import org.openwis.metadataportal.services.util.OpenWISMessages;
import jeeves.server.context.ServiceContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class OpenWisLoginCaptcha extends HttpServlet {

    private final String GOOGLE_CAPTCHA_PARAMETER_RESPONSE="g-recaptcha-response";

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        throw new ServletException("Method forbidden");
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

            ServiceContext context = (ServiceContext) request.getSession().getAttribute("context");

            Boolean captchaPassed = GoogleCaptchaVerificator.verify(request.getParameter(GOOGLE_CAPTCHA_PARAMETER_RESPONSE),context);
            if (captchaPassed)
            {
                // generate one time init token. this token is used to allow access to openWisInit service.
                String initToken = generateInitToken();
                request.getSession().setAttribute("initToken", initToken);

                String baseUrl = this.getBaseUrl(request.getRequestURI());
                baseUrl += "openWisInit";
                response.setStatus(307); //this makes the redirection keep your requesting method as is.
                response.addHeader("Location", baseUrl );
                Cookie cookie = new Cookie("OpenWISInitToken", initToken);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            }
            else {
                String errorMessage= OpenWISMessages.format("LoginCaptcha.captchaFailed", "en");
                forwardError(request, response, errorMessage);
            }
        } catch (Exception e) {
            Log.error(LoginConstants.LOG, "Error processing Login captcha  : " + e.getMessage(), e);
            forwardError(request, response, "Error during login captcha - " + e.getMessage());
        }
    }


    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              String message) throws ServletException, IOException {

        String baseUrl= this.getBaseUrl(request.getRequestURI());
        String redirect = baseUrl +"/srv/en/show.error";
        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.addHeader("Location", redirect);
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

    private static String generateInitToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
