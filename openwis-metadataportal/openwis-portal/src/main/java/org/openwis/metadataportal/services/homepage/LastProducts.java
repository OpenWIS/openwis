/**
 * 
 */
package org.openwis.metadataportal.services.homepage;

import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.dataservice.ExtractMode;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.RequestService;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.homepage.dto.LastProductDTO;
import org.openwis.metadataportal.services.mock.MockMode;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class LastProducts implements Service {

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element arg0, ServiceContext context) throws Exception {
        List<LastProductDTO> lastProducts = new ArrayList<LastProductDTO>();
        if (MockMode.isMockModeDataService()) {
            for (int i = 0; i < 5; i++) {
                lastProducts.add(new LastProductDTO(System.currentTimeMillis() + "-" + i, System
                        .currentTimeMillis() + "-" + i));
            }
        } else {
            String stagingPostUrl = OpenwisMetadataPortalConfig
                    .getString(ConfigurationConstants.URL_STAGING_POST);

            RequestService requestService = DataServiceProvider.getRequestService();
            List<ProcessedRequest> processedRequests = requestService.getLastProcessedRequest(
                    context.getUserSession().getUsername(), 10);
            for (ProcessedRequest processedRequest : processedRequests) {
                LastProductDTO dto = new LastProductDTO();
                dto.setId(processedRequest.getId() + "");
                
                dto.setDate(new ISODate(processedRequest.getCreationDate().toGregorianCalendar()
                     .getTimeInMillis()).toString()
                     + "Z");
                dto.setName(processedRequest.getRequest().getProductMetadata().getTitle());
                dto.setUrl(stagingPostUrl + processedRequest.getUri());
                
                if(processedRequest.getRequest().getExtractMode().equals(ExtractMode.GLOBAL)) {
                   dto.setExtractMode("CACHE");
                } else {
                   dto.setExtractMode(processedRequest.getRequest().getProductMetadata().getLocalDataSource());
                }
                dto.setRequestId(processedRequest.getRequest().getId() + "");
                dto.setRequestType(processedRequest.getRequest().getRequestType());
                dto.setProductMetadataURN(processedRequest.getRequest().getProductMetadata().getUrn());
                lastProducts.add(dto);
            }
        }
        return JeevesJsonWrapper.send(lastProducts);
    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String arg0, ServiceConfig arg1) throws Exception {

    }

}