/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.product;

import org.openwis.dataservice.ProductMetadata;
import org.openwis.dataservice.UpdateFrequency;
import org.openwis.metadataportal.model.metadata.Metadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ProductMetadataDefaultExtractor implements IProductMetadataExtractor {

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractFncPattern(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractFncPattern(Metadata metadata) throws Exception {
      return null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractOriginator(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractOriginator(Metadata metadata) throws Exception {
      return null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractTitle(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractTitle(Metadata metadata) throws Exception {
      return "Metadata";
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractLocalDataSource(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractLocalDataSource(Metadata metadata) throws Exception {
      return "";
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractUpdateFrequency(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public UpdateFrequency extractUpdateFrequency(Metadata metadata) throws Exception {
      return null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractFileExtension(org.openwis.metadataportal.model.metadata.Metadata)
    */
   @Override
   public String extractFileExtension(Metadata metadata) throws Exception {
      return null;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor#extractGTSCategoryGTSPriorityAndDataPolicy(org.openwis.metadataportal.model.metadata.Metadata, org.openwis.dataservice.ProductMetadata)
    */
   @Override
   public void extractGTSCategoryGTSPriorityAndDataPolicy(Metadata metadata, ProductMetadata pm)
         throws Exception {
      pm.setGtsCategory(GTS_CATEGORY_NONE);
      pm.setDataPolicy(UNKNOWN_DATAPOLICY);
   }
   @Override
   public boolean isGlobalExchange(Metadata metadata) throws Exception {
      return false;
   }

   @Override
   public boolean isIsoCoreProfile1_3(Metadata metadata) throws Exception {
  	  return false;
   }

}
