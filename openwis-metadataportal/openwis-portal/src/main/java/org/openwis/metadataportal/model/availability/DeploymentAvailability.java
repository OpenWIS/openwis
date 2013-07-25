/**
 * 
 */
package org.openwis.metadataportal.model.availability;

/**
 * An object to hold the information availability of a site. <P>
 * Explanation goes here. <P>
 * 
 */
public class DeploymentAvailability {

   private MetadataServiceAvailability metadataServiceAvailability;

   private DataServiceAvailability dataServiceAvailability;

   private SecurityServiceAvailability securityServiceAvailability;

   /**
    * Gets the metadataServiceAvailability.
    * @return the metadataServiceAvailability.
    */
   public MetadataServiceAvailability getMetadataServiceAvailability() {
      return metadataServiceAvailability;
   }

   /**
    * Sets the metadataServiceAvailability.
    * @param metadataServiceAvailability the metadataServiceAvailability to set.
    */
   public void setMetadataServiceAvailability(
         MetadataServiceAvailability metadataServiceAvailability) {
      this.metadataServiceAvailability = metadataServiceAvailability;
   }

   /**
    * Gets the dataServiceAvailability.
    * @return the dataServiceAvailability.
    */
   public DataServiceAvailability getDataServiceAvailability() {
      return dataServiceAvailability;
   }

   /**
    * Sets the dataServiceAvailability.
    * @param dataServiceAvailability the dataServiceAvailability to set.
    */
   public void setDataServiceAvailability(DataServiceAvailability dataServiceAvailability) {
      this.dataServiceAvailability = dataServiceAvailability;
   }

   /**
    * Gets the securityServiceAvailability.
    * @return the securityServiceAvailability.
    */
   public SecurityServiceAvailability getSecurityServiceAvailability() {
      return securityServiceAvailability;
   }

   /**
    * Sets the securityServiceAvailability.
    * @param securityServiceAvailability the securityServiceAvailability to set.
    */
   public void setSecurityServiceAvailability(
         SecurityServiceAvailability securityServiceAvailability) {
      this.securityServiceAvailability = securityServiceAvailability;
   }
}
