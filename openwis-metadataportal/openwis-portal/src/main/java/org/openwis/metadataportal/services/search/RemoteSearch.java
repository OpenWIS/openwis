/**
 * 
 */
package org.openwis.metadataportal.services.search;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openwis.metadataportal.services.login.LoginConstants;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
@SuppressWarnings("serial")
public class RemoteSearch extends HttpServlet {

   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
      // Get metadata URN
      String urn = req.getPathInfo().substring(1);

      String servletPath = req.getServletPath();
      String requestType = new String();
      if (servletPath.contains("request")) {
         requestType = "ADHOC";
      } else if (servletPath.contains("subscribe")) {
         requestType = "SUBSCRIPTION";
      }
      String backupRequestId = req.getParameter("backupRequestId");
      String deployment = req.getParameter("deployment");

      resp.sendRedirect(req.getContextPath() + "/openWisInit?" + LoginConstants.RELAY_STATE + "="
            + requestType + LoginConstants.RELAY_STATE_SEPARATOR + urn
            + LoginConstants.RELAY_STATE_SEPARATOR + backupRequestId
            + LoginConstants.RELAY_STATE_SEPARATOR + deployment);
   }
}
