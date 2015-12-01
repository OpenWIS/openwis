/**
 * 
 */
package org.openwis.usermanagement;

import static org.openwis.usermanagement.util.LdapUtils.BACKUPS;
import static org.openwis.usermanagement.util.LdapUtils.CLASSOFSERVICE;
import static org.openwis.usermanagement.util.LdapUtils.CN;
import static org.openwis.usermanagement.util.LdapUtils.CONTACT_EMAIL;
import static org.openwis.usermanagement.util.LdapUtils.DEFAULT;
import static org.openwis.usermanagement.util.LdapUtils.EMAILS;
import static org.openwis.usermanagement.util.LdapUtils.EQUAL;
import static org.openwis.usermanagement.util.LdapUtils.FTPS;
import static org.openwis.usermanagement.util.LdapUtils.GLOBAL;
import static org.openwis.usermanagement.util.LdapUtils.INET_USER_STATUS;
import static org.openwis.usermanagement.util.LdapUtils.INET_USER_STATUS_ACTIVE;
import static org.openwis.usermanagement.util.LdapUtils.NAME;
import static org.openwis.usermanagement.util.LdapUtils.NEEDUSERACCOUNT;
import static org.openwis.usermanagement.util.LdapUtils.OBJECT_CLASS;
import static org.openwis.usermanagement.util.LdapUtils.OPEN_WIS_USER;
import static org.openwis.usermanagement.util.LdapUtils.PASSWORD;
import static org.openwis.usermanagement.util.LdapUtils.PEOPLE;
import static org.openwis.usermanagement.util.LdapUtils.PROFILE;
import static org.openwis.usermanagement.util.LdapUtils.STAR;
import static org.openwis.usermanagement.util.LdapUtils.SURNAME;
import static org.openwis.usermanagement.util.LdapUtils.UID;
import static org.openwis.usermanagement.util.LdapUtils.UNIQUE_MEMBER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.lang.StringUtils;
import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.SecurityServiceAlerts;
import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.group.OpenWISGroup;
import org.openwis.usermanagement.model.user.OpenWISAddress;
import org.openwis.usermanagement.model.user.OpenWISUser;
import org.openwis.usermanagement.util.GroupUtils;
import org.openwis.usermanagement.util.JNDIConnectionUtils;
import org.openwis.usermanagement.util.JNDIUtils;
import org.openwis.usermanagement.util.LdapUtils;
import org.openwis.usermanagement.util.OpenWISEmailUtils;
import org.openwis.usermanagement.util.OpenWISFTPUtils;
import org.openwis.usermanagement.util.OpenWiSBackupUtils;
import org.openwis.usermanagement.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * Implements the user management interface.
 * The User Management Component is used for managed users. <P>
 * -  create / update / remove user <P>
 * -  get user informations <P>
 * -  add or update / get / remove email or ftp for dissemination parameters. <P>
 * -  get / modify user profile <P>
 * -  add / remove user to group
 * -  reset users
 */
