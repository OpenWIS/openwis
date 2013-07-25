/**
 * 
 */
package org.openwis.metadataportal.model.metadata.source;

import org.openwis.metadataportal.model.harvest.HarvestingTask;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvestingSource extends AbstractSource {

   private HarvestingTask harvestingTask;

   // Needed for some havesters...
   private String harvestingURI;

   /**
    * Default constructor.
    * Builds a HarvestingSource.
    * @param harvestingTask
    */
   public HarvestingSource(HarvestingTask harvestingTask) {
      super();
      this.harvestingTask = harvestingTask;
      if(this.harvestingTask.isSynchronizationTask()) {
         setProcessType(ProcessType.SYNCHRO);
      } else {
         setProcessType(ProcessType.HARVEST);
      }
   }

   /**
    * Gets the harvestingTask.
    * @return the harvestingTask.
    */
   public HarvestingTask getHarvestingTask() {
      return harvestingTask;
   }

   /**
    * Sets the harvestingTask.
    * @param harvestingTask the harvestingTask to set.
    */
   public void setHarvestingTask(HarvestingTask harvestingTask) {
      this.harvestingTask = harvestingTask;
   }

   /**
    * Gets the harvestingURI.
    * @return the harvestingURI.
    */
   public String getHarvestingURI() {
      return harvestingURI;
   }

   /**
    * Sets the harvestingURI.
    * @param harvestingURI the harvestingURI to set.
    */
   public void setHarvestingURI(String harvestingURI) {
      this.harvestingURI = harvestingURI;
   }

}
