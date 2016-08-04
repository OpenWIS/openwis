/**
 *
 */
package org.openwis.management.service;

/**
 * Message patterns and severity levels for alarms raised by the Security
 * Service components.
 * <ul>
 * <li>When Administrators and Operators fail authentication
 * <li>When any unauthorised access to the system is attempted
 * <li>When the number of registered authenticated users exceeds a defined
 * threshold
 * <li>When the number of active authenticated users exceeds a defined threshold
 * <li>When the number of active anonymous users exceeds a defined threshold
 * <li>When a user requests an extension of privileges
 * </ul>
 */
public interface SecurityAlerts {

   /**
    * When Administrators and Operators fail authentication. <br>
    * Expected message arguments are:
    * <ol>
    * <li>credentials, specifies the user credentials
    * </ol>
    */
   String AUTHENTICATION_FAILED = "securityservice.authenticationFailed";

   /**
    * When any unauthorised access to the system is attempted. <br>
    * Expected message arguments are:
    * <ol>
    * <li>credentials, specifies the user credentials
    * </ol>
    */
   String UNAUTHORIZED_ACCESS_ATTEMPT = "securityservice.unauthorizedAccessAttempt";

   /**
    * When the number of registered authenticated users exceeds a defined
    * threshold. <br>
    * Expected message arguments are:
    * <ol>
    * <li>threshold, configured number of users
    * <li>userCount, current number of users
    * </ol>
    */
   String TOO_MANY_REGISTERED_USERS = "securityservice.tooManyRegisteredUsers";

   /**
    * When the number of active authenticated users exceeds a defined threshold. <br>
    * Expected message arguments are:
    * <ol>
    * <li>threshold, configured number of users
    * <li>userCount, current number of users
    * </ol>
    */
   String TOO_MANY_ACTIVE_USERS = "securityservice.tooManyActiveUsers";

   /**
    * When the number of active anonymous users exceeds a defined threshold. <br>
    * Expected message arguments are:
    * <ol>
    * <li>threshold, configured number of users
    * <li>userCount, current number of users
    * </ol>
    */
   String TOO_MANY_ACTIVE_ANONYMOUS_USERS = "securityservice.tooManyActiveAnonymousUsers";

   /**
    * When a user requests an extension of privileges. <br>
    * Expected message arguments are:
    * <ol>
    * <li>credentials, specifies the user credentials
    * <li>privileges, specifies the requested privileges
    * </ol>
    */
   String USER_PRIVILEGES_EXTENSION_REQUEST = "securityservice.userPrivilegesExtensionRequest";

}