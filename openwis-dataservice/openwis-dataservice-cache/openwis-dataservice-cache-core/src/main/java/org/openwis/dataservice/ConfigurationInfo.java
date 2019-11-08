/**
 *
 */
package org.openwis.dataservice;

/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public interface ConfigurationInfo {

   // -------------------------------------------------------------------------
   // Directory Settings
   // -------------------------------------------------------------------------

   /** The property key mapped to the base directory to scan. */
   String HARNESS_WORKING_DIRECTORY_KEY = "cache.dir.harness.working";
   String HARNESS_INCOMING_DIRECTORY_KEY = "cache.dir.harness.incoming";
   String HARNESS_INGESTING_DIRECTORY_KEY = "cache.dir.harness.ingesting";
   String HARNESS_OUTGOING_DIRECTORY_KEY = "cache.dir.harness.outgoing";

   /** The property key mapped to the target directory for the collected products. */
   String CACHE_DIRECTORY_KEY = "cache.dir.cache";

   String TEMP_DIRECTORY_KEY = "cache.dir.temp";

   /** Identifies the name of the Request message queue. */
   String STAGING_POST_DIRECTORY_KEY = "cache.dir.stagingPost";

   // -------------------------------------------------------------------------
   // Extraction Service Settings
   // -------------------------------------------------------------------------
   String EXTRACTION_SERVICE_URL = "cache.service.url";
   String PROCESSED_REQUEST_SERVICE_URL = "processedRequest.service.url";
   String EXTRACTION_DELEGATE_URL = "extractionDelegate.url";

   // -------------------------------------------------------------------------
   // Collection Service Settings
   // -------------------------------------------------------------------------

   String COLLECTION_TIMER_PERIOD_KEY = "cache.gts.collection.collectionTimer.period";
//   String COLLECTION_TIMER_SERVICE_URL_KEY = "cache.gts.collection.collectionTimerService.url";     XXX - To delete
   String COLLECTION_TIMER_INITIAL_DELAY_KEY = "cache.gts.collection.collectionTimer.initialDelay";

   String SPLITTING_TIMER_PERIOD_KEY = "cache.gts.collection.splittingTimer.period";
   String SPLITTING_TIMER_SERVICE_URL_KEY = "cache.gts.collection.splittingTimerService.url";
   String SPLITTING_TIMER_INITIAL_DELAY_KEY = "cache.gts.collection.splittingTimer.initialDelay";

   String CACHE_MAXIMUM_SIZE = "cache.config.cacheMaximumSize";
   String STAGING_POST_MAXIMUM_SIZE = "cache.config.stagingPostMaximumSize";

   // -------------------------------------------------------------------------
   // Feeding Service Settings
   // -------------------------------------------------------------------------

   String FEEDER_URL_KEY = "cache.feeder.url";

   String FEEDING_TIMER_INITIAL_DELAY_KEY = "cache.gts.feeding.packedFeedingTimer.initialDelay";
   String FEEDING_TIMER_PERIOD_KEY = "cache.gts.feeding.packedFeedingTimer.period";

   String PACKED_FEEDING_TIMER_SERVICE_URL_KEY = "cache.gts.feeding.packedFeedingTimerService.url";

   String MAXIMUM_MESSAGE_COUNT_KEY = "cache.gts.feeding.config.maximumMessageCount";

   String FILE_PACKER_DATABASE_ACCESSOR_URL_KEY = "cache.gts.util.databaseAccessor.url";

   // -------------------------------------------------------------------------
   // Replication Service Settings
   // -------------------------------------------------------------------------

   String REPLICATION_CONFIG_FROM_REPLICATION_FOLDER_KEY = "cache.replication.config.fromReplication.folder";

   String REPLICATION_CONFIG_FOLDER_KEY = "cache.replication.config.folder";

   // -------------------------------------------------------------------------
   // Cache Index Service Settings
   // -------------------------------------------------------------------------
   String CACHE_INDEX_URL_KEY = "cache.cacheIndex.url";

   // -------------------------------------------------------------------------
   // Cache Manager Service Settings
   // -------------------------------------------------------------------------
   String CACHE_MANAGER_URL_KEY = "cache.cacheManager.url";
   String CACHE_MANAGER_HOUSEKEEPING_TIMER_INITIAL_DELAY_KEY = "cache.cacheManager.housekeepingTimer.initialDelay";
   String CACHE_MANAGER_HOUSEKEEPING_TIMER_PERIOD_KEY = "cache.cacheManager.housekeepingTimer.period";
   String CACHE_MANAGER_ALERT_CLEANER_TIMER_INITIAL_DELAY_KEY = "cache.cacheManager.alertCleanerTimer.initialDelay";
   String CACHE_MANAGER_ALERT_CLEANER_TIMER_PERIOD_KEY = "cache.cacheManager.alertCleanerTimer.period";
   String CACHE_MANAGER_ALERT_CLEANER_EXPIRATION_WINDOW_KEY = "cache.cacheManager.alertCleanerTimer.expirationWindow";
   String CACHE_MANAGER_TEMPORARY_DIRECTORY_PURGE_TIMER_INITIAL_DELAY_KEY = "cache.cacheManager.purgeTimer.initialDelay";
   String CACHE_MANAGER_TEMPORARY_DIRECTORY_PURGE_TIMER_PERIOD_KEY = "cache.cacheManager.purgeTimer.period";
   String CACHE_MANAGER_HOUSEKEEPING_EXPIRATION_WINDOW_KEY = "cache.cacheManager.housekeepingTimer.expirationWindow";
   String CACHE_MANAGER_PURGING_EXPIRATION_WINDOW_KEY = "cache.cacheManager.purgingTimer.expirationWindow";

   // -------------------------------------------------------------------------
   // Dissemination Service Settings
   // -------------------------------------------------------------------------
   String DISSEMINATION_TIMER_PERIOD_KEY = "cache.dissemination.disseminationTimer.period";
   String DISSEMINATION_TIMER_INITIAL_DELAY_KEY = "cache.dissemination.disseminationTimer.initialDelay";

   String MAIL_DIFFUSION_THRESHOLD_KEY = "cache.dissemination.threshold.mail";
   String FTP_DIFFUSION_THRESHOLD_KEY = "cache.dissemination.threshold.ftp";

   // -------------------------------------------------------------------------
   // Dissemination Harness Settings
   // -------------------------------------------------------------------------
   String DISSEMINATION_HARNESS_PUBLIC_URL_KEY = "cache.dissemination.disseminationHarness.public.url";
   String DISSEMINATION_HARNESS_RMDCN_URL_KEY = "cache.dissemination.disseminationHarness.rmdcn.url";


   /** The property key mapped to the include matching criteria. */
   String INCLUDE_PATTERNS_KEY = "cache.gts.collection.include.patterns";

   /** The property key mapped to the exclude matching criteria. */
   String EXCLUDE_PATTERNS_KEY = "cache.gts.collection.exclude.patterns";

   String MAX_NUMBER_INCLUDED_UNPACKED_FILES = "cache.gts.collection.include.max";

   String MAIL_SENDER_URL_KEY = "cache.mailSender.url";

   String MAIL_FROM = "mail.from";

   // -------------------------------------------------------------------------
   // Staging Post Settings
   // -------------------------------------------------------------------------
   /** The property key mapped to the staging post purging time (expressed in minutes). */
   String STAGING_POST_PURGE_TIME = "cache.dissemination.stagingPost.purgeTime";

   // -------------------------------------------------------------------------
   // JMS Queue
   // -------------------------------------------------------------------------
   /** Identifies the name of the Request message queue. */
   String REQUEST_QUEUE_NAME = "java:/queue/RequestQueue";
   /** Identifies the name of the Incoming message queue. */
   String INCOMING_DATA_QUEUE_NAME = "java:/queue/IncomingDataQueue";

   /** Identifies the names of the feeding queues. */
   String PACKED_FEEDING_QUEUE_NAME = "java:/queue/PackedFeedingQueue";
   String UNPACKED_FEEDING_QUEUE_NAME = "java:/queue/UnpackedFeedingQueue";

   /** Identifies the name of the Dissemination message queue. */
   String DISSEMINATION_QUEUE_NAME = "java:/queue/DisseminationQueue";

   // -------------------------------------------------------------------------
   // Operator config
   // -------------------------------------------------------------------------
   String NUMBER_OF_CHECKSUM_BYTES_KEY = "cache.config.numberOfChecksumBytes";
   String SENDING_CENTRE_LOCATION_IDENTIFIER = "cache.config.location.sendingCentre";

//   String ORIGINATOR_CHECK_REGULAR_EXPRESSIONS_KEY = "cache.gts.collection.config.originatorCheck";

   // -------------------------------------------------------------------------
   // JNDI Service Locator
   // -------------------------------------------------------------------------
   String DEFAULT_PROVIDER_URL = "jnp://localhost:1199";
   String DEFAULT_URL_PKG_PREFIXES = "org.jnp.interfaces.NamingContextFactory";
   String DEFAULT_INITIAL_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";

   String ALERT_SERVICE_URL_KEY = "alert.service.url";
   String ALERT_SERVICE_PROVIDER_URL_KEY = "alert.service.provider.url";

   String MONITORING_SERVICE_URL_KEY = "monitoring.service.url";
   String MONITORING_SERVICE_PROVIDER_URL_KEY = "monitoring.service.provider.url";

   String STATISTICS_SERVICE_URL_KEY = "statistics.service.url";
   String STATISTICS_SERVICE_PROVIDER_URL_KEY = "statistics.service.provider.url";

   String PRODUCT_METADATA_SERVICE_URL_KEY = "metadata.service.url";
   String PRODUCT_METADATA_SERVICE_PROVIDER_URL_KEY = "metadata.service.provider.url";

   String GTS_CATEGORY_ESSENTIAL_REGEXP = "metadata.gtsCategoryEssentialRegexp";
   String GTS_CATEGORY_ADDITIONAL_REGEXP = "metadata.gtsCategoryAdditionalRegexp";

   String STAGING_POST_URL_KEY = "stagingPost.url";
}
