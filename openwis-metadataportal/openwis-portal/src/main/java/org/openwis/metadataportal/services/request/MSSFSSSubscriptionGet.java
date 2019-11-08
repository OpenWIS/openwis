/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.harness.mssfss.GetRecentEventsForARouting;
import org.openwis.harness.mssfss.GetRecentEventsForARoutingResponse;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.harness.mssfss.RoutingSortColumn;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleStringIdDTO;
import org.openwis.metadataportal.services.request.dto.follow.SubscriptionDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MSSFSSSubscriptionGet implements Service {

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
      SimpleStringIdDTO idDto = JeevesJsonWrapper.read(params, SimpleStringIdDTO.class);
      MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
      
      GetRecentEventsForARouting routingParams = new GetRecentEventsForARouting();
      routingParams.setIdRequest(idDto.getId());
      routingParams.setPage(0);
      routingParams.setPageSize(1);
      routingParams.setSortColumn(RoutingSortColumn.ID.toString());
      routingParams.setRevert(false);
      
      GetRecentEventsForARoutingResponse routing = mssFssService.getRecentEventsForARouting(routingParams);
      
      return JeevesJsonWrapper.send(SubscriptionDTO.mssFssSubscriptionsToDTO(routing.getRouting()));
   }

}
