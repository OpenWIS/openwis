/**
 *
 */
package org.openwis.management.service;

/**
 * Message patterns and severity levels for alarms raised by the Metadata Service components.
 * <ul>
 * <li>The loss or corruption of metadata (alarm raised when validation fails)
 * <li>When the number of entries in the DAR catalogue exceeds a defined threshold
 * <li>When harvested metadata records fail schema validation
 * <li>Harvesting/synchronisation task is complete (information level)
 * <li>When an authorised user requests a volume data which exceeds the threshold
 * for the chosen distribution system
 * </ul>
 */
public interface MetadataServiceAlerts {

   /**
    * The loss or corruption of metadata. <br>
    * Expected message arguments are:
    * <ol>
    * <li>metadataUrn, the metadata URN being tested
    * <li>errorMessage, a description of the validation error
    * <ol>
    */
   String METADATA_VALIDATION_FAILS = "metadataservice.metadataValidationFails";

   /**
    * When the number of entries in the DAR catalogue exceeds a defined
    * threshold. <br>
    * Expected message arguments are:
    * <ol>
    * <li>maximumSize, the configured maximum size
    * <li>actualSize, the actual size
    * </ol>
    */
   String TOO_MANY_ENTRIES_IN_CATALOGUE = "metadataservice.tooManyEntriesInCatalogue";

   /**
    * When harvested metadata records fail schema validation. <br>
    * Expected message arguments are:
    * <ol>
    * <li>metadataUrn, the metadata URN being tested
    * <li>errorMessage, a description of the validation error
    * </ol>
    */
   String METADATA_RECORDS_FAIL_SCHEMA_VALIDATION = "metadataservice.metadataRecordsFailSchemaValidation";

   /**
    * Harvesting task is complete (information level). <br>
    * Expected message arguments are:
    * <ol>
    * <li>taskInfo, a description of task
    * </ol>
    */
   String HARVESTING_TASK_COMPLETE = "metadataservice.harvestingTaskComplete";

   /**
    * Synchronisation task is complete (information level). <br>
    * Expected message arguments are:
    * <ol>
    * <li>taskInfo, a description of task
    * </ol>
    */
   String SYNCHRONIZATION_TASK_COMPLETE = "metadataservice.synchronizationTaskComplete";

   /**
    * When an authorised user requests a volume data which exceeds the threshold
    * for the chosen distribution system. <br>
    * Expected message arguments are:
    * <ol>
    * <li>accountInfo, identifies the user account
    * <li>threshold, specifies the configured threshold for the given user
    * <li>requestedVolume, specifies the requested data volume
    * </ol>
    */
   String TOO_MANY_DATA_REQUESTED_BY_USER = "metadataservice.tooManyDataRequestedByUser";

}