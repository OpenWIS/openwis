/**
 * 
 */
package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import jeeves.utils.Log;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.UserAlreadyExistsException;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.user.dto.UserAction;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.user.dto.UserDTO;
import org.openwis.metadataportal.services.util.UserLogUtils;
import org.openwis.securityservice.OpenWISUserUpdateLog;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
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
      UserLogDTO userActionLogDTO = null;

      try {
         if (user.isCreationMode()) {
            um.createUser(user.getUser());

            // create action log entry
            userActionLogDTO = new UserLogDTO();
            userActionLogDTO.setActioner(this.getUsernameFromRequest(context));
            userActionLogDTO.setAction(UserAction.CREATE);
            userActionLogDTO.setUsername(user.getUser().getUsername());
            userActionLogDTO.setDate(LocalDateTime.now());
            UserLogUtils.saveLog(dbms, userActionLogDTO);

         } else {
            List<OpenWISUserUpdateLog> updateLogs = um.updateUser(user.getUser());
            for (OpenWISUserUpdateLog updateLog: updateLogs) {
               userActionLogDTO = UserLogUtils.buildLog(updateLog);
               userActionLogDTO.setActioner(context.getUserSession().getUsername());
               UserLogUtils.saveLog(dbms, userActionLogDTO);
            }
            // call method checkSubscription on RequestManager service.
            RequestManager requestManager = new RequestManager();
            requestManager.checkUserSubscription(user.getUser().getUsername(), dbms);

            // if password was changes update the timestamp
            if (!user.getUser().getPassword().isEmpty()) {
               this.updateLastPasswordChangeTimestamp(dbms, user.getUser().getUsername());
            }

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

   private void updateLastPasswordChangeTimestamp(Dbms dbms, String username) throws SQLException {
      String query = "UPDATE Users SET lastpasswordchange=? WHERE username=?";

      Timestamp timestamp = new Timestamp(new Date().getTime());
      int res = dbms.execute(query , timestamp, username);
      Log.debug(LoginConstants.LOG, "Update  last passowrd change: " + username );
      dbms.commit();
   }
}
