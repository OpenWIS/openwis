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
	 * Sets the category of the user alarm
	 *
	 * @param category the category
	 * @return this instance
	 */
	public UserAlarmBuilder category(UserAlarmCategory category) {
		this.userAlarm.setCategory(category);
		return this;
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
	 * @param refType The reference type.
	 * @param refKey The reference key.
	 * @return this instance
	 */
	public UserAlarmBuilder referenceTypeKey(UserAlarmReferenceType refType, long refKey) {
		this.userAlarm.setReferenceType(refType);
		this.userAlarm.setReferenceKey(refKey);
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
