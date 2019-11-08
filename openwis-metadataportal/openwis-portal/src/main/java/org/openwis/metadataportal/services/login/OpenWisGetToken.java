/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jeeves.server.UserSession;
import jeeves.utils.Log;

import org.apache.commons.lang.StringUtils;
import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.SecurityServiceAlerts;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;

/**
 * Servlet which gets the response of the user token preferred IdP requests. <P>
 * And Stores the user token session in the Jeeves user session. <P>
 * 
 */
@SuppressWarnings("serial")
public class OpenWisGetToken extends HttpServlet {

   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

      HttpSession httpSession = req.getSession();
      UserSession session = (UserSession) httpSession.getAttribute(LoginConstants.SESSION);

      // Get token
      String token = req.getParameter(LoginConstants.TOKEN);
      token = URLEncoder.encode(token, "UTF-8");

      // retrieve language
//      String language = (String) session.getProperty(LoginConstants.LANG);
//      if (StringUtils.isBlank(language)) {
//         // English is the default language
//         language = "en";
//      }

      String language = "en";
      Object relayState = session.getProperty(LoginConstants.RELAY_STATE);
      if (relayState != null && !(((String) relayState).equals("null"))
            && StringUtils.isNotBlank((String) relayState)) {

         String strRelayState = (String) relayState;
         if (strRelayState.contains(LoginConstants.LANG + ":")) {
            int start = strRelayState.indexOf(LoginConstants.LANG) + LoginConstants.LANG.length() + 1;
            language = strRelayState.substring(start, start + 2);
            if (language == null) {
               // en by default, if not found
               language = "en";
            }
         }
      }
      
      session.setProperty(LoginConstants.TOKEN, token);

      boolean isApplyGroupEmpty = (Boolean) session
            .getProperty(LoginConstants.IS_APPLY_GROUP_EMPTY);

      Log.debug(LoginConstants.LOG, "Token id is " + token);

      if (isApplyGroupEmpty) {
         AlertService alertService = ManagementServiceProvider.getAlertService();
         if (alertService == null) {
            Log.error(LoginConstants.LOG,
                  "Could not get hold of the AlertService. No alert was passed!");
            return;
         }

         String source = "openwis-metadataportal-OpenWisGetToken";
         String location = "OpenWisGetToken";
         String severity = null;
         String eventId = SecurityServiceAlerts.UNAUTHORIZED_ACCESS.getKey();

         List<Object> arguments = new ArrayList<Object>();
         arguments.add(session.getUsername());

         alertService.raiseEvent(source, location, severity, eventId, arguments);

         forwardError(req, resp, language, LoginConstants.ACCESS_DENIED_MSG);
         
      } else {
         SessionCounter.sessionAuthenticatedCreated();
         resp.sendRedirect("srv/" + language + "/user.login");
      }
   }
   
   private void forwardError(HttpServletRequest request, HttpServletResponse response, String language,
         String message) throws ServletException, IOException {
      request.getRequestDispatcher("/srv/" + language + "/show.error?message=" + message + "&needLogout=true").forward(request,
            response);
   }

}
