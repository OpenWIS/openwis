package org.openwis.datasource.server.service.impl;

import org.openwis.dataservice.common.service.MailSender;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
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
import java.net.URI;
import java.net.URL;
import sun.net.www.ParseUtil;
import java.nio.ByteBuffer;
import java.util.Properties;

import static org.openwis.dataservice.ConfigurationInfo.MAIL_AWS_REGION;
import software.amazon.awssdk.http.apache.ProxyConfiguration;

@Remote(MailSender.class)
@Stateless(name = "AwsMailSender")
public class AwsMailSenderImpl implements MailSender {

    private static Logger logger = LoggerFactory.getLogger(AwsMailSenderImpl.class);

    private static final String HTTPS_PROXY = "https_proxy";
    @Override
    public void sendMail(String from, String to, String subject, String content) {

        try {
            InstanceProfileCredentialsProvider provider = InstanceProfileCredentialsProvider.builder().build();
            Region region = Region.of(ConfigServiceFacade.getInstance().getString(MAIL_AWS_REGION));

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
                        logger.debug("Send mail using proxy user: "+ParseUtil.decode(userInfo));
                    } else {
                        proxyConfig.username(ParseUtil.decode(userInfo.substring(0, delimiter++)));
                        proxyConfig.password(ParseUtil.decode(userInfo.substring(delimiter)));
                        logger.debug("Send mail using proxy user: "+ParseUtil.decode(userInfo.substring(0, delimiter++)));
                    }
                }

                logger.debug("Send mail using proxy: " + url.getProtocol()+"://"+url.getHost()+":"+url.getPort());
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
