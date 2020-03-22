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
    private final String subjectVariable;

    /**
     * To whom we sent the email
     */
    private final String[] destinationAddresses;

    /**
     * Content variables
     */
    private final EmailTemplate emailTemplate;
    private final Map<String, Object> contentData;

    public OpenWISMail(ServiceContext context,
                       String subjectVariable,
                       String[] destinationAddresses,
                       EmailTemplate bodyTemplate,
                       Map<String,Object> contentData) {
        this.context = context;
        this.subjectVariable = subjectVariable;
        this.destinationAddresses = destinationAddresses;
        this.emailTemplate = bodyTemplate;
        this.contentData = contentData;
    }

    /**
     * Constructor for mails sent to administrator
     * @param context service context
     * @param subjectVariable subject variable
     * @param emailTemplate template use for body content
     * @param contentData data to generate the body content
     */
    public OpenWISMail(ServiceContext context,
                       String subjectVariable,
                       EmailTemplate emailTemplate,
                       Map<String,Object> contentData) {
        this.context = context;
        this.subjectVariable = subjectVariable;
        this.destinationAddresses = new String[]{this.getAdministratorAddress()};
        this.emailTemplate = emailTemplate;
        this.contentData = contentData;
    }

    @Override
    public String getAdministratorAddress() {
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        SettingManager sm = gc.getSettingManager();

        String from =  System.getProperty("openwis.mail.senderAddress");
        if (from == null)
            from=sm.getValue("system/feedback/email");

        return from;
    }

    @Override
    public String getSubject() {
        String thisSite = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME);
        String subject = OpenWISMessages.format(this.subjectVariable, context.getLanguage(), thisSite);
        if (hasSubject(subject)) {
            return subject;
        }

        return this.subjectVariable;
    }

    @Override
    public String getBody() {
        return this.emailTemplate.resolve(Locale.getLocale(this.context.getLanguage()), contentData);
    }

    @Override
    public String[] getDestinations() {
        return this.destinationAddresses;
    }

    /**
     * Return true if we were able to find a property key for subject
     * @param subject
     * @return boolean
     */
    private boolean hasSubject(String subject) {
        return !String.format("!%s!", this.subjectVariable).equals(subject);
    }
}
