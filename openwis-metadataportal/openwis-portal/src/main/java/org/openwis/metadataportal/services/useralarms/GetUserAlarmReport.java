package org.openwis.metadataportal.services.useralarms;

import java.util.List;

import jeeves.exceptions.BadInputEx;
import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openwis.dataservice.useralarms.UserAlarmManagerWebService;
import org.openwis.dataservice.useralarms.UserAlarmReportCriteriaDTO;
import org.openwis.dataservice.useralarms.UserAlarmReportDTO;
import org.openwis.dataservice.useralarms.UserAlarmReportSort;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

public class GetUserAlarmReport implements Service {

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

      if (StringUtils.equals(sortColumn, "userId")) {
         reportCriteriaDto.setSortBy(UserAlarmReportSort.USER);
      } else if (StringUtils.equals(sortColumn, "alarms")) {
         reportCriteriaDto.setSortBy(UserAlarmReportSort.ALARMS);
      } else {
         reportCriteriaDto.setSortBy(UserAlarmReportSort.USER);
      }

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
