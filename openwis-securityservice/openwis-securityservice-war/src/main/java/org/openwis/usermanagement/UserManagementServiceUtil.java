package org.openwis.usermanagement;

import static org.openwis.usermanagement.util.LdapUtils.ADDRESS_ADDRESS;
import static org.openwis.usermanagement.util.LdapUtils.ADDRESS_CITY;
import static org.openwis.usermanagement.util.LdapUtils.ADDRESS_COUNTRY;
import static org.openwis.usermanagement.util.LdapUtils.ADDRESS_STATE;
import static org.openwis.usermanagement.util.LdapUtils.ADDRESS_ZIP;
import static org.openwis.usermanagement.util.LdapUtils.BACKUPS;
import static org.openwis.usermanagement.util.LdapUtils.CLASSOFSERVICE;
import static org.openwis.usermanagement.util.LdapUtils.CN;
import static org.openwis.usermanagement.util.LdapUtils.CONTACT_EMAIL;
import static org.openwis.usermanagement.util.LdapUtils.DEFAULT;
import static org.openwis.usermanagement.util.LdapUtils.EMAILS;
import static org.openwis.usermanagement.util.LdapUtils.FTPS;
import static org.openwis.usermanagement.util.LdapUtils.GLOBAL;
import static org.openwis.usermanagement.util.LdapUtils.IS_MEMBER_OF;
import static org.openwis.usermanagement.util.LdapUtils.NAME;
import static org.openwis.usermanagement.util.LdapUtils.NEEDUSERACCOUNT;
import static org.openwis.usermanagement.util.LdapUtils.PASSWORD;
import static org.openwis.usermanagement.util.LdapUtils.PROFILE;
import static org.openwis.usermanagement.util.LdapUtils.SURNAME;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jws.WebParam;

import org.apache.commons.lang.StringUtils;
import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.group.OpenWISGroup;
import org.openwis.usermanagement.model.user.ClassOfService;
import org.openwis.usermanagement.model.user.OpenWISAddress;
import org.openwis.usermanagement.model.user.OpenWISEmail;
import org.openwis.usermanagement.model.user.OpenWISFTP;
import org.openwis.usermanagement.model.user.OpenWISUser;
import org.openwis.usermanagement.util.GroupUtils;
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
import com.novell.ldap.LDAPModification;

/**
 * User Management Service Utilities class. <P>
 * 
 */
public class UserManagementServiceUtil {

   /** The logger */
   private final Logger logger = LoggerFactory.getLogger(UserManagementServiceUtil.class);
   
   /**
    * Get all groups of an user.
    * @param username the username
    * @return openWis Groups list.
    * @throws UserManagementException if an error occurs.
    */
   protected List<OpenWISGroup> getUserGroups(@WebParam(name = "userName") String username)
         throws UserManagementException {
      logger.debug("getUsersGroup : " + username);
      int searchScope = LDAPConnection.SCOPE_SUB;
      String entryDN = UserUtils.getDn(username);
      String[] attrs = new String[] {IS_MEMBER_OF};
      LDAPEntry ldapEntry = UtilEntry.searchEntry(entryDN, searchScope, null, attrs);
      HashMap<String, OpenWISGroup> groups = new HashMap<String, OpenWISGroup>();
      if (ldapEntry != null) {
         LDAPAttributeSet attributeSet = ldapEntry.getAttributeSet();
         @SuppressWarnings("unchecked")
         Iterator<LDAPAttribute> allAttributes = attributeSet.iterator();
         while (allAttributes.hasNext()) {
            LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
            @SuppressWarnings("unchecked")
            Enumeration<String> memberOf = attribute.getStringValues();
            while (memberOf.hasMoreElements()) {
               String member = (String) memberOf.nextElement();
               OpenWISGroup openWISGroup = GroupUtils.getOpenWisGroupByDn(member);
               if (groups.containsKey(openWISGroup.getCentreName())) {
                  openWISGroup.getGroupIds().addAll(
                        groups.get(openWISGroup.getCentreName()).getGroupIds());
               }
               groups.put(openWISGroup.getCentreName(), openWISGroup);
            }
         }
      }
      List<OpenWISGroup> results = new ArrayList<OpenWISGroup>();
      for (OpenWISGroup openWISGroup : groups.values()) {
         results.add(openWISGroup);
      }
      return results;
   }

   /**
    * Set LDAP Address modification.
    * @param user The user to update
    * @param attributeSet Set of modification
    */
   protected void setLdapAddressModification(OpenWISUser user, LDAPAttributeSet attributeSet) {
      if (StringUtils.isNotBlank(user.getAddress().getAddress())) {
         attributeSet.add(new LDAPAttribute(ADDRESS_ADDRESS, user.getAddress().getAddress()));
      }
      if (StringUtils.isNotBlank(user.getAddress().getCountry())) {
         attributeSet.add(new LDAPAttribute(ADDRESS_COUNTRY, user.getAddress().getCountry()));
      }
      if (StringUtils.isNotBlank(user.getAddress().getState())) {
         attributeSet.add(new LDAPAttribute(ADDRESS_STATE, user.getAddress().getState()));
      }
      if (StringUtils.isNotBlank(user.getAddress().getCity())) {
         attributeSet.add(new LDAPAttribute(ADDRESS_CITY, user.getAddress().getCity()));
      }
      if (StringUtils.isNotBlank(user.getAddress().getZip())) {
         attributeSet.add(new LDAPAttribute(ADDRESS_ZIP, user.getAddress().getZip()));
      }
   }

