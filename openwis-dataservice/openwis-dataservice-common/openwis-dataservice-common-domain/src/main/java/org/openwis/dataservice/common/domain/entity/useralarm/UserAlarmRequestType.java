package org.openwis.dataservice.common.domain.entity.useralarm;

/**
 * The type of entity this alarm is referring to.  This, along with the reference key will identify the
 * user manipulatable object that this alarm was raised for.
 *
 * @author lmika
 *
 */
public enum UserAlarmRequestType {

	/**
	 * This alarm refers to a request.  The reference key will be the request id.
	 */
	REQUEST,

	/**
	 * This alarm refers to a subscription.  The reference key will be the processed request id.
	 */
	SUBSCRIPTION
}
