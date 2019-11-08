/**
 * 
 */
package org.openwis.metadataportal.kernel.metadata.extractor;

import org.fao.geonet.kernel.DataManager;

import jeeves.resources.dbms.Dbms;

/**
 * A metadata extractor factory. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataAlignerExtractorFactory {

   /**
    * Gets an extractor of metadata according to the file type.
    * @param fileType the file type.
    * @return the corresponding extractor.
    */
   public static IMetadataAlignerExtractor getMetadataAlignerExtractor(String fileType, Dbms dbms, String userName, String preferredSchema, DataManager dataManager) {
      if ("single".equals(fileType)) {
         return new MetadataAlignerXmlFileExtractor();
      }
      if ("mef".equals(fileType)) {
         return new MetadataAlignerMefFileExtractor(dbms, userName);
      }
      if ("mef2".equals(fileType)) {
         return new MetadataAlignerMef2FileExtractor(dbms, userName, preferredSchema, dataManager);
      }
      throw new UnsupportedOperationException();
   }

}
