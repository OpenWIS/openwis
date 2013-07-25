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
import org.openwis.metadataportal.services.requestsStatistics.dto.AllDataDisseminatedDTO;
import org.openwis.metadataportal.services.requestsStatistics.dto.DataDisseminatedDTO;

/**
 * List all categories. <P>
 * Explanation goes here. <P>
 *
 */
public class AllDataDisseminated implements Service {

   private static Map<String, UserDisseminatedDataColumn_0020> userDisseminatedDataColumnMap = new HashMap<String, UserDisseminatedDataColumn_0020>();
   static {
      userDisseminatedDataColumnMap.put("date", UserDisseminatedDataColumn_0020.DATE);
      userDisseminatedDataColumnMap.put("dissToolNbFiles", UserDisseminatedDataColumn_0020.DISS_TOOL_FILES_NUMBER);
      userDisseminatedDataColumnMap.put("dissToolSize", UserDisseminatedDataColumn_0020.DISS_TOOL_TOTAL_SIZE);
      userDisseminatedDataColumnMap.put("userId", UserDisseminatedDataColumn_0020.USER);
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
      String searchParam = Util.getParam(params, "any", null);
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
      if (searchParam != null) {
         disseminatedDataResult = disseminatedDataStatistics.getUsersDisseminatedDataByUser(
               searchParam, index, limit, udc, sd);
      } else {
         disseminatedDataResult = disseminatedDataStatistics.getUsersDisseminatedDataByUser("",
               index, limit, udc, sd);
      }

      // If XML export, just return the element with all the data
      if (xml != null) {
         return exportToFile(context, disseminatedDataResult);
      }
      else
      {
         // If Not XML report, send the DTO
         List<DataDisseminatedDTO> dtoList = new ArrayList<DataDisseminatedDTO>();
         for (UserDisseminationData userDisseminatedData : disseminatedDataResult.getList()) {
            DataDisseminatedDTO userDisseminatedDataDTO = new DataDisseminatedDTO(userDisseminatedData);
            dtoList.add(userDisseminatedDataDTO);
         }

         AllDataDisseminatedDTO allDataDisseminated = new AllDataDisseminatedDTO();
         allDataDisseminated.setAllDataDisseminated(dtoList);
         allDataDisseminated.setCount(disseminatedDataResult.getCount());

         return JeevesJsonWrapper.send(allDataDisseminated);
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
         data.setAttribute("user", userDisseminatedData.getUserId());
         data.setAttribute("size", String.valueOf(userDisseminatedData.getSize()));
         data.setAttribute("nbFiles", String.valueOf(userDisseminatedData.getNbFiles()));
         root.addContent(data);
      }

      File outputFile = new File(context.getUploadDir(), "disseminated-users-statistics.xml");
      FileOutputStream out = new FileOutputStream(outputFile);

      Xml.writeResponse(new Document(root), out);

      return BinaryFile.encode(200, outputFile.getAbsolutePath());
   }
}
