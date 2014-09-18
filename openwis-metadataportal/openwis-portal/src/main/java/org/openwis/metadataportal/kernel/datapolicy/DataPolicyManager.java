/**
 *
 */
package org.openwis.metadataportal.kernel.datapolicy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.SerialFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.dataservice.DataPolicyOperations;
import org.openwis.dataservice.UserDataPolicyOperations;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.datapolicy.DataPolicyAlias;
import org.openwis.metadataportal.model.datapolicy.DataPolicyGroupPrivileges;
import org.openwis.metadataportal.model.datapolicy.DataPolicyOperationsPerGroup;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.securityservice.OpenWISGroup;
import org.openwis.securityservice.UserManagementException_Exception;
import org.openwis.securityservice.UserManagementService;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class DataPolicyManager extends AbstractManager implements IDataPolicyManager {

   private static final String ID = "id";

   private static final String NAME = "name";

   private static final String DESC = "description";

   /** The Constant ADDITIONAL_DEFAULT_DATA_POLICY_NAME. */
   private static final String ADDITIONAL_DEFAULT_DATA_POLICY_NAME = "additional-default";

   private Collection<Operation> operations = null;

   private ISearchManager searchManager;

   /**
    * Default constructor.
    * Builds a DataPolicyManager.
    * @param dbms
    */
   public DataPolicyManager(Dbms dbms) {
      super(dbms);
   }

   /**
    * Default constructor.
    * Builds a DataPolicyManager.
    * @param dbms
    */
   public DataPolicyManager(Dbms dbms, ISearchManager searchManager) {
      super(dbms);
      this.searchManager = searchManager;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#createDataPolicy(org.openwis.metadataportal.model.datapolicy.DataPolicy)
    */
   @Override
   public void createDataPolicy(DataPolicy dp) throws Exception {

      // check data policy rules
      checkDataPolicy(dp);

      // Generate a new data policy id
      dp.setId(SerialFactory.getSerial(getDbms(), "DataPolicy"));

      //Insert the data policy.
      String insertQuery = "INSERT INTO DataPolicy VALUES(?,?,?)";
      getDbms().execute(insertQuery, dp.getId(), dp.getName(), dp.getDescription());

      //Insert the aliases.
      for (DataPolicyAlias alias : dp.getAliases()) {
         alias.setId(SerialFactory.getSerial(getDbms(), "DataPolicyAlias"));
         String query = "INSERT INTO DataPolicyAlias VALUES (?,?,?)";
         getDbms().execute(query, alias.getId(), alias.getAlias(), dp.getId());
      }

      //Insert the operations allowed.
      createDataPolicyOperationsPerGroup(dp.getDpOpPerGroup(), dp.getId());
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#updateDataPolicy(org.openwis.metadataportal.model.datapolicy.DataPolicy)
    */
   @SuppressWarnings("unchecked")
   @Override
   public void updateDataPolicy(DataPolicy dp) throws Exception {
      //Check that alias does not point to an existing data policy
      if (CollectionUtils.isNotEmpty(dp.getAliases())) {
         Collection<String> dpAliases = Collections2.transform(dp.getAliases(),
               new Function<DataPolicyAlias, String>() {

                  @Override
                  public String apply(DataPolicyAlias input) {
                     return StringEscapeUtils.escapeSql(input.getAlias());
                  }

               });
         
         String inPredicate = Joiner.on("','").join(dpAliases);
         
         String validateAlias = "SELECT dp.name AS dpName FROM DataPolicy dp "
               + "LEFT JOIN DataPolicyAlias dpa ON dpa.dpid = dp.id "
               + "WHERE dp.id <> ? AND (dp.name IN ('" + inPredicate + "') OR dpa.name IN ('"
               + inPredicate + "'))";

         List<Element> records = getDbms().select(validateAlias, dp.getId()).getChildren();
         if (!records.isEmpty()) {
            throw new InvalidDataPolicyAliasException(dpAliases);
         }
      }

      //Delete.
      String deleteOpAllowed = "DELETE FROM OperationAllowed WHERE datapolicyid = ?";
      getDbms().execute(deleteOpAllowed, dp.getId());

      String deleteDpAliasQuery = "DELETE FROM DataPolicyAlias WHERE dpid= ?";
      getDbms().execute(deleteDpAliasQuery, dp.getId());

      //Insert/Update.
      String updateDesc = "UPDATE DataPolicy SET description = ? WHERE id = ?";
      getDbms().execute(updateDesc, dp.getDescription(), dp.getId());

      for (DataPolicyAlias alias : dp.getAliases()) {
         alias.setId(SerialFactory.getSerial(getDbms(), "DataPolicyAlias"));
         String query = "INSERT INTO DataPolicyAlias VALUES (?,?,?)";
         getDbms().execute(query, alias.getId(), alias.getAlias(), dp.getId());
      }

      //Insert the operations allowed.
      createDataPolicyOperationsPerGroup(dp.getDpOpPerGroup(), dp.getId());
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#removeDataPolicy(org.openwis.metadataportal.model.datapolicy.DataPolicy)
    */
   @Override
   public void removeDataPolicy(DataPolicy dp) throws Exception {
      //Check if DP is used.
      if (searchManager.isMetadataLinkedToDataPolicy(dp.getName())) {
         throw new DataPolicyLinkedToMetadataException(dp.getName());
      }

      Log.debug(Geonet.DB, "Removing data policy : " + dp.getName());

      String deleteOpAllowed = "DELETE FROM OperationAllowed WHERE datapolicyid = ?";
      getDbms().execute(deleteOpAllowed, dp.getId());

      String deleteDpAliasQuery = "DELETE FROM DataPolicyAlias WHERE dpid= ?";
      getDbms().execute(deleteDpAliasQuery, dp.getId());

      String deleteDpQuery = "DELETE FROM DataPolicy WHERE id= ?";
      getDbms().execute(deleteDpQuery, dp.getId());
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getAllDataPolicies(boolean, boolean)
    */
   @Override
   @SuppressWarnings("unchecked")
   public List<DataPolicy> getAllDataPolicies(boolean loadAlias, boolean loadOperations)
         throws SQLException {
      //Get data policies.
      Map<Integer, DataPolicy> dataPolicies = new HashMap<Integer, DataPolicy>();
      String query = "SELECT * FROM DataPolicy";
      List<Element> records = getDbms().select(query).getChildren();
      for (Element e : records) {
         DataPolicy dp = buildDataPolicyFromElement(e);
         //Load operations allowed.
         if (loadAlias) {
            dp.getAliases().addAll(getDataPolicyAlias(dp.getId()));
         }

         if (loadOperations) {
            dp.setDpOpPerGroup(getDataPolicyOperationPerGroup(dp.getId()));
         }
         dataPolicies.put(dp.getId(), dp);
      }

      List<DataPolicy> sortedDataPolicies = new ArrayList<DataPolicy>(dataPolicies.values());
      Collections.sort(sortedDataPolicies, new DataPolicyComparator());
      return sortedDataPolicies;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getAllUserDataPolicies(java.lang.String, boolean, boolean)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<DataPolicy> getAllUserDataPolicies(String username, boolean loadAlias,
         boolean loadOperations) throws SQLException, UserManagementException_Exception {

      //Get All Groups for this user
      UserManagementService userManagementService = SecurityServiceProvider
            .getUserManagementService();
      List<OpenWISGroup> groups = userManagementService.getUserGroups(username);

      //Get data policies.
      Map<Integer, DataPolicy> dataPolicies = new HashMap<Integer, DataPolicy>();
      String query = "SELECT  DataPolicy.name as dpName, DataPolicy.id as dpId, DataPolicy.description as dpDesc  FROM DataPolicy, OperationAllowed, Groups "
            + "WHERE Groups.id = OperationAllowed.groupid "
            + "AND DataPolicy.id = OperationAllowed.datapolicyid";

      //Filter on group name
      for (OpenWISGroup group : groups) {
         if (group.isIsGlobal()
               || group.getCentreName().equals(
                     OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME))) {
            for (String groupId : group.getGroupIds()) {
               query.concat("AND Groups.name = " + groupId);
            }
         }
      }

      List<Element> records = getDbms().select(query).getChildren();
      for (Element record : records) {
         DataPolicy dp = new DataPolicy();
         dp.setId(Integer.parseInt(record.getChildText("dpid")));
         dp.setName(record.getChildText("dpname"));
         dp.setDescription(record.getChildText("dpdesc"));

         //Load operations allowed.
         if (loadAlias) {
            dp.getAliases().addAll(getDataPolicyAlias(dp.getId()));
         }

         if (loadOperations) {
            dp.setDpOpPerGroup(getDataPolicyOperationPerGroup(dp.getId()));
         }

         dataPolicies.put(dp.getId(), dp);
      }

      List<DataPolicy> sortedDataPolicies = new ArrayList<DataPolicy>(dataPolicies.values());
      Collections.sort(sortedDataPolicies, new DataPolicyComparator());
      return sortedDataPolicies;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getAllUserDataPoliciesOperations(java.lang.String)
    */
   @Override
   public List<DataPolicyOperations> getAllUserDataPoliciesOperations(String username)
         throws Exception {
      //Get All Groups for this user
      UserManagementService userManagementService = SecurityServiceProvider
            .getUserManagementService();
      List<OpenWISGroup> groups = userManagementService.getUserGroups(username);

      return getAllUserDataPoliciesOperations(groups);
   }

   public List<UserDataPolicyOperations> getAllDataPoliciesOperations() throws Exception {
      List<UserDataPolicyOperations> userDataPolicyOperations = new ArrayList<UserDataPolicyOperations>();
      // Get All Users
      UserManager userManager = new UserManager(getDbms());
      List<String> usernames = userManager.getAllUserNames();

      for (String username : usernames) {
         List<DataPolicyOperations> dataPolicyOperations = getAllUserDataPoliciesOperations(username);

         UserDataPolicyOperations userDPO = new UserDataPolicyOperations();
         userDPO.setUser(username);
         userDPO.getDataPolicyOperations().addAll(dataPolicyOperations);

         userDataPolicyOperations.add(userDPO);
      }

      return userDataPolicyOperations;
   }

   @SuppressWarnings("unchecked")
   private List<DataPolicyOperations> getAllUserDataPoliciesOperations(List<OpenWISGroup> groups)
         throws SQLException {
      //Get data policies.
      String query = "SELECT DISTINCT DataPolicy.name as dpName, Operations.name as opName  FROM DataPolicy, OperationAllowed, Groups, Operations "
            + "WHERE Groups.id = OperationAllowed.groupid AND DataPolicy.id = OperationAllowed.datapolicyid "
            + "AND Operations.id = OperationAllowed.operationId";

      //Filter on group name
      ArrayList<String> groupIds = new ArrayList<String>();
      for (OpenWISGroup group : groups) {
         if (group.isIsGlobal()
               || group.getCentreName().equals(
                     OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME))) {
            for (String groupId : group.getGroupIds()) {
               groupIds.add("\'" + groupId + "\'");
            }
         }
      }
            
      query = query.concat(" AND Groups.name IN (" + StringUtils.join(groupIds.toArray(), ',') + ")");

      query = query.concat(" order by dpName");

      List<DataPolicyOperations> userOperationsAllowed = new ArrayList<DataPolicyOperations>();

      List<Element> records = getDbms().select(query).getChildren();
      String lastDpName = "";
      DataPolicyOperations dataPolicyOperations = new DataPolicyOperations();
      //Parse All Elements.
      for (Element e : records) {
         String dpName = e.getChild("dpname").getText();

         // If dpName different -> create new data policy operations.
         if (!lastDpName.equals(dpName)) {
            // If lastDpName not empty -> store the data policy operations into the userOperationsAllowed list.
            if (!lastDpName.isEmpty()) {
               userOperationsAllowed.add(dataPolicyOperations);
            }
            lastDpName = dpName;
            //create new data policy operations
            dataPolicyOperations = new DataPolicyOperations();
            dataPolicyOperations.setDataPolicy(dpName);
         }

         // Get Operation Name and add to the data policy operations list.
         String operationName = e.getChild("opname").getText();
         org.openwis.dataservice.Operation operation = org.openwis.dataservice.Operation
               .fromValue(operationName);
         dataPolicyOperations.getOperations().add(operation);
      }
      // Add the last data policy operations into the userOperationsAllowed list.
      userOperationsAllowed.add(dataPolicyOperations);

      return userOperationsAllowed;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getDataPolicyByName(java.lang.String, boolean, boolean)
    */
   @Override
   @SuppressWarnings("unchecked")
   public DataPolicy getDataPolicyByName(String name, boolean loadAlias, boolean loadOperations)
         throws SQLException {
      DataPolicy datapolicy = null;
      String query = "SELECT * FROM DataPolicy WHERE name=?";
      List<Element> records = getDbms().select(query, name).getChildren();
      if (records.size() != 0) {
         datapolicy = buildDataPolicyFromElement(records.get(0));
         if (loadAlias) {
            datapolicy.getAliases().addAll(getDataPolicyAlias(datapolicy.getId()));
         }

         if (loadOperations) {
            datapolicy.setDpOpPerGroup(getDataPolicyOperationPerGroup(datapolicy.getId()));
         }
      }

      return datapolicy;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getDataPolicyByNameOrAlias(java.lang.String)
    */
   @Override
   @SuppressWarnings("unchecked")
   public DataPolicy getDataPolicyByNameOrAlias(String name, String defaultName)
         throws SQLException {
      DataPolicy datapolicy = null;

      String query = "SELECT dp.* FROM DataPolicy dp "
            + "LEFT JOIN DataPolicyAlias dpa ON dpa.dpid = dp.id "
            + "WHERE dp.name = ? OR dpa.name = ? ";
      List<Element> records = getDbms().select(query, name, name).getChildren();
      if (records.size() == 0 && defaultName != null) {
         records = getDbms().select(query, defaultName, defaultName).getChildren();
      }
      if (records.size() != 0) {
         datapolicy = buildDataPolicyFromElement(records.get(0));
      }
      
      return datapolicy;
   }

   /**
    * Gets the additional default data policy.
    *
    * @return the additional default data policy
    * @throws SQLException the sQL exception
    */
   @Override
   public DataPolicy getAdditionalDefaultDataPolicy() throws SQLException {
      return getDataPolicyByName(ADDITIONAL_DEFAULT_DATA_POLICY_NAME, false, false);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getDataPolicyById(int, boolean, boolean)
    */
   @Override
   @SuppressWarnings("unchecked")
   public DataPolicy getDataPolicyById(int id, boolean loadAlias, boolean loadOperations)
         throws SQLException {
      DataPolicy datapolicy = null;
      String query = "SELECT * FROM DataPolicy WHERE id=?";
      List<Element> records = getDbms().select(query, id).getChildren();
      if (records.size() != 0) {
         datapolicy = buildDataPolicyFromElement(records.get(0));
         if (loadAlias) {
            datapolicy.getAliases().addAll(getDataPolicyAlias(datapolicy.getId()));
         }

         if (loadOperations) {
            datapolicy.setDpOpPerGroup(getDataPolicyOperationPerGroup(datapolicy.getId()));
         }
      }

      return datapolicy;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getDataPolicyByMetadataUrn(java.lang.String, boolean, boolean)
    */
   @Override
   @SuppressWarnings("unchecked")
   public DataPolicy getDataPolicyByMetadataUrn(String metadataUrn, boolean loadAlias,
         boolean loadOperations) throws SQLException {
      DataPolicy datapolicy = null;
      String query = "SELECT DataPolicy.* FROM DataPolicy, Metadata WHERE Metadata.datapolicy = DataPolicy.id AND Metadata.uuid = ?";
      List<Element> records = getDbms().select(query, metadataUrn).getChildren();
      if (records.size() != 0) {
         datapolicy = buildDataPolicyFromElement(records.get(0));
         if (loadAlias) {
            datapolicy.getAliases().addAll(getDataPolicyAlias(datapolicy.getId()));
         }

         if (loadOperations) {
            datapolicy.setDpOpPerGroup(getDataPolicyOperationPerGroup(datapolicy.getId()));
         }
      }

      return datapolicy;

   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getDefaultDataPolicyName()
    */
   @Override
   public String getDefaultDataPolicyName() {
      return OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEFAULT_DATA_POLICY_NAME);
   }

   /**
    * Description goes here.
    * @param element
    * @return
    */
   private DataPolicy buildDataPolicyFromElement(Element record) {
      DataPolicy dp = new DataPolicy();
      dp.setId(Integer.parseInt(record.getChildText(ID)));
      dp.setName(record.getChildText(NAME));
      dp.setDescription(record.getChildText(DESC));
      return dp;
   }

   /**
    * Description goes here.
    * @param element
    * @return
    */
   private DataPolicyAlias buildDataPolicyAliasFromElement(Element record) {
      DataPolicyAlias dp = new DataPolicyAlias();
      dp.setId(Integer.parseInt(record.getChildText(ID)));
      dp.setAlias(record.getChildText(NAME));
      return dp;
   }

   /**
    * Description goes here.
    * @param datapolicy
    * @return
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   private void checkDataPolicy(DataPolicy datapolicy) throws Exception {
      // check if name or alias does not exists
      Collection<String> dpNames = getAllDataPolicyNames();

      Collection<String> dpAlias = CollectionUtils.collect(datapolicy.getAliases(),
            new Transformer() {
               @Override
               public Object transform(Object arg0) {
                  return ((DataPolicyAlias) arg0).getAlias();
               }
            });

      if (CollectionUtils.containsAny(dpNames, dpAlias)) {
         throw new InvalidDataPolicyAliasException(CollectionUtils.intersection(dpNames, dpAlias));
      }

      if (dpNames.contains(datapolicy.getName())) {
         throw new InvalidDataPolicyNameException(datapolicy.getName());
      }

      //TODO when updating the data policy.

   }

   /**
    * Description goes here.
    * @return
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   private Collection<String> getAllDataPolicyNames() throws SQLException {
      Set<String> dpNames = new HashSet<String>();

      String query = "SELECT name FROM DataPolicy";
      List<Element> names = getDbms().select(query).getChildren();
      if (names.size() != 0) {
         for (Element name : names) {
            dpNames.add(name.getChildText(NAME));
         }
      }

      query = "SELECT name FROM DataPolicyAlias";
      names = getDbms().select(query).getChildren();

      if (names.size() != 0) {
         for (Element name : names) {
            dpNames.add(name.getChildText(NAME));
         }
      }
      return dpNames;
   }

   /**
    * Description goes here.
    * @param records
    * @return
    */
   private List<DataPolicyOperationsPerGroup> buildDataPolicyOperationsPerGroup(
         List<Element> records) {
      Map<Group, DataPolicyOperationsPerGroup> assoc = new HashMap<Group, DataPolicyOperationsPerGroup>();
      for (Element e : records) {
         Integer groupId = Integer.parseInt(e.getChildText("groupid"));
         String groupName = e.getChildText("groupname");
         Integer operationId = Integer.parseInt(e.getChildText("operationid"));
         String operationName = e.getChildText("operationname");
         boolean global = "y".equals(e.getChildText("groupisglobal"));

         Group group = new Group(groupId, groupName);
         group.setGlobal(global);
         if (!assoc.containsKey(group)) {
            DataPolicyOperationsPerGroup opPerGroup = new DataPolicyOperationsPerGroup();
            opPerGroup.setGroup(group);
            assoc.put(group, opPerGroup);
         }

         DataPolicyGroupPrivileges priv = new DataPolicyGroupPrivileges();
         priv.setOperation(new Operation(operationId, operationName));
         priv.setAuthorized(true);
         assoc.get(group).getPrivilegesPerOp().add(priv);
      }
      return new ArrayList<DataPolicyOperationsPerGroup>(assoc.values());
   }

   /**
    * Description goes here.
    * @param dataPolicyId
    * @return
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   private Collection<DataPolicyAlias> getDataPolicyAlias(Integer dataPolicyId) throws SQLException {
      Collection<DataPolicyAlias> aliases = new ArrayList<DataPolicyAlias>();
      String query = "SELECT * FROM DataPolicyAlias WHERE dpid = ?";
      List<Element> records = getDbms().select(query, dataPolicyId).getChildren();
      for (Element e : records) {
         aliases.add(buildDataPolicyAliasFromElement(e));
      }
      return aliases;
   }

   /**
    * Description goes here.
    * @param dataPolicyId
    * @return
    * @throws SQLException
    */
   @SuppressWarnings("unchecked")
   private List<DataPolicyOperationsPerGroup> getDataPolicyOperationPerGroup(Integer dataPolicyId)
         throws SQLException {
      String query = "SELECT groups.id as groupId, groups.name as groupName,  groups.isglobal as groupIsGlobal, "
            + "operations.id as operationId, operations.name as operationName "
            + "FROM operationallowed, groups, operations "
            + "WHERE operationallowed.groupid = groups.id and operationallowed.operationid = operations.id "
            + "AND operationallowed.datapolicyid = ?";
      List<Element> records = getDbms().select(query, dataPolicyId).getChildren();

      return buildDataPolicyOperationsPerGroup(records);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#createDataPolicyOperationsPerGroup(java.util.Collection, int)
    */
   @Override
   public void createDataPolicyOperationsPerGroup(
         Collection<DataPolicyOperationsPerGroup> opsPerGroups, int dataPolicyId)
         throws SQLException {
      for (DataPolicyOperationsPerGroup opsPerGroup : opsPerGroups) {
         for (DataPolicyGroupPrivileges privilegesPerOp : opsPerGroup.getPrivilegesPerOp()) {
            if (privilegesPerOp.isAuthorized()) {
               String query = "INSERT INTO operationallowed(groupid, datapolicyid, operationid) VALUES (?,?,?)";
               getDbms().execute(query, opsPerGroup.getGroup().getId(), dataPolicyId,
                     privilegesPerOp.getOperation().getId());
            }
         }
      }
   }

   //------------------------------------------------- Operations

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getAllOperations()
    */
   @Override
   @SuppressWarnings("unchecked")
   public Collection<Operation> getAllOperations() throws SQLException {
      if (operations == null) {
         operations = new ArrayList<Operation>();
         String query = "SELECT * FROM Operations ORDER BY id";
         List<Element> records = getDbms().select(query).getChildren();
         for (Element e : records) {
            operations.add(buildOperationFromElement(e));
         }
      }
      return operations;
   }

   /**
    * Description goes here.
    * @param element
    * @return
    */
   private static Operation buildOperationFromElement(Element record) {
      Operation operation = new Operation();
      operation.setId(Integer.parseInt(record.getChildText(ID)));
      operation.setName(record.getChildText(NAME));
      return operation;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getAllOperationAllowedByMetadataId(java.lang.String, java.util.Collection)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Collection<Operation> getAllOperationAllowedByMetadataId(String metadataId,
         Collection<Group> groups) throws Exception {
      Collection<Operation> allOperations = new ArrayList<Operation>();
      Collection<String> groupIDs = CollectionUtils.collect(groups, new Transformer() {
         @Override
         public Object transform(Object arg0) {
            return ((Group) arg0).getId().toString();
         }
      });
      if (groupIDs != null && !groupIDs.isEmpty()) { // Otherwise return an empty list
         StringBuffer query = new StringBuffer();
         query.append("SELECT Operations.*, Metadata.id  FROM Operations, OperationAllowed, Metadata ");
         query.append("WHERE  OperationAllowed.groupId IN (");
         query.append(StringUtils.join(groupIDs.toArray(), ','));
         query.append(") AND OperationAllowed.datapolicyId = Metadata.datapolicy");
         query.append(" AND Operations.id = OperationAllowed.operationId ");
         query.append(" AND Metadata.id = ? ");

         List<Element> records = getDbms().select(query.toString(), new Integer(metadataId))
               .getChildren();
         for (Element e : records) {
            allOperations.add(buildOperationFromElement(e));
         }
      }
      return allOperations;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getAllOperationAllowedByMetadataId(java.util.Set, java.util.List)
    */
   @Override
   public Map<String, Set<OperationEnum>> getAllOperationAllowedByMetadataId(
         Set<String> metadataId, List<Group> groups) throws SQLException {
      Map<String, Set<OperationEnum>> result = new HashMap<String, Set<OperationEnum>>();
      Collection<String> groupIDs = Collections2.transform(groups, new Function<Group, String>() {
         @Override
         public String apply(Group group) {
            return group.getId().toString();
         }
      });
      if (groupIDs != null && !groupIDs.isEmpty() && metadataId != null && !metadataId.isEmpty()) { // Otherwise return an empty list

         StringBuffer query = new StringBuffer();
         query.append("SELECT Operations.id as opeId, Metadata.uuid as mdId  FROM Operations, OperationAllowed, Metadata ");
         query.append("WHERE  OperationAllowed.groupId IN (");
         query.append(StringUtils.join(groupIDs.toArray(), ','));
         query.append(") AND OperationAllowed.datapolicyId = Metadata.datapolicy");
         query.append(" AND Operations.id = OperationAllowed.operationId ");
         query.append(" AND Metadata.uuid IN ( ");
         boolean isFirst = true;
         for (String mdId : metadataId) {
            if (isFirst) {
               isFirst = false;
            } else {
               query.append(",");
            }
            query.append('\'');
            query.append(StringEscapeUtils.escapeSql(mdId));
            query.append('\'');
         }
         query.append(")");

         Element records = getDbms().select(query.toString());
         Set<OperationEnum> allOperations;
         @SuppressWarnings("unchecked")
         List<Element> children = records.getChildren();
         String mdId;
         int operation;
         for (Element elt : children) {
            mdId = elt.getChildText("mdid");
            allOperations = result.get(mdId);
            if (allOperations == null) {
               allOperations = new LinkedHashSet<OperationEnum>();
               result.put(mdId, allOperations);
            }
            operation = Integer.valueOf(elt.getChildText("opeid"));
            allOperations.add(OperationEnum.getFromId(operation));
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getAllOperationAllowedByMetadataUrn(java.lang.String, java.util.Collection)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Collection<Operation> getAllOperationAllowedByMetadataUrn(String metadataUrn,
         Collection<Group> groups) throws Exception {

      Collection<Operation> allOperations = new ArrayList<Operation>();
      Collection<String> groupIDs = CollectionUtils.collect(groups, new Transformer() {
         @Override
         public Object transform(Object arg0) {
            return ((Group) arg0).getId().toString();
         }
      });

      if (groupIDs != null && !groupIDs.isEmpty()) { // Otherwise return an empty list
         StringBuffer query = new StringBuffer();
         query.append("SELECT Operations.* FROM Operations, OperationAllowed, Metadata ");
         query.append("WHERE  OperationAllowed.groupId IN (");
         query.append(StringUtils.join(groupIDs.toArray(), ','));
         query.append(") AND OperationAllowed.datapolicyId = Metadata.datapolicy");
         query.append(" AND Operations.id = OperationAllowed.operationId ");
         query.append(" AND Metadata.uuid = ?");

         List<Element> records = getDbms().select(query.toString(), metadataUrn).getChildren();
         for (Element e : records) {
            allOperations.add(buildOperationFromElement(e));
         }
      }
      return allOperations;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager#getDefaultOperationsForDefaultDataPolicy()
    */
   @Override
   public Collection<Operation> getDefaultOperationsForDefaultDataPolicy() throws SQLException {
      String operations = OpenwisMetadataPortalConfig
            .getString(ConfigurationConstants.DEFAULT_DATA_POLICY_OPERATIONS);

      Collection<Operation> allOp = getAllOperations();
      final Collection<Integer> defaultOp = new ArrayList<Integer>();

      for (String op : operations.split(",")) {
         OperationEnum opEnum = OperationEnum.valueOf(op);
         if (opEnum != null) {
            defaultOp.add(opEnum.getId());
         }
      }

      CollectionUtils.filter(allOp, new Predicate() {
         @Override
         public boolean evaluate(Object arg0) {
            return defaultOp.contains(((Operation) arg0).getId());
         }
      });

      return allOp;
   }

}
