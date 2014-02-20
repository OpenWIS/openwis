package org.openwis.dataservice.useralarms;

import java.util.List;

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
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmReferenceType;
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
	@WebMethod(operationName = "getUserAlarmsForUserAndReferenceType")
   public List<UserAlarm> getUserAlarmsForUserAndReferenceType(@WebParam(name = "username") final String username,
                                               @WebParam(name = "referenceType") final UserAlarmReferenceType referenceType,
                                               @WebParam(name = "offset") final int offset,
                                               @WebParam(name = "limit") final int limit) {
      Validate.notNull(username);

      Query query = entityManager.createNamedQuery("UserAlarm.alarmsForUserAndReferenceType");
      query.setParameter("userId", username);
      query.setParameter("referenceType", referenceType);
      query.setFirstResult(offset);
      query.setMaxResults(limit);

      @SuppressWarnings("unchecked")
      List<UserAlarm> alarms = (List<UserAlarm>)query.getResultList();

      return alarms;
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
   @WebMethod(operationName = "acknowledgeAllAlarmsForUser")
   public void acknowledgeAllAlarmsForUser(@WebParam(name = "username") String username) {
      Validate.notNull(username);

      Query query = entityManager.createNamedQuery("UserAlarm.alarmsForUser");
      query.setParameter("userId", username);

      @SuppressWarnings("unchecked")
      List<UserAlarm> alarms = (List<UserAlarm>)query.getResultList();
      for (UserAlarm alarm : alarms) {
         entityManager.remove(alarm);
      }
   }
}
