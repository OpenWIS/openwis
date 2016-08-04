package org.openwis.dataservice.common.service;

import java.util.List;

import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmRequestType;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.GetUserAlarmCriteriaDTO;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.GetUserAlarmSort;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.UserAlarmReportCriteriaDTO;
import org.openwis.dataservice.common.domain.entity.useralarm.dto.UserAlarmReportDTO;

/**
 * Local interface to the UserAlarmManager.
 */
public interface UserAlarmManagerLocal {

	/**
	 * Raises a user alarm.  This will persist the user alarm.
	 */
	public void raiseUserAlarm(UserAlarm alarm);

	/**
	 * Return the number of user alarms which match the criteria.
	 *
	 * @param searchCriteria
	 * @return
	 */
	public int countUserAlarms(GetUserAlarmCriteriaDTO searchCriteria);

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
   public List<UserAlarm> getUserAlarms(GetUserAlarmCriteriaDTO searchCriteria);

   /**
    * Reports on the user alarms for the set of users which have user alarms.  The report, at the moment, consist of only
    * the total number of user alarms each user has.
    *
    * @param reportCriteria the reporting criteria
    * @return a list of user alarm report DTOs.
    */
   public List<UserAlarmReportDTO> reportOnUserAlarms(UserAlarmReportCriteriaDTO reportCriteria);

   /**
    * Returns the number of users who have at least 1 user alarm.
    *
    * @return the number of users who have user alarms.
    */
   public int getDistinctNumberOfUsersWithUserAlarms();

   /**
    * Removes user alarms.
    *
    * @param alarmId
    *       The id of the user alarm to remove.
    * @return the number of alarms deleted
    */
   public int deleteAlarms(List<Long> alarmIds);

   /**
    * Removes all user alarms that are associated with a particular request.
    *
    * @param requestId  The request ID
    * @return
    */
   public int deleteAlarmsOfRequest(long id);

   /**
    * Removes all user alarms associated with a particular user.
    *
    * @param userId
    * @return
    */
   public int deleteAlarmsOfUser(String userId);

   /**
    * Removes all user alarms.
    *
    * @return the number of alarms deleted
    */
   public int deleteAllAlarms();

   /**
    * Like {@link #acknowledgeUserAlarm(long)}, but takes a username along with a list of alarm Ids.  The alarms must be owned by
    * the particular user in order to be removed.  All other alarms are not removed.
    *
    * @param username   The username
    * @param alarmIds   The list of alarm ids
    * @return           The total number of alarms acknowledged.
    */
   public int acknowledgeAlarmsForUser(String username, List<Long> alarmIds);

   /**
    * Acknowledges all alarms for a particular user and reference type.
    *
    * @param username      The username
    * @param referenceType The reference type
    */
   public void acknowledgeAllAlarmsForUserAndRequestType(String username, UserAlarmRequestType requestTypes);
}
