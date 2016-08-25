/*
 *
 */
package org.openwis.datasource.server.service.impl;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.openwis.dataservice.common.service.MailSender;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MailSender.
 */
@Remote(MailSender.class)
@Stateless(name = "MailSender")
public class MailSenderImpl implements MailSender {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(MailSenderImpl.class);

   /** The mail session properties. */
   private Properties props;

   /** The session. */
   private Session session;

   /** The executor. */
   private final ExecutorService executor = Executors.newSingleThreadExecutor();

   /**
    * Instantiates a new mail sender.
    */
   public MailSenderImpl() {
      super();
   }

   /**
    * Send mail.
    *
    * @param from the from
    * @param to the to
    * @param subject the subject
    * @param content the content
    */
   @Override
   public void sendMail(String from, String to, String subject, String content) {
      try {
         if ("".equals(ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_HOST))) {
            return;
         }
         
         if (StringUtils.isBlank(to)) {
            return;
         }
         
         final Session mailSession = getSession();
         final Message msg = new MimeMessage(mailSession);
         msg.setFrom(new InternetAddress(from));
         msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
         msg.setSentDate(new Date());
         msg.setSubject(subject);
         // Add content message
         msg.setText(content);
         executor.execute(new Runnable() {
            /**
             * {@inheritDoc}
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               try {
                  Transport.send(msg);
               } catch (MessagingException e) {
                  logger.error("Message not sent", e);
               }
            }
         });
      } catch (Throwable e) {
         logger.error("Could no send the mail", e);
      }
   }

   /**
    * Gets the session.
    *
    * @return the session
    */
   private synchronized Session getSession() {
      if (session == null) {
         session = Session.getDefaultInstance(getProperties());
      }
      return session;
   }

   /**
    * Gets the properties.
    *
    * @return the properties
    */
   private synchronized Properties getProperties() {
      if (props == null) {
         props = new Properties();
         props.put("mail.transport.protocol",
               ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_TRANSPORT_PROTOCOL));
         props.put("mail.smtp.host", ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_HOST));
         props.put("mail.smtp.port", ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_PORT));
         props.put("mail.smtp.auth", "false");
      }
      return props;
   }

}
