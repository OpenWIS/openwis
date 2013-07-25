/**
 * 
 */
package org.openwis.metadataportal.services.group;

import java.util.List;

import jeeves.exceptions.BadInputEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.group.dto.PreparationSynchronizeDTO;
import org.openwis.metadataportal.services.group.dto.SynchronizeDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Synchronize implements Service {

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

      boolean force;
      try {
         force = Boolean.parseBoolean(Util.getParam(params, "force"));
      } catch (BadInputEx e) {
         force = false;
      }
      
      SynchronizeDTO dto = null;
      
      // Read from Ajax Request. Read from DTO prepare or perform.
      if (!force) {
         dto = JeevesJsonWrapper.read(params, SynchronizeDTO.class);
      }
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      GroupManager gm = new GroupManager(dbms);
      if (force || dto.isPerform()) {
         GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
         DataManager dm = gc.getDataManager();
         gm.synchronize(dm);
         return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
      } else {
         List<String> sync = gm.prepareSynchronization();
         PreparationSynchronizeDTO preSynchronizeDTO = new PreparationSynchronizeDTO();
         preSynchronizeDTO.setPrepSynchro(sync);
         return JeevesJsonWrapper.send(preSynchronizeDTO);
      }

   }

}