   /**
    * Update User Groups.
    * @param user The user to update.
    * @param userManagementService The user management service for execute add/remove actions.
    * @throws UserManagementException if an error occurs
    */
   protected void updateUserGroups(OpenWISUser user, UserManagementService userManagementService)
         throws UserManagementException {
      List<OpenWISGroup> usersGroup = getUserGroups(user.getUserName());
      List<OpenWISGroup> newgroup = user.getGroups();
      // Remove old group to this user
      removeOldGroupToUser(user, userManagementService, usersGroup);
      // Add new group to this user
      if (newgroup != null) {
         addUserToGroup(user, userManagementService, newgroup);
      }
   }

   /**
    * Add the user to a list of global/local groups.
    * @param user The user
    * @param userManagementService The user management service for execute "add user to group" actions.
    * @param groups The group list to add.
    * @throws UserManagementException if an error occurs.
    */
   protected void addUserToGroup(OpenWISUser user, UserManagementService userManagementService,
         List<OpenWISGroup> groups) throws UserManagementException {

      for (OpenWISGroup openWisGroup : groups) {
         if (openWisGroup.isIsGlobal()) {
            for (String groupId : openWisGroup.getGroupIds()) {
               userManagementService.addUserToGlobalGroup(user.getUserName(), groupId);
            }
         } else {
            for (String groupId : openWisGroup.getGroupIds()) {
               userManagementService.addUserToLocalGroup(user.getUserName(),
                     openWisGroup.getCentreName(), groupId);
            }
         }
      }
   }

   /**
    * Remove the old group to this user..
    * @param user The user
    * @param userManagementService The user management service for execute "remove user to group" actions.
    * @param usersGroup List of user's groups.
    * @throws UserManagementException if an error occurs.
    */
   private void removeOldGroupToUser(OpenWISUser user, UserManagementService userManagementService,
         List<OpenWISGroup> usersGroup) throws UserManagementException {
      for (OpenWISGroup openWisGroup : usersGroup) {
         if (openWisGroup.isIsGlobal()) {
            for (String groupId : openWisGroup.getGroupIds()) {
               userManagementService.removeUserToGroup(user.getUserName(), GLOBAL, groupId);
            }
         } else {
            for (String groupId : openWisGroup.getGroupIds()) {
               // Keep default groups where user is member of.
               if (!DEFAULT.equals(groupId)) {
                  userManagementService.removeUserToGroup(user.getUserName(),
                        openWisGroup.getCentreName(), groupId);
               }
            }
         }
      }
   }

   /**
    * Set OpenWIS user fields.
    * @param openWISUser The user.
    * @param attribute The user's attributes.
    */
   protected void setOpenWisUserFields(OpenWISUser openWISUser, LDAPAttribute attribute) {
      if (NAME.equals(attribute.getName())) {
         openWISUser.setName(attribute.getStringValue());
      } else if (CN.equals(attribute.getName())) {
         openWISUser.setUserName(attribute.getStringValue());
      } else if (SURNAME.equals(attribute.getName())) {
         openWISUser.setSurName(attribute.getStringValue());
      } else if (PROFILE.equals(attribute.getName())) {
         openWISUser.setProfile(attribute.getStringValue());
      } else if (CONTACT_EMAIL.equals(attribute.getName())) {
         openWISUser.setEmailContact(attribute.getStringValue());
      } else {
         setOpenWisUserPrivilegesFields(openWISUser, attribute);
      }
   }

   /**
    * Set OpenWIS user privileges fields.
    * @param openWISUser The user.
    * @param attribute The user's attributes.
    */
   protected void setOpenWisUserPrivilegesFields(OpenWISUser openWISUser, LDAPAttribute attribute) {
      if (EMAILS.equals(attribute.getName())) {
         List<OpenWISEmail> emails = OpenWISEmailUtils.convertToOpenWISEmails(attribute
               .getStringValue());
         openWISUser.setEmails(emails);
      } else if (FTPS.equals(attribute.getName())) {
         List<OpenWISFTP> ftps = OpenWISFTPUtils.convertToOpenWISFTPs(attribute.getStringValue());
         openWISUser.setFtps(ftps);
      } else if (BACKUPS.equals(attribute.getName())) {
         openWISUser.setBackUps(OpenWiSBackupUtils.convertStringToBackUpList(attribute
               .getStringValue()));
      } else if (CLASSOFSERVICE.equals(attribute.getName())) {
         openWISUser.setClassOfService(ClassOfService.valueOf(attribute.getStringValue()));
      } else if (NEEDUSERACCOUNT.equals(attribute.getName())) {
         openWISUser.setNeedUserAccount(Boolean.valueOf(attribute.getStringValue()));
      }
   }

