package org.openwis.usermanagement;

import java.util.List;

import javax.jws.WebParam;

import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.group.OpenWISGroup;
import org.openwis.usermanagement.model.user.OpenWISUser;

/**
 * The User Management Component is used for managed users and groups. <P>
 * - create / update / remove user <P>
 * - add / remove user to group (local or global)
 * - get user informations <P>
 * - update user profile <P>
 * - check user name existing <P>
 * - get Imported user <P>
 * - reset users <P>
 * - test if user is member of group.
 */
public interface UserManagementService {

   /** 
    * Create a user.
    * @param user The user to create.
    * @param centreName The centre name where the user is created.
    * @throws UserManagementException if an error occurs 
    */
   public void createUser(OpenWISUser user, String centreName) throws UserManagementException;

   /**
    * Update a user.
    * @param user The user to update.
    * @throws UserManagementException if an error occurs 
    */
   public void updateUser(OpenWISUser user) throws UserManagementException;

   /**
    * Get User Groups
    * @param username The username
    * @return
    * @throws UserManagementException
    */
   public List<OpenWISGroup> getUserGroups(@WebParam(name = "username") String username) throws UserManagementException;
   
   /**
    * Remove a user.
    * @param username The username to remove.
    * @param centre The centre where admin wants to remove the user.
    * @throws UserManagementException if an error occurs 
    */
   public void removeUser(String username, String centre) throws UserManagementException;

   /**
    * Get user informations.
    * @param username The user name.
    * @return Informations of user.
    * @throws UserManagementException if an error occurs 
    */
   public OpenWISUser getUserInfo(String username) throws UserManagementException;

   /**
    * Test if the user name exists in LDAP.
    * @param username The user name to test.
    * @return true if the user exists, false otherwise.
    * @throws UserManagementException if an error occurs
    */
   public boolean checkUserNameExists(String username) throws UserManagementException;

   /**
   * Update user profile.
   * @param userName The user.
   * @param profile The profile.
    * @throws UserManagementException if an error occurs 
   */
   public void updateUserProfile(String userName, String profile) throws UserManagementException;

   /**
    * Test if the user belongs to the group.
    * @param centreGroupName The centre group name.
    * @param groupId The group Identifier.
    * @param userName The user.
    * @return true if the user belongs to the group.
    * @throws UserManagementException if an error occurs 
    */
   public boolean userIsMemberOfGroup(String centreGroupName, String groupId, String userName)
         throws UserManagementException;

   /**
    * Add user to a local group.
    * @param userName The user.
    * @param centreGroupName The centre group name.
    * @param groupId The group Identifier.
    * @throws UserManagementException if an error occurs 
    */
   public void addUserToLocalGroup(String userName, String centreGroupName, String groupId)
         throws UserManagementException;

   /**
    * Add user to a global group.
    * @param userName The user.
    * @param groupId The group Identifier.
    * @throws UserManagementException if an error occurs 
    */
   public void addUserToGlobalGroup(String userName, String groupId) throws UserManagementException;

   /**
    * Remove user to a group.
    * @param userName The user.
    * @param centreGroupName The centre group name.
    * @param groupId The group Identifier.
    * @throws UserManagementException if an error occurs 
    */
   public void removeUserToGroup(String userName, String centreGroupName, String groupId)
         throws UserManagementException;

   /**
    * Remove All Users.
    * @throws UserManagementException if an error occurs 
    */
   public void resetUsers() throws UserManagementException;

   /**
    * Get All users who don't belong to this centre group.
    * @param centreGroupName The centre group name.
    * @return all users who don't belong to this centre group.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getImportUserList(String centreGroupName)
         throws UserManagementException;
   
   /**
    * Get All users who don't belong to this centre group and matches the filter.
    * @param userFilter The user filter
    * @param centreGroupName The centre group name.
    * @return all users wwho don't belong to this centre group and matches the filter.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getImportUserListFilter(String userFilter, 
         String centreGroupName) throws UserManagementException;

   /**
    * Get All users who belong to this centre group and who the username is like the usernamefilter.
    * @param userNameFilter The user name filter
    * @param centreGroupName The centre group name.
    * @return all users who belong to this centre group and who the username is like the usernamefilter.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getAllUserByUserNameFilter(String userNameFilter, String centreGroupName)
         throws UserManagementException;

   /**
    * Get All users who belong to these groups and who the username is like the usernamefilter.
    * @param userNameFilter The user name filter
    * @param groups The group list.
    * @return all users who belong to this centre group and who the username is like the usernamefilter.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getAllUserByUserNameAndGroupsFilter(String userNameFilter, List<OpenWISGroup> groups)
         throws UserManagementException;
   
   /**
    * Import User.
    * @param userNames List of userName to import.
    * @param centreGroupName The centre group name.
    * @throws UserManagementException if an error occurs.
    */
   public void importUser(List<String> userNames, String centreGroupName)
         throws UserManagementException;

   /**
    * Change user password.
    * @param username the user name.
    * @param password the password to update.
    * @throws UserManagementException if an error occurs.
    */
   public void changePassword(String username, String password) throws UserManagementException;

   /**
    * Initialize LDAP :
    * - Create admin user
    * - Create deployment node
    * - Create Institutional Gr
    * @param adminName The administrator username
    * @param adminPassword The administrator password
    * @param emailContact The email contact
    * @param deploymentName The deployment name
    * @param firstName The first name
    * @param lastName The last name
    * @throws UserManagementException if an error occurs.
    */
   public void initialize(String adminName, String adminPassword, String emailContact,
         String deploymentName, String firstName, String lastName) throws UserManagementException;

}
