/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.util.Arrays;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.RequestColumn;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockFollowAdhocDTO;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.remote.IRemoteService;
import org.openwis.metadataportal.services.request.dto.follow.AdhocDTO;

/**
 * This class enables to return all the Adhoc requests of the user. <P>
 * Explanation goes here. <P>
 * 
 */
public class FollowMyAdhocs implements Service, IRemoteService {

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
      SearchResultWrapper<AdhocDTO> dtos = null;

      if (MockMode.isMockModeDataService()) {
         List<AdhocDTO> adhocDtos = MockFollowAdhocDTO.getFollowAdhocDTO();
         dtos = new SearchResultWrapper<AdhocDTO>(adhocDtos.size() * 5, adhocDtos);
      } else {
         RequestManager requestManager = new RequestManager();

         String userName = GetUserNameHelper.getUserName(params, context);

         int start = Util.getParamAsInt(params, "start");
         int limit = Util.getParamAsInt(params, "limit");
         String sortColumn = Util.getParam(params, "sort", null);
         String sortDirection = Util.getParam(params, "dir", null);

         SearchCriteriaWrapper<List<String>, RequestColumn> wrapper = new SearchCriteriaWrapper<List<String>, RequestColumn>();
         wrapper.setCriteria(Arrays.asList(userName));
         wrapper.setStart(start);
         wrapper.setLimit(limit);
         if (sortColumn != null && sortDirection != null) {
            wrapper.setSort(requestManager.getRequestColumnAttribute(sortColumn));
            wrapper.setDir(SortDir.valueOf(sortDirection));
         }

         SearchResultWrapper<ProcessedRequest> adhocsWrapper = requestManager.getAllAdhocsByUsers(wrapper);

         dtos = new SearchResultWrapper<AdhocDTO>(adhocsWrapper.getTotal(),
               AdhocDTO.adhocProcessedRequestsToDTO(adhocsWrapper.getRows()));
      }

      return JeevesJsonWrapper.send(dtos);
   }
}
