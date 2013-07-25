/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.product;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class ProductMetadataExtractorFactory {

   /**
    * Default constructor.
    * Builds a ProductMetadataExtractManagerFactory.
    */
   private ProductMetadataExtractorFactory() {
      super();
   }

   /**
    * Gets an extractor of metadata according to the file type.
    * @param fileType the file type.
    * @return the corresponding extractor.
    */
   public static IProductMetadataExtractor getProductMetadataExtractor(String schema) {
      if ("iso19139".equals(schema)) {
         return new ProductMetadataISO19139Extractor();
      }
      return new ProductMetadataDefaultExtractor();
   }

}
