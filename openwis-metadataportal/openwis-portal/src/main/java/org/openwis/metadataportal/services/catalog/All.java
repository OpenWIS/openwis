/**
 * All
 */
package org.openwis.metadataportal.services.catalog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.jdom.Document;
import org.jdom.Element;
import org.openwis.management.monitoring.ExchangedData;
import org.openwis.management.monitoring.ExchangedDataColumn_0020;
import org.openwis.management.monitoring.ExchangedDataResult;
import org.openwis.management.monitoring.ExchangedDataStatistics;
import org.openwis.management.monitoring.SortDirection;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.catalog.dto.AllExchangedDataDTO;
import org.openwis.metadataportal.services.catalog.dto.ExchangedDataDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * All. <P>
 * Explanation goes here. <P>
 * 
 */
public class All implements Service {

   private static Map<String, ExchangedDataColumn_0020> exchangedDataColumnMap = new HashMap<String, ExchangedDataColumn_0020>();
   static {
      exchangedDataColumnMap.put("date", ExchangedDataColumn_0020.DATE);
      exchangedDataColumnMap.put("source", ExchangedDataColumn_0020.SOURCE);
      exchangedDataColumnMap.put("totalSize", ExchangedDataColumn_0020.TOTAL_SIZE);
      exchangedDataColumnMap.put("nbMetadata", ExchangedDataColumn_0020.METADATA_NUMBER);
   }

   private static Map<String, SortDirection> sortDirectionMap = new HashMap<String, SortDirection>();
   static {
      sortDirectionMap.put("ASC", SortDirection.ASC);
      sortDirectionMap.put("DESC", SortDirection.DESC);
   }

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
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      ExchangedDataStatistics exchangedDataStatistics = ManagementServiceProvider
            .getExchangedDataStatistics();

      // Get paging parameters
      String searchParam = Util.getParam(params, "any", null);
      int index = Util.getParamAsInt(params, "start");
      String limitStr = Util.getParam(params, "limit", null);
      int limit;
      if (limitStr != null) {
         limit = Integer.parseInt(limitStr);
      } else {
         limit = Integer.MAX_VALUE;
      }
      String sortColumnName = Util.getParam(params, "sort", null);
      String sortDir = Util.getParam(params, "dir", null);
      String xml = Util.getParam(params, "xml", null);

      ExchangedDataColumn_0020 edc = exchangedDataColumnMap.get(sortColumnName);
      SortDirection sd = sortDirectionMap.get(sortDir);
      ExchangedDataResult exchangedDataResult;
      if (searchParam != null) {
         exchangedDataResult = exchangedDataStatistics.getExchangedDataInIntervalBySources(
               searchParam, index, limit, edc, sd);
      } else {
         exchangedDataResult = exchangedDataStatistics.getExchangedDataInIntervalBySources("",
               index, limit, edc, sd);
      }

      // If XML export, just return the element with all the data
      if (xml != null) {
         return exportToFile(context, exchangedDataResult);
      } else {
         // If Not XML report, send the DTO
         List<ExchangedDataDTO> dtoList = new ArrayList<ExchangedDataDTO>();
         for (ExchangedData exchangedData : exchangedDataResult.getList()) {
            ExchangedDataDTO exchangedDataDTO = new ExchangedDataDTO(exchangedData);
            dtoList.add(exchangedDataDTO);
         }

         AllExchangedDataDTO allExchangedDataDTO = new AllExchangedDataDTO();
         allExchangedDataDTO.setAllExchangedData(dtoList);
         allExchangedDataDTO.setCount(exchangedDataResult.getCount());

         return JeevesJsonWrapper.send(allExchangedDataDTO);
      }
   }

   /**
    * Export statistics to file.
    * @throws Exception
    */
   private Element exportToFile(ServiceContext context, ExchangedDataResult exchangedDataResult)
         throws Exception {
      Element root = new Element("export");
      for (ExchangedData exchangedData : exchangedDataResult.getList()) {
         Element data = new Element("record");
         data.setAttribute("date", exchangedData.getDate().toString());
         data.setAttribute("source", exchangedData.getSource());
         data.setAttribute("totalSize", String.valueOf(exchangedData.getTotalSize()));
         data.setAttribute("nbMetadata", String.valueOf(exchangedData.getNbMetadata()));
         root.addContent(data);
      }

      File outputFile = new File(context.getUploadDir(), "catalog-statistics.xml");
      FileOutputStream out = new FileOutputStream(outputFile);

      Xml.writeResponse(new Document(root), out);

      return BinaryFile.encode(200, outputFile.getAbsolutePath());
   }
}
