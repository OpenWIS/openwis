/**
 *
 */
package org.openwis.management.utils;

/**
 * Message patterns and severity levels for alarms raised by the Metadata Service components.
 * <ul>
 * <li>the loss or corruption of metadata
 * <li>the number of entries in the DAR catalogue exceeds a defined
 * <li>harvested metadata records fail schema validation
 * <li>Harvesting task is complete (information level)
 * <li>Synchronisation task is complete (information level)
 * <li>Volume of data requested by user exceeds limit: , requested data volume:..
 * </ul>
 */
public enum MetadataServiceAlerts {

   /**
    * Notifies that the loss or corruption of metadata. <br>
    * Expected message arguments are:
    * <ol>
    * <li>metadataUrn, the metadata URN being tested
    * <li>errorMessage, a description of the validation error
    * </ol>
    */
   MDTA_VALIDATION_FAILED ("metadataservice.metadataValidationFails"),

   /**
    * Notifies that the number of entries in the DAR catalogue exceeds a defined <br>
    * Expected message arguments are:
    * <ol>
    * <li>maximumSize, the configured maximum size
    * <li>actualSize, the actual size
    * </ol>
    */
   TOO_MANY_ENTRIES_IN_CATALOGUE ("metadataservice.tooManyEntriesInCatalogue"),
   
   /**
    * Notifies that harvested metadata records fail schema validation <br>
    * Expected message arguments are:
    * <ol>
    * <li>metadataUrn, the metadata URN being tested
    * <li>errorMessage, a description of the validation error
    * </ol>
    */
   MDTA_RECORDS_FAIL_SCHEMA_VALIDATION ("metadataservice.metadataRecordsFailSchemaValidation"),
   
   /**
    * Notifies that Harvesting task is complete (information level) <br>
    * Expected message arguments are:
    * <ol>
    * <li>taskInfo, a description of task
    * </ol>
    */
   HARVESTING_TASK_COMPLETED ("metadataservice.harvestingTaskComplete"),
   
   /**
    * Notifies that Synchronisation task is complete (information level) <br>
    * Expected message arguments are:
    * <ol>
    * <li>taskInfo, a description of task
    * </ol>
    */
   SYNCHRONISATION_TASK ("metadataservice.synchronizationTaskComplete"),
   
   /**
    * Notifies that Volume of data requested by user exceeds limit: , requested data volume:.. <br>
    * Expected message arguments are:
    * <ol>
    * <li>accountInfo, identifies the user account
    * <li>threshold, specifies the configured threshold for the given user
    * <li>requestedVolume, specifies the requested data volume
    * </ol>
    */
   TOO_MANY_DATA_REQUESTED_BY_USER ("metadataservice.tooManyDataRequestedByUser"),

   /**
    * Notifies that Harvesting task is failed (error level) <br>
    * Expected message arguments are:
    * <ol>
    * <li>taskName, harvesting task name
    * <li>taskId, harvesting task id
    * <li>description, error message
    * </ol>
    */
   HARVESTING_TASK_FAILED ("metadataservice.harvestingTaskFail"),

   /**
    * Notifies that Synchronization task is failed (error level) <br>
    * Expected message arguments are:
    * <ol>
    * <li>taskName, harvesting task name
    * <li>taskId, harvesting task id
    * <li>description, error message
    * </ol>
    */
   SYNCHRONISATION_TASK_FAILED ("metadataservice.synchronizationTaskFail");

   private final String key;

   private MetadataServiceAlerts(String key){
      this.key = key;
   }

   public String getKey(){
      return key;
   }
}
