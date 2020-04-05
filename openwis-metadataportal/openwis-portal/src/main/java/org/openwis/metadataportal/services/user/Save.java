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
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.UserAlreadyExistsException;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.TwoFactorAuthenticationKey;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.user.dto.UserDTO;
import org.openwis.metadataportal.services.util.UserLogUtils;
import org.openwis.securityservice.OpenWISUserUpdateLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Save implements Service {

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
      UserDTO userDTO = JeevesJsonWrapper.read(params, UserDTO.class);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      UserManager um = new UserManager(dbms);

      AcknowledgementDTO acknowledgementDTO = null;
      UserLogDTO userLogDTO = null;

      try {
         if (userDTO.isCreationMode()) {
            User user = userDTO.getUser();
            user.setSecretKey(new TwoFactorAuthenticationKey().getKeyBase16());
            um.createUser(user);

            // create action log entry
            userLogDTO = new UserLogDTO();
            userLogDTO.setActioner(this.getUsernameFromRequest(context));
            userLogDTO.setAction(UserAction.CREATE);
            userLogDTO.setUsername(userDTO.getUser().getUsername());
            userLogDTO.setDate(LocalDateTime.now());
            UserLogUtils.save(dbms, userLogDTO);

         } else {
            List<OpenWISUserUpdateLog> updateLogs = um.updateUser(userDTO.getUser());
            for (OpenWISUserUpdateLog updateLog: updateLogs) {
               userLogDTO = UserLogUtils.buildLog(updateLog);
               userLogDTO.setActioner(context.getUserSession().getUsername());
               UserLogUtils.save(dbms, userLogDTO);
            }
            // call method checkSubscription on RequestManager service.
            RequestManager requestManager = new RequestManager();
            requestManager.checkUserSubscription(userDTO.getUser().getUsername(), dbms);
         }
         acknowledgementDTO = new AcknowledgementDTO(true);
      } catch (UserAlreadyExistsException e) {
         acknowledgementDTO = new AcknowledgementDTO(false, "The user " + e.getUserName() + " already exists");
      }

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

   /**
    * Extract the username of the user to retrieve from the request information.
    * @param context
    * @return
    */
   private String getUsernameFromRequest(ServiceContext context) {
       return context.getUserSession().getUsername();
   }
}
