/**
 * 
 */
package org.openwis.usermanagement;

import static org.openwis.usermanagement.util.LdapUtils.CN;
import static org.openwis.usermanagement.util.LdapUtils.COMMA;
import static org.openwis.usermanagement.util.LdapUtils.DEFAULT;
import static org.openwis.usermanagement.util.LdapUtils.EQUAL;
import static org.openwis.usermanagement.util.LdapUtils.GLOBAL;
import static org.openwis.usermanagement.util.LdapUtils.GROUP;
import static org.openwis.usermanagement.util.LdapUtils.GROUP_OF_UNIQUE_NAMES;
import static org.openwis.usermanagement.util.LdapUtils.OBJECT_CLASS;
import static org.openwis.usermanagement.util.LdapUtils.ORGANIZATIONAL_UNIT;
import static org.openwis.usermanagement.util.LdapUtils.OU;
import static org.openwis.usermanagement.util.LdapUtils.STAR;
import static org.openwis.usermanagement.util.LdapUtils.UID;
import static org.openwis.usermanagement.util.LdapUtils.UNIQUE_MEMBER;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.group.OpenWISGroup;
import org.openwis.usermanagement.model.user.OpenWISUser;
import org.openwis.usermanagement.util.GroupUtils;
import org.openwis.usermanagement.util.LdapUtils;
import org.openwis.usermanagement.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * Implements the Group Management Service : <P>
 * The Group Management Component is used for managed groups. <P>
 * -  create / update / remove local group id <P>
 * -  create / remove local group <P>
 * -  synchronize LDAP <P>
 * -  create global group <P>
 * -  create global group id <P>
 * -  reset all LDAP groups <P>
 */
@WebService(endpointInterface = "org.openwis.usermanagement.GroupManagementService", targetNamespace = "http://securityservice.openwis.org/", portName = "GroupManagementServicePort", serviceName = "GroupManagementService")
public class GroupManagementServiceImpl implements GroupManagementService {

   private static int MAX_RESULT = 1000;

   /**
    * The logger
    * @member: logger
    */
   private final Logger logger = LoggerFactory.getLogger(GroupManagementServiceImpl.class);

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#createGlobalGroup()
    */
   @Override
   public void createGlobalGroup() throws UserManagementException {
      logger.info("Creating Global Group node");
      LDAPAttributeSet attributeSet = new LDAPAttributeSet();
      attributeSet.add(new LDAPAttribute(OBJECT_CLASS, new String[] {ORGANIZATIONAL_UNIT}));
      attributeSet.add(new LDAPAttribute(OU, GLOBAL));
      String dn = GroupUtils.getGroupCentreDn(GLOBAL);
      LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
      UtilEntry.addNewEntry(newEntry);
   }

