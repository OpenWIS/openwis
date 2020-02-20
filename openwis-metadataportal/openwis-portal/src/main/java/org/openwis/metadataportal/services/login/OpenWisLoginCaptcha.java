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

    // Url for verifying client captcha response
    private final String GOOGLE_CAPTCHA_VERIFICATION_URL="https://www.google.com/recaptcha/api/siteverify";

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
            boolean captchaPassed = false;
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(GOOGLE_CAPTCHA_VERIFICATION_URL);
            // set headers
            method.setRequestHeader("Content-Type","application/json");
            method.setRequestHeader("Accept", "application/json");

            NameValuePair[] queryParam = {
                    new NameValuePair("secret", OpenwisMetadataPortalConfig
                            .getString(ConfigurationConstants.GOOGLE_RECAPTCHA_SECRET_KEY)),
                    new NameValuePair("response", request.getParameter(GOOGLE_CAPTCHA_PARAMETER_RESPONSE))
            };
            method.setQueryString(queryParam);

            int statusCode = client.executeMethod(method);
            if (statusCode == HttpStatus.SC_OK) {
                // Read the response body.
                byte[] responseBody = method.getResponseBody();

                JSONObject json = new JSONObject(new String(responseBody));
                // Deal with the response.
                captchaPassed = json.getBoolean("success");
            }

            if (captchaPassed)
            {
                String[] uris=request.getRequestURI().split("/");
                String redirect = "/"+uris[1]+"/openWisInit";
                response.setStatus(307); //this makes the redirection keep your requesting method as is.
                response.addHeader("Location", redirect);
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

        String[] uris=request.getRequestURI().split("/");
        String redirect = "/"+uris[1]+"/srv/en/user.loginCaptcha.get?errorMessage="+message;
        response.setStatus(307); //this makes the redirection keep your requesting method as is.
        response.addHeader("Location", redirect);
    }
}
