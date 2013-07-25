/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jeeves.utils.Log;

/**
 * Class for selected domain
 */
@SuppressWarnings("serial")
public class OpenWisDomainSelected extends HttpServlet {

   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      try {
         String domain = request.getParameter("radio-value");
         String relayState = request.getParameter("RelayState");
         String lang = request.getParameter("lang");

         String redirect = "openWisInit?idpEntityID=" + domain;
         if (relayState != null) {
            redirect = redirect + "&RelayState=" + relayState;
         }
         if (lang != null) {
            redirect += "&" + LoginConstants.LANG + "="  + lang;
         }
         response.sendRedirect(redirect);
      } catch (IOException e) {
         Log.error(LoginConstants.LOG, "Error processing Request  : " + e.getMessage());
         forwardError(request, response, "Error during logout process - " + e.getMessage());
      }
   }

   private void forwardError(HttpServletRequest request, HttpServletResponse response,
         String message) throws ServletException, IOException {
      request.getRequestDispatcher("/srv/en/show.error?message=" + message).forward(request,
            response);
   }

}
