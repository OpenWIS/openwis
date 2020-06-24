package org.openwis.metadataportal.services.login;

import jeeves.utils.Log;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.services.util.OpenWISMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OpenWisLoginCaptcha extends HttpServlet {

    private final String GOOGLE_CAPTCHA_PARAMETER_RESPONSE="g-recaptcha-response";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

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
            Boolean captchaPassed = GoogleCaptchaVerificator.verify(request.getParameter(GOOGLE_CAPTCHA_PARAMETER_RESPONSE));
            if (captchaPassed)
            {

                String baseUrl = this.getBaseUrl(request.getRequestURI());
                baseUrl += "openWisInit";
                response.setStatus(307); //this makes the redirection keep your requesting method as is.
                response.addHeader("Location", baseUrl );
            }
            else {
                String errorMessage= OpenWISMessages.format("LoginCaptcha.captchaFailed", "en");
                forwardError(request, response, errorMessage);
            }
        } catch (Exception e) {
            Log.error(LoginConstants.LOG, "Error processing Login captcha  : " + e.getMessage());
            forwardError(request, response, "Error during login captcha - " + e.getMessage());
        }
    }


    private void forwardError(HttpServletRequest request, HttpServletResponse response,
                              String message) throws ServletException, IOException {

        String baseUrl= this.getBaseUrl(request.getRequestURI());
        String redirect = baseUrl +"/srv/en/user.loginCaptcha.get?errorMessage="+message;
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
}
