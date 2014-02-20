package org.openwis.dataservice.useralarms;

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
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmReferenceType;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.GetUserAlarmCriteriaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(name = "UserAlarmManagerWebService")
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
      if (dto.getReferenceType() != null) {
         constraints.add("(u.referenceType = :referenceType)");
         params.put("referenceType", dto.getReferenceType());
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
   @WebMethod(operationName = "acknowledgeUserAlarm")
   public void acknowledgeUserAlarm(@WebParam(name = "alarmId") long alarmId) {
      UserAlarm userAlarm = (UserAlarm) entityManager.find(UserAlarm.class, new Long(alarmId));
      if (userAlarm != null) {
         entityManager.remove(userAlarm);
      }
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
   @WebMethod(operationName = "acknowledgeAllAlarmsForUserAndReferenceType")
   public void acknowledgeAllAlarmsForUserAndReferenceType(@WebParam(name = "username") final String username,
                                                           @WebParam(name = "referenceType") final UserAlarmReferenceType referenceType) {
      Validate.notNull(username);

      Query query = entityManager.createNamedQuery("UserAlarm.alarmsForUserAndReferenceType");
      query.setParameter("userId", username);
      query.setParameter("referenceType", referenceType);

      @SuppressWarnings("unchecked")
      List<UserAlarm> alarms = (List<UserAlarm>)query.getResultList();
      for (UserAlarm alarm : alarms) {
         entityManager.remove(alarm);
      }
   }
}
