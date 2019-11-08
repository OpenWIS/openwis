/**
 *
 */
package org.openwis.metadataportal.services.blacklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.BlacklistInfo;
import org.openwis.dataservice.BlacklistInfoColumn;
import org.openwis.dataservice.BlacklistInfoResult;
import org.openwis.dataservice.BlacklistService;
import org.openwis.dataservice.SortDirection;
import org.openwis.management.monitoring.DisseminatedDataStatistics;
import org.openwis.management.monitoring.UserDisseminationData;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.services.blacklist.dto.AllBlacklistDTO;
import org.openwis.metadataportal.services.blacklist.dto.BlacklistDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.requestsStatistics.dto.DataDisseminatedDTO;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * List all categories. <P>
 * Explanation goes here. <P>
 *
 */
public class All implements Service {

   private static Map<String, BlacklistInfoColumn> blacklistInfoColumnMap = new HashMap<String, BlacklistInfoColumn>();
   static {
      blacklistInfoColumnMap.put("user", BlacklistInfoColumn.USER);
      blacklistInfoColumnMap.put("nbDisseminationWarnThreshold", BlacklistInfoColumn.NB_WARN);
      blacklistInfoColumnMap.put("nbDisseminationBlacklistThreshold",
            BlacklistInfoColumn.NB_BLACKLIST);
      blacklistInfoColumnMap.put("volDisseminationWarnThreshold", BlacklistInfoColumn.VOL_WARN);
      blacklistInfoColumnMap.put("volDisseminationBlacklistThreshold",
            BlacklistInfoColumn.VOL_BLACKLIST);
      blacklistInfoColumnMap.put("status", BlacklistInfoColumn.STATUS);
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
      BlacklistService blacklistService = DataServiceProvider.getBlacklistService();
      DisseminatedDataStatistics disseminatedDataStatistics = ManagementServiceProvider
         .getDisseminatedDataStatistics();

      // Get paging parameters
      String searchParam = Util.getParam(params, "any", null);
      int index = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumnName = Util.getParam(params, "sort", null);
      String sortDir = Util.getParam(params, "dir", null);

      BlacklistInfoColumn bic = blacklistInfoColumnMap.get(sortColumnName);
      SortDirection sd = sortDirectionMap.get(sortDir);
      BlacklistInfoResult blacklistInfoResult;
      if (searchParam != null) {
         blacklistInfoResult = blacklistService.getUsersBlackListInfoByUser(searchParam, index,
               limit, bic, sd);
      } else {
         blacklistInfoResult = blacklistService.getUsersBlackListInfo(index, limit, bic, sd);
      }

      Set<String> users = new HashSet<String>();
      for (BlacklistInfo blacklistInfo : blacklistInfoResult.getList()) {
         users.add(blacklistInfo.getUser());
      }

      List<UserDisseminationData> userDisseminatedDataList = disseminatedDataStatistics
            .getUsersDisseminatedData(new ArrayList<String>(users),
                  DateTimeUtils.format(DateTimeUtils.getUTCDate()));
      Map<String, UserDisseminationData> userDisseminatedDataMap = new HashMap<String, UserDisseminationData>();
      for (UserDisseminationData userDisseminatedData : userDisseminatedDataList) {
         userDisseminatedDataMap.put(userDisseminatedData.getUserId(), userDisseminatedData);
      }

      List<BlacklistDTO> dtoList = new ArrayList<BlacklistDTO>();
      for (BlacklistInfo blacklistInfo : blacklistInfoResult.getList()) {
         BlacklistDTO blacklistDTO = new BlacklistDTO(blacklistInfo);
         UserDisseminationData userDisseminatedData = userDisseminatedDataMap.get(blacklistInfo
               .getUser());
         DataDisseminatedDTO userDisseminatedDataDTO;
         if (userDisseminatedData != null) {
            userDisseminatedDataDTO = new DataDisseminatedDTO(userDisseminatedData);
         } else {
            userDisseminatedDataDTO = new DataDisseminatedDTO();
         }
         blacklistDTO.setUserDisseminatedDataDTO(userDisseminatedDataDTO);
         dtoList.add(blacklistDTO);
      }

      AllBlacklistDTO allBlacklistDTO = new AllBlacklistDTO();
      // TODO : Column blacklisted is not implemented in BlacklistInfoColumn
      if (sortColumnName != null && sortColumnName.equals("blacklisted"))
      {
         Collections.sort(dtoList);
         if (sortDir != null && sortDir.equals("DESC"))
         {
            Collections.sort(dtoList, Collections.reverseOrder());
         }
      }
      allBlacklistDTO.setAllBlackList(dtoList);
      allBlacklistDTO.setCount(blacklistInfoResult.getCount());

      return JeevesJsonWrapper.send(allBlacklistDTO);
   }
}
