package org.openwis.datasource.server.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmRequestType;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.GetUserAlarmCriteriaDTO;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.UserAlarmReportCriteriaDTO;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.UserAlarmReportDTO;
import org.openwis.dataservice.common.service.UserAlarmManagerLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(name = "UserAlarmManagerWebService", targetNamespace = "http://dataservice.openwis.org/", portName = "UserAlarmManagerWebServicePort", serviceName = "UserAlarmManagerWebService")
@Stateless(name="UserAlarmManager")
@Local(UserAlarmManagerLocal.class)
public class UserAlarmManagerImpl implements UserAlarmManagerLocal {

	private static final Logger log = LoggerFactory.getLogger(UserAlarmManagerImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void raiseUserAlarm(final UserAlarm alarm) {
		Validate.notNull(alarm);

		entityManager.persist(alarm);
	}

   @Override
   @WebMethod(operationName = "countUserAlarms")
   public int countUserAlarms(GetUserAlarmCriteriaDTO searchCriteria) {
      Validate.notNull(searchCriteria);

      Pair<String, Map<String, Object>> queryAndParams = buildAlarmsForUserQuery("select count(u) from UserAlarm u", searchCriteria, false);

      Query query = entityManager.createQuery(queryAndParams.getLeft());
      for (Entry<String, Object> param : queryAndParams.getRight().entrySet()) {
         query.setParameter(param.getKey(), param.getValue());
      }

      Long cnt = (Long)query.getSingleResult();

      return cnt.intValue();
   }

   @Override
	@WebMethod(operationName = "getUserAlarms")
   public List<UserAlarm> getUserAlarms(@WebParam(name = "criteria") final GetUserAlarmCriteriaDTO dto) {
      Validate.notNull(dto);

      Pair<String, Map<String, Object>> queryAndParams = buildAlarmsForUserQuery("select u from UserAlarm u", dto, true);

      Query query = entityManager.createQuery(queryAndParams.getLeft());
      for (Entry<String, Object> param : queryAndParams.getRight().entrySet()) {
         query.setParameter(param.getKey(), param.getValue());
      }
      query.setFirstResult(dto.getOffset());
      query.setMaxResults(dto.getLimit());

      @SuppressWarnings("unchecked")
      List<UserAlarm> alarms = (List<UserAlarm>)query.getResultList();

      return alarms;
   }

   private Pair<String, Map<String, Object>> buildAlarmsForUserQuery(String prefix, GetUserAlarmCriteriaDTO dto, boolean addOrdering) {
      StringBuilder strBuilder = new StringBuilder(prefix);
      Map<String, Object> params = new HashMap<String, Object>();
      List<String> constraints = new ArrayList<String>();

      if (dto.getUsername() != null) {
         constraints.add("(u.userId = :userId)");
         params.put("userId", dto.getUsername());
      }
      if (dto.getRequestType() != null) {
         constraints.add("(u.requestType  = :requestType)");
         params.put("requestType", dto.getRequestType());
      }

      if (constraints.size() > 0) {
         strBuilder.append(" where ").append(StringUtils.join(constraints.iterator(), " and "));
      }

      if (addOrdering) {
         if (dto.getSortColumn() != null) {
            strBuilder.append(" order by ").append(dto.getSortColumn().sortFieldName());
            strBuilder.append(" ").append(BooleanUtils.toString(dto.isSortAscending(), "asc", "desc"));
         }
      }

      return new ImmutablePair<String, Map<String,Object>>(strBuilder.toString(), params);
   }

   @Override
   @WebMethod(operationName = "deleteAlarms")
   public int deleteAlarms(@WebParam(name = "alarmIds") List<Long> alarmIds) {
      int deletedAlarms = 0;

      for (Long alarmId : alarmIds) {
         UserAlarm userAlarm = (UserAlarm) entityManager.find(UserAlarm.class, new Long(alarmId));
         entityManager.remove(userAlarm);
         deletedAlarms++;
      }

      return deletedAlarms;
   }

   @Override
   @WebMethod(operationName = "deleteAllAlarms")
   public int deleteAllAlarms() {
      Query query = entityManager.createNamedQuery("UserAlarm.deleteAllUserAlarms");
      return query.executeUpdate();
   }

   @Override
   @WebMethod(operationName = "acknowledgeAlarmsForUser")
   public int acknowledgeAlarmsForUser(@WebParam(name = "username") String username,
                                       @WebParam(name = "alarmIds") List<Long> alarmIds) {
      int deletedAlarms = 0;

      for (Long alarmId : alarmIds) {
         UserAlarm userAlarm = (UserAlarm) entityManager.find(UserAlarm.class, new Long(alarmId));
         if (StringUtils.equals(userAlarm.getUserId(), username)) {
            entityManager.remove(userAlarm);
            deletedAlarms++;
         }
      }

      return deletedAlarms;
   }

   @Override
   @WebMethod(operationName = "acknowledgeAllAlarmsForUserAndRequestType")
   public void acknowledgeAllAlarmsForUserAndRequestType(@WebParam(name = "username") final String username,
                                                         @WebParam(name = "requestType") final UserAlarmRequestType requestType) {
      Validate.notNull(username);

      Query query = entityManager.createNamedQuery("UserAlarm.alarmsForUserAndReferenceType");
      query.setParameter("userId", username);
      query.setParameter("requestType", requestType);

      @SuppressWarnings("unchecked")
      List<UserAlarm> alarms = (List<UserAlarm>)query.getResultList();
      for (UserAlarm alarm : alarms) {
         entityManager.remove(alarm);
      }
   }

   @Override
   @WebMethod(operationName = "reportOnUserAlarms")
   public List<UserAlarmReportDTO> reportOnUserAlarms(@WebParam(name = "criteria") UserAlarmReportCriteriaDTO reportCriteria) {
      Validate.notNull(reportCriteria);

      // Get the query
      String queryString = buildReportQueryforUserAlarmReport(reportCriteria);
      Query query = entityManager.createNativeQuery(queryString);
      query.setFirstResult(reportCriteria.getOffset());
      query.setMaxResults(reportCriteria.getLimit());

      // Execute the query
      @SuppressWarnings("unchecked")
      List<Object[]> results = (List<Object[]>)query.getResultList();

      // Convert the results into DTO objects
      List<UserAlarmReportDTO> resultDtos = new ArrayList<UserAlarmReportDTO>(results.size());
      for (Object[] result : results) {
         if (result.length != 4) {
            throw new RuntimeException("Expected an array of length 4 but received an array with length " + result.length);
         }
         if (! (result[1] instanceof BigInteger)) {
            throw new RuntimeException("Expected the type of index 1 of the result array to be a BigInteger, but was " + result[1].getClass().toString());
         }
         if (! (result[2] instanceof BigInteger)) {
            throw new RuntimeException("Expected the type of index 2 of the result array to be a BigInteger, but was " + result[2].getClass().toString());
         }
         if (! (result[3] instanceof BigInteger)) {
            throw new RuntimeException("Expected the type of index 3 of the result array to be a BigInteger, but was " + result[3].getClass().toString());
         }

         String userId = result[0].toString();
         int requestCount = ((BigInteger)result[1]).intValue();
         int subscriptionCount = ((BigInteger)result[2]).intValue();
         int alarmCount = ((BigInteger)result[3]).intValue();

         resultDtos.add(UserAlarmReportDTO.createInstance(userId, requestCount, subscriptionCount, alarmCount));
      }

      return resultDtos;
   }

   /**
    * Builds a JPA query for reporting on the number of user alarms each user has.
    *
    * @param reportCriteria
    *       The report criteria.
    * @return
    *       The query string.
    */
   private String buildReportQueryforUserAlarmReport(
         UserAlarmReportCriteriaDTO reportCriteria) {
      StringBuilder query = new StringBuilder();

      // Create a native query for this report
      query.append("select user_id, ");
      query.append("coalesce(request_cnt, 0) as request_cnt, ");
      query.append("coalesce(subscription_cnt, 0) as subscription_cnt, ");
      query.append("coalesce(request_cnt, 0) + coalesce(subscription_cnt, 0) as total_cnt ");
      query.append("from ");
      query.append("(select user_id, count(1) as request_cnt from openwis_user_alarm where req_type = 'REQUEST' group by user_id, req_type) r ");
      query.append("full join ");
      query.append("(select user_id, count(1) as subscription_cnt from openwis_user_alarm where req_type = 'SUBSCRIPTION' group by user_id, req_type) s ");
      query.append("using (user_id) ");

      query.append(" order by ").append(reportCriteria.getSortBy().getSortFieldName());
      query.append(" ").append(BooleanUtils.toString(reportCriteria.isSortAsc(), "asc", "desc"));

      return query.toString();
   }

   @Override
   public int getDistinctNumberOfUsersWithUserAlarms() {
      Query query = entityManager.createNamedQuery("UserAlarm.distinctUserCount");
      Long count = (Long)query.getSingleResult();

      return count.intValue();
   }

   @Override
   public int deleteAlarmsOfRequest(long id) {
      Query query = entityManager.createNamedQuery("UserAlarm.deleteUserAlarmsOfRequest");
      query.setParameter("requestId", id);
      return query.executeUpdate();
   }

   @Override
   public int deleteAlarmsOfUser(String userId) {
      Query query = entityManager.createNamedQuery("UserAlarm.deleteUserAlarmsOfUser");
      query.setParameter("userId", userId);
      return query.executeUpdate();
   }
}
