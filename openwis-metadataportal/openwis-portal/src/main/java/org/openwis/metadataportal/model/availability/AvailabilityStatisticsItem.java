/**
 * 
 */
package org.openwis.metadataportal.model.availability;

/**
 * Model for Availability statistics.
 */
public class AvailabilityStatisticsItem {

   private String task;

   private int available;

   private int notAvailable;
   
   private String date;

   public String getTask() {
      return task;
   }

   public void setTask(String task) {
      this.task = task;
   }
   
   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public int getAvailable() {
      return available;
   }

   public void setAvailable(int available) {
      this.available = available;
   }

   public int getNotAvailable() {
      return notAvailable;
   }

   public void setNotAvailable(int notAvailable) {
      this.notAvailable = notAvailable;
   }

}
