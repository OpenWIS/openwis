/**
 * 
 */
package org.openwis.metadataportal.services.metadata.dto;

import java.util.ArrayList;
import java.util.List;

import org.openwis.metadataportal.model.metadata.Metadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MonitorMetadataDTO {

   /**
    * Comment for <code>count</code>
    */
   private int count;

   /**
   * Comment for <code>metadatas</code>
   */
   private List<Metadata> metadatas;

   /**
    * Gets the count.
    * @return the count.
    */
   public int getCount() {
      return count;
   }

   /**
    * Sets the count.
    * @param count the count to set.
    */
   public void setCount(int count) {
      this.count = count;
   }

   /**
    * Gets the metadatas.
    * @return the metadatas.
    */
   public List<Metadata> getMetadatas() {
      if (metadatas == null) {
         metadatas = new ArrayList<Metadata>();
      }
      return metadatas;
   }

   /**
    * Sets the metadatas.
    * @param metadatas the metadatas to set.
    */
   public void setMetadatas(List<Metadata> metadatas) {
      this.metadatas = metadatas;
   }

}
