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

    public static final String RECOVER_ADMIN_ACCOUNT_TEMPLATE = "recover-admin-account.html";

    private static final String TEMPLATE_LOCATION = "WEB-INF/templates/";

    private String templateName;

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
