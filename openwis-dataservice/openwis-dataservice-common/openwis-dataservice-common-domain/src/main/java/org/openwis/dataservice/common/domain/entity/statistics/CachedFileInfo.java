/**
 *
 */
package org.openwis.dataservice.common.domain.entity.statistics;

import java.util.Date;

/**
 * Provides descriptive information on a cached file.
 */
public class CachedFileInfo {

   /** The file name. */
   private String name;

   /** The computed checksum for the incoming file. */
   private String checksum;

   /** Indicating from where the file has been replicated (when applicable) */
   private String origin;

   /** The insertion date. */
   private Date insertionDate;

   /** The metadata ID. */
   private String metadataUrn;

   /**
    * Gets the name.
    * @return the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * @param name the name to set.
    */
   public void setName(final String name) {
      this.name = name;
   }

   /**
    * Gets the checksum.
    * @return the checksum.
    */
   public String getChecksum() {
      return checksum;
   }

   /**
    * Sets the checksum.
    * @param checksum the checksum to set.
    */
   public void setChecksum(final String checksum) {
      this.checksum = checksum;
   }

   /**
    * Gets the origin.
    * @return the origin.
    */
   public String getOrigin() {
      return origin;
   }

   /**
    * Sets the origin.
    * @param origin the origin to set.
    */
   public void setOrigin(final String origin) {
      this.origin = origin;
   }

   /**
    * Gets the metadataUrn.
    * @return the metadataUrn.
    */
   public String getMetadataUrn() {
      return metadataUrn;
   }

   /**
    * Sets the metadataUrn.
    * @param metadataUrn the metadataUrn to set.
    */
   public void setMetadataUrn(final String metadataUrn) {
      this.metadataUrn = metadataUrn;
   }

   /**
    * Gets the insertionDate.
    * @return the insertionDate.
    */
   public Date getInsertionDate() {
      return insertionDate;
   }

   /**
    * Sets the insertionDate.
    * @param insertionDate the insertionDate to set.
    */
   public void setInsertionDate(final Date insertionDate) {
      this.insertionDate = insertionDate;
   }

}
