/**
 * 
 */
package org.openwis.metadataportal.services.user;

import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.deployment.OpenwisDeploymentsConfig;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.model.user.BackUp;
import org.openwis.metadataportal.model.user.Profile;
import org.openwis.metadataportal.model.user.User;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.login.LoginConstants;
import org.openwis.metadataportal.services.user.dto.BackUpDTO;
import org.openwis.metadataportal.services.user.dto.ClassOfServiceDTO;
import org.openwis.metadataportal.services.user.dto.ProfileDTO;
import org.openwis.metadataportal.services.user.dto.UserDTO;
import org.openwis.securityservice.ClassOfService;

/**
 * Get User Service.
 * 
 */
public class Get implements Service {

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

      String username = getUsernameFromRequest(context, userDTO);

      if (StringUtils.isEmpty(username)) {
         User user = new User();
         user.setProfile(Profile.User.name());
         user.setClassOfService(ClassOfService.BRONZE);
         user.setNeedUserAccount(true);
         userDTO.setUser(user);
      } else {
         UserManager um = new UserManager(dbms);
         User user = um.getUserByUserName(username);

         //Remove group Default
         List<Group> userGroupExceptDefault = new ArrayList<Group>();
         for (Group group : user.getGroups()) {
            if (!LoginConstants.DEFAULT.equals(group.getName())) {
               //Remove group Default
               userGroupExceptDefault.add(group);
            }
         }
         user.setGroups(userGroupExceptDefault);
         userDTO.setUser(user);
      }

      //Build Class Of Service DTO
      for (ClassOfService classOfService : ClassOfService.values()) {
         ClassOfServiceDTO classOfServiceDTO = new ClassOfServiceDTO();
         classOfServiceDTO.setId(classOfService.value());
         classOfServiceDTO.setName(classOfService.value());
         userDTO.getClassOfServices().add(classOfServiceDTO);
      }

      //Build Profile DTO
      for (Profile profile : Profile.values()) {
         ProfileDTO profileDTO = new ProfileDTO();
         profileDTO.setId(profile.name());
         profileDTO.setName(profile.name());
         userDTO.getProfiles().add(profileDTO);
      }

      //Build Groups
      GroupManager gm = new GroupManager(dbms);
      List<Group> groups = gm.getAllGroups();
      for (Group group : userDTO.getUser().getGroups()) {
         if (groups.contains(group)) {
            groups.remove(group);
         }
      }
      List<Group> groupsWithoutDefault = new ArrayList<Group>();
      for (Group group : groups) {
         if (!LoginConstants.DEFAULT.equals(group.getName())) {
            //Remove group Default
            groupsWithoutDefault.add(group);
         }
      }

      userDTO.setGroups(groupsWithoutDefault);

      //Build Back up centres.
      for (String backup : OpenwisDeploymentsConfig.getBackUps()) {
         BackUpDTO backUpDTO = new BackUpDTO();
         backUpDTO.setName(backup);
         userDTO.getBackups().add(backUpDTO);
      }

      // Remove data already in backups list of the user.
      for (BackUp backup : userDTO.getUser().getBackUps()) {
         BackUpDTO backUpDTO = new BackUpDTO();
         backUpDTO.setName(backup.getName());
         if (userDTO.getBackups().contains(backUpDTO)) {
            userDTO.getBackups().remove(backUpDTO);
         }
      }
      return JeevesJsonWrapper.send(userDTO);
   }

   /**
    * Extract the username of the user to retrieve from the request information.  This
    * can be overridden by subclasses to restrict the usernames that can be retrieved. 
    * 
    * @param context
    * @param userDTO
    * @param username
    * @return
    */
   protected String getUsernameFromRequest(ServiceContext context, UserDTO userDTO) {
      if (userDTO != null && (userDTO.getUser() != null || userDTO.isEditingPersoInfo())) {
         if (userDTO.isEditingPersoInfo()) {
            return context.getUserSession().getUsername();
         } else if (userDTO.getUser().getUsername() != null) {
            return userDTO.getUser().getUsername();
         }
      }
      return null;
   }
}
