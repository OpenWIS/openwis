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
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionColumn;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockFollowSubscriptionDTO;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.request.dto.follow.FollowSubscriptionDTO;
import org.openwis.securityservice.GroupManagementService;
import org.openwis.securityservice.OpenWISGroup;

import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MonitorCurrentSubscriptions implements Service {

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
      SearchResultWrapper<FollowSubscriptionDTO> dtos = null;

      if (MockMode.isMockModeDataService()) {
         List<FollowSubscriptionDTO> subscriptionsDto = MockFollowSubscriptionDTO
               .getFollowSubscriptionDTO();
         dtos = new SearchResultWrapper<FollowSubscriptionDTO>(subscriptionsDto.size() * 5,
               subscriptionsDto);
      } else {
         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         RequestManager requestManager = new RequestManager();
         GroupManager groupManager = new GroupManager(dbms);
         GroupManagementService groupManagementService = SecurityServiceProvider
               .getGroupManagementService();

         int start = Util.getParamAsInt(params, "start");
         int limit = Util.getParamAsInt(params, "limit");
         String sortColumn = Util.getParam(params, "sort", null);
         String sortDirection = Util.getParam(params, "dir", null);
         String groups = Util.getParam(params, "groups", null);

         List<String> userNames = null;
         if (groups != null) {
            List<Group> groupsPortal = groupManager.getAllGroupsById(Arrays.asList(StringUtils
                  .split(groups)));
            Collection<OpenWISGroup> openwisGroups = CollectionUtils.collect(groupsPortal,
                  new Transformer() {
                     @Override
                     public Object transform(Object input) {
                        return GroupManager.buildOpenWisGroupFromGroup((Group) input);
                     }
                  });

            userNames = groupManagementService
                  .getAllUserNameByGroups(new ArrayList<OpenWISGroup>(openwisGroups));
         }
         
         if (userNames == null || CollectionUtils.isNotEmpty(userNames)) {
            SearchCriteriaWrapper<List<String>, SubscriptionColumn> wrapper = new SearchCriteriaWrapper<List<String>, SubscriptionColumn>();
            wrapper.setCriteria(userNames);
            wrapper.setStart(start);
            wrapper.setLimit(limit);
            if (sortColumn != null && sortDirection != null) {
               wrapper.setSort(requestManager.getSubscriptionColumnAttribute(sortColumn));
               wrapper.setDir(SortDir.valueOf(sortDirection));
            }

            SearchResultWrapper<Subscription> subscriptionsWrapper = requestManager
                  .getAllSubscriptionsByUsers(wrapper);

            dtos = new SearchResultWrapper<FollowSubscriptionDTO>(subscriptionsWrapper.getTotal(),
                  FollowSubscriptionDTO.subscriptionsToDTO(subscriptionsWrapper.getRows()));
         } else {
            dtos = new SearchResultWrapper<FollowSubscriptionDTO>(0, Lists.<FollowSubscriptionDTO>newArrayList());
         }
      }

      return JeevesJsonWrapper.send(dtos);
   }

}
