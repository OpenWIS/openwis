/**
 * 
 */
package org.openwis.metadataportal.services.login.error;

import javax.servlet.ServletException;

import org.openwis.metadataportal.services.login.LoginConstants;

/**
 * OpenWis Login Exception
 * When an error occurs during login or logout, this exception is dispatched.
 * 
 */
@SuppressWarnings("serial")
public class OpenWisLoginEx extends ServletException {

   /**
    * @member: message
    */
   private String message;

   /**
    * Default constructor.
    * Builds a OpenWisLoginEx.
    */
   public OpenWisLoginEx() {
      message = LoginConstants.ERROR_DURING_LOGIN_PROCESS_MSG;
   }

   /**
    * Default constructor.
    * Builds a OpenWisLoginEx.
    * @param message The message to display.
    */
   public OpenWisLoginEx(String message) {
      this.message = message;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Throwable#getMessage()
    */
   @Override
   public String getMessage() {
      return message;
   }

}
