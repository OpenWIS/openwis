/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.submit;

import org.openwis.dataservice.RecurrentScale;


/**
 * A DTO for the frequency configuration (Subscription only). <P>
 */
public class FrequencyDTO {

   /**
    * The frequency type.
    */
   private FrequencyType type;

   /**
    * The recurrence period.
    */
   private Integer recurrencePeriod;

   /**
    * The recurrent scale.
    */
   private RecurrentScale recurrentScale;

   /**
    * The starting date.
    */
   private String startingDate;

   /**
    * Gets the type.
    * @return the type.
    */
   public FrequencyType getType() {
      return type;
   }

   /**
    * Sets the type.
    * @param type the type to set.
    */
   public void setType(FrequencyType type) {
      this.type = type;
   }

   /**
    * Gets the recurrencePeriod.
    * @return the recurrencePeriod.
    */
   public Integer getRecurrencePeriod() {
      return recurrencePeriod;
   }

   /**
    * Sets the recurrencePeriod.
    * @param recurrencePeriod the recurrencePeriod to set.
    */
   public void setRecurrencePeriod(Integer recurrencePeriod) {
      this.recurrencePeriod = recurrencePeriod;
   }

   /**
    * Gets the recurrentScale.
    * @return the recurrentScale.
    */
   public RecurrentScale getRecurrentScale() {
      return recurrentScale;
   }

   /**
    * Sets the recurrentScale.
    * @param recurrentScale the recurrentScale to set.
    */
   public void setRecurrentScale(RecurrentScale recurrentScale) {
      this.recurrentScale = recurrentScale;
   }

   /**
    * Gets the value of the startingDate property.
    * @return startingDate the startingDate to set.
    */
   public String getStartingDate() {
       return startingDate;
   }

   /**
    * Sets the value of the startingDate property.
    * @param startingDate the recurrentScale to set.
    */
   public void setStartingDate(String startingDate) {
       this.startingDate = startingDate;
   }
}
