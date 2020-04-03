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
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.user.dto.UsersDTO;
import org.openwis.metadataportal.services.util.UserLogUtils;

import java.time.LocalDateTime;

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
         um.removeUser(user.getUsername());

         // save log
         userActionLogDTO = new UserLogDTO();
         userActionLogDTO.setAction(UserAction.REMOVE);
         userActionLogDTO.setDate(LocalDateTime.now());
         userActionLogDTO.setUsername(user.getUsername());
         userActionLogDTO.setActioner(context.getUserSession().getUsername());
         UserLogUtils.saveLog(dbms, userActionLogDTO);
      }

      //Send Acknowledgement
      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
