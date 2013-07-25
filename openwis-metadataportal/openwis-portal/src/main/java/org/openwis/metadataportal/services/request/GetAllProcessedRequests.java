/**
 * 
 */
package org.openwis.metadataportal.services.request;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openwis.dataservice.LightProcessedRequestDTO;
import org.openwis.dataservice.ProcessedRequestColumn;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class GetAllProcessedRequests implements Service {

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
      RequestManager requestManager = new RequestManager();

      Long id = new Long(Util.getParamAsInt(params, "id"));
      int start = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumn = Util.getParam(params, "sort", null);
      String sortDirection = Util.getParam(params, "dir", null);

      SearchCriteriaWrapper<Long, ProcessedRequestColumn> wrapper = new SearchCriteriaWrapper<Long, ProcessedRequestColumn>();
      wrapper.setCriteria(id);
      wrapper.setStart(start);
      wrapper.setLimit(limit);
      if (sortColumn != null && sortDirection != null) {
         wrapper.setSort(requestManager.getProcessedRequestColumnAttribute(sortColumn));
         wrapper.setDir(SortDir.valueOf(sortDirection));
      }
      SearchResultWrapper<LightProcessedRequestDTO> dtosRes = requestManager
            .getAllProcessedRequestsByRequest(wrapper);

      SearchResultWrapper<org.openwis.metadataportal.services.request.dto.follow.LightProcessedRequestDTO> dtos = 
         new SearchResultWrapper<org.openwis.metadataportal.services.request.dto.follow.LightProcessedRequestDTO>(dtosRes.getTotal(),
               org.openwis.metadataportal.services.request.dto.follow.LightProcessedRequestDTO.toUTC(dtosRes.getRows()));

      final String stagingPostUrl = OpenwisMetadataPortalConfig
            .getString(ConfigurationConstants.URL_STAGING_POST);

      CollectionUtils.forAllDo(dtos.getRows(), new Closure() {

         @Override
         public void execute(Object input) {
            LightProcessedRequestDTO dto = (LightProcessedRequestDTO) input;
            if (StringUtils.isNotEmpty(dto.getUri())) {
               dto.setUri(stagingPostUrl + dto.getUri());
            }
         }
      });

      return JeevesJsonWrapper.send(dtos);
   }
}
