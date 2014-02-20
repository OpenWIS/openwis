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

		log.error("##### USER ALARM #####");
		log.error("  Id:       " + alarm.getId());
		log.error("  User:     " + alarm.getUserId());
		log.error("  Category: " + alarm.getCategory());
		log.error("  Message:  " + alarm.getMessage());
		log.error("######################");
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
}
