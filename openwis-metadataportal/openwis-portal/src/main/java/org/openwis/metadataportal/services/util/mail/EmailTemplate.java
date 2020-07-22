package org.openwis.metadataportal.services.util.mail;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.util.Locale;
import java.util.Map;

/**
 * Generates HTML email bodies for different services: recover & request account, subscription service..
 * It uses Thymleaf as template resolver.
 */
public class EmailTemplate {

    /**
     * Template name for recover account email body
     */
    public static final String RECOVER_ACCOUNT_TEMPLATE = "recover-account.html";

    /**
     * Template name for recover account email body. This mail is sent to administrator
     */
    public static final String RECOVER_ADMIN_ACCOUNT_TEMPLATE = "recover-admin-account.html";

    /**
     * Template name for request account email body
     */
    public static final String REQUEST_ACCOUNT_TEMPLATE = "request-account.html";

    /**
     * Template name for request account email body. This mail sent to administrator
     */
    public static final String REQUEST_ADMIN_ACCOUNT_TEMPLATE = "request-admin-account.html";

    // Template for account suspension mail due to multiple failed login
    public static final String ACCOUNT_SUSPENSION_FAILED_LOGIN_TEMPLATE = "account-suspended-failed-login.html";

    // Template for account suspension admin mail due multiple failed login
    public static final String ACCOUNT_SUSPENTION_FAILED_LOGIN_ADMIN_TEMPLATE = "account-admin-suspended-failed-login.html";

    // Template for account suspension due to expired password
    public static final String ACCOUNT_SUSPENTION_PWD_EXPIRED_TEMPLATE = "account-suspended-pwd-expired.html";

    // Template for account suspension admin mail due to expired password
    public static final String ACCOUNT_SUSPENTION_PWD_EXPIRED_ADMIN_TEMPLATE = "account-admin-suspended-pwd-expired.html";

    // Template for account suspension mail due to inactivity
    public static final String ACCOUNT_SUSPENSION_TEMPLATE = "account-suspended.html";

    // Template for account suspension admin mail due to inactivity
    public static final String ACCOUNT_SUSPENTION_ADMIN_TEMPLATE = "account-admin-suspended.html";

    // Template for account inactivity notification
    public static final String ACCOUNT_INACTIVITY_TEMPLATE = "account-inactivity-notification.html";

    // template for account termination
    public static final String ACCOUNT_TERMINATION_TEMPLATE = "account-termination.html";

    // template for account termination
    public static final String ACCOUNT_CREATION_TEMPLATE = "creation-account.html";

    // template for password expire notification
    public static final String PASSWORD_EXPIRE_NOTIFICATION_TEMPLATE = "password-expire-notification.html";

    /**
     * Template for subscription notifications
     */
    public static final String SUBSCRIPTION_NOTIFICATION_TEMPLATE = "subscription-notification.html";

    /**
     * Templates location
     */
    private static final String TEMPLATE_LOCATION = "WEB-INF/templates/";

    /**
     * Template name
     */
    private String templateName;

    /**
     * Path of the deployed app
     */
    private String appPath;

    EmailTemplate(String appPath, String templateName) {
        this.templateName = templateName;
        this.appPath = appPath;
    }

    public String resolve(Locale locale, Map<String, Object> data) {
        TemplateEngine templateEngine = getTemplateEngine();
        Context context = new Context(locale, data);
        return templateEngine.process(this.templateName, context);
    }

    private TemplateEngine getTemplateEngine() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(this.appPath + TEMPLATE_LOCATION);
        resolver.setTemplateMode("HTML5");
        resolver.setSuffix(".html");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return templateEngine;
    }
}
