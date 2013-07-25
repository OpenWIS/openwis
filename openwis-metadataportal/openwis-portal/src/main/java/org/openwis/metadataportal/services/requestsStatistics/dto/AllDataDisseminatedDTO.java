/**
 * AllDataDisseminatedDTO
 */
package org.openwis.metadataportal.services.requestsStatistics.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class AllDataDisseminatedDTO {

   /**
    * Comment for <code>count</code>
    */
   private int count;

   /**
   * Comment for <code>allDataDisseminated</code>
   */
   private List<DataDisseminatedDTO> allDataDisseminated;

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
    * Gets the allDataDisseminated.
    * @return the allDataDisseminated.
    */
   public List<DataDisseminatedDTO> getAllDataDisseminated() {
      if (allDataDisseminated == null) {
         allDataDisseminated = new ArrayList<DataDisseminatedDTO>();
      }
      return allDataDisseminated;
   }

   /**
    * Sets the allDataDisseminated.
    * @param allDataDisseminated the allDataDisseminated to set.
    */
   public void setAllDataDisseminated(List<DataDisseminatedDTO> allDataDisseminated) {
      this.allDataDisseminated = allDataDisseminated;
   }

}
