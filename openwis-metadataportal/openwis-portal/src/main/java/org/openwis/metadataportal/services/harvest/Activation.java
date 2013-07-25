/**
 * 
 */
package org.openwis.metadataportal.services.harvest;

import java.util.ResourceBundle;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.harvest.dto.ActivationDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Activation implements Service {

   /** The bundle. */
   private ResourceBundle bundle;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      bundle = ResourceBundle.getBundle("openwisMessage");
   }
   
   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      HarvestingTaskManager harvesterManager = new HarvestingTaskManager(dbms);
      ActivationDTO dto = JeevesJsonWrapper.read(params, ActivationDTO.class);
      
      boolean isSucceeded= false; 

      if (dto.isActivate()) {
         isSucceeded = harvesterManager.activate(dto.getId(), context);
      } else {
         isSucceeded = harvesterManager.suspend(dto.getId());
      }
      
      if(isSucceeded) {
         return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
      } else {
         return JeevesJsonWrapper.send(new AcknowledgementDTO(false, bundle.getString("Harvesting.running")));
      }
   }

}
