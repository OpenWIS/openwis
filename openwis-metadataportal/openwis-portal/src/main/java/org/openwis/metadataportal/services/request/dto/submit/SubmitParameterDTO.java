package org.openwis.metadataportal.services.request.dto.submit;

import java.util.HashSet;
import java.util.Set;

/**
 * The sub-selection parameter DTO for the "Submit request" phase. <P>
 */
public class SubmitParameterDTO {

   /**
    * The code.
    */
   private String code;

   /**
    * The values.
    */
   private Set<String> values;

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
    * Gets the values.
    * @return the values.
    */
   public Set<String> getValues() {
      if (values == null) {
         values = new HashSet<String>();
      }
      return values;
   }

   /**
    * Sets the values.
    * @param values the values to set.
    */
   public void setValues(Set<String> values) {
      this.values = values;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((code == null) ? 0 : code.hashCode());
      result = prime * result + ((values == null) ? 0 : values.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof SubmitParameterDTO)) {
         return false;
      }
      SubmitParameterDTO other = (SubmitParameterDTO) obj;
      if (code == null) {
         if (other.code != null) {
            return false;
         }
      } else if (!code.equals(other.code)) {
         return false;
      }
      if (values == null) {
         if (other.values != null) {
            return false;
         }
      } else if (!values.equals(other.values)) {
         return false;
      }
      return true;
   }
}
