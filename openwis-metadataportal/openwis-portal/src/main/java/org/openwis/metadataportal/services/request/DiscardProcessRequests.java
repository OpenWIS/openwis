/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.openwis.dataservice.ProcessedRequestService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.request.dto.discard.DiscardRequestDTO;
import org.openwis.metadataportal.services.request.dto.discard.DiscardRequestsDTO;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

/**
 * This service enables to discard processed requests. <P>
 * 
 */
public class DiscardProcessRequests implements Service {

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
      DiscardRequestsDTO discardRequestsDTO = JeevesJsonWrapper.read(params, DiscardRequestsDTO.class);
      List<Long> processedRequestIDs = new ArrayList<Long>();
      for (DiscardRequestDTO discardRequestDTO : discardRequestsDTO.getDiscardRequests()) {
         processedRequestIDs.add(Long.valueOf(discardRequestDTO.getRequestID()));
      }
      
      ProcessedRequestService processedRequestService = DataServiceProvider
      .getProcessedRequestService();
      processedRequestService.deleteProcessedRequests(processedRequestIDs);
      return JeevesJsonWrapper.send(new AcknowledgementDTO(true, discardRequestsDTO));
   }

}
