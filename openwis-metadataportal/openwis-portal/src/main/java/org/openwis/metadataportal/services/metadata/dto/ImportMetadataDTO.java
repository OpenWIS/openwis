/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.MetadataValidation;
import org.openwis.metadataportal.model.styleSheet.Stylesheet;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ImportMetadataDTO {

   private String fileType;

   private Stylesheet stylesheet;

   private MetadataValidation validationMode;

   private Category category;

   /**
    * Gets the fileType.
    * @return the fileType.
    */
   public String getFileType() {
      return fileType;
   }

   /**
    * Sets the fileType.
    * @param fileType the fileType to set.
    */
   public void setFileType(String fileType) {
      this.fileType = fileType;
   }

   /**
    * Gets the stylesheet.
    * @return the stylesheet.
    */
   public Stylesheet getStylesheet() {
      return stylesheet;
   }

   /**
    * Sets the stylesheet.
    * @param stylesheet the stylesheet to set.
    */
   public void setStylesheet(Stylesheet stylesheet) {
      this.stylesheet = stylesheet;
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
    * @param category the v to set.
    */
   public void setCategory(Category category) {
      this.category = category;
   }
}
