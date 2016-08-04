/**
 *
 */
package org.openwis.management.service;

import java.util.List;

import org.openwis.management.entity.FeedingFilter;
import org.openwis.management.entity.IngestionFilter;
import org.openwis.management.entity.ReplicationFilter;

/**
 * Defines the control interface of the management service. <p>
 * Providing support for the following actions.
 * <ol>
 * <li>MSS/FSS Ingestion
 *    <ul>
 *    <li>enable / disable ingestion
 *    <li>define a list of regular expressions to filter on the ID of the metadata to ingest
 *    </ul>
 * <li>MSS/FSS Feeding
 *    <ul>
 *    <li>enable / disable feeding
 *    <li>define a list of regular expressions to filter on the ID of the metadata to feed
 *    <li>reset to a default regular expression
 *    </ul>
 * <li>Cache replication
 *    <ul>
 *    <li>New replicated source
 *    <li>Remove replicated source
 *    <li>Edit replicated source
 *    <li>Active / Suspend a replicated source
 *    </ul>
 * </ol>
 */
public interface ControlService {

   // -------------------------------------------------------------------------
   // Service Status Management
   // -------------------------------------------------------------------------

   /**
    * A convenience method to check whether a given service has the enabled
    * status set.
    * 
    * @param serviceId identifies the service
    * @return <code>true</code> if the ENABLED status is current set for the
    *         service
    */
   boolean isServiceEnabled(ManagedServiceIdentifier serviceId);

   /**
    * Return the status of a service identified by the given name.
    * 
    * @param serviceName identifies the service
    * @return the current status as a result of the call
    */
   String getServiceStatus(ManagedServiceIdentifier serviceId);

   /**
    * Attempts to modify the status of the named service
    * 
    * @param serviceId identifies the service
    * @param serviceStatus, the new status to apply
    */
   boolean setServiceStatus(ManagedServiceIdentifier serviceId, ManagedServiceStatus serviceStatus);

   // -------------------------------------------------------------------------
   // MSS/FSS Ingestion
   // -------------------------------------------------------------------------

   /**
    * Returns the list of available ingestion filters.
    *
    * @return list of available ingestion filters
    */
   List<IngestionFilter> getIngestionFilters();

   /**
    * Create a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @param description
    * @return {@code true} if the list of filters changed as a result of the call
    */
   boolean addIngestionFilter(String regex, String description);

   /**
    * Removes a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @return {@code true} if the list of filters changed as a result of the call
    */
   boolean removeIngestionFilter(String regex);

   // -------------------------------------------------------------------------
   // MSS/FSS Feeding
   // -------------------------------------------------------------------------

   /**
    * Returns the list of available ingestion filters.
    *
    * @return list of available ingestion filters
    */
   List<FeedingFilter> getFeedingFilters();

   /**
    * Create a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @param description
    * @return {@code true} if the list of filters changed as a result of the call
    */
   boolean addFeedingFilter(String regex, String description);

   /**
    * Removes a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @return {@code true} if the list of filters changed as a result of the call
    */
   boolean removeFeedingFilter(String regex);

   /**
    * Resets the list of filters to the default settings.
    *
    * @return list of available ingestion filters
    */
   List<FeedingFilter> resetFeedingFilters();

   // -------------------------------------------------------------------------
   // Cache Replication
   // -------------------------------------------------------------------------

   /**
    * Get the activation status of a given replication filter.
    * 
    * @param source
    * @param regex
    * @return the current status as a result of the call
    */
   boolean getReplicationFilterStatus(final String source, final String regex);

   /**
    * Enable/disable a given replication filter.
    * 
    * @param source
    * @param regex
    * @param enabled the new status
    */
   public boolean setReplicationFilterStatus(final String source, final String regex, boolean status);

   /**
    * Returns the list of available ingestion filters.
    * 
    * @return list of available ingestion filters
    */
   List<ReplicationFilter> getReplicationFilters();

   /**
    * Create a filter on the ID of the metadata to ingest.
    *
    * @param type
    * @param source
    * @param regex
    * @param description
    * @param active
    * @return {@code true} if the list of filters changed as a result of the call
    */
   boolean addReplicationFilter(String type, String source, String regex, String description, boolean active);

   /**
    * Removes a filter on the ID of the metadata to ingest.
    *
    * @param source
    * @param regex
    * @return {@code true} if the list of filters changed as a result of the call
    */
   boolean removeReplicationFilter(String source, String regex);
   
   boolean editReplicationFilter(String oldSource, String oldRegex, String type, String source, String regex, String description, boolean active);
}