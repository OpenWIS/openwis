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
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;

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
@Stateless(name = "SmtpMailSender")
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

         final String fromSmtp = ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_FROM);
         final Session mailSession = getSession();
         final Message msg = new MimeMessage(mailSession);
         msg.setFrom(new InternetAddress(fromSmtp));
         msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
         msg.setSentDate(new Date());
         msg.setSubject(subject);
         // Add content message
         msg.setText(content);

         MimeMultipart mimeMultipart = new MimeMultipart();
         MimeBodyPart htmlPart = new MimeBodyPart();
         htmlPart.setContent(content, "text/html; charset=UTF-8");
         mimeMultipart.addBodyPart(htmlPart);

         msg.setContent(mimeMultipart);
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
         if (ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_AUTH).equalsIgnoreCase("false")) {
            session = Session.getDefaultInstance(getProperties());
         } else {
            String username = ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_FROM);
            String password = ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_PASSWORD);
            session = Session.getInstance(getProperties(), new javax.mail.Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(username, password);
               }
            });
         }
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
         props = System.getProperties();
         if(!ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_STARTTLS_ENABLE).isEmpty())
         props.put("mail.smtp.starttls.enable",ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_STARTTLS_ENABLE));

         if(!ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_TRANSPORT_PROTOCOL).isEmpty())
         props.put("mail.transport.protocol",ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_TRANSPORT_PROTOCOL));

         if(!ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_HOST).isEmpty())
         props.put("mail.smtp.host", ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_HOST));

         if(!ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_PORT).isEmpty())
         props.put("mail.smtp.port", ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_PORT));

         if(!ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_AUTH).isEmpty())
         props.put("mail.smtp.auth", ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.MAIL_SMTP_AUTH));

      }
      return props;
   }

}
