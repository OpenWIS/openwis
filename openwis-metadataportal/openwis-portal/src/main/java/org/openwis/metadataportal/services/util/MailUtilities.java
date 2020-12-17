package org.openwis.metadataportal.services.util;

import jeeves.utils.Log;
import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.services.util.mail.IOpenWISMail;
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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 *  Use AWS SES Api to send mail
 * @author Tupangiu Cosmin
 *
 */
public class MailUtilities {

    private static final String HTTPS_PROXY = "https_proxy";
    /**
     * DEPRECATED. Use {@link #send(IOpenWISMail)} instead.
     */
    @Deprecated
    public boolean sendMail(String subject, String from, String[] to, String content) {
        return this.sendMail0(subject, from, to, content);
    }

    public boolean send(IOpenWISMail openWISMail) {
        return this.sendMail0(openWISMail.getSubject(), openWISMail.getAdministratorAddress(), openWISMail.getDestinations(), openWISMail.getBody());
    }
    /**
     * Send the mail to the registering user.
     *
     * @param subject The mail subject
     * @param from The sender
     * @param to The beneficiaries
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

                URL url=new URL(System.getenv(HTTPS_PROXY));

                String userInfo = url.getUserInfo();
                if (userInfo != null) { // get the user and password
                    int delimiter = userInfo.indexOf(':');
                    if (delimiter == -1) {
                        proxyConfig.username(ParseUtil.decode(userInfo));
                        Log.debug(Log.SERVICE, "Send mail using proxy: "+ParseUtil.decode(userInfo));
                    } else {

                        proxyConfig.username(ParseUtil.decode(userInfo.substring(0, delimiter++)));
                        proxyConfig.password(ParseUtil.decode(userInfo.substring(delimiter)));
                        Log.debug(Log.SERVICE, "Send mail using proxy: "+ParseUtil.decode(userInfo.substring(0, delimiter++)));
                    }
                }

                Log.debug(Log.SERVICE, "Send mail using proxy: " + url.getProtocol()+"://"+url.getHost()+":"+url.getPort());
                ProxyConfiguration proxyConfiguration = (ProxyConfiguration) proxyConfig.endpoint(new URI(url.getProtocol()+"://"+url.getHost()+":"+url.getPort())).build();

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