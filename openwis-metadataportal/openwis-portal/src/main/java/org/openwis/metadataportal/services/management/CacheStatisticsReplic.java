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
import org.openwis.management.monitoring.ReplicatedData;
import org.openwis.management.monitoring.ReplicatedDataColumn_0020;
import org.openwis.management.monitoring.ReplicatedDataStatistics;
import org.openwis.management.monitoring.SortDirection;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.mock.MockMode;
import org.openwis.metadataportal.services.util.ServiceParameter;
import org.openwis.metadataportal.services.util.ServiceResultPager;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 */
public class CacheStatisticsReplic extends FilterReports {

   private static final Map<String, ReplicatedDataColumn_0020> sortColumns = 
      new HashMap<String, ReplicatedDataColumn_0020>();

   static {
      sortColumns.put("date", ReplicatedDataColumn_0020.DATE);
      sortColumns.put("size", ReplicatedDataColumn_0020.SIZE);
      sortColumns.put("source", ReplicatedDataColumn_0020.SOURCE);
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
      List<ReplicatedData> elements = null;
      
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
         elements = getReplicatedData(params);
      }
      
      if (isExport()) {
         File outputFile = new File(context.getUploadDir(), "replication-statistics.xml");
         FileOutputStream out = new FileOutputStream(outputFile);

         Xml.writeResponse(new Document(getDataForExport(elements)), out);

         result = BinaryFile.encode(200, outputFile.getAbsolutePath());
      }
      else {         
         // ensure the result list will be returned in proper dimensions (start, limit)
         int totalCount = elements != null ? elements.size() : 0;
         
         SearchResultWrapper<ReplicatedData> wrapper =
            new ServiceResultPager<ReplicatedData>(serviceParameter, totalCount, elements);

         result =  JeevesJsonWrapper.send(wrapper);
      }
      return result;
   }
   
   /**
    * Gets filtered or un-filtered data from replicated data statistics.
    * @return
    */
   private List<ReplicatedData> getReplicatedData(final Element params) {
      List<ReplicatedData> replicatedData = new ArrayList<ReplicatedData>();
      
      // Delegate to the MC service
      ReplicatedDataStatistics service = ManagementServiceProvider.getReplicatedDataStatistics();
      if (service != null) {
         String sortColParam = getSortColumnParam(params);
         String sortDirParam = getSortDirectionParam(params);
         
         ReplicatedDataColumn_0020 sortColumn = sortColumns.get(sortColParam);
         if (sortColumn == null) {
            sortColumn = ReplicatedDataColumn_0020.DATE;;
         }         
         SortDirection sortDir = getSortDirection(sortDirParam);
         
         // query...
         List<ReplicatedData> serviceResults =
            service.getReplicatedDataStatistics(0, FilterReports.MAX_COUNT, sortColumn, sortDir);
                  
         if (hasFilterPeriod()) {
            for (ReplicatedData data: serviceResults) {               
               if (isInPeriod(data.getDate())) {
                  replicatedData.add(data);
               }
            }
         }
         else {
            replicatedData.addAll(serviceResults);
         }
      }
      return replicatedData;      
   }

   /**
    * Gets formatted xml data for export.
    * @return
    */
   private Element getDataForExport(final List<ReplicatedData> replicatedDataResult) {
      Element root = new Element("export");
      for (ReplicatedData replicatedData : replicatedDataResult) {
         Element data = new Element("record");
         data.setAttribute("date", replicatedData.getDate().toString());
         data.setAttribute("source", replicatedData.getSource());
         data.setAttribute("size", String.valueOf(replicatedData.getSize()));
         root.addContent(data);            
      }
      return root;
   }
}
