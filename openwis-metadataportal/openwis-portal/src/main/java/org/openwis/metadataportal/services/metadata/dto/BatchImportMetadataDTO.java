/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class BatchImportMetadataDTO extends ImportMetadataDTO {

   private String directory;

   /**
    * Gets the directory.
    * @return the directory.
    */
   public String getDirectory() {
      return directory;
   }

   /**
    * Sets the directory.
    * @param directory the directory to set.
    */
   public void setDirectory(String directory) {
      this.directory = directory;
   }
}
