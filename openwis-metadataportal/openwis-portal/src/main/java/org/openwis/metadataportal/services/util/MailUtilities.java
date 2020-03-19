package org.openwis.metadataportal.services.util;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

/**
 *  Use AWS SES Api to send mail
 * @author Tupangiu Cosmin
 *
 */
public class MailUtilities {

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
        try {
            InstanceProfileCredentialsProvider provider = InstanceProfileCredentialsProvider.builder().build();
            Region region = Region.of(OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION));
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
            isSendout = true;
        } catch (Exception ex) {
            Log.error(Geonet.UTIL, ex.getMessage());
        }
        return isSendout;
    }

    private MimeMessage createMessage(String subject, String from, String[] to, String content) throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // add subject, from
        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(from));
        for (String destination: to) {
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
        }

        // set body
        MimeMultipart mimeMultipart = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(content, "text/html; charset=UTF-8");
        mimeMultipart.addBodyPart(htmlPart);

        message.setContent(mimeMultipart);
        return message;
    }

}
