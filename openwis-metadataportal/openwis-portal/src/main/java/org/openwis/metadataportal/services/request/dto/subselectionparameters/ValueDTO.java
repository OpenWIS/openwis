/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.subselectionparameters;

import java.util.List;

/**
 * A DTO to describe a value for sub selection parameters. <P>
 */
public class ValueDTO {

   /**
    * The code.
    */
   private String code;

   /**
    * <code>true</code> if the value should be selected, <code>false</code> otherwise.
    */
   private boolean selected;

   /**
    * The value.
    */
   private String value;

   /**
    * A list of values' codes the current value is available for.
    */
   private List<String> availableFor;

   /**
    * Gets the code.
    * @return the code.
    */
   public String getCode() {
      return code;
   }

   /**
    * Sets the code.
    * @param code the code to set.
    */
   public void setCode(String code) {
      this.code = code;
   }

   /**
    * Gets the selected.
    * @return the selected.
    */
   public boolean isSelected() {
      return selected;
   }

   /**
    * Sets the selected.
    * @param selected the selected to set.
    */
   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   /**
    * Gets the value.
    * @return the value.
    */
   public String getValue() {
      return value;
   }

   /**
    * Sets the value.
    * @param value the value to set.
    */
   public void setValue(String value) {
      this.value = value;
   }

   /**
    * Gets the availableFor.
    * @return the availableFor.
    */
   public List<String> getAvailableFor() {
      return availableFor;
   }

   /**
    * Sets the availableFor.
    * @param availableFor the availableFor to set.
    */
   public void setAvailableFor(List<String> availableFor) {
      this.availableFor = availableFor;
   }
}
