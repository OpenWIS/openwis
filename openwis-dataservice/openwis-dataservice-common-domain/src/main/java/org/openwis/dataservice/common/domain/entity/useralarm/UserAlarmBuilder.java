package org.openwis.dataservice.common.domain.entity.useralarm;

import java.util.Date;

/**
 * Class which simplifies the process of building a user alarm.
 */
public class UserAlarmBuilder {
	private UserAlarm userAlarm;

	public UserAlarmBuilder(String userId) {
		this.userAlarm = new UserAlarm();
		this.userAlarm.setDateRaised(new Date());
		this.userAlarm.setUserId(userId);
	}

	/**
	 * Sets the message of the user alarm.
	 *
	 * @param message the message
	 * @return this instance
	 */
	public UserAlarmBuilder message(String message) {
		this.userAlarm.setMessage(message);
		return this;
	}

	/**
	 * Sets the reference type and reference key of the user alarm.
	 *
	 * @param reqType The reference type.
	 * @param reqKey The reference key.
	 * @return this instance
	 */
	public UserAlarmBuilder request(UserAlarmRequestType reqType, long processedRequestId, long reqKey) {
		this.userAlarm.setRequestType(reqType);
		this.userAlarm.setProcessedRequestId(processedRequestId);
		this.userAlarm.setRequestId(reqKey);
		return this;
	}

	/**
	 * Returns the built user alarm.
	 *
	 * @return the built user alarm.
	 */
	public UserAlarm getUserAlarm() {
		return this.userAlarm;
	}
}
