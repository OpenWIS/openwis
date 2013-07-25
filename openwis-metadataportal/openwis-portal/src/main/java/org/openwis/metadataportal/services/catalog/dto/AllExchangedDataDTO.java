/**
 * AllExchangedDataDTO
 */
package org.openwis.metadataportal.services.catalog.dto;

import java.util.ArrayList;
import java.util.List;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class AllExchangedDataDTO {

   /**
    * Comment for <code>count</code>
    */
   private int count;

   /**
   * Comment for <code>allExchangedData</code>
   */
   private List<ExchangedDataDTO> allExchangedData;

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
    * Gets the allExchangedData.
    * @return the allExchangedData.
    */
   public List<ExchangedDataDTO> getAllExchangedData() {
      if (allExchangedData == null) {
         allExchangedData = new ArrayList<ExchangedDataDTO>();
      }
      return allExchangedData;
   }

   /**
    * Sets the allExchangedData.
    * @param allExchangedData the allExchangedData to set.
    */
   public void setAllExchangedData(List<ExchangedDataDTO> allExchangedData) {
      this.allExchangedData = allExchangedData;
   }

}
