package org.openwis.metadataportal.services.useralarms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.exceptions.BadInputEx;
import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.UserAlarmManagerWebService;
import org.openwis.dataservice.UserAlarmReportCriteriaDTO;
import org.openwis.dataservice.UserAlarmReportDTO;
import org.openwis.dataservice.UserAlarmReportSort;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

public class GetUserAlarmReport implements Service {

   private static final Map<String, UserAlarmReportSort> SORT_COLUMN_TO_USER_ALARM_MAP = new HashMap<String, UserAlarmReportSort>();

   static
   {
      SORT_COLUMN_TO_USER_ALARM_MAP.put("userId", UserAlarmReportSort.USER_ID);
      SORT_COLUMN_TO_USER_ALARM_MAP.put("requestCount", UserAlarmReportSort.REQUEST_COUNT);
      SORT_COLUMN_TO_USER_ALARM_MAP.put("subscriptionCount", UserAlarmReportSort.SUBSCRIPTION_COUNT);
      SORT_COLUMN_TO_USER_ALARM_MAP.put("totalCount", UserAlarmReportSort.TOTAL_COUNT);
   }

   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * Builds the user alarm report criteria DTO from the parameters sent to the service.
    *
    * @param params
    * @return
    * @throws BadInputEx
    */
   private UserAlarmReportCriteriaDTO buildReportCriteriaDtoFromParams(
         Element params) throws BadInputEx {

      UserAlarmReportCriteriaDTO reportCriteriaDto = new UserAlarmReportCriteriaDTO();

      int start = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumn = Util.getParam(params, "sort", null);
      String sortDirection = Util.getParam(params, "dir", null);

      reportCriteriaDto.setSortBy(SORT_COLUMN_TO_USER_ALARM_MAP.get(sortColumn));
      reportCriteriaDto.setSortAsc(sortDirection.equals("ASC"));
      reportCriteriaDto.setOffset(start);
      reportCriteriaDto.setLimit(limit);

      return reportCriteriaDto;
   }

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      UserAlarmManagerWebService userAlarmManager = DataServiceProvider.getUserAlarmManagerService();

      UserAlarmReportCriteriaDTO reportCriteriaDto = buildReportCriteriaDtoFromParams(params);

      List<UserAlarmReportDTO> reportResults = userAlarmManager.reportOnUserAlarms(reportCriteriaDto);
      int countReportResults = userAlarmManager.getDistinctNumberOfUsersWithUserAlarms();

      SearchResultWrapper<UserAlarmReportDTO> searchResults = new SearchResultWrapper<UserAlarmReportDTO>(countReportResults, reportResults);

      return JeevesJsonWrapper.send(searchResults);
   }

}
