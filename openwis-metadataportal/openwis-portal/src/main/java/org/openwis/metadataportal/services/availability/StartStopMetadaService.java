/**
 * 
 */
package org.openwis.metadataportal.services.availability;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.kernel.availability.IAvailabilityManager;
import org.openwis.metadataportal.model.availability.Availability;
import org.openwis.metadataportal.services.availability.dto.ManageServiceAvailabilityDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class StartStopMetadaService implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      ManageServiceAvailabilityDTO dto = JeevesJsonWrapper.read(params,
            ManageServiceAvailabilityDTO.class);

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      String serviceName = dto.getServiceName();

      IAvailabilityManager availabilityManager = new AvailabilityManager(dbms);
      availabilityManager.startStopMetadataService(context, dto.isStarted(), serviceName);

      /* Metadata service */
      Availability availability = null;
      if ("synchronization".equals(serviceName)) {
         availability = availabilityManager.getMetadataServiceAvailability(dbms, dto.getServiceName()).getSynchronization();
      } else if ("harvesting".equals(serviceName)) {
         availability = availabilityManager.getMetadataServiceAvailability(dbms, dto.getServiceName()).getHarvesting();
      } else if ("userPortal".equals(serviceName)) {
         availability = availabilityManager.getMetadataServiceAvailability(dbms, dto.getServiceName()).getUserPortal();
      } 

      return JeevesJsonWrapper.send(availability);
   }
}
