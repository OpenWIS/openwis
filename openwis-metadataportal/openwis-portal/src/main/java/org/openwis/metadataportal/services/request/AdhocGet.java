/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.ProcessedRequestService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.SimpleIdDTO;
import org.openwis.metadataportal.services.request.dto.follow.AdhocDTO;

/**
 * This class enables to return all the Adhoc requests of the user. <P>
 * Explanation goes here. <P>
 * 
 */
public class AdhocGet implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   public Element exec(Element params, ServiceContext context) throws Exception {
      SimpleIdDTO idDto = JeevesJsonWrapper.read(params, SimpleIdDTO.class);
      ProcessedRequestService prs = DataServiceProvider.getProcessedRequestService();
      ProcessedRequest pr = prs.getFullProcessedRequestForAdhoc(idDto.getId());
      
      return JeevesJsonWrapper.send(AdhocDTO.adhocProcessedRequestToDTO(pr));
   }
}
