/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.follow;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SubSelectionParametersDTO {

   private String code;

   private String value;

   /**
    * Default constructor.
    * Builds a SubSelectionParametersDTO.
    * @param label
    * @param value
    */
   public SubSelectionParametersDTO(String code, String value) {
      super();
      this.code = code;
      this.value = value;
   }

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
}
