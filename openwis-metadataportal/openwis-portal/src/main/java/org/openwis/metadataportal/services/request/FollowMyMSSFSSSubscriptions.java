/**
 * 
 */
package org.openwis.metadataportal.services.request;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.harness.mssfss.Routing;
import org.openwis.harness.mssfss.RoutingSortColumn;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.request.MSSFSSManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockFollowSubscriptionDTO;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.request.dto.follow.FollowSubscriptionDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class FollowMyMSSFSSSubscriptions implements Service {

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
      SearchResultWrapper<FollowSubscriptionDTO> dtos = null;
      if (MockMode.isMockModeHarnessMSSFSS()) {
         List<FollowSubscriptionDTO> subscriptionDtos = MockFollowSubscriptionDTO
               .getFollowSubscriptionDTO();
         dtos = new SearchResultWrapper<FollowSubscriptionDTO>(subscriptionDtos.size() * 5,
               subscriptionDtos);
      } else {
         MSSFSSManager mssFssManager = new MSSFSSManager();
         
         String userName = GetUserNameHelper.getUserName(params, context);

         int start = Util.getParamAsInt(params, "start");
         int limit = Util.getParamAsInt(params, "limit");
         String sortColumn = Util.getParam(params, "sort", null);
         String sortDirection = Util.getParam(params, "dir", null);

         SearchCriteriaWrapper<String, RoutingSortColumn> wrapper = new SearchCriteriaWrapper<String, RoutingSortColumn>();
         wrapper.setCriteria(userName);
         wrapper.setStart(start);
         wrapper.setLimit(limit);
         if (sortColumn != null && sortDirection != null) {
            wrapper.setSort(mssFssManager.getRoutingSortColumnAttribute(sortColumn));
            wrapper.setDir(SortDir.valueOf(sortDirection));
         }

         SearchResultWrapper<Routing> mssFssSubscriptionsWrapper = mssFssManager
               .getAllMSSFSSSubscriptionsByUsers(wrapper);

         dtos = new SearchResultWrapper<FollowSubscriptionDTO>(mssFssSubscriptionsWrapper.getTotal(),
               FollowSubscriptionDTO.mssFssSubscriptionsToDTO(mssFssSubscriptionsWrapper.getRows()));
      }

      return JeevesJsonWrapper.send(dtos);
   }
}
