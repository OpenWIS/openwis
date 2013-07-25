/**
 * 
 */
package org.openwis.metadataportal.services.user.dto;

/**
 * The Password DTO.
 * 
 */
public class PasswordDTO {

   /**
    * The user password.
    * @member: password
    */
   private String password;

   /**
    * Gets the password.
    * @return the password.
    */
   public String getPassword() {
      return password;
   }

   /**
    * Sets the password.
    * @param password the password to set.
    */
   public void setPassword(String password) {
      this.password = password;
   }
}