   /**
    * Set OpenWIS address fields.
    * @param openWISAddress The address.
    * @param attribute The address user's attributes.
    */
   protected void setOpenWisAddressUserFields(OpenWISAddress openWISAddress, LDAPAttribute attribute) {
      if (ADDRESS_ADDRESS.equals(attribute.getName())) {
         openWISAddress.setAddress(attribute.getStringValue());
      } else if (ADDRESS_COUNTRY.equals(attribute.getName())) {
         openWISAddress.setCountry(attribute.getStringValue());
      } else if (ADDRESS_STATE.equals(attribute.getName())) {
         openWISAddress.setState(attribute.getStringValue());
      } else if (ADDRESS_CITY.equals(attribute.getName())) {
         openWISAddress.setCity(attribute.getStringValue());
      } else if (ADDRESS_ZIP.equals(attribute.getName())) {
         openWISAddress.setZip(attribute.getStringValue());
      }
   }

   /**
    * Update field.
    * @param ldapField The LDAP value to update.
    * @param userField The user value to set.
    * @param fieldName The field name to update.
    * @param modList The modification list
    * @return the modification list
    */
   protected List<LDAPModification> updateField(String ldapField, String userField, String fieldName,
         List<LDAPModification> modList) {
      LDAPAttribute attribute = null;
      // Value LDAP setted
      if (ldapField != null && !ldapField.isEmpty()) {
         if (userField != null && !userField.isEmpty()) {
            // Value user setted -> Replace the value
            if (!ldapField.equals(userField)) {
               attribute = new LDAPAttribute(fieldName, userField);
               modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
            }
         } else {
            // Value user not setted  & values not equals -> Delete the value
            attribute = new LDAPAttribute(fieldName, ldapField);
            modList.add(new LDAPModification(LDAPModification.DELETE, attribute));
         }
         // Value LDAP not setted
      } else if (userField != null && !userField.isEmpty()) {
         // Value user setted -> Add this value
         attribute = new LDAPAttribute(fieldName, userField);
         modList.add(new LDAPModification(LDAPModification.ADD, attribute));
      }
      return modList;
   }

   /**
    * Update User Privileges.
    * @param user The user to update privileges
    * @param modList The modification list.
    * @param ldapUser The LDAP User.
    */
   protected void updateUserPrivileges(OpenWISUser user, List<LDAPModification> modList,
         OpenWISUser ldapUser) {
      LDAPAttribute attribute;
      // profile
      if (!ldapUser.getProfile().equals(user.getProfile())) {
         attribute = new LDAPAttribute(PROFILE, user.getProfile());
         modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      }
      // class of service
      if ((ldapUser.getClassOfService() == null && user.getClassOfService() != null )
            || (!ldapUser.getClassOfService().name().equals(user.getClassOfService().name()))) {
         attribute = new LDAPAttribute(CLASSOFSERVICE, user.getClassOfService().name());
         modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      }
      // need user account
      if (!ldapUser.isNeedUserAccount() == user.isNeedUserAccount()) {
         attribute = new LDAPAttribute(NEEDUSERACCOUNT, Boolean.valueOf(user.isNeedUserAccount())
               .toString());
         modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      }
      // backups
      updateField(OpenWiSBackupUtils.convertBackUpListToString(ldapUser.getBackUps()),
            OpenWiSBackupUtils.convertBackUpListToString(user.getBackUps()), BACKUPS, modList);
   }
   
   /**
    * Update User Personal Informations.
    * @param user The user to update user personal informations
    * @param modList The modification list.
    * @param ldapUser The LDAP User.
    * @return  The modification list.
    */
   protected List<LDAPModification> updateUserPersoInfo(OpenWISUser user,
         List<LDAPModification> modList, OpenWISUser ldapUser) {
      LDAPAttribute attribute;
      if (!ldapUser.getName().equals(user.getName())) {
         attribute = new LDAPAttribute(NAME, user.getName());
         modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      }
      if (!ldapUser.getSurName().equals(user.getSurName())) {
         attribute = new LDAPAttribute(SURNAME, user.getSurName());
         modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      }
      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
         attribute = new LDAPAttribute(PASSWORD, user.getPassword());
         modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      }
      if (!ldapUser.getEmailContact().equals(user.getEmailContact())) {
         attribute = new LDAPAttribute(CONTACT_EMAIL, user.getEmailContact());
         modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
      }
      // address
      if (user.getAddress() != null && ldapUser.getAddress() != null) {
         modList = updateField(ldapUser.getAddress().getAddress(), user
               .getAddress().getAddress(), ADDRESS_ADDRESS, modList);
         modList = updateField(ldapUser.getAddress().getCountry(), user
               .getAddress().getCountry(), ADDRESS_COUNTRY, modList);
         modList = updateField(ldapUser.getAddress().getCity(), user
               .getAddress().getCity(), ADDRESS_CITY, modList);
         modList = updateField(ldapUser.getAddress().getZip(), user
               .getAddress().getZip(), ADDRESS_ZIP, modList);
         modList = updateField(ldapUser.getAddress().getState(), user
               .getAddress().getState(), ADDRESS_STATE, modList);
      }
      return modList;
   }
}
