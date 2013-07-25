/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.ProcessedRequestFilter;
import org.openwis.dataservice.RequestColumn;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.request.dto.follow.AdhocDTO;
import org.openwis.securityservice.GroupManagementService;
import org.openwis.securityservice.OpenWISGroup;

import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MonitorCurrentRequests implements Service {

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
   @SuppressWarnings("unchecked")
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      SearchResultWrapper<AdhocDTO> dtos = null;

      if (MockMode.isMockModeDataService()) {
         List<AdhocDTO> adhocDtos = new ArrayList<AdhocDTO>();
         dtos = new SearchResultWrapper<AdhocDTO>(adhocDtos.size() * 5, adhocDtos);
      } else {
         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         RequestManager requestManager = new RequestManager();
         GroupManager groupManager = new GroupManager(dbms);
         GroupManagementService groupManagementService = SecurityServiceProvider.getGroupManagementService();
         
         int start = Util.getParamAsInt(params, "start");
         int limit = Util.getParamAsInt(params, "limit");
         String sortColumn = Util.getParam(params, "sort", null);
         String sortDirection = Util.getParam(params, "dir", null);
         String groups = Util.getParam(params, "groups", null);
         String prFilterStr = Util.getParam(params, "prFilter", "BOTH");
         ProcessedRequestFilter prFilter = ProcessedRequestFilter.valueOf(prFilterStr);
          
         List<String> userNames = null;
         if (groups != null) {
            List<Group> groupsPortal = groupManager.getAllGroupsById(Arrays.asList(StringUtils.split(groups)));
            Collection<OpenWISGroup> openwisGroups = CollectionUtils.collect(groupsPortal, new Transformer() {
               @Override
               public Object transform(Object input) {
                  return GroupManager.buildOpenWisGroupFromGroup((Group) input);
               }
            });
            
            userNames = groupManagementService.getAllUserNameByGroups(new ArrayList<OpenWISGroup>(openwisGroups));
         }
         
         if (userNames == null || CollectionUtils.isNotEmpty(userNames)) {
            SearchCriteriaWrapper<List<String>, RequestColumn> wrapper = new SearchCriteriaWrapper<List<String>, RequestColumn>();
            wrapper.setCriteria(userNames);
            wrapper.setStart(start);
            wrapper.setLimit(limit);
            if (sortColumn != null && sortDirection != null) {
               wrapper.setSort(requestManager.getRequestColumnAttribute(sortColumn));
               wrapper.setDir(SortDir.valueOf(sortDirection));
            }

            SearchResultWrapper<ProcessedRequest> adhocsWrapper = requestManager.getAllProcessedRequestsByUsers(wrapper, prFilter);

            dtos = new SearchResultWrapper<AdhocDTO>(adhocsWrapper.getTotal(),
                  AdhocDTO.adhocProcessedRequestsToDTO(adhocsWrapper.getRows()));
         } else {
            dtos = new SearchResultWrapper<AdhocDTO>(0, Lists.<AdhocDTO>newArrayList());
         }
      }

      return JeevesJsonWrapper.send(dtos);
   }

}
