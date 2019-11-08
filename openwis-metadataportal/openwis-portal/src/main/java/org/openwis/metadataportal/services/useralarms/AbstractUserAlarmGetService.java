package org.openwis.metadataportal.services.useralarms;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.jdom.Element;
import org.openwis.dataservice.GetUserAlarmCriteriaDTO;
import org.openwis.dataservice.GetUserAlarmSort;
import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.ProcessedRequestService;
import org.openwis.dataservice.UserAlarm;
import org.openwis.dataservice.UserAlarmManagerWebService;
import org.openwis.dataservice.UserAlarmRequestType;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.useralarms.dto.UserAlarmAndRequestDTO;
import org.openwis.metadataportal.services.useralarms.dto.UserAlarmDTO;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public abstract class AbstractUserAlarmGetService implements Service {

   /**
    * A mapping from the column ID to the appropriate sort field.
    */
   private static final Map<String, GetUserAlarmSort> COLUMN_ID_TO_SORT_MAP = new HashMap<String, GetUserAlarmSort>();

   static {
      COLUMN_ID_TO_SORT_MAP.put("date", GetUserAlarmSort.DATE);
      COLUMN_ID_TO_SORT_MAP.put("userId", GetUserAlarmSort.USER_ID);
      COLUMN_ID_TO_SORT_MAP.put("alarmType", GetUserAlarmSort.ALARM_TYPE);
      COLUMN_ID_TO_SORT_MAP.put("requestId", GetUserAlarmSort.REQUEST_ID);
      COLUMN_ID_TO_SORT_MAP.put("message", GetUserAlarmSort.MESSAGE);
   }

   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   @Override
   public Element exec(Element params, ServiceContext ctx) throws Exception {
      List<UserAlarm> userAlarms = Collections.emptyList();
      int totalUserAlarms = 0;

      // Get the alarm service
      UserAlarmManagerWebService userAlarmManager = DataServiceProvider.getUserAlarmManagerService();

      if (userAlarmManager != null) {
         GetUserAlarmCriteriaDTO criteriaDto = buildCriteriaDTO(ctx, params);
         userAlarms = userAlarmManager.getUserAlarms(criteriaDto);
         totalUserAlarms = userAlarmManager.countUserAlarms(criteriaDto);
      }

      List<UserAlarmDTO> userAlarmsDto = convertToDtos(userAlarms);

      // Convert it into a search result
      SearchResultWrapper<UserAlarmDTO> searchResults = new SearchResultWrapper<UserAlarmDTO>(totalUserAlarms, userAlarmsDto);

      return JeevesJsonWrapper.send(searchResults);
   }


   /**
    * Converts the request parameters into a criteria DTO.
    */
   private GetUserAlarmCriteriaDTO buildCriteriaDTO(ServiceContext ctx,
         Element params) throws Exception {
      GetUserAlarmCriteriaDTO dto = new GetUserAlarmCriteriaDTO();

      dto.setUsername(getUserName(ctx));
      dto.setRequestType(getRequestType());

      int start = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumn = Util.getParam(params, "sort", null);
      String sortDirection = Util.getParam(params, "dir", null);

      if (sortColumn != null)
      {
         dto.setSortColumn(COLUMN_ID_TO_SORT_MAP.get(sortColumn));
         dto.setSortAscending(sortDirection.equals("ASC"));
      }
      dto.setOffset(start);
      dto.setLimit(limit);

      return dto;
   }

   /**
    * Returns the user name this request service is responsible for getting.
    */
   protected abstract String getUserName(ServiceContext ctx);

   /**
    * Returns the reference type this request service is responsible for getting.
    */
   protected abstract UserAlarmRequestType getRequestType();

   /**
    * Converts the list of user alarms to a list of UserAlarmDTOs.
    *
    * @param userAlarm
    *       The user alarm
    * @return
    *       The user alarm DTO.
    */
   private List<UserAlarmDTO> convertToDtos(List<UserAlarm> userAlarms) {

      final ProcessedRequestService prs = DataServiceProvider.getProcessedRequestService();

      return Lists.transform(userAlarms, new Function<UserAlarm, UserAlarmDTO>() {
         public UserAlarmDTO apply(UserAlarm userAlarm) {

            // TODO: This is a RMI call, which is expensive.  Replace this with a call which accepts
            // a batch of process request IDs.
            ProcessedRequest processedRequest = prs.getFullProcessedRequest(userAlarm.getProcessedRequestId());
            return new UserAlarmAndRequestDTO(userAlarm, processedRequest);
         }

         public boolean equals(Object obj) {
            return (this == obj);
         }
      });
   }
}
