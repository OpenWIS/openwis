/**
 *
 */
package org.openwis.metadataportal.services.requestsStatistics;

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
import org.openwis.management.monitoring.DisseminatedDataResult;
import org.openwis.management.monitoring.DisseminatedDataStatistics;
import org.openwis.management.monitoring.SortDirection;
import org.openwis.management.monitoring.UserDisseminatedDataColumn_0020;
import org.openwis.management.monitoring.UserDisseminationData;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.requestsStatistics.dto.AllDataExtractedDTO;
import org.openwis.metadataportal.services.requestsStatistics.dto.DataExtractedDTO;

/**
 * List all categories. <P>
 * Explanation goes here. <P>
 *
 */
public class AllDataExtracted implements Service {

   private static Map<String, UserDisseminatedDataColumn_0020> userDisseminatedDataColumnMap = new HashMap<String, UserDisseminatedDataColumn_0020>();
   static {
      userDisseminatedDataColumnMap.put("date", UserDisseminatedDataColumn_0020.DATE);
      userDisseminatedDataColumnMap.put("size", UserDisseminatedDataColumn_0020.TOTAL_SIZE);
      userDisseminatedDataColumnMap.put("dissToolSize", UserDisseminatedDataColumn_0020.DISS_TOOL_TOTAL_SIZE);
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
      DisseminatedDataStatistics disseminatedDataStatistics = ManagementServiceProvider
            .getDisseminatedDataStatistics();

      // Get paging parameters
      //String searchParam = Util.getParam(params, "any", null);
      int index = Util.getParamAsInt(params, "start");
      String limitStr = Util.getParam(params, "limit", null);
      int limit;
      if (limitStr != null)
      {
         limit = Integer.parseInt(limitStr);
      }
      else
      {
         limit = Integer.MAX_VALUE;
      }
      String sortColumnName = Util.getParam(params, "sort", null);
      String sortDir = Util.getParam(params, "dir", null);
      String xml = Util.getParam(params, "xml", null);

      UserDisseminatedDataColumn_0020 udc = userDisseminatedDataColumnMap.get(sortColumnName);
      SortDirection sd = sortDirectionMap.get(sortDir);
      DisseminatedDataResult disseminatedDataResult;
      
      disseminatedDataResult = disseminatedDataStatistics.getDisseminatedDataInInterval(
            null, null, index, limit, udc, sd);
      

      // If XML export, just return the element with all the data
      if (xml != null) {
         return exportToFile(context, disseminatedDataResult);
      }
      else
      {
         // If Not XML report, send the DTO
         List<DataExtractedDTO> dtoList = new ArrayList<DataExtractedDTO>();
         for (UserDisseminationData userDisseminatedData : disseminatedDataResult.getList()) {
            DataExtractedDTO userDisseminatedDataDTO = new DataExtractedDTO(userDisseminatedData);
            dtoList.add(userDisseminatedDataDTO);
         }

         AllDataExtractedDTO allDataExtracted = new AllDataExtractedDTO();
         allDataExtracted.setAllDataExtracted(dtoList);
         allDataExtracted.setCount(disseminatedDataResult.getCount());

         return JeevesJsonWrapper.send(allDataExtracted);
      }
   }
   
   /**
    * Export statistics to file.
    * @throws Exception
    */
   private Element exportToFile(ServiceContext context, DisseminatedDataResult disseminatedDataResult)
         throws Exception {
      Element root = new Element("export");
      for (UserDisseminationData userDisseminatedData : disseminatedDataResult.getList()) {
         Element data = new Element("record");
         data.setAttribute("date", userDisseminatedData.getDate().toString());
         data.setAttribute("extracted", String.valueOf(userDisseminatedData.getSize()));
         data.setAttribute("disseminated", String.valueOf(userDisseminatedData.getDissToolSize()));
         root.addContent(data);
      }

      File outputFile = new File(context.getUploadDir(), "disseminated-statistics.xml");
      FileOutputStream out = new FileOutputStream(outputFile);

      Xml.writeResponse(new Document(root), out);

      return BinaryFile.encode(200, outputFile.getAbsolutePath());
   }
}
