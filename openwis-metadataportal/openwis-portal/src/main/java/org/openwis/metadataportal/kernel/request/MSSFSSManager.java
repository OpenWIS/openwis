/**
 * 
 */
package org.openwis.metadataportal.kernel.request;

import java.util.HashMap;
import java.util.Map;

import org.openwis.harness.mssfss.GetRecentEventsForARouting;
import org.openwis.harness.mssfss.GetRecentEventsForARoutingResponse;
import org.openwis.harness.mssfss.ListRouting;
import org.openwis.harness.mssfss.ListRoutingResponse;
import org.openwis.harness.mssfss.MSSFSS;
import org.openwis.harness.mssfss.Routing;
import org.openwis.harness.mssfss.RoutingEvent;
import org.openwis.harness.mssfss.RoutingSortColumn;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.external.HarnessProvider;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MSSFSSManager {
   
   /**
    * Description goes here.
    * @param wrapper
    * @return
    */
   public SearchResultWrapper<Routing> getAllMSSFSSSubscriptionsByUsers(
         SearchCriteriaWrapper<String, RoutingSortColumn> searchCriteriaWrapper) {
      MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
      
      ListRouting params = new ListRouting();

      params.setUser(searchCriteriaWrapper.getCriteria());
      
      int page = searchCriteriaWrapper.getStart() / searchCriteriaWrapper.getLimit();
      params.setPage(page);
      params.setPageSize(searchCriteriaWrapper.getLimit());
      params.setSortColumn(searchCriteriaWrapper.getSort());
      params.setRevert(searchCriteriaWrapper.getDir().equals(SortDir.DESC));
      
      ListRoutingResponse response = mssFssService.listRouting(params);

      SearchResultWrapper<Routing> result = new SearchResultWrapper<Routing>();
      result.setTotal(response.getNumberOfResults());
      result.setRows(response.getRoutings());
      return result;
   }
   
   /**
    * Description goes here.
    * @param wrapper
    * @return
    */
   public SearchResultWrapper<RoutingEvent> getMSSFSSProcessedRequests(SearchCriteriaWrapper<String, String> wrapper) {
      MSSFSS mssFssService = HarnessProvider.getMSSFSSService();
      
      GetRecentEventsForARouting params = new GetRecentEventsForARouting();

      int page = wrapper.getStart() / wrapper.getLimit();
      params.setIdRequest(wrapper.getCriteria());
      params.setPage(page);
      params.setPageSize(wrapper.getLimit());
      params.setSortColumn(wrapper.getSort());
      params.setRevert(wrapper.getDir().equals(SortDir.DESC));
      
      GetRecentEventsForARoutingResponse response = mssFssService.getRecentEventsForARouting(params);

      SearchResultWrapper<RoutingEvent> result = new SearchResultWrapper<RoutingEvent>();
      result.setTotal(response.getNumberOfResults());
      result.setRows(response.getRoutingEvents());
      return result;
   }
   
   /**
    * Description goes here.
    * @param sortColumn
    * @return
    */
   public RoutingSortColumn getRoutingSortColumnAttribute(String columnName) {
      Map<String, RoutingSortColumn> rscEnumMap = new HashMap<String, RoutingSortColumn>();
      rscEnumMap.put("creationDate", RoutingSortColumn.CREATION_DATE);
      rscEnumMap.put("state", RoutingSortColumn.STATE);
      rscEnumMap.put("id", RoutingSortColumn.ID);
      rscEnumMap.put("urn", RoutingSortColumn.METADATA_URN);
      rscEnumMap.put("channel", RoutingSortColumn.CHANNEL);
      rscEnumMap.put("lastProcessingDate", RoutingSortColumn.LAST_EVENT_DATE);
      return rscEnumMap.get(columnName);
   }
}
