/**
 *
 */
package org.openwis.metadataportal.kernel.datapolicy;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openwis.dataservice.DataPolicyOperations;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.datapolicy.DataPolicyOperationsPerGroup;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;

/**
 * The Data Policy Manager. <P>
 *
 */
public interface IDataPolicyManager {

   /**
    * Create a data policy and its aliases.
    * @param dp the data policy to create.
    * @throws Exception if an error occurs.
    */
   void createDataPolicy(DataPolicy dp) throws Exception;

   /**
    * Updates a data policy and its aliases.
    * @param dp the data policy to update.
    * @throws Exception if an error occurs.
    */
   void updateDataPolicy(DataPolicy dp) throws Exception;

   /**
    * Remove a data policy. Note: The ID is used to delete data policy.
    * @param dp a data policy to delete.
    * @throws Exception if an error occurs.
    */
   void removeDataPolicy(DataPolicy dp) throws Exception;

   /**
    * Get all data policies.
    * @param loadAlias <code>true</code> if the alias should be loaded, <code>false</code> otherwise.
    * @param loadOperations <code>true</code> if the operations should be loaded, <code>false</code> otherwise.
    * @return the data policies.
    * @throws Exception if an error occurs.
    */
   List<DataPolicy> getAllDataPolicies(boolean loadAlias, boolean loadOperations)
         throws Exception;

   /**
    * Description goes here.
    * @param username The user name
    * @param loadAlias <code>true</code> if the alias should be loaded, <code>false</code> otherwise.
    * @param loadOperations <code>true</code> if the operations should be loaded, <code>false</code> otherwise.
    * @return data policy list
    * @throws Exception if an error occurs.
    */
   List<DataPolicy> getAllUserDataPolicies(String username, boolean loadAlias, boolean loadOperations)
         throws Exception;

   /**
    * get All User Data Policies Operations.
    * @param username The user name
    * @param userId The user Id
    * @return
    * @throws Exception if an error occurs.
    */
   List<DataPolicyOperations> getAllUserDataPoliciesOperations(String username) throws Exception;

   /**
    * Get a data policy by name
    * @param name the name of a data policy
    * @return a data policy or null if any data policy matches
    * @throws Exception if an error occurs.
    */
   DataPolicy getDataPolicyByName(String name, boolean loadAlias, boolean loadOperations)
         throws Exception;

   /**
    * Get a data policy by name
    * @param name the name of a data policy
    * @param default name in case the name not found, can be null
    * @return a data policy or null if any data policy matches
    * @throws Exception if an error occurs.
    */
   DataPolicy getDataPolicyByNameOrAlias(String name, String defaultName) throws Exception;

   /**
    * Get a data policy by ID
    * @param id the id of a data policy
    * @return a data policy or null if any data policy matches
    * @throws Exception if an error occurs.
    */
   DataPolicy getDataPolicyById(int id, boolean loadAlias, boolean loadOperations)
         throws Exception;

   /**
    * Get a data policy by URN
    * @param metadataUrn
    * @param loadAlias <code>true</code> if the alias should be loaded, <code>false</code> otherwise.
    * @param loadOperations <code>true</code> if the operations should be loaded, <code>false</code> otherwise.
    * @return
    * @throws Exception if an error occurs.
    */
   DataPolicy getDataPolicyByMetadataUrn(String metadataUrn, boolean loadAlias,
         boolean loadOperations) throws Exception;

   /**
    * Get the default data policy name
    * @return the default data policy name
    */
   String getDefaultDataPolicyName();

   /**
    * Insert the operations allowed per Groups
    * @param opsPerGroups
    * @throws Exception if an error occurs.
    */
   void createDataPolicyOperationsPerGroup(Collection<DataPolicyOperationsPerGroup> opsPerGroups,
         int dataPolicyId) throws Exception;

   /**
   * Get all groups.
   * @return all groups.
   * @throws Exception if an error occurs.
   */
   Collection<Operation> getAllOperations() throws Exception;

   /**
    * Returns all operations permitted by the user on a particular metadata.
    * @param metadataId The metadata Id
    * @param groups
    * @return
    * @throws Exception if an error occurs.
    */
   Collection<Operation> getAllOperationAllowedByMetadataId(String metadataId,
         Collection<Group> groups) throws Exception;

   /**
    * Gets the all operation allowed by metadata id.
    *
    * @param metadataId the metadata id.
    * @param groups the groups
    * @return the all operation allowed by metadata id
    * @throws Exception the SQL exception
    */
   Map<String, Set<OperationEnum>> getAllOperationAllowedByMetadataId(Set<String> metadataId,
         List<Group> groups) throws Exception;

   /**
    * Returns all operations permitted by the user on a particular metadata.
    * @param metadataId
    * @param groups
    * @return
    * @throws Exception if an error occurs.
    */
   Collection<Operation> getAllOperationAllowedByMetadataUrn(String metadataUrn,
         Collection<Group> groups) throws Exception;

   /**
    * Get default operations for the default Data Policy
    * @return a collection of Operation
    * @throws Exception if an error occurs.
    */
   Collection<Operation> getDefaultOperationsForDefaultDataPolicy() throws Exception;

   /**
    * Gets the additional default data policy.
    *
    * @return the additional default data policy
    * @throws SQLException the sQL exception
    */
   DataPolicy getAdditionalDefaultDataPolicy() throws SQLException;

}