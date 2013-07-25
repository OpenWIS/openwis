/**
 * 
 */
package org.openwis.metadataportal.services.group;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.group.GroupAlreadyExistsException;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Save implements Service {

    
   
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
      Group group = JeevesJsonWrapper.read(params, Group.class);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      GroupManager gm = new GroupManager(dbms);

      AcknowledgementDTO acknowledgementDTO = null;

      try {
         if (group.getId() == null) {
            gm.createGroup(group);
         } else {
            gm.updateGroup(group);
            // TODO AA Call method checkSubscription on RequestManager service ?
            // Call method checkSubscription on RequestManager service.
//            RequestManager requestManager = new RequestManager();
//            requestManager.checkUsersSubscription(dbms);
         }
         acknowledgementDTO = new AcknowledgementDTO(true);
      } catch (GroupAlreadyExistsException e) {
         acknowledgementDTO = new AcknowledgementDTO(false, "The group " + e.getName() + " already exists");
      } catch (Exception e) {
         Log.error(Geonet.ADMIN, e.getMessage(), e.getCause());
         acknowledgementDTO = new AcknowledgementDTO(false, "An error occured. This error might be due to a problem of synchronization. Please try to synchronize first.");
         
      }

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
