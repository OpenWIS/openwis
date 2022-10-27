package org.openwis.metadataportal.services.util;

import jeeves.utils.Log;
import org.apache.commons.lang3.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jvnet.fastinfoset.FastInfosetException;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.http.apache.ProxyConfiguration.Builder;
import sun.net.www.ParseUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Properties;

/**
 * Use AWS SES Api to send mail
 *
 * @author Tupangiu Cosmin
 */
public class MailUtilities {

    private static final String HTTPS_PROXY = "https_proxy";

    /**
     * The session.
     */
    private Session sessionSmtp;

    /**
     * The mail session properties.
     */
    private Properties propsSmtp;
    /**
     * The logger.
     */
    private static Logger logger = LoggerFactory.getLogger(MailUtilities.class);

    /**
     * DEPRECATED. Use {@link #send(IOpenWISMail)} instead.
     */
    @Deprecated
    public boolean sendMail(String subject, String from, String[] to, String content) {
        boolean isSent = false;
        //check if aws properties are set
        try {
            boolean emailRegion = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION).isEmpty();
        } catch (Exception e) {
            logger.error("Aws properies  should be set at openwis-metadat-portal-properties", e);
        }
        //check if smtp properties are set
        try {
            boolean emailRegion = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST).isEmpty();
        } catch (Exception e) {
            logger.error("Smtp properies  should be set at openwis-metadat-portal-properties", e);
        }

        //use aws send mail
        if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION).isEmpty() &&
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST).isEmpty())
            isSent = this.sendMail0(subject, from, to, content);

        //use of smtp send mail
        if (OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION).isEmpty() &&
                !OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST).isEmpty())
            isSent = this.sendMailSmtp(subject, from, to, content);

        return isSent;
    }

    public boolean send(IOpenWISMail openWISMail) {
        boolean isSent = false;

        //check if aws properties are set
        try {
            boolean emailRegion = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION).isEmpty();
        } catch (Exception e) {
            logger.error("Aws properies  should be set at openwis-metadat-portal-properties", e);
        }
        //check if smtp properties are set
        try {
            boolean emailRegion = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST).isEmpty();
        } catch (Exception e) {
            logger.error("Smtp properies  should be set at openwis-metadat-portal-properties", e);
        }

        //use aws send mail
        if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION).isEmpty() &&
                OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST).isEmpty())
            isSent = this.sendMail0(openWISMail.getSubject(), openWISMail.getAdministratorAddress(), openWISMail.getDestinations(), openWISMail.getBody());

        //use of smtp send mail
        if (OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION).isEmpty() &&
                !OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST).isEmpty())
            isSent = this.sendMailSmtp(openWISMail.getSubject(), openWISMail.getAdministratorAddress(), openWISMail.getDestinations(), openWISMail.getBody());
        return isSent;
    }

    /**
     * Send the mail to the registering user.
     *
     * @param subject The mail subject
     * @param from    The sender
     * @param to      The beneficiaries
     * @param content The mail content
     * @return
     */
    private boolean sendMail0(String subject, String from, String[] to, String content) {
        boolean isSendout = false;
        try {
            InstanceProfileCredentialsProvider provider = InstanceProfileCredentialsProvider.builder().build();
            Region region = Region.of(OpenwisMetadataPortalConfig.getString(ConfigurationConstants.AWS_EMAIL_REGION));

            SesClient client;


            if (System.getenv(HTTPS_PROXY) != null) {

                ProxyConfiguration.Builder proxyConfig =
                        ProxyConfiguration.builder();

                URL url = new URL(System.getenv(HTTPS_PROXY));

                String userInfo = url.getUserInfo();
                if (userInfo != null) { // get the user and password
                    int delimiter = userInfo.indexOf(':');
                    if (delimiter == -1) {
                        proxyConfig.username(ParseUtil.decode(userInfo));
                        Log.debug(Log.SERVICE, "Send mail using proxy User: " + ParseUtil.decode(userInfo));
                    } else {

                        proxyConfig.username(ParseUtil.decode(userInfo.substring(0, delimiter++)));
                        proxyConfig.password(ParseUtil.decode(userInfo.substring(delimiter)));
                        Log.debug(Log.SERVICE, "Send mail using proxy User: " + ParseUtil.decode(userInfo.substring(0, delimiter++)));
                    }
                }

                Log.debug(Log.SERVICE, "Send mail using proxy: " + url.getProtocol() + "://" + url.getHost() + ":" + url.getPort());
                ProxyConfiguration proxyConfiguration = (ProxyConfiguration) proxyConfig.endpoint(new URI(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort())).build();

                ApacheHttpClient.Builder httpClientBuilder =
                        ApacheHttpClient.builder()
                                .proxyConfiguration(proxyConfiguration);

                ClientOverrideConfiguration.Builder overrideConfig =
                        ClientOverrideConfiguration.builder();

                client = SesClient.builder()
                        .httpClientBuilder(httpClientBuilder)
                        .overrideConfiguration(overrideConfig.build())
                        .credentialsProvider(provider).region(region).build();
            } else {
                client = SesClient.builder()
                        .credentialsProvider(provider).region(region).build();
            }

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
        for (String destination : to) {
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


    public boolean sendMailSmtp(String subject, String from, String[] to, String content) {
        boolean isSendout = false;

        Log.info(Geonet.UTIL, "protocol : " + OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_PROTOCOL)
                + " host : " + OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST)
                + " port : " + OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_PORT));

        try {
            //Session mailSession = Session.getDefaultInstance(props);
            final String fromSmtp = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_FROM);
            final Session mailSession = getSessionSmtp();
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(fromSmtp));
            for (int i = 0; i < to.length; i++) {
                if (StringUtils.isNotBlank(to[i])) {
                    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
                }
                //msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to[i]));
            }
            msg.setSentDate(new Date());
            msg.setSubject(subject);
            // Add content message
            msg.setText(content);

            // set body
            MimeMultipart mimeMultipart = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");
            mimeMultipart.addBodyPart(htmlPart);

            msg.setContent(mimeMultipart);
            Transport.send(msg);
            isSendout = true;
        } catch (Exception e) {
            isSendout = false;
            Log.error(Geonet.UTIL, e.getMessage(), e);
        }
        return isSendout;
    }

    /**
     * Gets the session.
     *
     * @return the session
     */
    private synchronized Session getSessionSmtp() {
        if (sessionSmtp == null) {
            if (OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_AUTH).equalsIgnoreCase("false")) {
                sessionSmtp = Session.getDefaultInstance(getProperties());
            } else {
                String username = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_FROM);
                String password = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_PASSWORD);
                sessionSmtp = Session.getInstance(getProperties(), new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
            }
        }
        return sessionSmtp;
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    private synchronized Properties getProperties() {
        if (propsSmtp == null) {
            propsSmtp = System.getProperties();
            if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_STARTTLS).isEmpty())
                propsSmtp.put("mail.smtp.starttls.enable", OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_STARTTLS));

            if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_PROTOCOL).isEmpty())
                propsSmtp.put("mail.transport.protocol", OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_PROTOCOL));

            if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST).isEmpty())
                propsSmtp.put("mail.smtp.host", OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_HOST));

            if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_PORT).isEmpty())
                propsSmtp.put("mail.smtp.port", OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_PORT));

            if (!OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_AUTH).isEmpty())
                propsSmtp.put("mail.smtp.auth", OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SMTP_AUTH));
        }
        return propsSmtp;
    }

}