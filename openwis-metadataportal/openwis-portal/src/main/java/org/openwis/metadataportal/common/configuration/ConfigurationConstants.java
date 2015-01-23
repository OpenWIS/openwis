/**
 *
 */
package org.openwis.metadataportal.common.configuration;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public interface ConfigurationConstants {

   /**
    * The URL of the WSDL to access the processed request service of the data service.
    */
   String DATASERVICE_PROCESSEDREQUESTSERVICE_WSDL = "openwis.metadataportal.dataservice.processedrequestservice.wsdl";

   /**
    * The URL of the WSDL to access the request service of the data service.
    */
   String DATASERVICE_REQUESTSERVICE_WSDL = "openwis.metadataportal.dataservice.requestservice.wsdl";

   /**
    * The URL of the WSDL to access the subscription service of the data service.
    */
   String DATASERVICE_SUBSCRIPTIONSERVICE_WSDL = "openwis.metadataportal.dataservice.subscriptionservice.wsdl";

   /**
    * The URL of the WSDL to access the product metadata service of the data service.
    */
   String DATASERVICE_PRODUCTMETADATASERVICE_WSDL = "openwis.metadataportal.dataservice.productmetadataservice.wsdl";

   /**
    * The URL of the WSDL to access the cache index service of the data service.
    */
   String DATASERVICE_CACHEINDEXSERVICE_WSDL = "openwis.metadataportal.dataservice.cacheindexservice.wsdl";

  /**
   * The URL of the WSDL to access the cache index service of the data service.
   */
  String DATASERVICE_CONTROLSERVICE_WSDL = "openwis.metadataportal.dataservice.controlservice.wsdl";

   /**
    * The URL of the WSDL to access the AlertService the management IF.
    */
   String MANAGEMENT_ALERTSERVICE_WSDL = "openwis.management.alertservice.wsdl";

   /**
    * The URL of the WSDL to access the ControlService the management IF.
    */
   String MANAGEMENT_CONTROLSERVICE_WSDL = "openwis.management.controlservice.wsdl";

   /**
    * The URL of the WSDL to access the monitoring service the disseminated data statistics IF.
    */
   String MANAGEMENT_DISSEMINATEDDATA_STATISTICS_WSDL = "openwis.management.disseminateddatastatistics.wsdl";

   /**
    * The URL of the WSDL to access the monitoring service the exchanged data statistics IF.
    */
   String MANAGEMENT_EXCHANGEDDATA_STATISTICS_WSDL = "openwis.management.exchangeddatastatistics.statistics.wsdl";

   /**
    * The URL of the WSDL to access the monitoring service the replicated data statistics IF.
    */
   String MANAGEMENT_REPLICATEDDATA_STATISTICS_WSDL = "openwis.management.replicateddatastatistics.wsdl";

   /**
    * The URL of the WSDL to access the monitoring service the replicated data statistics IF.
    */
   String MANAGEMENT_INGESTEDDATA_STATISTICS_WSDL = "openwis.management.ingesteddatastatistics.wsdl";

   /**
    * The URL of the WSDL to access the blacklist service of the data service.
    */
   String DATASERVICE_BLACKLISTSERVICE_WSDL = "openwis.metadataportal.dataservice.blacklistservice.wsdl";

   /**
    * The URL of the WSDL for accessing user security service.
    */
   String SECURITYSERVICE_USERMANAGEMENT_WSDL = "openwis.metadataportal.securityservice.usermanagement.wsdl";

   /**
    * The URL of the WSDL for accessing group security service.
    */
   String SECURITYSERVICE_GROUPMANAGEMENT_WSDL = "openwis.metadataportal.securityservice.groupmanagement.wsdl";

   /**
    * The URL of the WSDL for accessing dissemination parameters management security service.
    */
   String SECURITYSERVICE_DISS_PARAM_MANAGEMENT_WSDL = "openwis.metadataportal.securityservice.dissemparammanagement.wsdl";

   /**
    * The monitoring service of security service.
    */
   String SECURITYSERVICE_MONITORING_SERVICE_WSDL = "openwis.metadataportal.securityservice.monitoringservice.wsdl";

   /**
    * The URL of the WSDL of the MSS/FSS harness.
    */
   String HARNESS_MSSFSS_WSDL = "openwis.metadataportal.harness.mssfss.wsdl";

   /**
    * The URL of the WSDL of the sub selection parameters harness.
    */
   String HARNESS_SUBSELECTIONPARAMETERS_WSDL = "openwis.metadataportal.harness.subselectionparameters.wsdl";

   /**
    * The URL of the WSDL of the user alarm manager.
    */
   String USER_ALARM_SERVICE_WSDL = "openwis.management.useralarmmanager.wsdl";

   /**
    * The URL of the staging post.
    */
   String MSSFSS_SUPPORT = "openwis.metadataportal.mssfss.support";

   /**
    * The URL of SolR search.
    */
   String SOLR_URL = "openwis.metadataportal.solr.url";

   /**
   * The URL of the staging post.
   */
   String URL_STAGING_POST = "openwis.metadataportal.url.staging.post";

   /**
    * The date format.
    */
   String DATE_FORMAT = "openwis.metadataportal.date.format";

   /**
    * The date time format.
    */
   String DATETIME_FORMAT = "openwis.metadataportal.datetime.format";

   /**
    * The deploy name
    */
   String DEPLOY_NAME = "openwis.metadataportal.deploy.name";

   /**
    * The default data policy name
    */
   String DEFAULT_DATA_POLICY_NAME = "openwis.metadataportal.datapolicy.default.name";

   /**
    * Default operations for the default data policy
    */
   String DEFAULT_DATA_POLICY_OPERATIONS = "openwis.metadataportal.datapolicy.default.operations";

   /**
    * Link to the OpenSSO
    */
   String SSO_MANAGEMENT = "openwis.metadataportal.sso";

   /** Is the cache is enable. */
   String CACHE_ENABLE = "openwis.metadataportal.cache.enable";

   /** Max records returned by OAI protocol */
   String OAI_MAX_RECORDS = "openwis.metadataportal.oai.maxRecords";

   /** Accepted list of file extensions (deduced from the metadata and used during file unpacking) */
   String ACCEPTED_FILE_EXTENSIONS = "openwis.metadataportal.acceptedFileExtensions";

   /**
    * URL of user portal used for monitoring.
    */
   String MONITORING_USERPORTAL_URL = "openwis.metadataportal.monitoring.userportal.url";

   /**
    * Warn Limit for monitoring synchro tasks.
    */
   String MONITORING_SYNCHRO_WARN_LIMIT = "openwis.metadataportal.monitoring.synchro.warn.limit";

   /**
    * Warn Limit for monitoring harvesting tasks.
    */
   String MONITORING_HARVEST_WARN_LIMIT = "openwis.metadataportal.monitoring.harvesting.warn.limit";

   /** Extract xpath */
   String EXTRACT_XPATH = "openwis.metadataportal.extract.xpath";

   /** Reg exp for gts category additional */
   String GTS_CATEGORY_ADDITIONAL_REGEXP = "openwis.metadataportal.extract.gtsCategoryAdditionalRegexp";

   /** Reg exp for gts category essential */
   String GTS_CATEGORY_ESSENTIAL_REGEXP = "openwis.metadataportal.extract.gtsCategoryEssentialRegexp";

   /** Reg exp for gts priority */
   String GTS_PRIORITY_REGEXP = "openwis.metadataportal.extract.gtsPriorityRegexp";
   
   /** The pattern applied on URN to determine if FNC Pattern should be ignored */
   String URN_PATTERN_FOR_IGNORED_FNC_PATTERN = "openwis.metadataportal.extract.urnPatternForIgnoredFncPattern";
   
   /** Raise an alarm when the number of entries in the DAR catalogue exceeds a defined threshold */
   String CATALOG_SIZE_ALARM_PERIOD = "openwis.metadataportal.catalogsize.alarm.period";
   String CATALOG_SIZE_ALARM_LIMIT = "openwis.metadataportal.catalogsize.alarm.limit";

   /**
    * Too Many Active Users
    */
   String TOO_MANY_ACTIVE_USERS = "openwis.metadataportal.session.securityservice.tooManyActiveUsers";
   
   /**
    * Too Many Authenticated Users
    */
   String TOO_MANY_ANONYMOUS_USERS = "openwis.metadataportal.securityservice.tooManyActiveAnonymousUsers";

   /**
    * Language list
    */
   String LANGUAGE_LIST = "openwis.metadataportal.lang.list";
   
   
   /**
    * Path for harvesting report folder
    */
   String REPORT_FILE_PATH = "openwis.metadataportal.report.file.path";
   
   /**
    * Prefix of the name of the harvesting file report
    */
   String REPORT_FILE_PREFIX = "openwis.metadataportal.report.file.prefix";

   /**
    * Extension of the harvesting file report
    */
   String REPORT_FILE_EXT = "openwis.metadataportal.report.file.ext";

}
