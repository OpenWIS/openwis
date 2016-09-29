/**
 * 
 */
package org.openwis.metadataportal.services.privileges;

import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.SecurityServiceAlerts;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleStringDTO;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.OpenWISMessages;

/**
 * Extends UsePrivileges service. <P>
 * Send an e-mail to the administrator. <P>
 */
public class ExtendsPrivileges implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      SimpleStringDTO msg = JeevesJsonWrapper.read(params, SimpleStringDTO.class);
      
      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);
      
      String username = context.getUserSession().getUsername();
      String firstname = context.getUserSession().getName();
      String lastname = context.getUserSession().getSurname();

      Log.info(Geonet.PRIVILEGES, "Privilege Extension request: from " + username + "; message: " + msg.getContent());
      //Send Mail To User
      String subject = OpenWISMessages.format("ExtendsPrivileges.subject", context.getLanguage(), username);
      String content = getContent(username, firstname,
            lastname, msg.getContent(), context.getLanguage());

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      SettingManager sm = gc.getSettingManager();

      String host = sm.getValue("system/feedback/mailServer/host");
      String port = sm.getValue("system/feedback/mailServer/port");
      String from = context.getUserSession().getMail();
      Log.debug(Geonet.PRIVILEGES, "host : " + host + " port: " + port + " from : " + from);
      
      MailUtilities mail = new MailUtilities();
      boolean result = mail.sendMail(host, Integer.parseInt(port), subject, from, new String[]{from},
            content);
      
      if (!result) {
         acknowledgementDTO = new AcknowledgementDTO(false, OpenWISMessages.getString("ExtendsPrivileges.error", context.getLanguage()));
         Log.error(Geonet.PRIVILEGES, "Privilege Extension: Error while sending email to feedback email " + from);
      } else {
         Log.info(Geonet.PRIVILEGES, "Privilege Extension: successfully requested");
      }
      
      raiseAlarm(username, msg.getContent());

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }
   
   private void raiseAlarm(String username, String content) {
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null){
         Log.error(LoginConstants.LOG, "Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "Portal";
      String location = "ExtendsPrivileges";
      String eventId = SecurityServiceAlerts.USER_PRIVILEGES_REQUEST.getKey();

      List<Object> arguments = new ArrayList<Object>();
      arguments.add(username);
      arguments.add(content);

      alertService.raiseEvent(source, location, null, eventId, arguments);
   }

   // --------------------------------------------------------------------------

   /**
    * Get content for the email message to user.
    * 
    * @param username The user name
    * @param siteURL The site URL
    * @param thisSite This site.
    * @return the content of the mail.
    */
   private String getContent(String username, String firstname, String lastname, String msg, String lang) {
      String mailContent = OpenWISMessages.format("ExtendsPrivileges.mailContent", lang, firstname, lastname, username, msg);
      Log.debug(Geonet.PRIVILEGES, "Mail Content : " + mailContent);
      return mailContent;
   }
}
