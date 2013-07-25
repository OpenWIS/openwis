/**
 * 
 */
package org.openwis.metadataportal.model.availability;

/**
 * An object to list all modules of metadata service with corresponding availability. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataServiceAvailability {

   /**
    * The user catalog.
    */
   private Availability userPortal;

   /**
    * The synchronization module.
    */
   private Availability synchronization;

   /**
    * The harvesting module.
    */
   private Availability harvesting;

   /**
    * The indexing.
    */
   private Availability indexing;

   /**
    * Gets the userPortal.
    * @return the userPortal.
    */
   public Availability getUserPortal() {
      return userPortal;
   }

   /**
    * Sets the userPortal.
    * @param userPortal the userPortal to set.
    */
   public void setUserPortal(Availability userPortal) {
      this.userPortal = userPortal;
   }

   /**
    * Gets the synchronization.
    * @return the synchronization.
    */
   public Availability getSynchronization() {
      return synchronization;
   }

   /**
    * Sets the synchronization.
    * @param synchronization the synchronization to set.
    */
   public void setSynchronization(Availability synchronization) {
      this.synchronization = synchronization;
   }

   /**
    * Gets the harvesting.
    * @return the harvesting.
    */
   public Availability getHarvesting() {
      return harvesting;
   }

   /**
    * Sets the harvesting.
    * @param harvesting the harvesting to set.
    */
   public void setHarvesting(Availability harvesting) {
      this.harvesting = harvesting;
   }

   /**
    * Gets the indexing.
    * @return the indexing.
    */
   public Availability getIndexing() {
      return indexing;
   }

   /**
    * Sets the indexing.
    * @param indexing the indexing to set.
    */
   public void setIndexing(Availability indexing) {
      this.indexing = indexing;
   }
}
