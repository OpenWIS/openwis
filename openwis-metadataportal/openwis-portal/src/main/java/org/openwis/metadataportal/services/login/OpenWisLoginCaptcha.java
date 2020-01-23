package org.openwis.metadataportal.services.login;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import org.openwis.metadataportal.services.util.OpenWISMessages;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OpenWisLoginCaptcha extends HttpServlet {

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
            String userCaptchaResponse = request.getParameter("jcaptcha");
            //Check whether the captcha passed or not
            boolean captchaPassed = OpenWisImageCaptchaServlet.validateResponse(request, userCaptchaResponse);
            ServiceContext context = (ServiceContext) request.getSession().getAttribute("context");


            //If captcha passed, send mail to end user
            if (captchaPassed)
            {
                String[] uris=request.getRequestURI().split("/");
                String redirect = "/"+uris[1]+"/openWisInit";
                response.setStatus(307); //this makes the redirection keep your requesting method as is.
                response.addHeader("Location", redirect);
            }
            else {
                String errorMessage= OpenWISMessages.format("LoginCaptcha.captchaFailed", context.getLanguage());
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
