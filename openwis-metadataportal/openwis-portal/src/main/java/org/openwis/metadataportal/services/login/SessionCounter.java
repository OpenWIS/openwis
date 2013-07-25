/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import jeeves.server.UserSession;
import jeeves.utils.Log;

import org.apache.commons.lang.StringUtils;
import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.SecurityServiceAlerts;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;

/**
 * Session Counter: used to check activity thresholds and statistics.
 */
public class SessionCounter implements HttpSessionListener {
   
   private static int activeSessions = 0;
   
   private static int authenticatedSession = 0;
   
   /** number of sessions created since last call (statistics) */
   private static int sessionsCreated = 0;

   /** number of authenticated sessions created since last call (statistics) */
   private static int authenticatedSessionsCreated = 0;

   public void sessionCreated(HttpSessionEvent se) {
      activeSessionCreated();
   }

   public void sessionDestroyed(HttpSessionEvent se) {
      activeSessionDestroyed(se);
   }

   private static int getActiveSessions() {
        return activeSessions;
   }
   
   private static int getSessionAuthenticated() {
      return authenticatedSession;
   }
   
   public synchronized static void activeSessionCreated() {
      activeSessions++;  
      sessionsCreated++;
      SessionCounter sessionCounter = new SessionCounter();
      sessionCounter.checkActiveAnonymousUsersThreshold();
   }
   
   public synchronized static void activeSessionDestroyed(HttpSessionEvent se) {
      UserSession session = (UserSession) se.getSession().getAttribute(LoginConstants.SESSION);
      if (session != null && StringUtils.isNotEmpty(session.getUserId())) {
         if (authenticatedSession > 0) {
            authenticatedSession--;
         }
      }
      if (activeSessions > 0) {
         activeSessions--;
      }
   }
   
   public synchronized static void sessionAuthenticatedCreated() {
      authenticatedSession++;
      authenticatedSessionsCreated++;
      SessionCounter sessionCounter = new SessionCounter();
      sessionCounter.checkAuthenticatedUsersThreshold();
      
   }
   
   public synchronized static void sessionAuthenticatedDestroyed() {
      authenticatedSession--;
   }
   
   private void checkAuthenticatedUsersThreshold() {
      int authenticatedUsersThreshold = OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.TOO_MANY_ACTIVE_USERS);
      int authenticatedSession = SessionCounter.getSessionAuthenticated();
      if (authenticatedSession >= authenticatedUsersThreshold) {
         AlertService alertService = ManagementServiceProvider.getAlertService();
         if (alertService == null){
            Log.error(LoginConstants.LOG, "Could not get hold of the AlertService. No alert was passed!");
            return;
         }

         String source = "Portal";
         String location = "SessionCounter";
         String eventId = SecurityServiceAlerts.TOO_MANY_ACTIVE_USERS.getKey();

         List<Object> arguments = new ArrayList<Object>();
         arguments.add(authenticatedUsersThreshold);
         arguments.add(authenticatedSession);

         alertService.raiseEvent(source, location, null, eventId, arguments);
      } 
   }
   
   private void checkActiveAnonymousUsersThreshold() {
      int activeAnonymousUsersThreshold = OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.TOO_MANY_ANONYMOUS_USERS);
      int nbAnonymousSession = SessionCounter.getActiveSessions() - SessionCounter.getSessionAuthenticated();
      if (nbAnonymousSession >= activeAnonymousUsersThreshold) {
         AlertService alertService = ManagementServiceProvider.getAlertService();
         if (alertService == null){
            Log.error(LoginConstants.LOG, "Could not get hold of the AlertService. No alert was passed!");
            return;
         }

         String source = "Portal";
         String location = "SessionCounter";
         String eventId = SecurityServiceAlerts.TOO_MANY_ANONYMOUS_USERS.getKey();

         List<Object> arguments = new ArrayList<Object>();
         arguments.add(activeAnonymousUsersThreshold);
         arguments.add(nbAnonymousSession);

         alertService.raiseEvent(source, location, null, eventId, arguments);
      } 
   }
   
   /**
    * Get the number of sessions created and authenticated sessions created since last call,
    * and reset counters.
    * @return [sessionsCreated, authenticatedSessionsCreated]
    */
   public static int[] getSessionsCreated() {
      int[] sessionsArray = new int[] {sessionsCreated, authenticatedSessionsCreated};
      sessionsCreated = 0;
      authenticatedSessionsCreated = 0;
      return sessionsArray;
   }
   
}
