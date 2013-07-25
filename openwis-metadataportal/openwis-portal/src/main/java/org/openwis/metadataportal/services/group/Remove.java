/**
 * 
 */
package org.openwis.metadataportal.services.group;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.group.dto.GroupsDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Remove implements Service {

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
      //Read from Ajax Request.

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      GroupsDTO dto = JeevesJsonWrapper.read(params, GroupsDTO.class);

      GroupManager gm = new GroupManager(dbms);
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      DataManager dm = gc.getDataManager();
      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);
      try {
         for (Group group : dto.getGroups()) {
            gm.removeGroup(group, dm);
            
            // call method checkSubscription on RequestManager service.
            RequestManager requestManager = new RequestManager();
            requestManager.checkUsersSubscription(context);
         }         
      } catch (Exception e) {
         acknowledgementDTO = new AcknowledgementDTO(false, "An error occured. This error might be due to a problem of synchronization. Please try to synchronize first.");
      }

      //Send Acknowledgement
      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
