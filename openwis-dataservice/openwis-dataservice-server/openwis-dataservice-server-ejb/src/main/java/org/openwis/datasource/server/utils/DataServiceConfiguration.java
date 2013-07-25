/**
 *
 */
package org.openwis.datasource.server.utils;

/**
 * Interface containing configuration keys . <P>
 * The Values are to be retrieved in the JNDI configuration file. <P>
 *
 */
public interface DataServiceConfiguration {

   /** The Constant LOCA_DATA_SOURCE_CONFIGURATION_LOCATION. */
   public final static String LOCA_DATA_SOURCE_CONFIGURATION_LOCATION = "ws/localdatasourceservice";

   /** The Constant LOCAL_DATA_SOURCE_POLLING_ENDS. */
   public final static String LOCAL_DATA_SOURCE_POLLING_ENDS = ".polling";

   /** The Constant STAGING_POST_URI_KEY. */
   public final static String STAGING_POST_URI_KEY = "cache.dir.stagingPost";

   /** The Constant CACHE_URL_KEY. */
   public final static String CACHE_URL_KEY = "cache.service.url";

   /** The Constant CACHE_URL_KEY. */
   public final static String CACHE_INDEX_URL_KEY = "cache.cacheIndex.url";

   /** The Constant MAIL_FROM. */
   public static final String MAIL_FROM = "mail.from";

   /** The Constant MAIL_TRANSPORT_PROTOCOL. */
   public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

   /** The Constant MAIL_SMTP_HOST. */
   public static final String MAIL_SMTP_HOST = "mail.smtp.host";

   /** The Constant MAIL_SMTP_PORT. */
   public static final String MAIL_SMTP_PORT = "mail.smtp.port";

   /** The Constant BLACKLIST_DEFAULT_NB_WARN. */
   public static final String BLACKLIST_DEFAULT_NB_WARN = "blacklist.default.nb.warn";

   /** The Constant BLACKLIST_DEFAULT_NB_BLACKLIST. */
   public static final String BLACKLIST_DEFAULT_NB_BLACKLIST = "blacklist.default.nb.blacklist";

   /** The Constant BLACKLIST_DEFAULT_VOL_WARN. */
   public static final String BLACKLIST_DEFAULT_VOL_WARN = "blacklist.default.vol.warn";

   /** The Constant BLACKLIST_DEFAULT_VOL_BLACKLIST. */
   public static final String BLACKLIST_DEFAULT_VOL_BLACKLIST = "blacklist.default.vol.blacklist";

   /** The Constant DISSEMINATION_STATISTICS_URL. */
   public static final String DISSEMINATION_STATISTICS_URL = "openwis.management.disseminateddatastatistics.wsdl";

   /** The Constant CONTROL_SERVICE_URL. */
   public static final String CONTROL_SERVICE_URL = "openwis.management.controlservice.wsdl";

   /** The Constant SUBSCRIPTION_RECURRENT_RETROPROCESS_INCREMENT. */
   public static final String SUBSCRIPTION_RECURRENT_RETROPROCESS_INCREMENT = "subscription.timer.period";

}
