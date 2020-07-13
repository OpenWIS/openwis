package org.openwis.metadataportal.services.util.mail;

import com.sun.identity.shared.locale.Locale;
import jeeves.server.context.ServiceContext;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.services.util.OpenWISMessages;

import java.util.Map;

public class OpenWISMail implements IOpenWISMail {

    private final ServiceContext context;
    /**
     * subject variable name. the subject will be fetched from properties files.
     * If subject variable is not found, the subject variable will be returned as subject
     */
    private String subject;

    /**
     * To whom we sent the email
     */
    private String[] destinations;

    /**
     * Content variables
     */
    private final EmailTemplate emailTemplate;
    private Map<String, Object> contentData;

    public OpenWISMail(ServiceContext context,
                       String subject,
                       String[] destinationAddresses,
                       EmailTemplate bodyTemplate,
                       Map<String, Object> contentData) {
        this.context = context;
        this.subject = subject;
        this.destinations = destinationAddresses;
        this.emailTemplate = bodyTemplate;
        this.contentData = contentData;
    }

    /**
     * Constructor for mails sent to administrator
     *
     * @param context       service context
     * @param subject       subject var
     * @param emailTemplate template use for body content
     * @param contentData   data to generate the body content
     */
    public OpenWISMail(ServiceContext context,
                       String subject,
                       EmailTemplate emailTemplate,
                       Map<String, Object> contentData) {
        this.context = context;
        this.subject = subject;
        this.destinations = new String[]{this.getAdministratorAddress()};
        this.emailTemplate = emailTemplate;
        this.contentData = contentData;
    }

    @Override
    public String getAdministratorAddress() {
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

        String from = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.EMAIL_SENDER_ADDRESS);
        if (from.isEmpty()) {
            SettingManager sm = gc.getSettingManager();
            from = sm.getValue("system/feedback/email");
        }

        return from;
    }

    @Override
    public String getSubject() {
        String thisSite = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME);
        String subject = OpenWISMessages.format(this.subject, context.getLanguage(), thisSite);
        if (hasSubject(subject)) {
            return subject;
        }

        return this.subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getBody() {
        return this.emailTemplate.resolve(Locale.getLocale(this.context.getLanguage()), contentData);
    }

    @Override
    public void setContentData(Map<String, Object> content) {
        this.contentData = content;
    }

    @Override
    public void addContentVariable(String name, Object value) {
        this.contentData.put(name, value);
    }

    @Override
    public String[] getDestinations() {
        return this.destinations;
    }

    @Override
    public void setDestinations(String[] destinations) {
        this.destinations = destinations;
    }

    /**
     * Return true if we were able to find a property key for subject
     *
     * @param subject
     * @return boolean
     */
    private boolean hasSubject(String subject) {
        return !String.format("!%s!", this.subject).equals(subject);
    }
}
