/**
 * 
 */
package org.openwis.metadataportal.model.harvest;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvestingStatistics {

   /**
    * The number of active harvesting tasks.
    */
   private int active;

   /**
    * The number of harvesting tasks in failure.
    */
   private int failure;
   
   /**
    * The number of harvesting tasks.
    */
   private int total;


   /**
    * Gets the active.
    * @return the active.
    */
   public int getActive() {
      return active;
   }

   /**
    * Sets the active.
    * @param active the active to set.
    */
   public void setActive(int active) {
      this.active = active;
   }
   
   /**
    * Increment the active tasks.
    */
   public void incActive() {
      this.active++;
   }

   /**
    * Gets the failure.
    * @return the failure.
    */
   public int getFailure() {
      return failure;
   }

   /**
    * Sets the failure.
    * @param failure the failure to set.
    */
   public void setFailure(int failure) {
      this.failure = failure;
   }
   
   /**
    * Gets the total.
    * @return the total.
    */
   public int getTotal() {
      return total;
   }
   
   /**
    * Increment the total tasks.
    */
   public void incTotal() {
      this.total++;
   }
   
   /**
    * Sets the total.
    * @param total the total to set.
    */
   public void setTotal(int total) {
      this.total = total;
   }
   
   public double getSuccessRatioPrct() {
      if(this.active == 0) {
         return 100;
      }
      int success = active - failure;
      return Math.floor(((float) success / active) * 100);
   }
}
