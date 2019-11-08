/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.jdom.Element;
import org.openwis.management.monitoring.DisseminatedDataResult;
import org.openwis.management.monitoring.DisseminatedDataStatistics;
import org.openwis.management.monitoring.SortDirection;
import org.openwis.management.monitoring.UserDisseminatedDataColumn_0020;
import org.openwis.management.monitoring.UserDisseminationData;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.mock.MockStatistics;
import org.openwis.metadataportal.services.requestsStatistics.dto.AllDataExtractedDTO;
import org.openwis.metadataportal.services.requestsStatistics.dto.DataExtractedDTO;
import org.openwis.metadataportal.services.util.DateTimeUtils;
import org.openwis.metadataportal.services.util.ServiceParameter;
import org.openwis.metadataportal.services.util.ServiceResultPager;

/**
 * Service class used for the global report's extracted data store.
 */
public class GlobalExtractedReports extends GlobalReports {

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
      // search result
      ServiceParameter serviceParameter = new ServiceParameter(params);
      List<DataExtractedDTO> elements = null;

      // get filter parameter
      setFilterPeriod(params);

      // get export parameter
      setExport(params);
      
      // Test mode
      if (MockMode.isMockModeMonitoringService()) {
         elements = MockStatistics.getDisseminatedExtractedStatistics();
      }
      // Operation mode
      else {
         // Delegate to the MC service
         elements = getExtractedData(params);
         
         sortResults(elements, params);
      }

      if (isExport()) {
         result = getDataForExport(elements);
      }
      else {
         // ensure the result list will be returned in proper dimensions (start, limit)
         int totalCount = elements != null ? elements.size() : 0;
   
         SearchResultWrapper<DataExtractedDTO> wrapper = new ServiceResultPager<DataExtractedDTO>(
               serviceParameter, totalCount, elements);
   
         result = JeevesJsonWrapper.send(wrapper);
      }      
      return result;
   }

   /**
    * Gets the filtered extracted data list
    * @return
    */
   private List<DataExtractedDTO> getExtractedData(final Element params) {
      DisseminatedDataStatistics disseminatedDataStatisticsService = 
         ManagementServiceProvider.getDisseminatedDataStatistics();

      if (disseminatedDataStatisticsService == null) {
         return null;
      }

      List<DataExtractedDTO> dataExtractedList = new ArrayList<DataExtractedDTO>();
      
      String from = DateTimeUtils.format(new Date(fromTime));
      String to = DateTimeUtils.format(new Date(toTime));
      
      UserDisseminatedDataColumn_0020 sortColumn = UserDisseminatedDataColumn_0020.DATE;
      SortDirection sortDir = SortDirection.DESC;
      
      DisseminatedDataResult disseminatedDataResult = 
         disseminatedDataStatisticsService.getDisseminatedDataInInterval(from, 
                                                                         to, 
                                                                         0, 
                                                                         10, 
                                                                         sortColumn,
                                                                         sortDir);

      for (UserDisseminationData userDisseminatedData : disseminatedDataResult.getList()) {
         DataExtractedDTO userDisseminatedDataDTO = new DataExtractedDTO(userDisseminatedData);
         dataExtractedList.add(userDisseminatedDataDTO);
      }

      AllDataExtractedDTO allDataExtracted = new AllDataExtractedDTO();
      allDataExtracted.setAllDataExtracted(dataExtractedList);
      allDataExtracted.setCount(disseminatedDataResult.getCount());

      return dataExtractedList;
   }

   /**
    * Gets formatted xml data for export.
    * @return
    */
   private Element getDataForExport(final List<DataExtractedDTO> extractedDataResult) {
      Element root = new Element("export");
      for (DataExtractedDTO extractedData: extractedDataResult) {
         Element data = new Element("record");
         data.setAttribute("date", extractedData.getDate().toString());
         data.setAttribute("extracted", String.valueOf(extractedData.getSize()));
         data.setAttribute("disseminated", String.valueOf(extractedData.getDissToolSize()));
         root.addContent(data);                     
      }
      return root;
   }
   
   /**
    * Sorts the result list containing <code>DataExtractedDTO</code> according the sorting
    * parameters from the script.
    * @param serviceResults result list
    * @param params service parameters
    */
   private void sortResults(final List<DataExtractedDTO> serviceResults, final Element params) { 
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
    * <code>Comparator</code> used to sort a list of <code>DataExtractedDTO</code>
    * 
    */
   private static final class SortComparator implements Comparator<DataExtractedDTO> {
      
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
      public int compare(final DataExtractedDTO o1, final DataExtractedDTO o2) {
         int result = 0;
         switch (sortColumn) {
            case DATE:
               result = o1.getDate().compareTo(o2.getDate());
               break;
            case DISS_TOOL_TOTAL_SIZE:
               result = compareTo(o1.getDissToolSize(), o2.getDissToolSize());
               break;
            case TOTAL_SIZE:
               result = compareTo(o1.getSize(), o2.getSize());
               break;
         }
         return (sortDir == SortDirection.ASC) ? result : result * -1;
      }         
   }
   
   private static int compareTo(long l1, long l2) {
      return l1 < l2 ? -1 : l1==l2? 0 : 1;
   }   
   
}
