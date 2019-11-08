/**
 * MetadataValidatorResult
 */
package org.openwis.metadataportal.kernel.metadata.validator;

/**
 * MetadataValidatorResult. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataValidatorResult {

   private boolean validate;

   public boolean isValidate() {
      return validate;
   }

   public void setValidate(boolean validate) {
      this.validate = validate;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message.replaceAll("\"", "&#148;");
   }

   private String message;


}
