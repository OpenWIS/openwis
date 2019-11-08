/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.BlacklistInfo;
import org.openwis.dataservice.BlacklistService;
import org.openwis.management.monitoring.DisseminatedDataStatistics;
import org.openwis.management.monitoring.SortDirection;
import org.openwis.management.monitoring.UserDisseminatedDataColumn_0020;
import org.openwis.management.monitoring.UserDisseminationData;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.mock.MockStatistics;
import org.openwis.metadataportal.services.util.ServiceParameter;
import org.openwis.metadataportal.services.util.ServiceResultPager;

/**
 * Service class used for the global report's disseminated data store.
 */
public class GlobalDisseminatedReports extends GlobalReports {
   
   private static final String REQUEST_GET_FILTER = "GET_FILTER_PERIOD";
   private static final String PARAM_REQUEST_TYPE = "requestType";
   
   private static final String RESPONSE_RESULT = "filter";
   private static final String RESPONSE_ATTRIB_SUCCESS = "success";
   private static final String RESPONSE_ATTRIB_TARGET = "target";
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void init(final String appPath, final ServiceConfig params) throws Exception {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Element exec(final Element params, final ServiceContext context) throws Exception {
      Element result = null;
      // check for special requests

      Log.info(LOG_MODULE, listParams(params));
      
      String requestType = Util.getParam(params, PARAM_REQUEST_TYPE, null);
      if (requestType != null) {
         if (requestType.equals(REQUEST_GET_FILTER)) {
            return getFilter();            
         }
      }
      
      // get filter parameter
      setFilterPeriod(params);
      
      // get export parameter
      setExport(params);
      
      // search result
      ServiceParameter serviceParameter = new ServiceParameter(params);
      List<UserDisseminationData> elements = null;
      
      // Test mode
      if (MockMode.isMockModeMonitoringService()) {
         elements = MockStatistics.getDisseminatedStatistics();
      }
      // Operation mode
      else {
         elements = getDisseminatedData(params);
         updateUserThresholds(elements);         

         sortResults(elements, params);
      }

      if (isExport()) {
         result = getDataForExport(elements);
      }
      else {
         // ensure the result list will be returned in proper dimensions (start, limit)
         int totalCount = elements != null ? elements.size() : 0;
         
         SearchResultWrapper<UserDisseminationData> wrapper = 
            new ServiceResultPager<UserDisseminationData>(serviceParameter, totalCount, elements);
   
         result = JeevesJsonWrapper.send(wrapper);
      }      
      return result;
   }
   
   /**
    * Gets disseminated data statistics from disseminated data statistics service.
    * @return
    */
   private List<UserDisseminationData> getDisseminatedData(final Element params) {
      List<UserDisseminationData> disseminatedData = null;
      
      // Delegate to the MC service
      DisseminatedDataStatistics service =
         ManagementServiceProvider.getDisseminatedDataStatistics();
      
      if (service != null) {
         List<UserDisseminationData> serviceResults = 
            service.getDisseminatedDataStatistics(maxRowCount);
         
         disseminatedData = getFilteredResults(serviceResults);
      }
      
      return disseminatedData;
   }
   
   /**
    * Completes the result data with threshold values from the blacklist service
    * @param dataList
    */
   private void updateUserThresholds(final List<UserDisseminationData> dataList) {
      if (dataList != null && dataList.size() != 0) {
         // create a map with all different users
         Map<String, Long> userMap = new HashMap<String, Long>();
         for (UserDisseminationData data: dataList) {
            String userID = data.getUserId();
            if (!userMap.containsKey(userID)) {
               userMap.put(userID, Long.valueOf(0));
            }
         }
         // get User information from black list service
         BlacklistService userService = DataServiceProvider.getBlacklistService();
         if (userService != null) {
            for (Map.Entry<String, Long> mapEntry: userMap.entrySet()) {
               String userID = mapEntry.getKey();
               BlacklistInfo userInfo = 
                  userService.getUserBlackListInfoIfExists(userID);
               if (userInfo != null) {
                  mapEntry.setValue(userInfo.getVolDisseminationWarnThreshold());
               }
            }
         }
         for (UserDisseminationData data: dataList) {
            Long userTheshold = userMap.get(data.getUserId());
            data.setSize(userTheshold);
         }
      }
   }

   /**
    * Returns the current filter parameters as response attributes.
    * @return response element
    */
   private Element getFilter() {
      Element result = new Element(RESPONSE_RESULT);
      
      result.setAttribute(RESPONSE_ATTRIB_TARGET, REQUEST_GET_FILTER);
      result.setAttribute(RESPONSE_ATTRIB_SUCCESS, "true");
      result.setAttribute(PARAM_FILTER_PERIOD, String.valueOf(getFilterPeriod()));
      
      return result;
   }

   /**
    * Filters the original result list.
    * @param rawResults
    * @return
    */
   private List<UserDisseminationData> getFilteredResults(final List<UserDisseminationData> rawResults) {
      List<UserDisseminationData> filteredResults = new ArrayList<UserDisseminationData>();

      if (rawResults != null) {         
         for (UserDisseminationData result: rawResults) {
            if (isInPeriod(result.getDate())) {
               filteredResults.add(result);
            }
         }
      }
      return filteredResults;
   }

   /**
    * Gets formatted XML data for export.
    * @return
    */
   private Element getDataForExport(final List<UserDisseminationData> disseminatedDataResult) {
      Element root = new Element("export");
      for (UserDisseminationData disseminatedData: disseminatedDataResult) {
         Element data = new Element("record");
         data.setAttribute("date", disseminatedData.getDate().toString());
         data.setAttribute("size", String.valueOf(disseminatedData.getDissToolSize()));
         data.setAttribute("user", disseminatedData.getUserId());
         data.setAttribute("threshold", String.valueOf(disseminatedData.getSize()));
         root.addContent(data);            
      }
      return root;
   }
   
   /**
    * Sorts the result list containing <code>UserDisseminationData</code> according the sorting
    * parameters from the script.
    * @param serviceResults result list
    * @param params service parameters
    */
   private void sortResults(final List<UserDisseminationData> serviceResults, final Element params) { 
      if (serviceResults != null && params != null) {
         String sortColParam = getSortColumnParam(params);
         String sortDirParam = getSortDirectionParam(params);
         
         UserDisseminatedDataColumn_0020 sortColumn = getSortColumn(sortColParam);
         SortDirection sortDir = getSortDirection(sortDirParam);
         
         try {
            Collections.sort(serviceResults, new SortComparator(sortColumn, sortDir));
         }
         catch(Exception e) {
            Log.error(LOG_MODULE, "Exception in sorting result list - " + e.getMessage());           
         }
      }      
   }

   /**
    * <code>Comparator</code> used to sort a list of <code>UserDisseminationData</code>
    * 
    */
   private static final class SortComparator implements Comparator<UserDisseminationData> {
      
      private UserDisseminatedDataColumn_0020 sortColumn;
      private SortDirection sortDir;
      
      /**
       * Default constructor.
       * Builds a SortComparator.
       * @param sortCol
       * @param dir
       */
      public SortComparator(final UserDisseminatedDataColumn_0020 sortCol, final SortDirection dir) {
         sortColumn = sortCol;
         sortDir = dir;
      }

      /**
       * {@inheritDoc}
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(final UserDisseminationData o1, final UserDisseminationData o2) {
         int result = 0;
         switch (sortColumn) {
            case DATE:
               result = o1.getDate().compare(o2.getDate());
               break;
            case DISS_TOOL_TOTAL_SIZE:
               result = compareTo(o1.getDissToolSize(), o2.getDissToolSize());
               break;
            case TOTAL_SIZE:
               result = compareTo(o1.getSize(), o2.getSize());
               break;
            case USER:
               result = o1.getUserId().compareTo(o2.getUserId());
               break;                        
         }
         return (sortDir == SortDirection.ASC) ? result : result * -1;
      }         
   }
   
   private static int compareTo(long l1, long l2) {
      return l1 < l2 ? -1 : l1==l2? 0 : 1;
   }   
}