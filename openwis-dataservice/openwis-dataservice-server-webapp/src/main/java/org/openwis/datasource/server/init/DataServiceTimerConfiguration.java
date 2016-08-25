/**
 *
 */
package org.openwis.datasource.server.init;

/**
 * Interface containing configuration keys . <P>
 * The Values are to be retrieved in the jndi configuration file. <P>
 *
 */
public interface DataServiceTimerConfiguration {

   /** The Constant EXTRACTION_TIMER_URL_KEY. */
   public final static String EXTRACTION_TIMER_URL_KEY = "extraction.timer.url";

   /** The Constant DISSEMINATION_TIMER_URL_KEY. */
   public final static String DISSEMINATION_TIMER_URL_KEY = "dissemination.timer.url";

   /** The Constant SUBSCRUPTION_TIMER_URL_KEY. */
   public final static String SUBSCRUPTION_TIMER_URL_KEY = "subscription.timer.url";

   /** The Constant EXTRACTION_TIMER_PERIOD_KEY. */
   public final static String EXTRACTION_TIMER_PERIOD_KEY = "extraction.timer.period";

   /** The Constant DISSEMINATION_TIMER_PERIOD_KEY. */
   public final static String DISSEMINATION_TIMER_PERIOD_KEY = "dissemination.timer.period";

   /** The Constant SUBSCRIPTION_TIMER_PERIOD_KEY. */
   public final static String SUBSCRIPTION_TIMER_PERIOD_KEY = "subscription.timer.period";

   /** The Constant STAGING_POST_URI_KEY. */
   public final static String STAGING_POST_URI_KEY = "staging.post.uri";

   /** The Constant LOCAL_DATA_SOURCE_POLLING_URL_KEY. */
   public static final String LOCAL_DATA_SOURCE_POLLING_URL_KEY = "localDataSource.polling.timer.url";

   /** The Constant LOCAL_DATA_SOURCE_POLLING_PERIOD_KEY. */
   public static final String LOCAL_DATA_SOURCE_POLLING_PERIOD_KEY = "localDataSource.polling.timer.period";

}
