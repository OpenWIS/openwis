package org.openwis.metadataportal.services.util.mail;

import jeeves.server.context.ServiceContext;

import java.util.Map;

public class OpenWISMailFactory {

    /**
     * Return a new recover account mail to be sent to users
     * @param context service context
     * @param subjectVar subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildRecoverAccountUserMail(ServiceContext context, String subjectVar, String[] destinations, Map<String,Object> contentVars) {
        EmailTemplate emailTemplate = new EmailTemplate(context.getAppPath(), EmailTemplate.RECOVER_ACCOUNT_TEMPLATE);
        return new OpenWISMail(context, subjectVar, destinations, emailTemplate, contentVars);
    }

    /**
     * Return a new recover account mail to be sent to administrator
     * @param context service context
     * @param subjectVar subject property key
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildRecoverAccountAdminMail(ServiceContext context, String subjectVar, Map<String, Object> contentVars) {
        EmailTemplate emailTemplate = new EmailTemplate(context.getAppPath(), EmailTemplate.RECOVER_ADMIN_ACCOUNT_TEMPLATE);
        return new OpenWISMail(context, subjectVar, emailTemplate, contentVars);
    }
}
