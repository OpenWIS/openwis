package org.openwis.datasource.server.service.impl;

import org.openwis.dataservice.common.service.MailSender;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import static org.openwis.dataservice.ConfigurationInfo.MAIL_AWS_REGION;

@Remote(MailSender.class)
@Stateless(name = "AwsMailSender")
public class AwsMailSenderImpl implements MailSender {

    private static Logger logger = LoggerFactory.getLogger(AwsMailSenderImpl.class);

    @Override
    public void sendMail(String from, String to, String subject, String content) {

        try {
            InstanceProfileCredentialsProvider provider = InstanceProfileCredentialsProvider.builder().build();
            Region region = Region.of(ConfigServiceFacade.getInstance().getString(MAIL_AWS_REGION));
            SesClient client = SesClient.builder().credentialsProvider(provider).region(region).build();

            MimeMessage message = createMessage(subject, from, to, content);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);

            ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());
            byte[] arr = new byte[buf.remaining()];
            buf.get(arr);
            SdkBytes data = SdkBytes.fromByteArray(arr);

            RawMessage rawMessage = RawMessage.builder()
                    .data(data)
                    .build();

            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

            client.sendRawEmail(rawEmailRequest);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private MimeMessage createMessage(String subject, String from, String to, String content) throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // add subject, from
        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        // set body
        MimeMultipart mimeMultipart = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(content, "text/html; charset=UTF-8");
        mimeMultipart.addBodyPart(htmlPart);

        message.setContent(mimeMultipart);
        return message;
    }

}
