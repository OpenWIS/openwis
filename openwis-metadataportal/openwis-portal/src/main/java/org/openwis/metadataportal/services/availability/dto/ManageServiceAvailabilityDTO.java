/**
 * 
 */
package org.openwis.metadataportal.services.availability.dto;

/**
 * Manage Service Availability DTO.
 * 
 */
public class ManageServiceAvailabilityDTO {

   /**
    * The service name
    * @member: serviceName
    */
   private String serviceName;
   
   /**
    * True if the service is started.
    * @member: isStarted
    */
   private boolean started;

   /**
    * Gets the serviceName.
    * @return the serviceName.
    */
   public String getServiceName() {
      return serviceName;
   }

   /**
    * Sets the serviceName.
    * @param serviceName the serviceName to set.
    */
   public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
   }

   /**
    * Gets the isStarted.
    * @return the isStarted.
    */
   public boolean isStarted() {
      return started;
   }

   /**
    * Sets the isStarted.
    * @param isStarted the isStarted to set.
    */
   public void setStarted(boolean isStarted) {
      this.started = isStarted;
   }
   
   
}
