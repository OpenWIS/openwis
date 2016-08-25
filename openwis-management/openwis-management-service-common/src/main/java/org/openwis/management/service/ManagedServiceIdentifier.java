/**
 *
 */
package org.openwis.management.service;

/**
 * Identifies the manageable services.
 * <p>
 * Enumerated item value to use in the
 * {@link ControlService#setServiceStatus(ManagedServiceIdentifier, ManagedServiceStatus)}
 * method.
 */
public enum ManagedServiceIdentifier {
   // collection services
   FEEDING_SERVICE,
   INGESTION_SERVICE,
   REPLICATION_SERVICE,
   // dissemination services
   DISSEMINATION_SERVICE,
   // request services
   REQUEST_SERVICE,
   SUBSCRIPTION_SERVICE,
   PRODUCT_METADATA_SERVICE,
   PROCESSED_REQUEST_SERVICE,
   // extraction services
   CACHE_EXTRACT_SERVICE,
   LOCAL_DATASOURCE_EXTRACT_SERVICE, ;
}
