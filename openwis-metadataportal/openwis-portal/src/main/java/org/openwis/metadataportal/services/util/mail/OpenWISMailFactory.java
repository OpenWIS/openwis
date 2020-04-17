package org.openwis.metadataportal.services.util.mail;

import jeeves.server.context.ServiceContext;

import java.util.Map;

public class OpenWISMailFactory {

    /**
     * Return a new recover account mail to be sent to users
     * @param context service context
     * @param subject subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildRecoverAccountUserMail(ServiceContext context, String subject, String[] destinations, Map<String,Object> contentVars) {
        return buildUserMail(context, subject, destinations, EmailTemplate.RECOVER_ACCOUNT_TEMPLATE, contentVars);
    }

    /**
     * Return a new recover account mail to be sent to administrator
     * @param context service context
     * @param subject subject property key
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildRecoverAccountAdminMail(ServiceContext context, String subject, Map<String, Object> contentVars) {
        return buildAdminMail(context, subject, EmailTemplate.RECOVER_ADMIN_ACCOUNT_TEMPLATE, contentVars);
    }

    /**
     * Return a new request account mail
     * @param context service context
     * @param subject subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildRequestAccountUserMail(ServiceContext context, String subject, String[] destinations, Map<String, Object> contentVars) {
        return buildUserMail(context, subject, destinations, EmailTemplate.REQUEST_ACCOUNT_TEMPLATE, contentVars);
    }

    /**
     * Return a new request account mail to be sent to administrator
     * @param context service context
     * @param subject subject property key
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildRequestAccountAdminMail(ServiceContext context, String subject,  Map<String, Object> contentVars) {
        return buildAdminMail(context, subject, EmailTemplate.REQUEST_ADMIN_ACCOUNT_TEMPLATE, contentVars);
    }

    /**
     * Return a new account disabled mail
     * @param context service context
     * @param subject subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildAccountSuspensionMail(ServiceContext context, String subject, String[] destinations, Map<String, Object> contentVars) {
        return buildUserMail(context, subject, destinations, EmailTemplate.ACCOUNT_SUSPENSION_TEMPLATE, contentVars);
    }

    /**
     * Return a new account disabled mail to be sent to admin
     * @param context service context
     * @param subject subject property key
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildAccountSuspensionAdminMail(ServiceContext context, String subject,  Map<String, Object> contentVars) {
        return buildAdminMail(context, subject, EmailTemplate.ACCOUNT_SUSPENSION_TEMPLATE, contentVars);
    }

    /**
     * Return a new account termination mail
     * @param context service context
     * @param subject subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildAccountTerminationMail(ServiceContext context, String subject, String[] destinations, Map<String, Object> contentVars) {
        return buildUserMail(context, subject, destinations, EmailTemplate.ACCOUNT_TERMINATION_TEMPLATE, contentVars);
    }

    /**
     * Return a new account creation mail
     * @param context service context
     * @param subject subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildAccountCreationMail(ServiceContext context, String subject, String[] destinations, Map<String, Object> contentVars) {
        return buildUserMail(context, subject, destinations, EmailTemplate.ACCOUNT_CREATION_TEMPLATE, contentVars);
    }

    /**
     * Return a new password expire notification mail
     * @param context service context
     * @param subject subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildPasswordExpireNotificationMail(ServiceContext context, String subject, String[] destinations, Map<String, Object> contentVars) {
        return buildUserMail(context, subject, destinations, EmailTemplate.PASSWORD_EXPIRE_NOTIFICATION_TEMPLATE, contentVars);
    }

    /**
     * Return a new password expire notification mail
     * @param context service context
     * @param subject subject property key
     * @param destinations destination addresses
     * @param contentVars body data
     * @return OpenWISMail
     */
    public static OpenWISMail buildSubscriptionNotificationMail(ServiceContext context, String subject, String[] destinations, Map<String, Object> contentVars) {
        return buildUserMail(context, subject, destinations, EmailTemplate.SUBSCRIPTION_NOTIFICATION_TEMPLATE, contentVars);
    }

    private static OpenWISMail buildUserMail(ServiceContext context, String subject, String[] destinations, String templateName, Map<String, Object> contentVars) {
        EmailTemplate emailTemplate = new EmailTemplate(context.getAppPath(), templateName);
        return new OpenWISMail(context, subject,destinations, emailTemplate, contentVars);
    }

    private static OpenWISMail buildAdminMail(ServiceContext context, String subject,  String templateName, Map<String, Object> contentVars) {
        EmailTemplate emailTemplate = new EmailTemplate(context.getAppPath(), templateName);
        return new OpenWISMail(context, subject ,emailTemplate, contentVars);
    }
}
