/**
 * 
 */
package org.openwis.metadataportal.model.datapolicy;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPolicyGroupPrivileges {

   private Operation operation;

   private boolean authorized;

   /**
    * Default constructor.
    * Builds a DataPolicyGroupPrivilegesPerOperation.
    */
   public DataPolicyGroupPrivileges() {
      super();
   }

   /**
    * Default constructor.
    * Builds a DataPolicyGroupPrivilegesPerOperation.
    * @param operation
    * @param authorized
    */
   public DataPolicyGroupPrivileges(Operation operation, boolean authorized) {
      super();
      this.operation = operation;
      this.authorized = authorized;
   }

   /**
    * Gets the operation.
    * @return the operation.
    */
   public Operation getOperation() {
      return operation;
   }

   /**
    * Sets the operation.
    * @param operation the operation to set.
    */
   public void setOperation(Operation operation) {
      this.operation = operation;
   }

   /**
    * Gets the authorized.
    * @return the authorized.
    */
   public boolean isAuthorized() {
      return authorized;
   }

   /**
    * Sets the authorized.
    * @param authorized the authorized to set.
    */
   public void setAuthorized(boolean authorized) {
      this.authorized = authorized;
   }
}
