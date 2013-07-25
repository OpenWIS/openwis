/**
 * 
 */
package org.openwis.metadataportal.services.datapolicy;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyLinkedToMetadataException;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.datapolicy.dto.DataPoliciesDTO;

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
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      //Read from Ajax Request.
      DataPoliciesDTO dto = JeevesJsonWrapper.read(params, DataPoliciesDTO.class);
      
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ISearchManager searchManager = gc.getSearchmanager();
      
      DataPolicyManager dpm = new DataPolicyManager(dbms, searchManager);
      
      try {
         for (DataPolicy dp : dto.getDataPolicies()) {
             dpm.removeDataPolicy(dp);
             // Call method checkSubscription on RequestManager service.
             RequestManager requestManager = new RequestManager();
             requestManager.checkUsersSubscription(context);
         }
         //Send Acknowledgement
         return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
         
      } catch (DataPolicyLinkedToMetadataException e) {
         return JeevesJsonWrapper.send(new AcknowledgementDTO(false, "This data policy is linked to metadata(s)."));
      }
   }

}
