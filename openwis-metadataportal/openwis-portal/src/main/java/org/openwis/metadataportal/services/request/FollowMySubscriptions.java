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
import org.openwis.dataservice.Subscription;
import org.openwis.dataservice.SubscriptionColumn;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.request.RequestManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockFollowSubscriptionDTO;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.request.dto.follow.FollowSubscriptionDTO;

/**
 * This class enables to return all the Subscriptions of the user. <P>
 * Explanation goes here. <P>
 * 
 */
public class FollowMySubscriptions implements Service {

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
      if (MockMode.isMockModeDataService()) {
         List<FollowSubscriptionDTO> subscriptionDtos = MockFollowSubscriptionDTO.getFollowSubscriptionDTO();
         dtos = new SearchResultWrapper<FollowSubscriptionDTO>(subscriptionDtos.size() * 5, subscriptionDtos);
      } else {
         RequestManager requestManager = new RequestManager();

         String userName = GetUserNameHelper.getUserName(params, context);
         
         int start = Util.getParamAsInt(params, "start");
         int limit = Util.getParamAsInt(params, "limit");
         String sortColumn = Util.getParam(params, "sort", null);
         String sortDirection = Util.getParam(params, "dir", null);

         SearchCriteriaWrapper<List<String>, SubscriptionColumn> wrapper = new SearchCriteriaWrapper<List<String>, SubscriptionColumn>();
         wrapper.setCriteria(Arrays.asList(userName));
         wrapper.setStart(start);
         wrapper.setLimit(limit);
         if (sortColumn != null && sortDirection != null) {
             wrapper.setSort(requestManager.getSubscriptionColumnAttribute(sortColumn));
             wrapper.setDir(SortDir.valueOf(sortDirection));
         }

         SearchResultWrapper<Subscription> subscriptionsWrapper = requestManager.getAllSubscriptionsByUsers(wrapper);

         dtos = new SearchResultWrapper<FollowSubscriptionDTO>(subscriptionsWrapper.getTotal(),
               FollowSubscriptionDTO.subscriptionsToDTO(subscriptionsWrapper.getRows()));
      }
      
      return JeevesJsonWrapper.send(dtos);
   }
}
