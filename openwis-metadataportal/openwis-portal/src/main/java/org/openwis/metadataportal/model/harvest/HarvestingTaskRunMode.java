/**
 * 
 */
package org.openwis.metadataportal.model.harvest;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvestingTaskRunMode {

   private boolean recurrent;

   private Integer recurrentPeriod;

   /**
    * Default constructor.
    * Builds a HarvestingRunMode.
    */
   public HarvestingTaskRunMode() {
      super();
   }

   /**
    * Default constructor.
    * Builds a HarvestingRunMode.
    * @param recurrent
    */
   public HarvestingTaskRunMode(boolean recurrent) {
      super();
      this.recurrent = recurrent;
   }

   /**
    * Default constructor.
    * Builds a HarvestingRunMode.
    * @param recurrent
    * @param recurrentPeriod
    */
   public HarvestingTaskRunMode(boolean recurrent, Integer recurrentPeriod) {
      super();
      this.recurrent = recurrent;
      this.recurrentPeriod = recurrentPeriod;
   }

   /**
    * Gets the recurrent.
    * @return the recurrent.
    */
   public boolean isRecurrent() {
      return recurrent;
   }

   /**
    * Sets the recurrent.
    * @param recurrent the recurrent to set.
    */
   public void setRecurrent(boolean recurrent) {
      this.recurrent = recurrent;
   }

   /**
    * Gets the recurrentPeriod.
    * @return the recurrentPeriod.
    */
   public Integer getRecurrentPeriod() {
      return recurrentPeriod;
   }

   /**
    * Sets the recurrentPeriod.
    * @param recurrentPeriod the recurrentPeriod to set.
    */
   public void setRecurrentPeriod(Integer recurrentPeriod) {
      this.recurrentPeriod = recurrentPeriod;
   }
}
