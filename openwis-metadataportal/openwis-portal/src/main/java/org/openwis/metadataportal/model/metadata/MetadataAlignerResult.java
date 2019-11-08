/**
 * 
 */
package org.openwis.metadataportal.model.metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataAlignerResult {

   private Date date;

   private int total;

   private int added;

   private int updated;

   private int unchanged;

   private int locallyRemoved;

   private int unknownSchema;

   private int badFormat;

   private int doesNotValidate;

   private int ignored;

   private int unexpected;

   private boolean fail;
   
   private int volume;

   private List<MetadataAlignerError> errors;
     
   private List<String> urnAdded = new ArrayList<String>();
   
   private List<String> urnUpdated = new ArrayList<String>();
   
   private List<String> urnRemoved = new ArrayList<String>();

   /**
    * Gets the date.
    * @return the date.
    */
   public Date getDate() {
      return date;
   }

   /**
    * Sets the date.
    * @param date the date to set.
    */
   public void setDate(Date date) {
      this.date = date;
   }

   /**
   * Gets the total.
   * @return the total.
   */
   public int getTotal() {
      return total;
   }

   /**
    * Sets the total.
    * @param total the total to set.
    */
   public void setTotal(int total) {
      this.total = total;
   }

   /**
    * Gets the added.
    * @return the added.
    */
   public int getAdded() {
      return added;
   }

   /**
    * Sets the added.
    * @param added the added to set.
    */
   public void setAdded(int added) {
      this.added = added;
   }

   /**
    * Gets the updated.
    * @return the updated.
    */
   public int getUpdated() {
      return updated;
   }

   /**
    * Sets the updated.
    * @param updated the updated to set.
    */
   public void setUpdated(int updated) {
      this.updated = updated;
   }

   /**
    * Gets the unchanged.
    * @return the unchanged.
    */
   public int getUnchanged() {
      return unchanged;
   }

   /**
    * Sets the unchanged.
    * @param unchanged the unchanged to set.
    */
   public void setUnchanged(int unchanged) {
      this.unchanged = unchanged;
   }

   /**
    * Gets the locallyRemoved.
    * @return the locallyRemoved.
    */
   public int getLocallyRemoved() {
      return locallyRemoved;
   }

   /**
    * Sets the locallyRemoved.
    * @param locallyRemoved the locallyRemoved to set.
    */
   public void setLocallyRemoved(int locallyRemoved) {
      this.locallyRemoved = locallyRemoved;
   }

   /**
    * Gets the unknownSchema.
    * @return the unknownSchema.
    */
   public int getUnknownSchema() {
      return unknownSchema;
   }

   /**
    * Sets the unknownSchema.
    * @param unknownSchema the unknownSchema to set.
    */
   public void setUnknownSchema(int unknownSchema) {
      this.unknownSchema = unknownSchema;
   }

   /**
    * Gets the badFormat.
    * @return the badFormat.
    */
   public int getBadFormat() {
      return badFormat;
   }

   /**
    * Sets the badFormat.
    * @param badFormat the badFormat to set.
    */
   public void setBadFormat(int badFormat) {
      this.badFormat = badFormat;
   }

   /**
    * Gets the doesNotValidate.
    * @return the doesNotValidate.
    */
   public int getDoesNotValidate() {
      return doesNotValidate;
   }

   /**
    * Sets the doesNotValidate.
    * @param doesNotValidate the doesNotValidate to set.
    */
   public void setDoesNotValidate(int doesNotValidate) {
      this.doesNotValidate = doesNotValidate;
   }

   /**
    * Gets the ignored.
    * @return the ignored.
    */
   public int getIgnored() {
      return ignored;
   }

   /**
    * Sets the ignored.
    * @param ignored the ignored to set.
    */
   public void setIgnored(int ignored) {
      this.ignored = ignored;
   }

   /**
    * Gets the unexpected.
    * @return the unexpected.
    */
   public int getUnexpected() {
      return unexpected;
   }

   /**
    * Sets the unexpected.
    * @param unexpected the unexpected to set.
    */
   public void setUnexpected(int unexpected) {
      this.unexpected = unexpected;
   }

   /**
    * Gets the fail.
    * @return the fail.
    */
   public boolean isFail() {
      return fail;
   }

   /**
    * Sets the fail.
    * @param fail the fail to set.
    */
   public void setFail(boolean fail) {
      this.fail = fail;
   }

   /**
    * Gets the errors.
    * @return the errors.
    */
   public List<MetadataAlignerError> getErrors() {
      if (errors == null) {
         errors = new ArrayList<MetadataAlignerError>();
      }
      return errors;
   }

   /**
    * Sets the errors.
    * @param errors the errors to set.
    */
   public void setErrors(List<MetadataAlignerError> errors) {
      this.errors = errors;
   }

   //----------------------------------------------------------- Incrementers.

   /**
     * Sets the total.
     * @param total the total to set.
     */
   public void incTotal() {
      this.total++;
   }

   /**
    * Sets the added.
    * @param added the added to set.
    */
   public void incAdded() {
      this.added++;
   }

   /**
    * Sets the updated.
    * @param updated the updated to set.
    */
   public void incUpdated() {
      this.updated++;
   }

   /**
    * Sets the unchanged.
    * @param unchanged the unchanged to set.
    */
   public void incUnchanged() {
      this.unchanged++;
   }

   /**
    * Sets the locallyRemoved.
    * @param locallyRemoved the locallyRemoved to set.
    */
   public void incLocallyRemoved() {
      this.locallyRemoved++;
   }

   /**
    * Sets the unknownSchema.
    * @param unknownSchema the unknownSchema to set.
    */
   public void incUnknownSchema() {
      this.unknownSchema++;
   }

   /**
    * increment the badFormat : openwis not compliant.
    */
   public void incBadFormat() {
      this.badFormat++;
   }

   /**
    * Sets the doesNotValidate.
    * @param doesNotValidate the doesNotValidate to set.
    */
   public void incDoesNotValidate() {
      this.doesNotValidate++;
   }

   /**
    * Sets the ignored.
    * @param ignored the ignored to set.
    */
   public void incIgnored() {
      this.ignored++;
   }
   
   /**
    * Sets the ignored.
    * @param ignored the ignored to set.
    */
   public void incUnexpected() {
      this.unexpected++;
   }
   
   /**
    * Get the total volume of processed metadata.
    * @return the volume
    */
   public int getVolume() {
      return volume;
   }
   
   /**
    * Increments the volume.
    * @param volum the volume to increment.
    */
   public void incVolume(int volume) {
      this.volume += volume;
   }

   /**
    * Get list of urn for added metadata
    * @return
    */
   public List<String> getUrnAdded() {
	   return urnAdded;
   }
   /**
	* Set list of urn for added metadata
	* @param urnAdded list of urn
	*/
	public void setUrnAdded(List<String> urnAdded) {
		this.urnAdded = urnAdded;
	}
	/**
	 * Get list of urn for updated metadata
	 * @return
	 */
	public List<String> getUrnUpdated() {
		return urnUpdated;
	}
	/**
	 * Set list of urn for updated metadata
	 * @param urnUpdated list of urn
	 */
	public void setUrnUpdated(List<String> urnUpdated) {
		this.urnUpdated = urnUpdated;
	}
	/**
	 * Get list of urn for removed metadata
	 * @return
	 */
	public List<String> getUrnRemoved() {
		return urnRemoved;
	}
	/**
	 * Set list of urn for removed metadata
	 * @param urnRemoved list of urn
	 */
	public void setUrnRemoved(List<String> urnRemoved) {
		this.urnRemoved = urnRemoved;
	}
	
   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return "HarvestingTaskResult [date=" + date + ", total=" + total + ", fail=" + fail + ", added=" + added
            + ", updated=" + updated + ", unchanged=" + unchanged + ", locallyRemoved="
            + locallyRemoved + ", unknownSchema=" + unknownSchema + ", badFormat=" + badFormat
            + ", doesNotValidate=" + doesNotValidate + ", ignored=" + ignored + ", unexpected=" + unexpected + "]";
   }

}
