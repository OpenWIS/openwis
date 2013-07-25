/**
 *
 */
package org.openwis.metadataportal.services.availability.dto;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class SwitchToBackupDTO {

   private String deploymentName;

   private boolean switchedOn;

   private int hour;

   /**
    * Gets the deploymentName.
    * @return the deploymentName.
    */
   public String getDeploymentName() {
      return deploymentName;
   }

   /**
    * Sets the deploymentName.
    * @param deploymentName the deploymentName to set.
    */
   public void setDeploymentName(String deploymentName) {
      this.deploymentName = deploymentName;
   }

   /**
    * Gets the switchedOn.
    * @return the switchedOn.
    */
   public boolean isSwitchedOn() {
      return switchedOn;
   }

   /**
    * Sets the switchedOn.
    * @param switchedOn the switchedOn to set.
    */
   public void setSwitchedOn(boolean switchedOn) {
      this.switchedOn = switchedOn;
   }

   /**
    * Gets the hour.
    *
    * @return the hour
    */
   public int getHour() {
      return hour;
   }

   /**
    * Sets the hour.
    *
    * @param hour the new hour
    */
   public void setHour(int hour) {
      this.hour = hour;
   }

}
