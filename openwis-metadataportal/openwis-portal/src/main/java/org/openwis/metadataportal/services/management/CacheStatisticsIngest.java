/**
 *
 */
package org.openwis.metadataportal.services.management;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import jeeves.utils.Xml;

import org.jdom.Document;
import org.jdom.Element;
import org.openwis.management.monitoring.IngestedData;
import org.openwis.management.monitoring.IngestedDataColumn_0020;
import org.openwis.management.monitoring.IngestedDataStatistics;
import org.openwis.management.monitoring.SortDirection;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.util.ServiceParameter;
import org.openwis.metadataportal.services.util.ServiceResultPager;

/**
 * Service class used for ingested data store.
 */
public class CacheStatisticsIngest extends FilterReports {
   
   private static final Map<String, IngestedDataColumn_0020> sortColumns = 
      new HashMap<String, IngestedDataColumn_0020>();

   static {
      sortColumns.put("date", IngestedDataColumn_0020.DATE);
      sortColumns.put("size", IngestedDataColumn_0020.SIZE);
   };
   
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
      // search result
      Element result = null;
      ServiceParameter serviceParameter = new ServiceParameter(params);
      List<IngestedData> elements = null;      
      
      // get filter parameter (valid if called from global reports)
      // ignore it if parameter does not exist (called from cache statistics)
      setFilterPeriod(params, false);
      
      // get export parameter
      setExport(params);
      
      // Test mode
      if (MockMode.isMockModeMonitoringService()) {
         elements = null;
      }
      // Operation mode
      else {
         elements = getIngestedData(params);
      }
      
      if (isExport()) {
         File outputFile = new File(context.getUploadDir(), "ingestion-statistics.xml");
         FileOutputStream out = new FileOutputStream(outputFile);

         Xml.writeResponse(new Document(getDataForExport(elements)), out);

         result = BinaryFile.encode(200, outputFile.getAbsolutePath());
      }
      else {
         // ensure the result list will be returned in proper dimensions (start, limit)
         int totalCount = elements != null ? elements.size() : 0;
         
         SearchResultWrapper<IngestedData> wrapper =
            new ServiceResultPager<IngestedData>(serviceParameter, totalCount, elements);
   
         result = JeevesJsonWrapper.send(wrapper);         
      }
      return result;
   }
   
   /**
    * Gets filtered or unfiltered data from ingested data statistics.
    * @return
    */
   private List<IngestedData> getIngestedData(final Element params) {
      // Delegate to the MC service
      List<IngestedData> ingestedData = new ArrayList<IngestedData>();
      
      IngestedDataStatistics service = ManagementServiceProvider.getIngestedDataStatistics();
      if (service != null) {
         String sortColParam = getSortColumnParam(params);
         String sortDirParam = getSortDirectionParam(params);
         
         IngestedDataColumn_0020 sortColumn = sortColumns.get(sortColParam);
         if (sortColumn == null) {
            sortColumn = IngestedDataColumn_0020.DATE;;
         }         
         SortDirection sortDir = getSortDirection(sortDirParam);
         
         // query...
         List<IngestedData> serviceResults = 
            service.getIngestedDataStatistics(0, FilterReports.MAX_COUNT, sortColumn, sortDir);
         
         if (hasFilterPeriod()) {
            for (IngestedData data: serviceResults) {
               if (isInPeriod(data.getDate())) {
                  ingestedData.add(data);
               }
            }
         }
         else {
            ingestedData.addAll(serviceResults);
         }
      }      
      return ingestedData;
   }
   
   /**
    * Gets formatted xml data for export.
    * @return
    */
   private Element getDataForExport(final List<IngestedData> ingestedDataResult) {
      Element root = new Element("export");
      for (IngestedData ingestedData : ingestedDataResult) {
         Element data = new Element("record");
         data.setAttribute("date", ingestedData.getDate().toString());
         data.setAttribute("size", String.valueOf(ingestedData.getSize()));
         root.addContent(data);            
      }
      return root;
   }
}
