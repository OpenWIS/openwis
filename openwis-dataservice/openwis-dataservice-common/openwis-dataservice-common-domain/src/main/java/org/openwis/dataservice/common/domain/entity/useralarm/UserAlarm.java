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
   @NamedQuery(name = "UserAlarm.alarmsForUserAndReferenceType", query = "select u from UserAlarm u where u.userId = :userId and referenceType = :referenceType")
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

    @Column(name = "CATEGORY")
    @Enumerated(EnumType.STRING)
	private UserAlarmCategory category;

    @Column(name = "REF_TYPE", nullable = true)
    @Enumerated(EnumType.STRING)
    private UserAlarmReferenceType referenceType;

    @Column(name = "REF_KEY")
    private long referenceKey;

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
	 * The category associated with this user alarm.  This is a general identification on what
	 * the user alarm is.
	 */
	public UserAlarmCategory getCategory() {
		return category;
	}

	public void setCategory(UserAlarmCategory category) {
		this.category = category;
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
	 * The type of user-controllable entity this user alarm refers to.  Can be <code>null</code>,
	 * in which case this alarm does not refer to any user-controllable entity.
	 */
	public UserAlarmReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(UserAlarmReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	/**
	 * The primary key of the user-controllable entity this user alarm refers to.  This is dependent on the UserAlarmReferenceType.
	 */
	public long getReferenceKey() {
		return referenceKey;
	}

	public void setReferenceKey(long referenceKey) {
		this.referenceKey = referenceKey;
	}
}
