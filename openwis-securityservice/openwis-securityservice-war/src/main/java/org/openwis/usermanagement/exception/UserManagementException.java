/**
 * 
 */
package org.openwis.usermanagement.exception;


/**
 * User Management Exception. <P>
 * 
 */
public class UserManagementException extends Exception {

   /**
    * Comment for <code>MSG_ERROR</code>
    * @member: MSG_ERROR
    */
   protected static final String MSG_ERROR = "An error occurs during the user management process.";

   /**
    * The message to display.
    * @member: message
    */
   protected String message;

   /**
    * {@inheritDoc}
    * @see java.lang.Throwable#getMessage()
    */
   @Override
   public String getMessage() {
      return message;
   }

}
