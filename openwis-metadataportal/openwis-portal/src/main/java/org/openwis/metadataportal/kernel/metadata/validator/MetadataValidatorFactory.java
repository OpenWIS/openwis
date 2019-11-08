/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.validator;

import org.openwis.metadataportal.model.metadata.MetadataValidation;

/**
 * A factory to return the validators corresponding to a validation mode. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataValidatorFactory {

   /**
    * Gets a validator using the specified metadata validation mode. 
    * @param metadataValidation the metadata validation mode.
    * @return the validator corresponding to the metadata validation mode.
    */
   public static IMetadataValidator getValidator(MetadataValidation metadataValidation) {
      if(MetadataValidation.NONE.equals(metadataValidation)) {
         return new NoneMetadataValidator();
      } else if (MetadataValidation.FULL.equals(metadataValidation)) {
         return new FullMetadataValidator();
      } else if (MetadataValidation.XSD_ONLY.equals(metadataValidation)) {
         return new XSDMetadataValidator();
      }
      throw new UnsupportedOperationException();
   }

}
