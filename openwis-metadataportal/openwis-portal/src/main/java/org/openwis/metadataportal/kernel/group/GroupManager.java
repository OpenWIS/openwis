/**
 * 
 */
package org.openwis.metadataportal.kernel.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.SerialFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.deployment.DeploymentManager;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.datapolicy.DataPolicyGroupPrivileges;
import org.openwis.metadataportal.model.datapolicy.DataPolicyOperationsPerGroup;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.securityservice.OpenWISGroup;
import org.openwis.securityservice.UserManagementException_Exception;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GroupManager extends AbstractManager {

   /**
    * Comment for <code>ID</code>
    * @member: ID
    */
   private static final String ID = "id";

   /**
    * Comment for <code>NAME</code>
    * @member: NAME
    */
   private static final String NAME = "name";

   /**
    * Comment for <code>GLOBAL</code>
    * @member: GLOBAL
    */
   private static final String GLOBAL = "isglobal";

   /**
   * The deployment manager.
   */
   private DeploymentManager deploymentManager;

   //----------------------------------------------------------------------- Constructors.

   /**
    * Default constructor.
    * Builds a GroupManager.
    * @param dbms Database connection
    */
   public GroupManager(Dbms dbms) {
      super(dbms);
   }

   //----------------------------------------------------------------------- Public methods.

   /**
    * Get all groups.
    * @return all groups.
    * @throws SQLException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public List<Group> getAllGroups() throws SQLException {
      List<Group> allGroups = new ArrayList<Group>();
      String query = "SELECT * FROM Groups";
      List<Element> records = getDbms().select(query).getChildren();
      for (Element e : records) {
         allGroups.add(buildGroupFromElement(e));
      }
      return allGroups;
   }
   
   /**
    * Get all groups.
    * @param ids The groups Ids.
    * @return all groups.
    * @throws SQLException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public List<Group> getAllGroupsById(Collection<String> ids) throws SQLException {
      if(CollectionUtils.isEmpty(ids)) {
         return new ArrayList<Group>();
      }
      List<Group> allGroups = new ArrayList<Group>();
      String query = "SELECT * FROM Groups WHERE id IN (";
      query += StringUtils.join(ids.toArray());
      query += ")";
      
      List<Element> records = getDbms().select(query).getChildren();
      for (Element e : records) {
         allGroups.add(buildGroupFromElement(e));
      }
      return allGroups;
   }

   /**
    * Gets all LDAP groups.
    * @return all LDAP Groups.
    * @throws Exception if an error occurs.
    */
   public List<Group> getAllLdapGroups() throws Exception {
      List<Group> allGroups = new ArrayList<Group>();
      List<OpenWISGroup> ldapGroups = SecurityServiceProvider.getGroupManagementService()
            .getAllGroups(getDeploymentManager().getLocalDeployment().getName());
      for (OpenWISGroup ldapGroup : ldapGroups) {
         allGroups.addAll(buildGroupFromOpenWisGroup(ldapGroup));
      }
      return allGroups;
   }

   /**
    * Gets a group by its name.
    * @param name the name of the group.
    * @param isGlobal True if the group is global, false otherwise.
    * @return the group.
    * @throws SQLException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public Group getGroupByName(String name, boolean isGlobal) throws SQLException {
      Group group = null;
      String query = "SELECT * FROM Groups WHERE name=? and isglobal=?";
      List<Element> records = getDbms().select(query, name,
            BooleanUtils.toString(isGlobal, "y", "n")).getChildren();
      if (!records.isEmpty()) {
         group = buildGroupFromElement(records.get(0));
      }
      return group;
   }

   /**
    * Gets a group by its id.
    * @param id the id of the group.
    * @return the group.
    * @throws SQLException if an error occurs.
    */
   @SuppressWarnings("unchecked")
   public Group getGroupById(int id) throws SQLException {
      Group group = null;
      String query = "SELECT * FROM Groups WHERE id=?";
      List<Element> records = getDbms().select(query, id).getChildren();
      if (!records.isEmpty()) {
         group = buildGroupFromElement(records.get(0));
      }
      return group;
   }

   /**
    * Synchronize the groups of the portal with groups in LDAP.
    * @param dm The Data Manager.
    * @throws Exception if an error occurs.
    */
   public void synchronize(DataManager dm) throws Exception {
      // Get All Groups stored in the LDAP (Local and Global groups)
      List<OpenWISGroup> groups = SecurityServiceProvider.getGroupManagementService()
            .synchronizeLDAP(
                  OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
      List<Group> ldapGroups = new ArrayList<Group>();

      //Convert all LDAP openwis groups into Database groups.
      for (OpenWISGroup openWISGroup : groups) {
         for (String groupName : openWISGroup.getGroupIds()) {
            Group group = new Group();
            group.setGlobal(openWISGroup.isIsGlobal());
            group.setName(groupName);
            ldapGroups.add(group);
         }
      }

      // Remove all groups which are not in the LDAP and which are in the database.
      List<Group> bdGroups = getAllGroups();
      for (Group bdGroup : bdGroups) {
         if (!ldapGroups.contains(bdGroup)) {
            removeGroupOnDB(bdGroup, dm);
         }
      }

      // Create missing groups in local database.
      DataPolicyManager dpm = new DataPolicyManager(getDbms());
      for (Group ldapGroup : ldapGroups) {
         if (!bdGroups.contains(ldapGroup)) {
            createGroupOnDB(ldapGroup, dpm);
         }
      }
   }

   /**
    * Prepare the synchronization for the groups of the portal with groups in LDAP.
    * @return the modification list 
    * @throws Exception  if an error occurs.
    */
   public List<String> prepareSynchronization() throws Exception {
      List<OpenWISGroup> groups = SecurityServiceProvider.getGroupManagementService()
            .synchronizeLDAP(
                  OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME));
      List<Group> ldapGroups = new ArrayList<Group>();
      List<String> result = new ArrayList<String>();
      for (OpenWISGroup openWISGroup : groups) {
         for (String groupName : openWISGroup.getGroupIds()) {
            Group group = new Group();
            group.setGlobal(openWISGroup.isIsGlobal());
            group.setName(groupName);
            ldapGroups.add(group);
         }
      }

      List<Group> bdGroups = getAllGroups();
      for (Group bdGroup : bdGroups) {
         if (!ldapGroups.contains(bdGroup)) {
            result.add("The group " + bdGroup.getName() + " will be removed");
         }
      }

      for (Group ldapGroup : ldapGroups) {
         if (!bdGroups.contains(ldapGroup)) {
            result.add("The group " + ldapGroup.getName() + " will be added");
         }
      }
      return result;
   }

   /**
    * Creates the group.
    * @param group the group to create.
    * @throws Exception  if an error occurs.
    */
   public void createGroup(Group group) throws Exception {
      // check group rules
      checkGroup(group);

      //Check LDAP AND create into LDAP.
      if (group.isGlobal()) {
         SecurityServiceProvider.getGroupManagementService().createGlobalGroupId(group.getName());
      } else {
         SecurityServiceProvider.getGroupManagementService().createLocalGroupId(
               OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME),
               group.getName());
      }

      createGroupOnDB(group, new DataPolicyManager(getDbms()));
   }

   /**
    * Creates the group on database (only portal side).
    * @param group the group to create.
    * @param dpm The data policy manager.
    * @throws Exception  if an error occurs.
    */
   public void createGroupOnDB(Group group, DataPolicyManager dpm) throws Exception {
      // check group rules
      checkGroup(group);

      // Generate a new group id
      group.setId(SerialFactory.getSerial(getDbms(), "Groups"));

      String insertQuery = "INSERT INTO Groups(id, name, description, email, isglobal) VALUES (?, ?, ?, ?, ?)";
      getDbms().execute(insertQuery, group.getId(), group.getName(), "", "",
            BooleanUtils.toString(group.isGlobal(), "y", "n"));

      // Add default operations allowed for this group into the default data policy
      DataPolicyOperationsPerGroup dpOpPerGroup = new DataPolicyOperationsPerGroup();
      dpOpPerGroup.setGroup(group);
      List<DataPolicyGroupPrivileges> privilegesPerOp = new ArrayList<DataPolicyGroupPrivileges>();
      for (Operation op : dpm.getDefaultOperationsForDefaultDataPolicy()) {
         privilegesPerOp.add(new DataPolicyGroupPrivileges(op, true));
      }
      dpOpPerGroup.setPrivilegesPerOp(privilegesPerOp);
      List<DataPolicyOperationsPerGroup> dpOpPerGroups = new ArrayList<DataPolicyOperationsPerGroup>();
      dpOpPerGroups.add(dpOpPerGroup);

      // Get the default data policy ID
      DataPolicy dp = dpm.getDataPolicyByName(dpm.getDefaultDataPolicyName(), false, false);

      dpm.createDataPolicyOperationsPerGroup(dpOpPerGroups, dp.getId());
   }

   /**
    * Updates the group..
    * @param group the group to update.
    * @throws Exception  if an error occurs.
    */
   public void updateGroup(Group group) throws Exception {
      // check group rules
      checkGroup(group);

      Group oldGroup = getGroupById(group.getId());

      //Check LDAP AND update LDAP.
      SecurityServiceProvider.getGroupManagementService().updateLocalGroupId(
            OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME),
            oldGroup.getName(), group.getName());

      // Update the group.
      String query = "UPDATE Groups SET name=?, isglobal=? WHERE id=?";

      getDbms().execute(query, group.getName(), BooleanUtils.toString(group.isGlobal(), "y", "n"),
            group.getId());
   }

   /**
    * Deletes a group.
    * @param group the group to delete.
    * @param dm The data manager.
    * @throws Exception  if an error occurs.
    */
   public void removeGroup(Group group, DataManager dm) throws Exception {
      if (group.isGlobal()) {
         SecurityServiceProvider.getGroupManagementService().removeGlobalGroupId(group.getName());
      } else {
         SecurityServiceProvider.getGroupManagementService().removeLocalGroupId(
               OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME),
               group.getName());
      }
      removeGroupOnDB(group, dm);
   }

   /**
    * Deletes a group on database (only portal side).
    * @param group the group to delete.
    * @param dm The Data Manager.
    * @throws Exception  if an error occurs.
    */
   public void removeGroupOnDB(Group group, DataManager dm) throws Exception {
      // Remove the usergroup.
      String queryUserGroup = "DELETE FROM usergroups WHERE groupid=?";
      getDbms().execute(queryUserGroup, group.getId());

      // Remove the operation allowed for this group.
      String queryOpAllowed = "DELETE FROM operationallowed WHERE groupid=?";
      getDbms().execute(queryOpAllowed, group.getId());

      // Remove the groups descriptions.
      String queryGroupDesc = "DELETE FROM groupsdes WHERE iddes=?";
      getDbms().execute(queryGroupDesc, group.getId());

      // Remove the group.
      String queryGroup = "DELETE FROM groups WHERE id=?";
      getDbms().execute(queryGroup, group.getId());
   }

   /**
    * Gets all groups of the given user.
    * @param userId the user identifier.
    * @return all groups linked to the user.
    * @throws SQLException if an exception occurs.
    */
   @SuppressWarnings("unchecked")
   public List<Group> getAllUserGroups(String userId) throws SQLException {
      List<Group> allGroups = new ArrayList<Group>();
      String query = "SELECT Groups.* FROM Groups, UserGroups WHERE Groups.id = UserGroups.groupId AND UserGroups.userId=?";
      List<Element> records = getDbms().select(query, new Integer(userId)).getChildren();
      for (Element e : records) {
         allGroups.add(buildGroupFromElement(e));
      }
      return allGroups;
   }
   
   /**
    * Gets all user names of given groups.
    * @param groups the list of group name.
    * @return all users linked to these groups.
    * @throws UserManagementException_Exception  if an exception occurs.
    */
   public List<String> getAllUserNameByGroups(List<Group> groups) throws UserManagementException_Exception {
      List<OpenWISGroup> openWISGroups = new ArrayList<OpenWISGroup>();
      for (Group group : groups) {
         OpenWISGroup openWISGroup = buildOpenWisGroupFromGroup(group);
         openWISGroups.add(openWISGroup);
      }
      List<String> users = SecurityServiceProvider.getGroupManagementService().getAllUserNameByGroups(openWISGroups);
      return users;
   }

   //----------------------------------------------------------------------- Private methods.

   /**
    * Builds a group from a JDOM element.
    * @param record the element.
    * @return the group.
    */
   private static Group buildGroupFromElement(Element record) {
      Group group = new Group();
      group.setId(Integer.parseInt(record.getChildText(ID)));
      group.setName(record.getChildText(NAME));
      group.setGlobal(!StringUtils.equals(record.getChildText(GLOBAL), "n"));
      return group;
   }

   /**
    * Description goes here.
    * @param group The group to check.
    * @throws Exception if an error occurs.
    */
   private void checkGroup(Group group) throws Exception {
      Group groupDb = getGroupByName(group.getName(), group.isGlobal());
      if (groupDb != null) {
         if (group.getId() == null || group.getId() != groupDb.getId()) {
            //Throw exception
            throw new GroupAlreadyExistsException(group.getName());
         }
      }
   }
   
   /**
    * Return the deployment manager.
    * @return The deployment manager.
    */
   private DeploymentManager getDeploymentManager() {
      if(deploymentManager == null) {
         deploymentManager = new DeploymentManager();
      }
      return deploymentManager;
   }

   /**
    * Builds a list of groups from an OpenWIS group.
    * @param openWISGroup the OpenWIS Group.
    * @return the group.
    */
   public static List<Group> buildGroupFromOpenWisGroup(OpenWISGroup openWISGroup) {
      List<Group> result = new ArrayList<Group>();

      for (String id : openWISGroup.getGroupIds()) {
         if (OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME).equals(
               openWISGroup.getCentreName())
               || openWISGroup.isIsGlobal()) {
            Group group = new Group();
            group.setName(id);
            group.setGlobal(openWISGroup.isIsGlobal());
            result.add(group);
         }
      }

      return result;
   }

   /**
   * Builds an OpenWIS group from a group.
   * @param group The group to transform.
   * @return the OpenWIS group.
   */
   public static OpenWISGroup buildOpenWisGroupFromGroup(Group group) {
      OpenWISGroup openWISGroup = new OpenWISGroup();
      openWISGroup.getGroupIds().add(group.getName());
      if (!group.isGlobal()) {
         openWISGroup.setCentreName(OpenwisMetadataPortalConfig
               .getString(ConfigurationConstants.DEPLOY_NAME));
      }
      openWISGroup.setIsGlobal(group.isGlobal());
      return openWISGroup;
   }
   
   
}
