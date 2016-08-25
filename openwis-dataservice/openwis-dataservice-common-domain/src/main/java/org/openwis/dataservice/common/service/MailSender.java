package org.openwis.dataservice.common.service;

/**
 * The Interface MailSender.
 */
public interface MailSender {

   /**
    * Send mail.
    *
    * @param from the from
    * @param to the to
    * @param subject the subject
    * @param content the content
    */
   void sendMail(String from, String to, String subject, String content);

}
