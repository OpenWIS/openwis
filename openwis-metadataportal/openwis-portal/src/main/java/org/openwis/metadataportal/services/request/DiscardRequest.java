/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.ProcessedRequestService;
import org.openwis.dataservice.RequestService;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.harness.mssfss.DeleteRouting;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.external.HarnessProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.request.dto.common.TypeRequestDTO;
import org.openwis.metadataportal.services.request.dto.discard.DiscardRequestDTO;
import org.openwis.metadataportal.services.request.dto.discard.DiscardRequestsDTO;

/**
 * This service enables to discard AdHoc requests and Subscriptions. <P>
 * 
 */
public class DiscardRequest implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {

      DiscardRequestsDTO discardRequestsDTO = JeevesJsonWrapper.read(params, DiscardRequestsDTO.class);

      for (DiscardRequestDTO discardRequestDTO : discardRequestsDTO.getDiscardRequests()) {
       //Persist request calling external EJBs.

         if (discardRequestDTO.getTypeRequest().equals(TypeRequestDTO.SUBSCRIPTION)) {
            SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
            Long requestIDLong = Long.parseLong(discardRequestDTO.getRequestID());
            subscriptionService.deleteSubscription(requestIDLong);
         } else if (discardRequestDTO.getTypeRequest().equals(TypeRequestDTO.ADHOC)) {
            RequestService requestService = DataServiceProvider.getRequestService();
            Long requestIDLong = Long.parseLong(discardRequestDTO.getRequestID());
            requestService.deleteRequest(requestIDLong);
         } else if (discardRequestDTO.getTypeRequest().equals(TypeRequestDTO.PROCESSED_REQUEST)) {
            ProcessedRequestService processedRequestService = DataServiceProvider
                  .getProcessedRequestService();
            Long requestIDLong = Long.parseLong(discardRequestDTO.getRequestID());
            processedRequestService.deleteProcessedRequestWithAdHoc(requestIDLong);
         } else if (discardRequestDTO.getTypeRequest().equals(TypeRequestDTO.ROUTING)) {
            MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
            DeleteRouting deleteRouting = new DeleteRouting();
            deleteRouting.setIdRequest(discardRequestDTO.getRequestID());
            mssFssService.deleteRouting(deleteRouting);
         }
      }
      return JeevesJsonWrapper.send(new AcknowledgementDTO(true, discardRequestsDTO));
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   public void init(String arg0, ServiceConfig arg1) throws Exception {

   }

}
