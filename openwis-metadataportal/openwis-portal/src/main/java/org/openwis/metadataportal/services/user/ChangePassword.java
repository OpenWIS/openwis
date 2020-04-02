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
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.user.dto.PasswordDTO;
import org.openwis.metadataportal.services.user.dto.UserActions;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;
import org.openwis.metadataportal.services.util.UserLogUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ChangePassword implements Service {

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

      PasswordDTO password = JeevesJsonWrapper.read(params, PasswordDTO.class);

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);
      UserLogDTO userActionLogDTO = null;

      UserManager um = new UserManager(dbms);
      String username = context.getUserSession().getUsername();
      um.changePassword(username, password.getPassword());

      try {
         updateLastPasswordChangeTimestamp(dbms, username);

         // save log
          userActionLogDTO = new UserLogDTO();
          userActionLogDTO.setAction(UserActions.PASSWORD_CHANGE);
          userActionLogDTO.setDate(LocalDateTime.now());
          userActionLogDTO.setUsername(username);
          userActionLogDTO.setActioner(username);
          userActionLogDTO.setActioner(username);
          UserLogUtils.saveLog(dbms, userActionLogDTO);

      } catch (SQLException e) {
         Log.error(LoginConstants.LOG, "Error during sql requests  : " + e.getMessage());
      }
       //Send Acknowledgement
      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

   private void updateLastPasswordChangeTimestamp(Dbms dbms, String username) throws SQLException {
      String query = "UPDATE Users SET lastpasswordchange=? WHERE username=?";

      Timestamp timestamp = new Timestamp(new Date().getTime());
      int res = dbms.execute(query , timestamp, username);
      Log.debug(LoginConstants.LOG, "Update  last passowrd change: " + username );
      dbms.commit();
   }

}
