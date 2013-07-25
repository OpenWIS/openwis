/**
 * 
 */
package org.openwis.metadataportal.services.login;

import java.sql.SQLException;
import java.util.List;

import jeeves.exceptions.UserLoginEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.SerialFactory;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.jdom.Element;
import org.openwis.metadataportal.services.login.error.OpenWisLoginEx;

/**
 * Class for authorization.
 * Create or Update user in database.
 * Create table user groups user.id and group.id
 * 
 */
public class OpenWisLogin implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {
	  Log.debug(LoginConstants.LOG, "User login: " + context.getUserSession());
	  
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      try {

         // update or insert user in database.
         updateUser(context, dbms, context.getUserSession());

         // attempt to load user from db.
         String username = context.getUserSession().getUsername();
         String query = "SELECT * FROM Users WHERE username = ?";

         // if no user in database => throw exception.
         @SuppressWarnings("unchecked")
         List<Element> list = dbms.select(query, username).getChildren();
         if (list.size() == 0) {
            throw new UserLoginEx(username);
         }
         Element user = (Element) list.get(0);

         String sId = user.getChildText(Geonet.Elem.ID);
         String sName = user.getChildText(Geonet.Elem.NAME);
         String sSurname = user.getChildText(Geonet.Elem.SURNAME);
         String sProfile = user.getChildText(Geonet.Elem.PROFILE);
         String sMail = context.getUserSession().getMail();

         //Update table UserGroups
         @SuppressWarnings("unchecked")
         List<String> groups = (List<String>) context.getUserSession().getProperty(Params.GROUPS);
         updateTableUserGroup(sId, dbms, groups);

         context.info("User '" + username + "' logged in as '" + sProfile + "'");
         context.getUserSession().authenticate(sId, username, sName, sSurname, sProfile, sMail);
      } catch (SQLException e) {
         context.getUserSession().authenticate(null, null, null, null, null, null);
         Log.error(LoginConstants.LOG, "Error during sql requests  : " + e.getMessage());
         throw new OpenWisLoginEx();
      }

      return new Element("ok");
   }

   /**
    * Update the user if he exists or Insert the user otherwise.
    * @param context The service context.
    * @param dbms The dbms.
    * @param info The user session info
    * @throws SQLException if an error occurs.
    */
   private void updateUser(ServiceContext context, Dbms dbms, UserSession info) throws SQLException {
      //--- update user information into the database
      String query = "UPDATE Users SET name=?, surname=?, profile=? WHERE username=?";

      int res = dbms.execute(query, info.getName(), info.getSurname(), info.getProfile(), info.getUsername());
      Log.debug(LoginConstants.LOG, "Update Users " + info.getUsername());

      //--- if the user was not found --> add it

      if (res == 0) {
         int id = SerialFactory.getSerial(dbms, "Users");
         query = "INSERT INTO Users(id, username, password, surname, name, profile) "
               + "VALUES(?,?,?,?,?,?)";

         dbms.execute(query, id, info.getUsername(), "DUMMY", info.getSurname(), info.getName(),
               info.getProfile());
         Log.debug(LoginConstants.LOG, "User does not exits, insert into Users " + info.getUsername());
      }

      dbms.commit();
   }

   /**
    * Update the Table User Group.
    * @param sId The user id.
    * @param dbms The dbms.
    * @param listGroups List of user's groups.
    * @throws SQLException if an error occurs.
    */
   private void updateTableUserGroup(String sId, Dbms dbms, List<String> listGroups)
         throws SQLException {

      dbms.execute("DELETE FROM UserGroups WHERE userId=?", new Integer(sId));
      Log.debug(LoginConstants.LOG, "Delete from UserGroups " + sId +  " listGroups=" + listGroups);
 	  	
      for (int i = 0; i < listGroups.size(); i++) {
         String group = (String) listGroups.get(i);

         //get Group Name
         String[] groupDN = group.split(",");
         String groupName = groupDN[0].split("cn=")[1];

         dbms.execute(
               "INSERT INTO UserGroups(groupId, userId) VALUES ((SELECT Groups.id FROM Groups WHERE Groups.name = ?),?)",
               groupName, new Integer(sId));
         Log.debug(LoginConstants.LOG, "Insert into UserGroups " + sId + ", groupName=" + groupName);

      }

   }

}
