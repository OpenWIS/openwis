/**
 * 
 */
package org.openwis.metadataportal.model.harvest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.deployment.Deployment;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.MetadataValidation;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvestingTask {

   private Integer id;

   private String uuid;

   private String name;

   private String type;

   private Date lastRun;

   private Map<String, String> configuration;

   private MetadataAlignerResult lastResult;

   private Deployment backup;

   private HarvestingTaskStatus status;

   private HarvestingTaskRunMode runMode;

   private MetadataValidation validationMode;

   private boolean synchronizationTask;

   private boolean incremental;

   private Category category;

   /**
    * Gets the id.
    * @return the id.
    */
   public Integer getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(Integer id) {
      this.id = id;
   }

   /**
    * Gets the uuid.
    * @return the uuid.
    */
   public String getUuid() {
      return uuid;
   }

   /**
    * Sets the uuid.
    * @param uuid the uuid to set.
    */
   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   /**
    * Gets the name.
    * @return the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * @param name the name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the type.
    * @return the type.
    */
   public String getType() {
      return type;
   }

   /**
    * Sets the type.
    * @param type the type to set.
    */
   public void setType(String type) {
      this.type = type;
   }

   /**
    * Gets the lastRun.
    * @return the lastRun.
    */
   public Date getLastRun() {
      return lastRun;
   }

   /**
    * Sets the lastRun.
    * @param lastRun the lastRun to set.
    */
   public void setLastRun(Date lastRun) {
      this.lastRun = lastRun;
   }

   /**
    * Gets the configuration.
    * @return the configuration.
    */
   public Map<String, String> getConfiguration() {
      if (configuration == null) {
         configuration = new HashMap<String, String>();
      }
      return configuration;
   }

   /**
    * Sets the configuration.
    * @param configuration the configuration to set.
    */
   public void setConfiguration(Map<String, String> configuration) {
      this.configuration = configuration;
   }

   /**
    * Gets the lastResult.
    * @return the lastResult.
    */
   public MetadataAlignerResult getLastResult() {
      return lastResult;
   }

   /**
    * Sets the lastResult.
    * @param lastResult the lastResult to set.
    */
   public void setLastResult(MetadataAlignerResult lastResult) {
      this.lastResult = lastResult;
   }

   /**
    * Gets the backup.
    * @return the backup.
    */
   public Deployment getBackup() {
      return backup;
   }

   /**
    * Sets the backup.
    * @param backup the backup to set.
    */
   public void setBackup(Deployment backup) {
      this.backup = backup;
   }

   /**
    * Gets the status.
    * @return the status.
    */
   public HarvestingTaskStatus getStatus() {
      return status;
   }

   /**
    * Sets the status.
    * @param status the status to set.
    */
   public void setStatus(HarvestingTaskStatus status) {
      this.status = status;
   }

   /**
    * Gets the runMode.
    * @return the runMode.
    */
   public HarvestingTaskRunMode getRunMode() {
      return runMode;
   }

   /**
    * Sets the runMode.
    * @param runMode the runMode to set.
    */
   public void setRunMode(HarvestingTaskRunMode runMode) {
      this.runMode = runMode;
   }

   /**
    * Gets the synchronizationTask.
    * @return the synchronizationTask.
    */
   public boolean isSynchronizationTask() {
      return synchronizationTask;
   }

   /**
    * Sets the synchronizationTask.
    * @param synchronizationTask the synchronizationTask to set.
    */
   public void setSynchronizationTask(boolean synchronizationTask) {
      this.synchronizationTask = synchronizationTask;
   }

   /**
    * Gets the incremental.
    * @return the incremental.
    */
   public boolean isIncremental() {
      return incremental;
   }

   /**
    * Sets the incremental.
    * @param incremental the incremental to set.
    */
   public void setIncremental(boolean incremental) {
      this.incremental = incremental;
   }

   /**
    * Gets the validationMode.
    * @return the validationMode.
    */
   public MetadataValidation getValidationMode() {
      return validationMode;
   }

   /**
    * Sets the validationMode.
    * @param validationMode the validationMode to set.
    */
   public void setValidationMode(MetadataValidation validationMode) {
      this.validationMode = validationMode;
   }

   /**
    * Gets the category.
    * @return the category.
    */
   public Category getCategory() {
      return category;
   }

   /**
    * Sets the category.
    * @param category the category to set.
    */
   public void setCategory(Category category) {
      this.category = category;
   }
}
