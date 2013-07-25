/**
 * 
 */
package org.openwis.harness.samples.fs.localdatasource;

import java.io.File;
import java.util.Calendar;

import org.openwis.harness.samples.common.Product;

/**
 * The Class ProductFile. <P>
 * Explanation goes here. <P>
 */
public class ProductFile implements Product {

   /** The timestamp. */
   private final Calendar timestamp;

   /** The urn. */
   private final String urn;

   /** The metadata urn. */
   private final String metadataURN;

   /** The file. */
   private final File file;

   /**
    * Instantiates a new product file.
    *
    * @param metadataURN the metadata urn
    * @param urn the urn
    * @param timestamp the timestamp
    * @param file the file
    */
   public ProductFile(String metadataURN, String urn, Calendar timestamp, File file) {
      super();
      this.metadataURN = metadataURN;
      this.urn = urn;
      this.timestamp = timestamp;
      this.file = file;
   }

   @Override
   public String toString() {
      return urn;
   }

   /**
    * Gets the file.
    *
    * @return the file
    */
   public File getFile() {
      return file;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.harness.samples.common.Product#getMetadataURN()
    */
   @Override
   public String getMetadataURN() {
      return metadataURN;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.harness.samples.common.Product#getURN()
    */
   @Override
   public String getURN() {
      return urn;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.harness.samples.common.Product#getTimestamp()
    */
   @Override
   public Calendar getTimestamp() {
      return timestamp;
   }

}
