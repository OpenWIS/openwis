/**
 * 
 */
package org.openwis.usermanagement;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.openwis.usermanagement.exception.UserManagementException;
import org.openwis.usermanagement.model.group.OpenWISGroup;
import org.openwis.usermanagement.model.user.OpenWISUser;

/**
 * The Group Management Component is used for managed groups. <P>
 * -  create / update / remove local group id <P>
 * -  create / remove local group <P>
 * -  synchronize LDAP <P>
 * -  create global group <P>
 * -  create global group id <P>
 * -  reset all LDAP groups <P>
 */
@WebService(targetNamespace = "http://securityservice.openwis.org/", name = "GroupManagementService", portName = "GroupManagementServicePort", serviceName = "GroupManagementService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupManagementService {

   /**
    * Create Global group.
    * @throws UserManagementException if an error occurs 
    */
   public void createGlobalGroup() throws UserManagementException;

   /**
    * Create Local group.
    * @param localCentreGroupName The Local Group to add.
    * @throws UserManagementException if an error occurs 
    */
   public void createLocalGroup(@WebParam(name = "localCentreGroupName") String localCentreGroupName) throws UserManagementException;

   /**
    * Create Global group identifier.
    * @param globalGroupId The Global Group identifier to add.
    * @throws UserManagementException if an error occurs 
    */
   public void createGlobalGroupId(@WebParam(name = "globalGroupId") String globalGroupId) throws UserManagementException;

   /**
    * Create Local group identifier.
    * @param localCentreGroupName The Local Group to add group Id.
    * @param localGroupId The Local Group identifier to add.
    * @throws UserManagementException if an error occurs 
    */
   public void createLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupId") String localGroupId)
         throws UserManagementException;

   /**
    * Update Local group identifier = rename group identifier. 
    * Change the local group id by another one.
    * Update the isMemberOf value of each user which belongs to the old local group id to the the new one.
    * @param localCentreGroupName The Local Group to update group Id.
    * @param oldLocalGroupId The Local Group id to remove.
    * @param newLocalGroupId The Local Group id to add.
    * @throws UserManagementException if an error occurs 
    */
   public void updateLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "oldLocalGroupId") String oldLocalGroupId,
         @WebParam(name = "newLocalGroupId") String newLocalGroupId) throws UserManagementException;

   /**
    * Remove Local group.
    * @param localCentreGroupName The Local Group to remove.
    * @throws UserManagementException if an error occurs 
    */
   public void removeLocalGroup(@WebParam(name = "localCentreGroupName") String localCentreGroupName) throws UserManagementException;

   /**
    * Remove Local group id.
    * @param localCentreGroupName The Local Group to remove the Local Group identifier.
    * @param localGroupId The Local Group identifier to remove.
    * @throws UserManagementException if an error occurs 
    */
   public void removeLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupId") String localGroupId)
         throws UserManagementException;

   /**
    * Remove Global group.
    * @throws UserManagementException if an error occurs 
    */
   public void removeGlobalGroup() throws UserManagementException;

   /**
    * Remove Global group id.
    * @param globalGroupId The Global Group identifier to remove.
    * @throws UserManagementException if an error occurs 
    */
   public void removeGlobalGroupId(@WebParam(name = "globalGroupId") String globalGroupId) throws UserManagementException;

   /**
    * Synchronize LDAP.
    * Return the complete list of Global and Local Groups.
    * @param localGroupCentreName The local group centre name.
    * @return the list of Global and Local Groups with group identifiers list.
    * @throws UserManagementException if an error occurs 
    */
   public List<OpenWISGroup> synchronizeLDAP(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName)
         throws UserManagementException;

   /**
    * Remove All Groups.
    * @throws UserManagementException if an error occurs 
    */
   public void resetGroups() throws UserManagementException;

   /**
    * Get All Users belonging to this centre group.
    * @param localCentreGroupName the centre group name.
    * @return the user list who belongs to this centre group.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getAllUsersByGroup(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName)
         throws UserManagementException;

   /**
    * Get All Users belonging to the global group.
    * @return the user list who belongs to the global group.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getAllUsersByGlobalGroup() throws UserManagementException;
   
   /**
    * Get All Users belonging to the local group of this centre group.
    * @param localCentreGroupName the centre group name.
    * @param localGroupId the local group identifier.
    * @return the user list who belongs to the local group of this centre group.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getAllUsersByLocalGroupId(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupId") String localGroupId) throws UserManagementException;

   /**
    * Get All Users belonging to the global group id.
    * @param globalGroupId The global group Id.
    * @return the user list who belongs to the global group id.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getAllUsersByGlobalGroupId(
         @WebParam(name = "globalGroupId") String globalGroupId) throws UserManagementException;

   /**
    * Get all groups (local + global) for a deployment.
    * @param centreGroupName the centre group name.
    * @return all groups (local + global) for a deployment.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISGroup> getAllGroups(@WebParam(name = "centreGroupName") String centreGroupName) throws UserManagementException;
   
   /**
    * Get all user for these groups. (limited to 1000)
    * @param groups The groups list.
    * @return the user list.
    * @throws UserManagementException if an error occurs.
    */
   public List<OpenWISUser> getAllUsersByGroups(@WebParam(name = "groups") List<OpenWISGroup> groups) throws UserManagementException;
   
   /**
    * Get all user name for these groups.  (limited to 1000 by group)
    * @param groups The groups list.
    * @return the username list.
    * @throws UserManagementException if an error occurs.
    */
   public List<String> getAllUserNameByGroups(@WebParam(name = "groups") List<OpenWISGroup> groups) throws UserManagementException;
   
   /**
    * Get all user name for the given centre. (no limit)
    * @param localCentreGroupName The centre name.
    * @return the username list.
    * @throws UserManagementException if an error occurs.
    */
   public List<String> getAllUserNameByCentre(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName) throws UserManagementException;

   /**
    * getAllUsersByGlobalGroupIds.
    * @param localCentreGroupName
    * @param localGroupIds
    * @return
    * @throws UserManagementException
    */
   public List<OpenWISUser> getAllUsersByGlobalGroupIds(
         @WebParam(name = "localCentreGroupName") String localCentreGroupName,
         @WebParam(name = "localGroupIds") List<String> localGroupIds) throws UserManagementException;

   /**
    * Count users belonging to a Centre.
    * @param centreName the name of the centre
    * @return the number of users in the centre
    * @throws UserManagementException if an error occurs
    */
   public int countUsersInGroup(String centreName) throws UserManagementException;
}