   /**
    * Check Global Group Node Exists.
    * @return true if the global group node exists in LDAP.
    * @throws UserManagementException if an error occurs
    */
   protected boolean checkGlobalGroupExists() throws UserManagementException {
      logger.debug("checkGlobalGroupExists");
      int searchScope = LDAPConnection.SCOPE_SUB;
      String entryDN = OU + EQUAL + GLOBAL + COMMA + GROUP;
      String searchFilter = OBJECT_CLASS + EQUAL + STAR;
      LDAPEntry ldapEntry = UtilEntry.searchEntry(entryDN, searchScope, searchFilter, null);
      return ldapEntry != null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#createLocalGroup(java.lang.String)
    */
   @Override
   public void createLocalGroup(@WebParam(name = "localCentreGroupName") String localCentreGroupName)
         throws UserManagementException {
      logger.info("Creating Centre node " + localCentreGroupName);
      LDAPAttributeSet attributeSet = new LDAPAttributeSet();
      attributeSet.add(new LDAPAttribute(OBJECT_CLASS, new String[] {ORGANIZATIONAL_UNIT}));
      attributeSet.add(new LDAPAttribute(OU, localCentreGroupName));
      String dn = GroupUtils.getGroupCentreDn(localCentreGroupName);
      LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
      UtilEntry.addNewEntry(newEntry);
      //Add Default Group
      createLocalGroupId(localCentreGroupName, DEFAULT);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#createGlobalGroupId(java.lang.String)
    */
   @Override
   public void createGlobalGroupId(@WebParam(name = "globalGroupId") String globalGroupId)
         throws UserManagementException {
      logger.info("Creating Global Group " + globalGroupId);
      LDAPAttributeSet attributeSet = new LDAPAttributeSet();
      attributeSet.add(new LDAPAttribute(OBJECT_CLASS, new String[] {GROUP_OF_UNIQUE_NAMES}));
      attributeSet.add(new LDAPAttribute(CN, globalGroupId));
      String dn = GroupUtils.getGroupDn(GLOBAL, globalGroupId);
      LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
      UtilEntry.addNewEntry(newEntry);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#createLocalGroupId(java.lang.String, java.lang.String)
    */
   @Override
   public void createLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupId") String localGroupId) throws UserManagementException {
      logger.info("Creating Local Group " + localCentreGroupName + " / " + localGroupId);
      LDAPAttributeSet attributeSet = new LDAPAttributeSet();
      attributeSet.add(new LDAPAttribute(OBJECT_CLASS, new String[] {GROUP_OF_UNIQUE_NAMES}));
      attributeSet.add(new LDAPAttribute(CN, localGroupId));
      String dn = GroupUtils.getGroupDn(localCentreGroupName, localGroupId);
      LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
      UtilEntry.addNewEntry(newEntry);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#removeLocalGroup(java.lang.String)
    */
   @Override
   public void removeLocalGroup(@WebParam(name = "localCentreGroupName") String localCentreGroupName)
         throws UserManagementException {
      logger.info("Removing Local Group " + localCentreGroupName);
      String deleteDN = GroupUtils.getGroupCentreDn(localCentreGroupName);
      UtilEntry.deleteEntry(deleteDN);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#removeLocalGroupId(java.lang.String, java.lang.String)
    */
   @Override
   public void removeLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupId") String localGroupId) throws UserManagementException {
      logger.info("Removing Local Group " + localCentreGroupName + " / " + localGroupId);
      String deleteDN = GroupUtils.getGroupDn(localCentreGroupName, localGroupId);
      UtilEntry.deleteEntry(deleteDN);
   }

   /**
    * Get User Member Of Group Id.
    * @param centreGroupName The centre group name
    * @param groupId The group Id
    * @return users list
    * @throws UserManagementException if an error occurs.
    */
   protected List<String> getUserMemberOfGroupId(String centreGroupName, String groupId)
         throws UserManagementException {
      logger.debug("getUserMemberOfGroupId " + centreGroupName + "- " + groupId);
      List<String> users = new ArrayList<String>();
      String entryDN = GroupUtils.getGroupDn(centreGroupName, groupId);
      int searchScope = LDAPConnection.SCOPE_BASE;
      @SuppressWarnings("rawtypes")
      Enumeration memberOf = null;
      String[] attrs = new String[] {UNIQUE_MEMBER};
      LDAPEntry nextEntry = UtilEntry.searchEntry(entryDN, searchScope, null, attrs);
      if (nextEntry != null) {
         LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
         @SuppressWarnings("unchecked")
         Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();
         while (allAttributes.hasNext()) {
            LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
            memberOf = attribute.getStringValues();
            while (memberOf.hasMoreElements()) {
               String member = (String) memberOf.nextElement();
               users.add(member);
            }
         }
      }
      return users;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#updateLocalGroupId(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void updateLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "oldLocalGroupId") String oldLocalGroupId,
         @WebParam(name = "newLocalGroupId") String newLocalGroupId) throws UserManagementException {
      logger.info("Updating Local Group in Centre " + localCentreGroupName + " from " + oldLocalGroupId
            + " to " + newLocalGroupId);
      // Get All users which belong to the oldLocalGroupCentre.
      List<String> userDNs = getUserMemberOfGroupId(localCentreGroupName, oldLocalGroupId);
      // Remove the oldLocalGroupCentre
      removeLocalGroupId(localCentreGroupName, oldLocalGroupId);
      //Create the newLocalGroupCentre
      createLocalGroupId(localCentreGroupName, newLocalGroupId);
      //Add all user in this new group
      UserManagementServiceImpl userManagementServiceImpl = new UserManagementServiceImpl();
      for (String userDN : userDNs) {
         userManagementServiceImpl.addUserToLocalGroup(UserUtils.getUserNameByDn(userDN),
               localCentreGroupName, newLocalGroupId);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#synchronizeLDAP(java.lang.String)
    */
   @Override
   public List<OpenWISGroup> synchronizeLDAP(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName)
         throws UserManagementException {
      logger.debug("synchronizeLDAP " + localCentreGroupName);
      List<OpenWISGroup> ldapGroups = new ArrayList<OpenWISGroup>();
      // Get Local Centre Group
      // Check if local centre group name is defined in the LDAP.
      OpenWISGroup localGroup = getLocalGroup(localCentreGroupName);
      if (localGroup == null) {
         logger.info("Local Group doesn't exist for deployment : " + localCentreGroupName
               + ". Initializing...");
         createLocalGroup(localCentreGroupName);
         localGroup = getLocalGroup(localCentreGroupName);
      }
      ldapGroups.add(localGroup);
      // Get Global Centre Group
      OpenWISGroup globalGroup = getGlobalGroup();
      ldapGroups.add(globalGroup);
      return ldapGroups;
   }

   /**
    * Get the local group.
    * @param localCentreGroupName The local centre group name.
    * @return the openWis Group.
    * @throws UserManagementException if an error occurs.
    */
   protected OpenWISGroup getLocalGroup(String localCentreGroupName) throws UserManagementException {
      logger.debug("getLocalGroup " + localCentreGroupName);
      String entryDN = OU + EQUAL + localCentreGroupName + COMMA + GROUP;
      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = OBJECT_CLASS + EQUAL + STAR;
      LDAPSearchResults results = UtilEntry.searchEntries(entryDN, searchScope, searchFilter, null);
      OpenWISGroup localGroup = new OpenWISGroup(false);
      localGroup.setCentreName(localCentreGroupName);
      while (results.hasMore()) {
         try {
            LDAPEntry result = results.next();
            String dn = result.getDN();
            String[] splitResult = dn.split(COMMA + OU + EQUAL + localCentreGroupName);
            if (splitResult.length == 2) {
               // Get cn=groupId for global group.
               String[] splitResult2 = splitResult[0].split(CN + EQUAL);
               if (splitResult2.length == 2) {
                  localGroup.getGroupIds().add(splitResult2[1]);
               }
            }
         } catch (LDAPException e) {
            if (e.getResultCode() == LDAPException.NO_SUCH_OBJECT) {
               logger.error("LDAP Exception : The local group " + localCentreGroupName
                     + "doesn't exist");
               localGroup = null;
            } else {
               logger.error("LDAP Exception : Error during getting local group"
                     + localCentreGroupName, e);
               throw new UserManagementException();
            }
         }
      }
      return localGroup;
   }

   /**
    * Get the global group
    * @return the global group (centre + ids)
    * @throws UserManagementException if an error occurs.
    */
   private OpenWISGroup getGlobalGroup() throws UserManagementException {
      logger.debug("getGlobalGroup ");
      OpenWISGroup globalGroup = new OpenWISGroup(true);
      String entryDN = OU + EQUAL + GLOBAL + COMMA + GROUP;
      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = OBJECT_CLASS + EQUAL + STAR;
      LDAPSearchResults results = UtilEntry.searchEntries(entryDN, searchScope, searchFilter, null);
      while (results.hasMore()) {
         try {
            LDAPEntry result = results.next();
            String dn = result.getDN();
            String[] splitResult = dn.split(COMMA + OU + EQUAL + GLOBAL);
            if (splitResult.length == 2) {
               // Get cn=groupId for global group.
               String[] splitResult2 = splitResult[0].split(CN + EQUAL);
               if (splitResult2.length == 2) {
                  globalGroup.getGroupIds().add(splitResult2[1]);
               }
            }
         } catch (LDAPException e) {
            if (e.getResultCode() == LDAPException.NO_SUCH_OBJECT) {
               logger.error("LDAP Exception : The global group " + entryDN + " doesn't exist", e);
               globalGroup = null;
            } else {
               logger.error("LDAP Exception : Error during getting global group", e);
               throw new UserManagementException();
            }
         }
      }
      return globalGroup;
   }

   /**
    * check if Global Group Id Exists.
    * @param name The global group name
    * @return true if the global group exists
    * @throws UserManagementException if an error occurs.
    */
   protected boolean checkGlobalGroupIdExists(String name) throws UserManagementException {
      logger.debug("checkGlobalGroupIdExists " + name);
      int searchScope = LDAPConnection.SCOPE_SUB;
      String entryDN = CN + EQUAL + name + COMMA + OU + EQUAL + GLOBAL + COMMA + GROUP;
      String searchFilter = OBJECT_CLASS + EQUAL + STAR;
      LDAPEntry ldapEntry = UtilEntry.searchEntry(entryDN, searchScope, searchFilter, null);
      return ldapEntry != null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#resetGroups()
    */
   @Override
   public void resetGroups() throws UserManagementException {
      logger.info("Resetting Groups");
      int searchScope = LDAPConnection.SCOPE_SUB;
      String searchFilter = OBJECT_CLASS + EQUAL + GROUP_OF_UNIQUE_NAMES;
      String[] attrs = new String[] {CN};
      LDAPSearchResults results = UtilEntry.searchEntries(GROUP, searchScope, searchFilter, attrs);
      try {
         while (results.hasMore()) {
            LDAPEntry result = results.next();
            UtilEntry.deleteEntry(result.getDN());
         }
         searchFilter = OBJECT_CLASS + EQUAL + ORGANIZATIONAL_UNIT;
         attrs = new String[] {OU};
         results = UtilEntry.searchEntries(GROUP, searchScope, searchFilter, attrs);
         while (results.hasMore()) {
            LDAPEntry result = results.next();
            String dn = result.getDN();
            // Remove only centre name on "groups" or "global" nodes.
            if (dn.split(OU + EQUAL).length == 3) {
               UtilEntry.deleteEntry(result.getDN());
            }

         }
      } catch (LDAPException e) {
         logger.error("LDAP Exception : Error during reset groups", e);
         throw new UserManagementException();
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#removeGlobalGroup()
    */
   @Override
   public void removeGlobalGroup() throws UserManagementException {
      logger.info("Removig Global Group node");
      String deleteDN = GroupUtils.getGroupCentreDn(GLOBAL);
      UtilEntry.deleteEntry(deleteDN);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#removeGlobalGroupId(java.lang.String)
    */
   @Override
   public void removeGlobalGroupId(@WebParam(name = "globalGroupId") String globalGroupId)
         throws UserManagementException {
      logger.info("Removing Global Group " + globalGroupId);
      String deleteDN = GroupUtils.getGroupDn(GLOBAL, globalGroupId);
      UtilEntry.deleteEntry(deleteDN);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllUsersByGlobalGroup()
    */
   @Override
   public List<OpenWISUser> getAllUsersByGlobalGroup() throws UserManagementException {
      logger.debug("getAllUsersByGlobalGroup");
      List<OpenWISUser> result = new ArrayList<OpenWISUser>();
      OpenWISGroup group = getGlobalGroup();
      // Get All users which belong to the localGroupId.
      List<String> userDNs = new ArrayList<String>();
      for (String id : group.getGroupIds()) {
         List<String> users = getUserMemberOfGroupId(GLOBAL, id);
         userDNs.addAll(users);
      }
      //Parse all user and add openwis user to the result list.
      UserManagementServiceImpl userManagementServiceImpl = new UserManagementServiceImpl();
      for (String userDN : userDNs) {
         result.add(userManagementServiceImpl.getUserInfo(UserUtils.getUserNameByDn(userDN)));
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllUsersByGroup(java.lang.String)
    */
   @Override
   public List<OpenWISUser> getAllUsersByGroup(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName)
         throws UserManagementException {
      logger.debug("getAllUsersByGroup : " + localCentreGroupName);
      List<String> userDNs = getUserMemberOfGroupId(localCentreGroupName, DEFAULT);
      //Parse all user and add openwis user to the result list.
      UserManagementServiceImpl userManagementServiceImpl = new UserManagementServiceImpl();
      List<OpenWISUser> result = new ArrayList<OpenWISUser>();
      if (userDNs.size() > MAX_RESULT) {
         userDNs = userDNs.subList(0, MAX_RESULT);
      }
      for (String userDN : userDNs) {
         result.add(userManagementServiceImpl.getUserInfo(UserUtils.getUserNameByDn(userDN)));
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllUsersByLocalGroupId(java.lang.String, java.lang.String)
    */
   @Override
   public List<OpenWISUser> getAllUsersByLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupId") String localGroupId) throws UserManagementException {
      logger.debug("getAllUsersByLocalGroupId : " + localCentreGroupName + " " + localGroupId);
      List<OpenWISUser> result = new ArrayList<OpenWISUser>();
      // Get All users which belong to the localGroupId.
      List<String> userDNs = getUserMemberOfGroupId(localCentreGroupName, localGroupId);
      //Parse all user and add openwis user to the result list.
      UserManagementServiceImpl userManagementServiceImpl = new UserManagementServiceImpl();
      for (String userDN : userDNs) {
         result.add(userManagementServiceImpl.getUserInfo(UserUtils.getUserNameByDn(userDN)));
      }
      return result;
   }

   
   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllUsersByGlobalGroupIds(java.lang.String, java.util.List)
    */
   @Override
   public List<OpenWISUser> getAllUsersByGlobalGroupIds(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupIds") List<String> localGroupIds)
         throws UserManagementException {
      logger.debug("getAllUsersByLocalGroupIds : " + localCentreGroupName + " " + localGroupIds);
      List<OpenWISUser> result = new ArrayList<OpenWISUser>();
      for (String localGroupId : localGroupIds) {
         result.addAll(getAllUsersByLocalGroupId(localCentreGroupName, localGroupId));
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllUsersByGlobalGroupId(java.lang.String)
    */
   @Override
   public List<OpenWISUser> getAllUsersByGlobalGroupId(
         @WebParam(name = "globalGroupId") String globalGroupId) throws UserManagementException {
      logger.debug("getAllUsersByGlobalGroupId : " + globalGroupId);
      List<OpenWISUser> result = new ArrayList<OpenWISUser>();
      // Get All users which belong to the localGroupId.
      List<String> userDNs = getUserMemberOfGroupId(GLOBAL, globalGroupId);
      //Parse all user and add openwis user to the result list.
      UserManagementServiceImpl userManagementServiceImpl = new UserManagementServiceImpl();
      for (String userDN : userDNs) {
         result.add(userManagementServiceImpl.getUserInfo(UserUtils.getUserNameByDn(userDN)));
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllGroups(java.lang.String)
    */
   @Override
   public List<OpenWISGroup> getAllGroups(@WebParam(name = "centreGroupName") String centreGroupName)
         throws UserManagementException {
      List<OpenWISGroup> groups = new ArrayList<OpenWISGroup>();
      groups.add(getGlobalGroup());
      groups.add(getLocalGroup(centreGroupName));
      return groups;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllUsersByGroups(java.util.List)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<OpenWISUser> getAllUsersByGroups(@WebParam(name = "groups") List<OpenWISGroup> groups)
         throws UserManagementException {
      List<OpenWISUser> users = new ArrayList<OpenWISUser>();
      for (OpenWISGroup openWISGroup : groups) {
         if (openWISGroup.isIsGlobal()) {
            for (String groupId : openWISGroup.getGroupIds()) {
               users.addAll(CollectionUtils.collect(getAllUsersByGlobalGroupId(groupId),
                     new Transformer() {
                        @Override
                        public Object transform(Object user) {
                           OpenWISUser openWISUser = (OpenWISUser) user;
                           return openWISUser;
                        }
                     }));
            }
         } else {
            for (String groupId : openWISGroup.getGroupIds()) {
               users.addAll(CollectionUtils.collect(
                     getAllUsersByLocalGroupId(openWISGroup.getCentreName(), groupId),
                     new Transformer() {
                        @Override
                        public Object transform(Object user) {
                           OpenWISUser openWISUser = (OpenWISUser) user;
                           return openWISUser;
                        }
                     }));
            }
         }

      }
      return users;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#getAllUserNameByGroups(java.util.List)
    */
   @Override
   public List<String> getAllUserNameByGroups(@WebParam(name = "groups") List<OpenWISGroup> groups)
         throws UserManagementException {
      List<String> usernames = new ArrayList<String>();
      for (OpenWISGroup openWISGroup : groups) {
         usernames.addAll(getAllUserNameByGroup(openWISGroup));
      }
      return usernames;
   }
   
   public List<String> getAllUserNameByCentre(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName)
         throws UserManagementException {
      logger.debug("getAllUserNameByCentre : " + localCentreGroupName);
      List<String> userDNs = getUserMemberOfGroupId(localCentreGroupName, DEFAULT);
      // Transform list of DN by list of usernames
      List<String> usernames = new ArrayList<String>();
      for (String userDN : userDNs) {
         usernames.add(UserUtils.getUserNameByDn(userDN));
      }
      return usernames;
   }
   
   /**
    * Get usernames of users belonging to the given OpenWISGroup.
    */
   private List<String> getAllUserNameByGroup(OpenWISGroup group)
         throws UserManagementException {
      logger.debug("getAllUserNameByGroup : " + group.getCentreName() + " - " + group.getGroupIds());
      String localCentreGroupName;
      if (group.isIsGlobal()) {
         localCentreGroupName = GLOBAL;
      } else {
         localCentreGroupName = group.getCentreName();
      }

      List<String> userDNs = new ArrayList<String>();
      for (String groupId : group.getGroupIds()) {
         userDNs.addAll(getUserMemberOfGroupId(localCentreGroupName, groupId));
      }
      // Transform list of DN by list of usernames
      List<String> usernames = new ArrayList<String>();
      for (String userDN : userDNs) {
         usernames.add(UserUtils.getUserNameByDn(userDN));
      }
      return usernames;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.GroupManagementService#countUsersInGroup(java.lang.String)
    */
   @Override
   public int countUsersInGroup(String centreName) throws UserManagementException {
      logger.debug("countUsersInGroup " + centreName);
      String[] attrs = new String[] {UID};
      int searchScope = LDAPConnection.SCOPE_SUB;
      String entryDN = LdapUtils.PEOPLE;
      String groupDn = GroupUtils.getGroupDn(centreName, DEFAULT);

      String searchFilter = "(ismemberof=" + groupDn + ")";

      LDAPSearchConstraints constraints = new LDAPSearchConstraints();
      constraints.setBatchSize(0);
      constraints.setMaxResults(0);
      LDAPSearchResults searchResults = UtilEntry.searchEntries(entryDN, searchScope, searchFilter,
            attrs, constraints);
      searchResults.hasMore();
      return searchResults.getCount();
   }

}
