package org.openwis.dataservice.useralarms;

import java.util.List;

import javax.jws.WebParam;

import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmReferenceType;

/**
 * Local interface to the UserAlarmManager.
 */
public interface UserAlarmManagerLocal {

	/**
	 * Raises a user alarm.  This will persist the user alarm.
	 */
	public void raiseUserAlarm(UserAlarm alarm);

	/**
	 * Return a list containing all the user alarms for a particular user with a reference to a particular reference
	 * type.
	 *
	 * @param username The user's username.  Cannot be <code>null</code>
	 * @param referenceType The reference type.  Cannot be <code>null</code>.
	 * @param offset The offset
	 * @param limit The limit
	 * @return A list of user alarms.
	 */
   public List<UserAlarm> getUserAlarmsForUserAndReferenceType(String username, UserAlarmReferenceType referenceType, int offset, int limit);
}
