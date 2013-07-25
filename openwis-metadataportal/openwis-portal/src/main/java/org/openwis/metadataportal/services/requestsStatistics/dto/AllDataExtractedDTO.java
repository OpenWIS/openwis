/**
 * AllDataExtractedDTO
 */
package org.openwis.metadataportal.services.requestsStatistics.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class AllDataExtractedDTO {

   /**
    * Comment for <code>count</code>
    */
   private int count;

   /**
   * Comment for <code>allDataExtractedDTO</code>
   */
   private List<DataExtractedDTO> allDataExtracted;

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
    * Gets the allDataExtracted.
    * @return the allDataExtracted.
    */
   public List<DataExtractedDTO> getAllDataExtracted() {
      if (allDataExtracted == null) {
         allDataExtracted = new ArrayList<DataExtractedDTO>();
      }
      return allDataExtracted;
   }

   /**
    * Sets the allDataExtracted.
    * @param allDataExtracted the allDataExtracted to set.
    */
   public void setAllDataExtracted(List<DataExtractedDTO> allDataExtracted) {
      this.allDataExtracted = allDataExtracted;
   }

}
