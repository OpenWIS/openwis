/**
 * 
 */
package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.bouncycastle.util.Strings;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.UserAlreadyExistsException;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.ActionLog;
import org.openwis.metadataportal.services.user.dto.UserActionLogDTO;
import org.openwis.metadataportal.services.user.dto.UserDTO;
import org.openwis.metadataportal.services.util.DateTimeUtils;
import org.openwis.metadataportal.services.util.UserActionLogUtils;
import org.openwis.securityservice.OpenWISUserUpdateLog;
import java.util.List;
import java.sql.Timestamp;

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
      UserDTO user = JeevesJsonWrapper.read(params, UserDTO.class);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      UserManager um = new UserManager(dbms);

      AcknowledgementDTO acknowledgementDTO = null;
      UserActionLogDTO userActionLogDTO = null;

      try {
         if (user.isCreationMode()) {
            um.createUser(user.getUser());

            // create action log entry
            userActionLogDTO = new UserActionLogDTO();
            userActionLogDTO.setActionerUsername(this.getUsernameFromRequest(context));
            userActionLogDTO.setAction(ActionLog.CREATE);
            userActionLogDTO.setUsername(user.getUser().getUsername());
            userActionLogDTO.setDate(Timestamp.from(DateTimeUtils.getUTCInstant()));
            UserActionLogUtils.saveLog(dbms, userActionLogDTO);

         } else {
            List<OpenWISUserUpdateLog> updateLogs = um.updateUser(user.getUser());
            for (OpenWISUserUpdateLog updateLog: updateLogs) {
               userActionLogDTO = UserActionLogUtils.buildLog(updateLog);
               userActionLogDTO.setActionerUsername(context.getUserSession().getUsername());
               UserActionLogUtils.saveLog(dbms, userActionLogDTO);
            }
            // call method checkSubscription on RequestManager service.
            RequestManager requestManager = new RequestManager();
            requestManager.checkUserSubscription(user.getUser().getUsername(), dbms);

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
