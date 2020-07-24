/**
 * 
 */
package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.user.dto.UsersDTO;
import org.openwis.metadataportal.services.util.MailUtilities;
import org.openwis.metadataportal.services.util.UserLogUtils;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;
import org.openwis.metadataportal.services.util.mail.OpenWISMailFactory;
import org.openwis.securityservice.InetUserStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Remove implements Service {

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
      //Read from Ajax Request.

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      UsersDTO dto = JeevesJsonWrapper.read(params, UsersDTO.class);

      UserManager um = new UserManager(dbms);

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);
      UserLogDTO userActionLogDTO = null;

      for (User user : dto.getUsers()) {
         // Remove all associated requests/subscriptions
         RequestManager requestManager = new RequestManager();
         requestManager.removeUserRequests(user.getUsername());
         
         // Remove user
         User user1 = um.getUserByUserName(user.getUsername());
         um.removeUser(user.getUsername());
         if (user1.getInetUserStatus() == InetUserStatus.INACTIVE) {
            this.sendEmailToUser(context, user1);
         }

         // save log
         userActionLogDTO = new UserLogDTO();
         userActionLogDTO.setAction(UserAction.REMOVE);
         userActionLogDTO.setDate(LocalDateTime.now());
         userActionLogDTO.setUsername(user.getUsername());
         userActionLogDTO.setActioner(context.getUserSession().getUsername());
         UserLogUtils.save(dbms, userActionLogDTO);
      }

      //Send Acknowledgement
      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

   private boolean sendEmailToUser(ServiceContext context, User user) {
      Map<String, Object> bodyData = new HashMap<>();
      bodyData.put("firstname", user.getName());
      bodyData.put("lastname", user.getSurname());
      bodyData.put("period", OpenwisMetadataPortalConfig.getString(ConfigurationConstants.ACCOUNT_TASK_INACTIVITY_PERIOD));
      bodyData.put("timeUnit", OpenwisMetadataPortalConfig.getString(ConfigurationConstants.ACCOUNT_TASK_TIME_UNIT));

      IOpenWISMail mail = OpenWISMailFactory.buildAccountTerminationMail(context, "UserTermination.subject", new String[]{user.getEmailContact()}, bodyData);
      return new MailUtilities().send(mail);
   }

}
