package org.openwis.metadataportal.services.request.dto.submit;

import org.openwis.dataservice.DisseminationZipMode;
import org.openwis.dataservice.FtpDiffusion;
import org.openwis.dataservice.MailDiffusion;

/**
 * A DTO to wrap the diffusion (mail or FTP). <P>
 */
public class DiffusionDTO {

   /**
    * The zip Mode.
    */
   private DisseminationZipMode zipMode;

   /**
    * The FTP.
    */
   private FtpDiffusion ftp;

   /**
    * The mail.
    */
   private MailDiffusion mail;

   /**
    * Gets the ftp.
    * @return the ftp.
    */
   public FtpDiffusion getFtp() {
      return ftp;
   }

   /**
    * Sets the ftp.
    * @param ftp the ftp to set.
    */
   public void setFtp(FtpDiffusion ftp) {
      this.ftp = ftp;
   }

   /**
    * Gets the mail.
    * @return the mail.
    */
   public MailDiffusion getMail() {
      return mail;
   }

   /**
    * Sets the mail.
    * @param mail the mail to set.
    */
   public void setMail(MailDiffusion mail) {
      this.mail = mail;
   }

   /**
    * Gets the zipMode.
    * @return the zipMode.
    */
   public DisseminationZipMode getZipMode() {
      return zipMode;
   }

   /**
    * Sets the zipMode.
    * @param zipMode the zipMode to set.
    */
   public void setZipMode(DisseminationZipMode zipMode) {
      this.zipMode = zipMode;
   }

}
