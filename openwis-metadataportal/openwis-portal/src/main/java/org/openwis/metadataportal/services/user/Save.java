/**
 * 
 */
package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.kernel.user.UserAlreadyExistsException;
import org.openwis.metadataportal.kernel.user.UserManager;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserDTO;

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
      UserDTO user = JeevesJsonWrapper.read(params, UserDTO.class);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      UserManager um = new UserManager(dbms);

      AcknowledgementDTO acknowledgementDTO = null;

      try {
         if (user.isCreationMode()) {
            um.createUser(user.getUser());
         } else {
            um.updateUser(user.getUser());
            // call method checkSubscription on RequestManager service.
            RequestManager requestManager = new RequestManager();
            requestManager.checkUserSubscription(user.getUser().getUsername(), dbms);
         }

         acknowledgementDTO = new AcknowledgementDTO(true);
      } catch (UserAlreadyExistsException e) {
         acknowledgementDTO = new AcknowledgementDTO(false, "The user " + e.getUserName() + " already exists");
      }

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
