/**
 * 
 */
package org.openwis.metadataportal.kernel.datapolicy;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataPolicyLinkedToMetadataException extends Exception {

   private String dataPolicyName;

   /**
     * Default constructor.
     * Builds a InvalidDataPolicyAliasException.
     */
   public DataPolicyLinkedToMetadataException(String dataPolicyName) {
      super();
      this.dataPolicyName = dataPolicyName;
   }

   /**
    * Gets the dataPolicyName.
    * @return the dataPolicyName.
    */
   public String getDataPolicyName() {
      return dataPolicyName;
   }
}
