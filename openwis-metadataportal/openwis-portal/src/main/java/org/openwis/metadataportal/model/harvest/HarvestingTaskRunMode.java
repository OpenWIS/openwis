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
   
   //rajout d'attributs correspondants à la nouvelle IHM
   private String recurrentScale;
   private Integer recurrencePeriod;
   private String startingDate;

   public String getRecurrentScale() {
      return recurrentScale;
   }

   public void setRecurrentScale(String recurrentScale) {
      this.recurrentScale = recurrentScale;
   }

   public Integer getRecurrencePeriod() {
      return recurrencePeriod;
   }

   public void setRecurrencePeriod(Integer recurrencePeriod) {
      this.recurrencePeriod = recurrencePeriod;
   }

   public String getStartingDate() {
      return startingDate;
   }

   public void setStartingDate(String startingDate) {
      this.startingDate = startingDate;
   }

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
