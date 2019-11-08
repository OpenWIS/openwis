/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleIdDTO;
import org.openwis.metadataportal.services.request.dto.follow.SubscriptionDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SubscriptionGet implements Service {

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
      SimpleIdDTO idDto = JeevesJsonWrapper.read(params, SimpleIdDTO.class);
      SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
      Subscription subscription = subscriptionService.getFullSubscription(idDto.getId());
      
      return JeevesJsonWrapper.send(SubscriptionDTO.subscriptionToDTO(subscription));
   }

}
