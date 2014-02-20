package org.openwis.dataservice.useralarms;

import java.util.List;

import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;

/**
 * Local interface to the UserAlarmManager.
 */
public interface UserAlarmManagerLocal {

	/**
	 * Raises a user alarm.  This will persist the user alarm.
	 */
	public void raiseUserAlarm(UserAlarm alarm);

	/**
	 * Return a list containing all the user alarms for a particular user.  If there are no alarms
	 * associated with the particular user, the returned list will be empty.
	 *
	 * @param username The user's username
	 * @return A list of user alarms.
	 */
	public List<UserAlarm> getUserAlarmsForUser(String username);
}
