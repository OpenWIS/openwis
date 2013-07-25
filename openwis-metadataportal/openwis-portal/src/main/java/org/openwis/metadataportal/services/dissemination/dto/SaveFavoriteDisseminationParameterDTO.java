/**
 * 
 */
package org.openwis.metadataportal.services.dissemination.dto;

import org.openwis.securityservice.OpenWISEmail;
import org.openwis.securityservice.OpenWISFTP;

/**
 * A DTO used to save the changes made by the user to his favorite dissemination parameters. <P>
 * The edition is available on two types of objects : the FTP and the Emails. <P>
 * The user can edit the attributes once at a time.
 * 
 */
public class SaveFavoriteDisseminationParameterDTO {

   /**
    * The FTP to save.
    */
   private OpenWISFTP ftp;

   /**
    * The mail to save.
    */
   private OpenWISEmail mail;

   /**
    * Gets the ftp.
    * @return the ftp.
    */
   public OpenWISFTP getFtp() {
      return ftp;
   }

   /**
    * Sets the ftp.
    * @param ftp the ftp to set.
    */
   public void setFtp(OpenWISFTP ftp) {
      this.ftp = ftp;
   }

   /**
    * Gets the mail.
    * @return the mail.
    */
   public OpenWISEmail getMail() {
      return mail;
   }

   /**
    * Sets the mail.
    * @param mail the mail to set.
    */
   public void setMail(OpenWISEmail mail) {
      this.mail = mail;
   }
}
