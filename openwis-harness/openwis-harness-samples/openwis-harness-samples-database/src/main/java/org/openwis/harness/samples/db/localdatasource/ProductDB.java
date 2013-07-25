/**
 * 
 */
package org.openwis.harness.samples.db.localdatasource;

import java.util.Calendar;

import org.openwis.harness.samples.common.Product;

/**
 * The Class ProductFile. <P>
 * Explanation goes here. <P>
 */
public class ProductDB implements Product {

   /** The timestamp. */
   private final Calendar timestamp;

   /** The urn. */
   private final String urn;

   /** The metadata urn. */
   private final String metadataURN;

   /** The id. */
   private final long id;

   /**
    * Instantiates a new product file.
    *
    * @param id the id
    * @param metadataURN the metadata urn
    * @param urn the urn
    * @param timestamp the timestamp
    */
   public ProductDB(long id, String metadataURN, String urn, Calendar timestamp) {
      super();
      this.metadataURN = metadataURN;
      this.urn = urn;
      this.timestamp = timestamp;
      this.id = id;
   }

   @Override
   public String toString() {
      return urn;
   }

   /**
    * Gets the id.
    *
    * @return the id
    */
   public long getId() {
      return id;
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
