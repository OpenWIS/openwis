/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jeeves.server.UserSession;
import jeeves.utils.Log;

import org.apache.commons.lang.StringUtils;

import com.sun.identity.saml2.common.SAML2Constants;

/**
 * Logout the user
 */
@SuppressWarnings("serial")
public class Logout extends HttpServlet {

   /**
    * Comment for <code>FEDLET_SLO_SP_ENTITY_ID</code>
    * @member: FEDLET_SLO_SP_ENTITY_ID
    */
   private static final String FEDLET_SLO_SP_ENTITY_ID = "fedletSloInit?spEntityID=";

   /**
    * Comment for <code>FEDLET_SLO_IDP_ENTITY_ID</code>
    * @member: FEDLET_SLO_IDP_ENTITY_ID
    */
   private static final String FEDLET_SLO_IDP_ENTITY_ID = "&idpEntityID=";

   /**
    * Comment for <code>FEDLET_SLO_NAME_ID_VALUE</code>
    * @member: FEDLET_SLO_NAME_ID_VALUE
    */
   private static final String FEDLET_SLO_NAME_ID_VALUE = "&NameIDValue=";

   /**
    * Comment for <code>FEDLET_SLO_SESSION_INDEX</code>
    * @member: FEDLET_SLO_SESSION_INDEX
    */
   private static final String FEDLET_SLO_SESSION_INDEX = "&SessionIndex=";

   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      try {
         String lang = request.getParameter(LoginConstants.LANG);
         
         UserSession userSession = (UserSession) request.getSession().getAttribute("session");
         if (userSession != null) {

            //Get User token
            String token = (String) userSession.getProperty(LoginConstants.TOKEN);
            String idpUrl = (String) userSession.getProperty(LoginConstants.PREFERRED_IDP_URL);
            String entityID = (String) userSession.getProperty(LoginConstants.IDP_ENTITY_ID);
            String spEntityID = (String) userSession.getProperty(LoginConstants.SP_ENTITY_ID);
            String sessionIndex = (String) userSession.getProperty(LoginConstants.SESSION_INDEX);
            String nameId = (String) userSession.getProperty(LoginConstants.NAME_ID);

            userSession.authenticate(null, null, null, null, null, null);

            userSession.removeProperty(LoginConstants.TOKEN);
            userSession.removeProperty(LoginConstants.PREFERRED_IDP_URL);
            userSession.removeProperty(LoginConstants.IDP_ENTITY_ID);
            userSession.removeProperty(LoginConstants.SP_ENTITY_ID);
            userSession.removeProperty(LoginConstants.SESSION_INDEX);
            userSession.removeProperty(LoginConstants.NAME_ID);
            userSession.removeProperty(LoginConstants.MAIN_SEARCH);

            request.getSession().setAttribute("session", userSession);
            
            SessionCounter.sessionAuthenticatedDestroyed();

            //Check if token is valid
            TokenUtilities tokenUtilities = new TokenUtilities();
            String redirectURL = new String();
            if (tokenUtilities.isTokenValid(idpUrl, token)) {
               redirectURL = FEDLET_SLO_SP_ENTITY_ID + spEntityID + FEDLET_SLO_IDP_ENTITY_ID
                     + entityID + FEDLET_SLO_NAME_ID_VALUE + nameId + FEDLET_SLO_SESSION_INDEX
                     + sessionIndex;

               // add relay state to redirect to specific language home page
               if (StringUtils.isNotBlank(lang)) {
                  redirectURL += "&" + SAML2Constants.RELAY_STATE + "=srv/" + lang + "/main.home";
               }
               
            } else {
               redirectURL = LoginConstants.HOME_PAGE;
            }
            response.sendRedirect(redirectURL);
         } else {
            forwardError(request, response, "No session defined");
         }
      } catch (Exception e) {
         Log.error(LoginConstants.LOG, e.getMessage(), e);
         forwardError(request, response, "Error during logout process - " + e.getMessage());
      }
   }

   private void forwardError(HttpServletRequest request, HttpServletResponse response,
         String message) throws ServletException, IOException {
      request.getRequestDispatcher("/srv/en/show.error?message=" + message).forward(request,
            response);
   }
}
