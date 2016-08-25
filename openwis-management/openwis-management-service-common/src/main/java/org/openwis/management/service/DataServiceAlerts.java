/**
 *
 */
package org.openwis.management.service;


/**
 * Message patterns and severity levels for alarms raised by the DataService components.
 * <ul>
 * <li>pre-defined thresholds are exceeded
 * <li>The arrival of critical, high priority data
 * <li>The loss or corruption of data
 * <li>The ingestion of products which are greater than 1 GBytes in size
 * <li>The ingestion of data for which no metadata record exists
 * <li>When the size of the data Cache exceeds a defined value
 * <li>When delivery fails (error, warning raised by the dissemination system )
 * <li>When the size of products in one Staging Post exceeds a configurable size
 * <li>Failure to contact a local data source or when extraction fails
 * </ul>
 */
public enum DataServiceAlerts {
	
   /**
    * Notifies that pre-defined thresholds are exceeded. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the context
    * <li>threshold, specifies the pre-defined threshold
    * <li>value, indicates the actual value
    * </ol>
    */
   THRESHOLD_EXCEEDED ("dataservice.thresholdExceeded"),

   /**
    * Notifies the arrival of critical data. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the context
    * <li>metadataUrl, from the received product
    * <li>criticality, specifies the criticality
    * </ol>
    */
//   CRITICAL_DATA_RECEIVED ("dataservice.criticalDataReceived"),

   /**
    * Notifies the arrival of critical data. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the context
    * <li>metadataUrl, from the received product
    * <li>priority, specifies the priority
    * </ol>
    */
   HIGH_PRIORITY_DATA_RECEIVED ("dataservice.highPriorityDataReceived"),

   /**
    * Notifies the loss or corruption of data. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the context
    * <li>filename, from the received product
    * <li>cause, specifies the cause for the loss or corruption
    * </ol>
    */
   CORRUPTED_DATA_RECEIVED ("dataservice.corruptedDataReceived"),

   /**
    * Notifies the ingestion of products which are greater than 1 GBytes in
    * size. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the context
    * <li>metadataUrl, from the received product
    * <li>productSize, specifies the size of the product
    * </ol>
    */
   LARGE_PRODUCT_INGESTION ("dataservice.largeProductIngestion"),

   /**
    * Notifies the ingestion of data for which no metadata record exists. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the context
    * <li>productId, identifies the product
    * <li>productSize, specifies the size of the product
    * </ol>
    */
   NO_METADATA_RECORD_FOUND_FOR_PRODUCT ("dataservice.noMetadataRecordFoundForProduct"),

   /**
    * When the size of the data Cache exceeds a defined value. <br>
    * Expected message arguments are:
    * <ol>
    * <li>maximumSize, configured maximum size
    * <li>actualSize, actual size
    * </ol>
    */
   CACHE_MAXIMUM_SIZE_REACHED ("dataservice.cacheMaximumSizeReached"),

   /**
    * When delivery fails (error, warning raised by the dissemination system ). <br>
    * Expected message arguments are:
    * <ol>
    * <li>user, specifies the destination
    * <li>productInfo, specifies the product(s) to deliver
    * <li>cause, indicate the cause for the failure
    * </ol>
    */
   DELIVERY_FAILS ("dataservice.deliveryFails"),

   /**
    * When the size of products in one Staging Post exceeds a configurable size. <br>
    * Expected message arguments are:
    * <ol>
    * <li>maximumSize, configured maximum size
    * <li>actualSize, actual size
    * </ol>
    */
   STAGING_POST_MAXIMUM_SIZE_REACHED ("dataservice.stagingPostMaximumSizeReached"),

   /**
    * When extraction fails. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the destination
    * <li>productInfo, specifies the product(s) to deliver
    * <li>cause, indicate the cause for the failure
    * </ol>
    */
   EXTRACTION_FAILS ("dataservice.extractionFails"),

   /**
    * Failure to contact a local data source. <br>
    * Expected message arguments are:
    * <ol>
    * <li>source, specifies the destination
    * <li>productInfo, specifies the product(s) to deliver
    * <li>cause, indicate the cause for the failure
    * </ol>
    */
   UNREACHABLE_LOCAL_DATASOURCE ("dataservice.unreachableLocalDataSource"),
   
   /**
    * A specific component cannot operate normally anmyore.
    * Expected message arguments are:
    * <ol>
    * <li>component, specifies the component
    * <li>cause, specifies the cause of the problem
    * </ol>
    */
   SERVICE_DEGRADED("dataservice.componentFailure"),
   
   /**
    * # Notifies that a stopgap metadata has been created
# Expected message arguments are:
# <ol>
# <li>component, specifies the component
# <li>filename
# <li>metadata-urn
# </ol>
    */
   STOPGAP_METADATA_CREATED("dataservice.stopgapMetadataCreated");
   
   private final String key;
	
   private DataServiceAlerts(String key){
		this.key = key;
   }
   
   public String getKey(){
	   return key;
   }
}