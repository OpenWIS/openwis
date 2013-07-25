/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.request.dto.state.SubscriptionStateDTO;
import org.openwis.metadataportal.services.request.dto.state.TypeSubscriptionStateSet;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SetSubscriptionState implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {
       SubscriptionStateDTO subscriptionStateDTO = JeevesJsonWrapper.read(params,
               SubscriptionStateDTO.class);

      if (!MockMode.isMockModeDataService()) {
         //Suspend or resume subscription according to the state.
         SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
         
         Long subscriptionId = Long.parseLong(subscriptionStateDTO.getRequestID());
          
         if (subscriptionStateDTO.getTypeStateSet().equals(TypeSubscriptionStateSet.SUSPEND)) {
            subscriptionService.suspendSubscription(subscriptionId);
         } else if (subscriptionStateDTO.getTypeStateSet().equals(TypeSubscriptionStateSet.RESUME)) {
            subscriptionService.resumeSubscription(subscriptionId);
         }
      }
      return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   public void init(String arg0, ServiceConfig arg1) throws Exception {
      
   }

}
