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
	@WebMethod(operationName = "getUserAlarmsForUser")
   public List<UserAlarm> getUserAlarmsForUser(@WebParam(name = "username") final String username) {
      Validate.notNull(username);

      Query query = entityManager.createNamedQuery("UserAlarm.alarmsForUser");
      query.setParameter("userId", username);

      @SuppressWarnings("unchecked")
      List<UserAlarm> alarms = (List<UserAlarm>)query.getResultList();

      return alarms;
   }
}
