/**
 * 
 */
package org.openwis.metadataportal.services.availability.dto;

import org.openwis.metadataportal.model.availability.DeploymentAvailability;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class RemoteAvailabilityDTO {

   private DeploymentAvailability deploymentAvailability;

   private boolean backupedByLocalServer;

   /**
    * Default constructor.
    * Builds a RemoteAvailabilityDTO.
    * @param deploymentAvailability
    * @param backupedByLocalServer
    */
   public RemoteAvailabilityDTO(DeploymentAvailability deploymentAvailability,
         boolean backupedByLocalServer) {
      super();
      this.deploymentAvailability = deploymentAvailability;
      this.backupedByLocalServer = backupedByLocalServer;
   }

   /**
    * Gets the deploymentAvailability.
    * @return the deploymentAvailability.
    */
   public DeploymentAvailability getDeploymentAvailability() {
      return deploymentAvailability;
   }

   /**
    * Sets the deploymentAvailability.
    * @param deploymentAvailability the deploymentAvailability to set.
    */
   public void setDeploymentAvailability(DeploymentAvailability deploymentAvailability) {
      this.deploymentAvailability = deploymentAvailability;
   }

   /**
    * Gets the backupedByLocalServer.
    * @return the backupedByLocalServer.
    */
   public boolean isBackupedByLocalServer() {
      return backupedByLocalServer;
   }

   /**
    * Sets the backupedByLocalServer.
    * @param backupedByLocalServer the backupedByLocalServer to set.
    */
   public void setBackupedByLocalServer(boolean backupedByLocalServer) {
      this.backupedByLocalServer = backupedByLocalServer;
   }

}
