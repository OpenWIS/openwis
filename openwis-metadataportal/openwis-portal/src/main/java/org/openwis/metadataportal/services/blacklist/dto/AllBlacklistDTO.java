/**
 * AllBlacklistDTO
 */
package org.openwis.metadataportal.services.blacklist.dto;

import java.util.ArrayList;
import java.util.List;


/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class AllBlacklistDTO {

   /**
    * Comment for <code>count</code>
    */
   private int count;

   /**
   * Comment for <code>allBlackList</code>
   */
   private List<BlacklistDTO> allBlackList;

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
    * Gets the allBlackList.
    * @return the allBlackList.
    */
   public List<BlacklistDTO> getAllBlackList() {
      if (allBlackList == null) {
         allBlackList = new ArrayList<BlacklistDTO>();
      }
      return allBlackList;
   }

   /**
    * Sets the allBlackList.
    * @param allBlackList the allBlackList to set.
    */
   public void setAllBlackList(List<BlacklistDTO> allBlackList) {
      this.allBlackList = allBlackList;
   }

}
