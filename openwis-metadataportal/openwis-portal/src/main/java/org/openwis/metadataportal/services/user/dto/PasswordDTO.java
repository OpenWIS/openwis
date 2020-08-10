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
    * User's old password
    */
   private String oldPassword;

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

   public String getOldPassword() {
      return oldPassword;
   }

   public void setOldPassword(String oldPassword) {
      this.oldPassword = oldPassword;
   }
}
