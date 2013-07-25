package org.openwis.management.utils;


/**
 * Message patterns and severity levels for alarms raised by the Metadata Service components.
 * <ul>
 * <li>the loss or corruption of metadata
 * <li>the number of entries in the DAR catalogue exceeds a defined
 * <li>harvested metadata records fail schema validation
 * <li>Harvesting task is complete (information level)
 * <li>Synchronisation task is complete (information level)
 * <li>Volume of data requested by user exceeds limit: , requested data volume:..
 * </ul>
 */
public enum SecurityServiceAlerts {


   /**
    * Notifies that Administrators and Operators fail authentication. <br>
    * Expected message arguments are:
    * <ol>
    * <li>credentials, specifies the user credentials
    * </ol>
    */
   AUTHENTICATION_FAILED ("securityservice.authenticationFailed"),
   
   /**
    * Notifies that any unauthorised access to the system is attempted. <br>
    * Expected message arguments are:
    * <ol>
    * <li>credentials, specifies the user credentials
    * </ol>
    */
   UNAUTHORIZED_ACCESS ("securityservice.unauthorizedAccessAttempt"),


   /**
    * Notifies that the number of registered authenticated users exceeds a defined threshold. <br>
    * Expected message arguments are:
    * <ol>
    * <li>threshold, configured number of users
    * <li>userCount, current number of users
    * </ol>
    */
   TOO_MANY_REGISTERED_USERS ("securityservice.tooManyRegisteredUsers"),

   /**
    * Notifies that number of active anonymous users exceeds a defined threshold. <br>
    * Expected message arguments are:
    * <ol>
    * <li>threshold, configured number of users
    * <li>userCount, current number of users
    * </ol>
    */
   TOO_MANY_ANONYMOUS_USERS ("securityservice.tooManyActiveAnonymousUsers"),
   
   /**
    * Notifies that number of active authenticated users exceeds a defined threshold. <br>
    * Expected message arguments are:
    * <ol>
    * <li>threshold, configured number of users
    * <li>userCount, current number of users
    * </ol>
    */
   TOO_MANY_ACTIVE_USERS ("securityservice.tooManyActiveUsers"),
      
   /**
    * Notifies that a user requests an extension of privileges. <br>
    * Expected message arguments are:
    * <ol>
    * <li>credentials, specifies the user credentials
    * <li>privileges, specifies the requested privileges
    * </ol>
    */
   USER_PRIVILEGES_REQUEST ("securityservice.userPrivilegesExtensionRequest");




   private final String key;

   private SecurityServiceAlerts(String key){
      this.key = key;
   }

   public String getKey(){
      return key;
   }
}