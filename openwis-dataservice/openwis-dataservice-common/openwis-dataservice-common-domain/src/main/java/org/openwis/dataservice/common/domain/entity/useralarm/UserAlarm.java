package org.openwis.dataservice.common.domain.entity.useralarm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A user alarm.
 */
@Entity
@Table(name = "OPENWIS_USER_ALARM")
@SequenceGenerator(name = "USER_ALARM_GEN", sequenceName = "USER_ALARM_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
   @NamedQuery(name = "UserAlarm.alarmsForUserAndReferenceType", query = "select u from UserAlarm u where u.userId = :userId and u.requestType = :requestType"),
   @NamedQuery(name = "UserAlarm.alarmsForUser", query = "select u from UserAlarm u where u.userId = :userId"),
   @NamedQuery(name = "UserAlarm.distinctUserCount", query = "select count(distinct u.userId) from UserAlarm u"),
   @NamedQuery(name = "UserAlarm.deleteAllUserAlarms", query = "delete from UserAlarm u"),
   @NamedQuery(name = "UserAlarm.deleteUserAlarmsOfRequest", query = "delete from UserAlarm u where u.requestId = :requestId"),
   @NamedQuery(name = "UserAlarm.deleteUserAlarmsOfUser", query = "delete from UserAlarm u where u.userId = :userId")
})
public class UserAlarm {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ALARM_GEN")
   @Column(name = "ALARM_ID")
	private long id;

   @Column(name = "DATE")
   @Temporal(TemporalType.TIMESTAMP)
	private Date dateRaised;

   @Column(name = "USER_ID")
	private String userId;

   @Column(name = "REQ_TYPE", nullable = true)
   @Enumerated(EnumType.STRING)
   private UserAlarmRequestType requestType;

   @Column(name = "REQ_ID")
   private long requestId;

   @Column(name = "PROCESSED_REQ_ID")
   private long processedRequestId;

   @Column(name = "MESSAGE")
	private String message;

	/**
	 * The user alarm ID.
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * The time of the user alarm.
	 */
	public Date getDateRaised() {
		return dateRaised;
	}

	public void setDateRaised(Date dateRaised) {
		this.dateRaised = dateRaised;
	}

	/**
	 * The user ID associated with this user alarm.
	 */
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * The message associated with the user alarm.
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * The type of request.
	 */
	public UserAlarmRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(UserAlarmRequestType referenceType) {
		this.requestType = referenceType;
	}

	/**
	 * The ID of the request.
	 */
	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long referenceKey) {
		this.requestId = referenceKey;
	}

	/**
	 * The processed ID of the request.
	 */
   public long getProcessedRequestId() {
      return processedRequestId;
   }

   public void setProcessedRequestId(long processedRequestId) {
      this.processedRequestId = processedRequestId;
   }
}
