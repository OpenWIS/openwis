/**
 * 
 */
package org.openwis.metadataportal.services.request.dto.follow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SubSelectionParametersDTO {

   private String code;

   private List<String> value;

   /**
    * Default constructor.
    * Builds a SubSelectionParametersDTO.
    * @param label
    * @param value
    */
   public SubSelectionParametersDTO(String code, Collection<String> value) {
      super();
      this.code = code;
      this.value = new ArrayList<String>(value);
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
   public List<String> getValue() {
      return value;
   }

   /**
    * Sets the value.
    * @param value the value to set.
    */
   public void setValue(List<String> value) {
      this.value = new ArrayList<String>(value);
   }
}