@WebService(endpointInterface = "org.openwis.usermanagement.UserManagementService", targetNamespace = "http://securityservice.openwis.org/", portName = "UserManagementServicePort", serviceName = "UserManagementService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class UserManagementServiceImpl implements UserManagementService {

   /** The logger */
   private final Logger logger = LoggerFactory.getLogger(UserManagementServiceImpl.class);

   private static int MAX_RESULT = 1000;

   /**
    * The user management service utilities.
    * @member: userManagementServiceUtil
    */
   private UserManagementServiceUtil userManagementServiceUtil = new UserManagementServiceUtil();

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#createUser(org.openwis.usermanagement.model.user.OpenWISUser, java.lang.String)
    */
   @Override
   public void createUser(@WebParam(name = "user") OpenWISUser user,
         @WebParam(name = "centreName") String centreName) throws UserManagementException {
      logger.info("Creating User " + user.getUserName() + " in Centre " + centreName);

      JNDIConnectionUtils ldap = new JNDIConnectionUtils();
      GroupManagementServiceImpl groupManagementService = new GroupManagementServiceImpl();
      int threshold = ldap.getInt("register_users_threshold");
      int userCount = groupManagementService.countUsersInGroup(centreName);
      if (threshold < userCount) {
         AlertService alertService = ManagementServiceProvider.getAlertService();
         if (alertService == null) {
            logger.error("Could not get hold of the AlertService. No alert was passed!");
         } else {
            String source = "Security Service ";
            String location = "User Management Service Impl";

            String eventId = SecurityServiceAlerts.TOO_MANY_REGISTERED_USERS.getKey();

            List<Object> arguments = new ArrayList<Object>();
            arguments.add(threshold);
            arguments.add(userCount);

            alertService.raiseEvent(source, location, null, eventId, arguments);
         }
      }

      LDAPAttributeSet attributeSet = new LDAPAttributeSet();
      attributeSet.add(new LDAPAttribute(OBJECT_CLASS, new String[] {"OpenWisUser",
            "inetOrgPerson", "iPlanetPreferences", "inetuser",
            "iplanet-am-auth-configuration-service", "iplanet-am-managed-person",
            "iplanet-am-user-service", "sunAMAuthAccountLockout", "sunFMSAML2NameIdentifier",
            "sunFederationManagerDataStore", "sunIdentityServerLibertyPPService"}));
      attributeSet.add(new LDAPAttribute(UID, new String[] {user.getUserName()}));
      attributeSet.add(new LDAPAttribute(INET_USER_STATUS, INET_USER_STATUS_ACTIVE));
      attributeSet.add(new LDAPAttribute(CN, new String[] {user.getUserName()}));
      attributeSet.add(new LDAPAttribute(NAME, user.getName()));
      attributeSet.add(new LDAPAttribute(SURNAME, user.getSurName()));
      attributeSet.add(new LDAPAttribute(PASSWORD, user.getPassword()));
      attributeSet.add(new LDAPAttribute(CONTACT_EMAIL, user.getEmailContact()));
      // address
      if (user.getAddress() != null) {
         userManagementServiceUtil.setLdapAddressModification(user, attributeSet);
      }
      // emails
      if (user.getEmails() != null) {
         attributeSet.add(new LDAPAttribute(EMAILS, OpenWISEmailUtils.convertToString(user
               .getEmails())));
      }
      // ftps
      if (user.getFtps() != null) {
         attributeSet.add(new LDAPAttribute(FTPS, OpenWISFTPUtils.convertToString(user.getFtps())));
      }
      // profile
      attributeSet.add(new LDAPAttribute(PROFILE, user.getProfile()));
      // backup
      if (user.getBackUps() != null) {
         attributeSet.add(new LDAPAttribute(BACKUPS, OpenWiSBackupUtils
               .convertBackUpListToString(user.getBackUps())));
      }
      // need user account
      attributeSet.add(new LDAPAttribute(NEEDUSERACCOUNT, Boolean.valueOf(user.isNeedUserAccount())
            .toString()));
      // class of service
      if (user.getClassOfService() != null) {
         attributeSet.add(new LDAPAttribute(CLASSOFSERVICE, user.getClassOfService().name()));
      }
      String dn = UserUtils.getDn(user.getUserName());
      LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
      UtilEntry.addNewEntry(newEntry);
      // Add user to group
      if (user.getGroups() != null) {
         userManagementServiceUtil.addUserToGroup(user, this, user.getGroups());
      }
      //Add user to the "default" group of the centre
      addUserToLocalGroup(user.getUserName(), centreName, DEFAULT);
      logger.debug("\nAdded object: " + dn + " successfully.");

   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#getUserGroups(java.lang.String)
    */
   public List<OpenWISGroup> getUserGroups(@WebParam(name = "username") String username)
         throws UserManagementException {
      return userManagementServiceUtil.getUserGroups(username);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#removeUser(java.lang.String, java.lang.String)
    */
   @Override
   public void removeUser(@WebParam(name = "username") String username,
         @WebParam(name = "centre") String centre) throws UserManagementException {
      List<OpenWISGroup> openWISGroups = userManagementServiceUtil.getUserGroups(username);
      // Number of local centre where user belongs to.
      int numberOfLocalCentre = 0;
      OpenWISGroup userLocalCentre = null;
      for (OpenWISGroup openWISGroup : openWISGroups) {
         if (!openWISGroup.isIsGlobal()) {
            numberOfLocalCentre = numberOfLocalCentre + 1;
            if (centre.equals(openWISGroup.getCentreName())) {
               userLocalCentre = openWISGroup;
            }
         }
      }
      //User belongs to one local centre => remove the user on the LDAP.
      if (numberOfLocalCentre == 1) {
         logger.info("Removing User " + username);
         String deleteDN = UserUtils.getDn(username);
         UtilEntry.deleteEntry(deleteDN);
      } else {
         //User belongs to several local centres 
         // => remove the user of the centre where the admin wants to remove the user.
         logger.info("Removing User " + username + " from Centre " + centre);
         for (String groupId : userLocalCentre.getGroupIds()) {
            removeUserToGroup(username, userLocalCentre.getCentreName(), groupId);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#getUserInfo(java.lang.String)
    */
   @Override
   public OpenWISUser getUserInfo(@WebParam(name = "userName") String username)
         throws UserManagementException {
      logger.debug("getUserInfo : " + username);
      int searchScope = LDAPConnection.SCOPE_SUB;
      String entryDN = UserUtils.getDn(username);
      String searchFilter = OBJECT_CLASS + EQUAL + STAR;
      LDAPEntry ldapEntry = UtilEntry.searchEntry(entryDN, searchScope, searchFilter, null);
      OpenWISUser openWISUser = new OpenWISUser();
      if (ldapEntry != null) {
         LDAPAttributeSet attributeSet = ldapEntry.getAttributeSet();
         @SuppressWarnings("unchecked")
         Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();
         OpenWISAddress openWISAddress = new OpenWISAddress();
         while (allAttributes.hasNext()) {
            LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
            userManagementServiceUtil.setOpenWisUserFields(openWISUser, attribute);
            userManagementServiceUtil.setOpenWisAddressUserFields(openWISAddress, attribute);
         }
         openWISUser.setAddress(openWISAddress);
         List<OpenWISGroup> groups = userManagementServiceUtil.getUserGroups(username);
         openWISUser.setGroups(groups);
      }
      return openWISUser;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#updateUserProfile(java.lang.String, java.lang.String)
    */
   @Override
   public void updateUserProfile(@WebParam(name = "userName") String userName,
         @WebParam(name = "profile") String profile) throws UserManagementException {
      logger.debug("updateUserProfile : " + userName);
      String entryDN = UserUtils.getDn(userName);
      UtilEntry.replaceParamToEntry(profile, entryDN, PROFILE);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#updateUser(org.openwis.usermanagement.model.OpenWISUser)
    */
   @Override
   public void updateUser(@WebParam(name = "user") OpenWISUser user) throws UserManagementException {
      logger.info("Updating User " + user.getUserName());

      boolean updatePersoInfo = user.getBackUps() == null && user.getGroups() == null
            && user.getProfile() == null && user.getClassOfService() == null;

      List<LDAPModification> modList = new ArrayList<LDAPModification>();

      OpenWISUser ldapUser = getUserInfo(user.getUserName());

      modList = userManagementServiceUtil.updateUserPersoInfo(user, modList, ldapUser);
      // emails
      userManagementServiceUtil.updateField(
            OpenWISEmailUtils.convertToString(ldapUser.getEmails()),
            OpenWISEmailUtils.convertToString(user.getEmails()), EMAILS, modList);
      // ftps
      userManagementServiceUtil.updateField(OpenWISFTPUtils.convertToString(ldapUser.getFtps()),
            OpenWISFTPUtils.convertToString(user.getFtps()), FTPS, modList);

      if (!updatePersoInfo) {
         userManagementServiceUtil.updateUserPrivileges(user, modList, ldapUser);
      }

      String dn = UserUtils.getDn(user.getUserName());
      UtilEntry.updateEntry(modList, dn);
      if (!updatePersoInfo) {
         userManagementServiceUtil.updateUserGroups(user, this);
         
         if (modList.size() == 0) {
            // Force at least one attribute modification
            // so that OpenAM propagate user groups modification
            // see OpenAM bug http://java.net/jira/browse/OPENSSO-4977
            LDAPAttribute attribute = new LDAPAttribute(NAME, user.getName());
            modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
            UtilEntry.updateEntry(modList, dn);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#userIsMemberOfGroup(java.lang.String, java.lang.String, java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public boolean userIsMemberOfGroup(@WebParam(name = "centreGroupName") String centreGroupName,
         @WebParam(name = "groupId") String groupId, @WebParam(name = "userName") String userName)
         throws UserManagementException {
      logger.debug("userIsMemberOfGroup : " + centreGroupName + " - " + groupId + " -- " + userName);
      boolean result = false;
      String entryDN = GroupUtils.getGroupDn(centreGroupName, groupId);
      int searchScope = LDAPConnection.SCOPE_BASE;
      Enumeration<String> memberOf = null;
      String[] attrs = new String[] {UNIQUE_MEMBER};
      LDAPEntry nextEntry = UtilEntry.searchEntry(entryDN, searchScope, null, attrs);
      if (nextEntry != null) {
         LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
         Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();
         while (allAttributes.hasNext()) {
            LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
            memberOf = attribute.getStringValues();
            while (memberOf.hasMoreElements()) {
               String member = (String) memberOf.nextElement();
               String[] ldapPath = member.split(",");
               if (ldapPath.length != 0) {
                  if (ldapPath[0].equals(UID + EQUAL + userName)) {
                     result = true;
                  }
               }
            }
         }
      }

      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#removeUserToGroup(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void removeUserToGroup(@WebParam(name = "userName") String userName,
         @WebParam(name = "centreGroupName") String centreGroupName,
         @WebParam(name = "groupId") String groupId) throws UserManagementException {
      logger.info("Removing User " + userName + " from Group " + centreGroupName + " / " + groupId);
      String dn = UserUtils.getDn(userName);
      String groupdn = GroupUtils.getGroupDn(centreGroupName, groupId);
      UtilEntry.deleteParamToEntry(dn, groupdn, UNIQUE_MEMBER);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#resetUsers()
    */
   @Override
   public void resetUsers() throws UserManagementException {
      logger.info("Resetting all Users");
      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = OBJECT_CLASS + EQUAL + OPEN_WIS_USER;
      String[] attrs = new String[] {CN};
      LDAPSearchConstraints constraints = new LDAPSearchConstraints();
      constraints.setMaxResults(0);
      constraints.setBatchSize(0);
      LDAPSearchResults results = UtilEntry.searchEntries(PEOPLE, searchScope, searchFilter, attrs,
            constraints);
      while (results.hasMore()) {
         try {
            LDAPEntry result = results.next();
            UtilEntry.deleteEntry(result.getDN());
         } catch (LDAPException e) {
            logger.error("LDAP Exception : Error during reset users", e);
            throw new UserManagementException();
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#addUserToLocalGroup(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void addUserToLocalGroup(@WebParam(name = "userName") String userName,
         @WebParam(name = "centreGroupName") String centreGroupName,
         @WebParam(name = "groupId") String groupId) throws UserManagementException {
      logger.info("Adding User " + userName + " to Group " + centreGroupName + " / " + groupId);
      String dn = UserUtils.getDn(userName);
      String groupdn = GroupUtils.getGroupDn(centreGroupName, groupId);
      UtilEntry.addParamToEntry(dn, groupdn, UNIQUE_MEMBER);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#addUserToGlobalGroup(java.lang.String, java.lang.String)
    */
   @Override
   public void addUserToGlobalGroup(@WebParam(name = "userName") String userName,
         @WebParam(name = "groupId") String groupId) throws UserManagementException {
      logger.info("Adding User " + userName + " to Global Group " + groupId);
      String dn = UserUtils.getDn(userName);
      String groupdn = GroupUtils.getGroupDn(GLOBAL, groupId);
      UtilEntry.addParamToEntry(dn, groupdn, UNIQUE_MEMBER);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#checkUserNameExists(java.lang.String)
    */
   @Override
   public boolean checkUserNameExists(@WebParam(name = "userName") String username)
         throws UserManagementException {
      logger.debug("checkUserNameExists : " + username);
      int searchScope = LDAPConnection.SCOPE_SUB;
      String entryDN = UserUtils.getDn(username);
      String searchFilter = OBJECT_CLASS + EQUAL + STAR;
      LDAPEntry ldapEntry = UtilEntry.searchEntry(entryDN, searchScope, searchFilter, null);
      return ldapEntry != null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#getImportUserList(java.lang.String)
    */
   @Override
   public List<OpenWISUser> getImportUserList(
         @WebParam(name = "centreGroupName") String centreGroupName) throws UserManagementException {
      logger.debug("getImportUserList for the centre: " + centreGroupName);

      String groupFilter = "(!(ismemberof=cn=DEFAULT,"
            + GroupUtils.getGroupCentreDn(centreGroupName) + "))";

      List<OpenWISUser> result = getAllUserLike(groupFilter);
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#getImportUserListFilter(java.lang.String, java.lang.String)
    */
   public List<OpenWISUser> getImportUserListFilter(
         @WebParam(name = "userFilter") String userFilter,
         @WebParam(name = "centreGroupName") String centreGroupName) throws UserManagementException {
      logger.debug("getImportUserListFilter: " + userFilter + " for the centre: " + centreGroupName);

      String userNameFilter = "(|(uid=*" + userFilter + "*)(givenname=*" + userFilter + "*)(sn=*"
            + userFilter + "*))";
      String groupFilter = "(!(ismemberof=cn=DEFAULT,"
            + GroupUtils.getGroupCentreDn(centreGroupName) + "))";
      String searchFilter = "(&" + userNameFilter + groupFilter + ")";

      List<OpenWISUser> result = getAllUserLike(searchFilter);
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#importUser(java.util.List, java.lang.String)
    */
   @Override
   public void importUser(@WebParam(name = "userNames") List<String> userNames,
         @WebParam(name = "centreGroupName") String centreGroupName) throws UserManagementException {
      for (String userName : userNames) {
         addUserToLocalGroup(userName, centreGroupName, DEFAULT);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#changePassword(java.lang.String, java.lang.String)
    */
   @Override
   public void changePassword(String username, String password) throws UserManagementException {
      logger.info("Changing password for User " + username);
      List<LDAPModification> modList = new ArrayList<LDAPModification>();
      LDAPAttribute attribute = new LDAPAttribute(PASSWORD, password);
      modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      String dn = UserUtils.getDn(username);
      UtilEntry.updateEntry(modList, dn);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#initialize(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void initialize(@WebParam(name = "adminName") String adminName,
         @WebParam(name = "adminPassword") String adminPassword,
         @WebParam(name = "emailContact") String emailContact,
         @WebParam(name = "centreName") String centreName,
         @WebParam(name = "firstName") String firstName,
         @WebParam(name = "lastName") String lastName) throws UserManagementException {
      logger.info("Initializing Centre " + centreName + ", default admin " + adminName);

      logger.debug("Group Creation : " + centreName);
      GroupManagementServiceImpl groupManagementServiceImpl = new GroupManagementServiceImpl();
      if (groupManagementServiceImpl.getLocalGroup(centreName) == null) {
         groupManagementServiceImpl.createLocalGroup(centreName);
      }

      if (!groupManagementServiceImpl.checkGlobalGroupExists()) {
         logger.debug("Global Group Creation : ");
         groupManagementServiceImpl.createGlobalGroup();
      }

      String[] globalgroups = JNDIUtils.getInstance().getGlobalGroups();
      for (int i = 0; i < globalgroups.length; i++) {
         logger.debug("Global Group Id Creation : " + globalgroups[i]);
         if (!groupManagementServiceImpl.checkGlobalGroupIdExists(globalgroups[i])) {
            groupManagementServiceImpl.createGlobalGroupId(globalgroups[i]);
         }
      }

      logger.debug("Admin Creation : " + adminName);
      OpenWISUser user = new OpenWISUser();
      user.setName(firstName);
      user.setUserName(adminName);
      user.setSurName(lastName);
      user.setEmailContact(emailContact);
      user.setNeedUserAccount(true);
      user.setPassword(adminPassword);
      user.setProfile("Administrator");
      createUser(user, centreName);

   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#getAllUserByUserNameFilter(java.lang.String, java.lang.String)
    */
   @Override
   public List<OpenWISUser> getAllUserByUserNameFilter(
         @WebParam(name = "userNameFilter") String userNameFilter,
         @WebParam(name = "centreGroupName") String centreGroupName) throws UserManagementException {
      logger.debug("getAllUserByUserNameFilter : " + userNameFilter + " for the centre group: "
            + centreGroupName);

      // Create the default group for the given centre
      OpenWISGroup defaultGroup = new OpenWISGroup();
      defaultGroup.setCentreName(centreGroupName);
      defaultGroup.setGroupIds(Collections.singletonList(LdapUtils.DEFAULT));

      return getAllUserByUserNameAndGroupsFilter(userNameFilter,
            Collections.singletonList(defaultGroup));
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.UserManagementService#getAllUserByUserNameAndGroupsFilter(java.lang.String, java.util.List)
    */
   @Override
   public List<OpenWISUser> getAllUserByUserNameAndGroupsFilter(
         @WebParam(name = "userNameFilter") String userNameFilter,
         @WebParam(name = "groups") List<OpenWISGroup> groups) throws UserManagementException {
      logger.debug("getAllUserByUserNameAndGroupsFilter : " + userNameFilter + " for the groups: "
            + groups);

      String groupFilter = "";
      if (groups != null) {
         for (OpenWISGroup openWISGroup : groups) {
            for (String groupId : openWISGroup.getGroupIds()) {
               String centreName = openWISGroup.getCentreName();
               if (StringUtils.isBlank(centreName)) {
                  centreName = LdapUtils.GLOBAL;
               }
               String dn = GroupUtils.getGroupDn(centreName, groupId);
               groupFilter = groupFilter + "(ismemberof=" + dn + ")";
            }
         }
         if (groups.size() > 1) {
            groupFilter = "(|" + groupFilter + ")";
         }
      }

      String userFilter = "";
      if (StringUtils.isNotBlank(userNameFilter)) {
         userFilter = "(|(uid=*" + userNameFilter + "*)(givenname=*" + userNameFilter + "*)(sn=*"
               + userNameFilter + "*))";
      }

      String searchFilter;
      if (StringUtils.isBlank(userFilter) && StringUtils.isBlank(groupFilter)) {
         searchFilter = null;
      } else if (StringUtils.isBlank(userFilter)) {
         searchFilter = groupFilter;
      } else if (StringUtils.isBlank(groupFilter)) {
         searchFilter = userFilter;
      } else {
         searchFilter = "(&" + userFilter + groupFilter + ")";
      }

      List<OpenWISUser> result = getAllUserLike(searchFilter);
      return result;
   }

   /**
    * Get All users who matches the filter.
    * @param searchFilter The filter
    * @return all users who matches the filter.
    * @throws UserManagementException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   private List<OpenWISUser> getAllUserLike(String searchFilter) throws UserManagementException {
      int searchScope = LDAPConnection.SCOPE_SUB;
      String entryDN = LdapUtils.PEOPLE;
      List<OpenWISUser> result = new ArrayList<OpenWISUser>();
      LDAPSearchConstraints constraints = new LDAPSearchConstraints();
      constraints.setMaxResults(MAX_RESULT);

      LDAPSearchResults results = UtilEntry.searchEntries(entryDN, searchScope, searchFilter, null,
            constraints);
      int nbResult = 0;
      while (results.hasMore() && nbResult < MAX_RESULT) {
         try {
            LDAPEntry ldapEntry = results.next();
            nbResult++;
            OpenWISUser openWISUser = new OpenWISUser();
            if (ldapEntry != null) {
               LDAPAttributeSet attributeSet = ldapEntry.getAttributeSet();
               Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();
               while (allAttributes.hasNext()) {
                  LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
                  userManagementServiceUtil.setOpenWisUserFields(openWISUser, attribute);
               }

               if (StringUtils.isNotBlank(openWISUser.getName())) {
                  result.add(openWISUser);
               }
            }
         } catch (LDAPException e) {
            logger.error("LDAP Exception : Error during get All User Like", e);
            throw new UserManagementException();
         }
      }
      return result;
   }
}
