/**
 *
 */
package org.openwis.metadataportal.kernel.availability;

import java.util.Date;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.kernel.search.ISearchManager;
import org.openwis.metadataportal.model.availability.DataServiceAvailability;
import org.openwis.metadataportal.model.availability.MetadataServiceAvailability;
import org.openwis.metadataportal.model.availability.SecurityServiceAvailability;

/**
 * Availability Manager Interface. <P>
 *
 */
public interface IAvailabilityManager {

   /**
    * Starts the backup mode for the given deployment.
    * @param deploymentName the deployment name.
    * @param context the service context.
    * @throws Exception if an error occurs.
    */
   void switchBackupMode(boolean isSwitchedOn, String deploymentName, Date retroProcessDate,
         ServiceContext context) throws Exception;

   /**
    * Returns <code>true</code> of local server is backuping the specified deployment, <code>false</code> otherwise.
    * @param deploymentName the deployment name.
    * @return <code>true</code> of local server is backuping the specified deployment, <code>false</code> otherwise.
    * @throws Exception if an error occurs.
    */
   boolean isLocalServerBackupingDeployment(String deploymentName) throws Exception;

   /**
    * Start/Stop Metadata Service
    * @param context the service context.
    * @param isStarted <code>true</code> if the service is started.
    * @param metadataService the metadata service.
    * @throws Exception if an error occurs.
    */
   void startStopMetadataService(ServiceContext context, boolean isStarted, String metadataService)
         throws Exception;

   /**
    * Start/Stop Data Service
    * @param isStarted <code>true</code> if the service is started.
    * @param metadataService the metadata service.
    */
   void startStopDataService(boolean isStarted, String metadataService);

   /**
    * Gets the data service availability.
    * @return the data service availability.
    */
   DataServiceAvailability getDataServiceAvailability();
   
   /**
    * Gets the data service availability.
    * @param serviceName the service name.
    * @return the data service availability.
    */
   DataServiceAvailability getDataServiceAvailability(String serviceName);
   
   /**
    * Create the metadata service availability.
    * @param dbms the dbms.
    * @param searchManager the search manager.
    * @return the metadata service availability.
    */
   MetadataServiceAvailability getMetadataServiceAvailability(Dbms dbms,
         ISearchManager searchManager);
   
   /**
    * Create the metadata service availability.
    * @param dbms the dbms.
    * @param serviceName the service name.
    * @return the availability for this service.
    */
   MetadataServiceAvailability getMetadataServiceAvailability(Dbms dbms, String serviceName);

   /**
    * Create the security service availability.
    * @param session the user session.
    * @return the security service availability.
    */
   SecurityServiceAvailability getSecurityServiceAvailability();
   
   /**
    * Is User Portal Enable.
    * @return <code>true</code> if the user portal is enable.
    */
   boolean isUserPortalEnable();
   
   /**
    * Set User Portal Enable.
    * @param enable <code>true</code> if the user portal is enable.
    */
   void setUserPortalEnable(boolean enable);
}
