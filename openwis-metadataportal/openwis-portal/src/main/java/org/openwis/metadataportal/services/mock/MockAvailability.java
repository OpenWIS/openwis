/**
 * 
 */
package org.openwis.metadataportal.services.mock;

import org.openwis.metadataportal.model.availability.Availability;
import org.openwis.metadataportal.model.availability.AvailabilityLevel;
import org.openwis.metadataportal.model.availability.DataServiceAvailability;
import org.openwis.metadataportal.model.availability.DeploymentAvailability;
import org.openwis.metadataportal.model.availability.MetadataServiceAvailability;
import org.openwis.metadataportal.model.availability.SecurityServiceAvailability;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MockAvailability {

   public static DeploymentAvailability get() {
      DeploymentAvailability d = new DeploymentAvailability();

      //Metadata Service.
      d.setMetadataServiceAvailability(new MetadataServiceAvailability());
      d.getMetadataServiceAvailability().setUserPortal(new Availability(AvailabilityLevel.UP));
      d.getMetadataServiceAvailability().setSynchronization(
            new Availability(AvailabilityLevel.DOWN));
      d.getMetadataServiceAvailability().getSynchronization().getAdditionalInfo().put("active", "5");
      d.getMetadataServiceAvailability().getSynchronization().getAdditionalInfo().put("failure", "3");
      d.getMetadataServiceAvailability().setHarvesting(new Availability(AvailabilityLevel.UP));
      d.getMetadataServiceAvailability().getHarvesting().getAdditionalInfo().put("active", "4");
      d.getMetadataServiceAvailability().getHarvesting().getAdditionalInfo().put("failure", "0");
      d.getMetadataServiceAvailability().setIndexing(new Availability(AvailabilityLevel.DOWN));

      //Data service
      d.setDataServiceAvailability(new DataServiceAvailability());
      d.getDataServiceAvailability().setReplicationProcess(new Availability(AvailabilityLevel.UP));
      d.getDataServiceAvailability().setIngestion(new Availability(AvailabilityLevel.UP));
      d.getDataServiceAvailability().setSubscriptionQueue(new Availability(AvailabilityLevel.UP));
      d.getDataServiceAvailability().setDisseminationQueue(new Availability(AvailabilityLevel.UP));

      //Security service
      d.setSecurityServiceAvailability(new SecurityServiceAvailability());
      d.getSecurityServiceAvailability().setSecurityService(new Availability(AvailabilityLevel.UP));
      d.getSecurityServiceAvailability().setSsoService(new Availability(AvailabilityLevel.UP));

      return d;
   }

}
