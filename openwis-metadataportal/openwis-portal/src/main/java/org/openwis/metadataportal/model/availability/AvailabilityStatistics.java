/**
 * 
 */
package org.openwis.metadataportal.model.availability;

import java.util.List;

/**
 * Model for Availability statistics.
 */
public class AvailabilityStatistics {

   private List<AvailabilityStatisticsItem> items;
   
   private int count;

   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public List<AvailabilityStatisticsItem> getItems() {
      return items;
   }

   public void setItems(List<AvailabilityStatisticsItem> items) {
      this.items = items;
   }

}
