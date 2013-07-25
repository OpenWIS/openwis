/**
 * 
 */
package org.openwis.metadataportal.services.category.dto;

import java.util.List;

import org.openwis.metadataportal.model.category.Category;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class EditCategoryDTO {

   /**
    * Comment for <code>productsMetadataUrn</code>
    */
   private List<String> productsMetadataUrn;

   /**
    * Comment for <code>category</code>
    */
   private Category category;

   /**
    * Gets the productsMetadataUrn.
    * @return the productsMetadataUrn.
    */
   public List<String> getProductsMetadataUrn() {
      return productsMetadataUrn;
   }

   /**
    * Sets the productsMetadataUrn.
    * @param productsMetadataUrn the productsMetadataUrn to set.
    */
   public void setProductsMetadataUrn(List<String> productsMetadataUrn) {
      this.productsMetadataUrn = productsMetadataUrn;
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
