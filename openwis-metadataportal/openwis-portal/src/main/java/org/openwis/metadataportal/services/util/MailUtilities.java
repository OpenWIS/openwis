/**
 * 
 */
package org.openwis.metadataportal.services.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;

/**
 * Utilities class for send an e-mail. <P>
 * 
 */
public class MailUtilities {

   /**
    * SMTP protocol
    * @member: PROTOCOL
    */
   private static final String PROTOCOL = "smtp";
   
   /**
    * Send the mail to the registering user.
    * 
    * @param host The server host
    * @param port The server port
    * @param subject The mail subject
    * @param from The sender
    * @param to The beneficiaries
    * @param content The mail content
    * @return
    */
   public boolean sendMail(String host, int port, String subject, String from, String[] to, String content) {
      boolean isSendout = false;

      Properties props = new Properties();

      props.put("mail.transport.protocol", PROTOCOL);
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", port);
      props.put("mail.smtp.auth", "false");

      Log.info(Geonet.UTIL, "protocol : " + PROTOCOL + " host : " + host + " port : " + port );

      try {
         Session mailSession = Session.getDefaultInstance(props);
         Message msg = new MimeMessage(mailSession);
         msg.setFrom(new InternetAddress(from));
         for (int i = 0; i < to.length; i++) {
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
         }
         msg.setSentDate(new Date());
         msg.setSubject(subject);
         // Add content message
         msg.setText(content);
         Transport.send(msg);
         isSendout = true;
      } catch (Exception e) {
         isSendout = false;
         Log.error(Geonet.UTIL, e.getMessage(), e);
      }
      return isSendout;
   }

}
