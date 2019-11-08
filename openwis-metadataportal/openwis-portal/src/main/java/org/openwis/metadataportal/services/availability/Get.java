/**
 * 
 */
package org.openwis.metadataportal.services.availability;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.model.availability.DeploymentAvailability;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Get implements Service {

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
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

      AvailabilityManager availabilityManager = new AvailabilityManager(dbms);
      
      DeploymentAvailability deploymentAvailability = new DeploymentAvailability();

      /* Metadata service */
      deploymentAvailability.setMetadataServiceAvailability(availabilityManager.getMetadataServiceAvailability(dbms,
            gc.getSearchmanager()));

      /* Data service */
      deploymentAvailability.setDataServiceAvailability(availabilityManager.getDataServiceAvailability());

      /* Security service */
      deploymentAvailability.setSecurityServiceAvailability(availabilityManager.getSecurityServiceAvailability());

      return JeevesJsonWrapper.send(deploymentAvailability);
   }

}
