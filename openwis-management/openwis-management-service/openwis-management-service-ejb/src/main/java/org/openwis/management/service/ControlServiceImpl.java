/**
 *
 */
package org.openwis.management.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openwis.management.entity.FeedingFilter;
import org.openwis.management.entity.IngestionFilter;
import org.openwis.management.entity.ReplicationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the web service exposing the public interface of the {@link ControlService}.
 */
@WebService(name = "ControlService", portName = "ControlServicePort", serviceName = "ControlService", targetNamespace = "http://control.management.openwis.org/")
@SOAPBinding(use = SOAPBinding.Use.LITERAL, style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(ControlService.class)
@Stateless(name = "ControlService")
public class ControlServiceImpl implements ControlService {

   private static final String DEFAULT_FEEDING_FILTER_LOCATION = "openwis.management.controlservice.defaultFeedingFilterLocation";

   private static final String REPLICATION_STATUS_FOLDER = "cache.replication.config.folder";

   private static final String DATASERVICE_SERVICE_STATUS_FOLDER = "dataservice.service.status.folder";

   private static final String REPLICATION_STATUS_FILE_NAME = "replication-status";

   private static final String FILE_STATUS_ENABLED = ".enabled";

   private static final String FILE_STATUS_DISABLED = ".disabled";

   // Timeout after which we consider the replication process is dead : 1 minute
   private static final long KEEP_ALIVE_TIMEOUT = 60000;

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ControlServiceImpl.class);

   @PersistenceContext
   private EntityManager entityManager;
   
   @EJB
   private ConfigService configService;

   // -------------------------------------------------------------------------
   // DataService Management
   // -------------------------------------------------------------------------

   /**
    * A convenience method to check whether a given service has the enabled
    * status set.
    *
    * @param serviceId identifies the service
    * @return <code>true</code> if the ENABLED status is current set for the
    *         service
    */
   @WebMethod(operationName = "isServiceEnabled")
   public boolean isServiceEnabled(
         @WebParam(name = "serviceId") final ManagedServiceIdentifier serviceId) {

      String serviceStatus = getServiceStatus(serviceId);
      boolean enabled = ManagedServiceStatus.ENABLED.name().equals(serviceStatus);
      return enabled;
   }

   /**
    * Return the status of a service identified by the given identifier.
    *
    * @param serviceId identifies the service
    * @param status, the new status to apply
    * @return the current status as a result of the call
    */
   @WebMethod(operationName = "getServiceStatus")
   public String getServiceStatus(
         @WebParam(name = "serviceId") final ManagedServiceIdentifier serviceId) {

      // fail fast
      if (serviceId == null) {
         return null;
      }

      // Perform Replication service status check
      if (ManagedServiceIdentifier.REPLICATION_SERVICE.equals(serviceId)) {
         return checkReplicationServiceStatus();
      } else {
         return checkServiceStatusWithFile(serviceId);
      }
   }

   /**
    * Check file status for the replication service.
    * @return
    */
   private String checkServiceStatusWithFile(ManagedServiceIdentifier serviceId) {

      String path = configService.getString(DATASERVICE_SERVICE_STATUS_FOLDER);
      File fEnabled = new File(path, serviceId.name() + FILE_STATUS_ENABLED);
      File fDisabled = new File(path, serviceId.name() + FILE_STATUS_DISABLED);
      try {
         if (fEnabled.exists()) {
            return ManagedServiceStatus.ENABLED.name();
         } else if (fDisabled.exists()) {
            return ManagedServiceStatus.DISABLED.name();
         } else {
            // File not found. We will try to create a file with enabled status
            try {
               if (fEnabled.createNewFile()) {
                  return ManagedServiceStatus.ENABLED.name();
               } else {
                  return ManagedServiceStatus.UNKNOWN.name();
               }
            } catch (Exception e) {
               logger.error("Cannot create status file " + fEnabled + ": " + e.getMessage(), e);
               return ManagedServiceStatus.UNKNOWN.name();
            }
         }
      } catch (SecurityException e) {
         // If a security manager exists and its SecurityManager.checkRead(java.lang.String) method denies 
         // read access to the file or directory
         logger.error("Access denied to " + path, e);
         return ManagedServiceStatus.UNKNOWN.name();
      }
   }

   /**
    * Check file status for the replication service.
    * @return
    */
   private String checkReplicationServiceStatus() {

      String path = configService.getString(REPLICATION_STATUS_FOLDER);
      File fEnabled = new File(path, REPLICATION_STATUS_FILE_NAME + FILE_STATUS_ENABLED);
      File fDisabled = new File(path, REPLICATION_STATUS_FILE_NAME + FILE_STATUS_DISABLED);
      try {
         if (fEnabled.exists()) {
            return checkKeepAliveStatus(fEnabled, ManagedServiceStatus.ENABLED);
         } else if (fDisabled.exists()) {
            return checkKeepAliveStatus(fDisabled, ManagedServiceStatus.DISABLED);
         } else {
            // File not found
            return ManagedServiceStatus.UNKNOWN.name();
         }
      } catch (SecurityException e) {
         // If a security manager exists and its SecurityManager.checkRead(java.lang.String) method denies 
         // read access to the file or directory
         logger.error("Access denied to " + path, e);
         return ManagedServiceStatus.UNKNOWN.name();
      }
   }

   /**
    * Returns currentStatus value should the process keep alive is still on. Otherwise returns ManagedServiceStatus.UNKNOWN
    * @param f
    * @param currentStatus
    * @return
    */
   private String checkKeepAliveStatus(File f, ManagedServiceStatus currentStatus) {
      long now = System.currentTimeMillis();

      if (now - f.lastModified() > KEEP_ALIVE_TIMEOUT) {
         return ManagedServiceStatus.DEGRADED.name();
      }
      return currentStatus.name();
   }

   /**
    * Attempts to modify the status of the named service
    *
    * @param serviceId identifies the service
    * @param status, the new status to apply
    * @return the current status as a result of the call
    */
   @WebMethod(operationName = "setServiceStatus")
   public boolean setServiceStatus(
         @WebParam(name = "serviceId") final ManagedServiceIdentifier serviceId,
         @WebParam(name = "serviceStatus") final ManagedServiceStatus serviceStatus) {

      boolean wasSuccess = false;

      // fail fast
      if (serviceId == null || serviceStatus == null) {
         return wasSuccess;
      }

      // if the target service is the replication service
      // trigger a direct notification to replication process
      if (ManagedServiceIdentifier.REPLICATION_SERVICE.equals(serviceId)) {
         return updateReplicationServiceStatus(serviceStatus);
      } else {
         return updateServiceStatus(serviceId, serviceStatus);
      }
   }

   // -------------------------------------------------------------------------
   // MSS/FSS Ingestion
   // -------------------------------------------------------------------------

   /**
    * Returns the list of available ingestion filters.
    *
    * @return list of available ingestion filters
    */
   @WebMethod(operationName = "getIngestionFilters")
   public List<IngestionFilter> getIngestionFilters() {
      Query query = entityManager.createQuery("SELECT if FROM IngestionFilter if");
      @SuppressWarnings("unchecked")
      List<IngestionFilter> ingestionFilterList = (List<IngestionFilter>) query.getResultList();
      return ingestionFilterList;
   }

   /**
    * Create a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @param description
    * @return {@code true} if the list of filters changed as a result of the call
    */
   @WebMethod(operationName = "addIngestionFilter")
   public boolean addIngestionFilter(@WebParam(name = "regex") final String regex,
         @WebParam(name = "description") final String description) {
      IngestionFilter newFilter = new IngestionFilter();
      newFilter.setDescription(description);
      newFilter.setRegex(regex);

      try {
         entityManager.persist(newFilter);
      } catch (Exception e) {
         return false;
      }
      entityManager.flush();
      return true;
   }

   /**
    * Removes a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @return {@code true} if the list of filters changed as a result of the call
    */
   @WebMethod(operationName = "removeIngestionFilter")
   public boolean removeIngestionFilter(@WebParam(name = "regex") final String regex) {
      Query query = entityManager.createQuery("DELETE FROM IngestionFilter if WHERE if.regex = '"
            + regex + "'");
      int removed = query.executeUpdate();

      return (removed == 1);
   }

   // -------------------------------------------------------------------------
   // MSS/FSS Feeding
   // -------------------------------------------------------------------------

   /**
    * Returns the list of available ingestion filters.
    *
    * @return list of available ingestion filters
    */
   @WebMethod(operationName = "getFeedingFilters")
   public List<FeedingFilter> getFeedingFilters() {
      Query query = entityManager.createQuery("SELECT ff FROM FeedingFilter ff");
      @SuppressWarnings("unchecked")
      List<FeedingFilter> feedingFilterList = (List<FeedingFilter>) query.getResultList();
      return feedingFilterList;
   }

   /**
    * Create a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @param description
    * @return {@code true} if the list of filters changed as a result of the call
    */
   @WebMethod(operationName = "addFeedingFilter")
   public boolean addFeedingFilter(@WebParam(name = "regex") final String regex,
         @WebParam(name = "description") final String description) {
      FeedingFilter newFilter = new FeedingFilter();
      newFilter.setDescription(description);
      newFilter.setRegex(regex);

      try {
         entityManager.persist(newFilter);
      } catch (Exception e) {
         return false;
      }
      entityManager.flush();
      return true;
   }

   /**
    * Removes a filter on the ID of the metadata to ingest.
    *
    * @param regex
    * @return {@code true} if the list of filters changed as a result of the call
    */
   @WebMethod(operationName = "removeFeedingFilter")
   public boolean removeFeedingFilter(@WebParam(name = "regex") final String regex) {
      Query query = entityManager.createQuery("DELETE FROM FeedingFilter ff WHERE ff.regex = '"
            + regex + "'");
      int removed = query.executeUpdate();

      return (removed == 1);
   }

   /**
    * Resets the list of filters to the default settings.
    *
    * @return list of available ingestion filters
    */
   @WebMethod(operationName = "resetFeedingFilters")
   public List<FeedingFilter> resetFeedingFilters() {
      // remove all current feeding filters
      Query query = entityManager.createQuery("DELETE FROM FeedingFilter ff");
      query.executeUpdate();

      // create default feeding filters
      String defaultFeedingFilterLocation = configService.getString(DEFAULT_FEEDING_FILTER_LOCATION);
      File feedingFilterFile = new File(defaultFeedingFilterLocation);
      FileReader fileReader = null;
      BufferedReader br = null;

      List<FeedingFilter> filterList = new ArrayList<FeedingFilter>();
      String regExp = null;

      try {
         fileReader = new FileReader(feedingFilterFile);
         br = new BufferedReader(fileReader);

         while ((regExp = br.readLine()) != null) {
            FeedingFilter filter = new FeedingFilter();
            filter.setDescription("DEFAULT FEEDING FILTER");
            filter.setRegex(regExp);

            filterList.add(filter);
         }
      } catch (IOException e) {
         logger.error(e.getMessage(), e);
      } finally {
         try {
            if (br != null)
               br.close();
            if (fileReader != null)
               fileReader.close();
         } catch (IOException e) {
         }
      }

      // persist default feeding filter
      for (FeedingFilter filter : filterList) {
         entityManager.persist(filter);
      }

      return filterList;
   }

   // -------------------------------------------------------------------------
   // Cache Replication
   // -------------------------------------------------------------------------

   /**
    * Get the activation status of a given replication filter.
    * @param enabled
    * @param type
    * @param source
    * @return the current status as a result of the call
    */
   @WebMethod(operationName = "getReplicationFilterStatus")
   public boolean getReplicationFilterStatus(@WebParam(name = "source") final String source,
         @WebParam(name = "regex") final String regex) {
      // default value
      boolean status = false;

      // query entity
      try {
         Query query = entityManager
               .createQuery("SELECT rf FROM ReplicationFilter rf WHERE rf.source = '" + source
                     + "' AND rf.regex = '" + regex + "')");

         ReplicationFilter filter = (ReplicationFilter) query.getSingleResult();
         status = filter.isActive();

      } catch (NoResultException e) {
         return false;
      } finally {
      }

      // feedback
      return status;

   }

   /**
    * Enable/disable a given replication filter.
    *
    * @return the current status as a result of the call
    */
   @WebMethod(operationName = "setReplicationFilterStatus")
   public boolean setReplicationFilterStatus(@WebParam(name = "source") final String source,
         @WebParam(name = "regex") final String regex,
         @WebParam(name = "status") final boolean status) {
      boolean wasSuccess = false;

      Query query = entityManager
            .createQuery("SELECT rf FROM ReplicationFilter rf WHERE rf.source = '" + source
                  + "' AND rf.regex = '" + regex + "'");

      ReplicationFilter filter = null;
      try {
         filter = (ReplicationFilter) query.getSingleResult();
         boolean oldStatus = filter.isActive();
         filter.setActive(status);

         if (status == true && oldStatus == false) {
            filter.setUptime(new Date(System.currentTimeMillis()));
         }
         entityManager.merge(filter);
         entityManager.flush();
         wasSuccess = true;
      } catch (Exception e) {
      } finally {
      }

      return wasSuccess;
   }

   /**
    * Returns the list of available ingestion filters.
    *
    * @return list of available ingestion filters
    */
   @WebMethod(operationName = "getReplicationFilters")
   public List<ReplicationFilter> getReplicationFilters() {
      Query query = entityManager.createQuery("SELECT rf FROM ReplicationFilter rf");
      @SuppressWarnings("unchecked")
      List<ReplicationFilter> replicationFilterList = (List<ReplicationFilter>) query
            .getResultList();
      return replicationFilterList;
   }

   /**
    * Create a filter on the ID of the metadata to ingest.
    *
    * @param type
    * @param source
    * @return {@code true} if the list of filters changed as a result of the call
    */
   @WebMethod(operationName = "addReplicationFilter")
   public boolean addReplicationFilter(@WebParam(name = "type") final String type,
         @WebParam(name = "source") final String source,
         @WebParam(name = "regex") final String regex,
         @WebParam(name = "description") final String description,
         @WebParam(name = "active") final boolean active) {
      if (isDuplicateReplicationFilter(source, regex)) {
         return false;
      }

      ReplicationFilter newFilter = new ReplicationFilter();
      newFilter.setActive(active);
      newFilter.setDescription(description);
      newFilter.setRegex(regex);
      newFilter.setSource(source);
      newFilter.setType("OpenWIS");
      if (active == true) {
         newFilter.setUptime(new Date(System.currentTimeMillis()));
      }

      try {
         entityManager.persist(newFilter);
      } catch (Exception e) {
         return false;
      }
      entityManager.flush();

      return true;
   }

   @Override
   @WebMethod(operationName = "editReplicationFilter")
   public boolean editReplicationFilter(@WebParam(name = "oldSource") final String oldSource,
         @WebParam(name = "oldRegex") final String oldRegex,
         @WebParam(name = "type") final String type,
         @WebParam(name = "source") final String source,
         @WebParam(name = "regex") final String regex,
         @WebParam(name = "description") final String description,
         @WebParam(name = "active") final boolean active) {
      if (isDuplicateReplicationFilter(source, regex)) {
         return false;
      }

      ReplicationFilter oldFilter = null;

      try {
         Query oldFilterQuery = entityManager
               .createQuery("SELECT rf FROM ReplicationFilter rf WHERE rf.source = '" + oldSource
                     + "' AND rf.regex = '" + oldRegex + "'");
         oldFilter = (ReplicationFilter) oldFilterQuery.getSingleResult();
      } catch (NoResultException e) {
         return false;
      } finally {
      }

      oldFilter.setActive(active);
      oldFilter.setDescription(description);
      oldFilter.setRegex(regex);
      oldFilter.setSource(source);
      oldFilter.setType("OpenWIS");
      if (active == true) {
         oldFilter.setUptime(new Date(System.currentTimeMillis()));
      }

      try {
         entityManager.merge(oldFilter);
         entityManager.flush();
      } catch (Exception e) {
         return false;
      } finally {
      }

      return true;
   }

   private boolean isDuplicateReplicationFilter(String source, String regex) {
      boolean isDuplicate = false;

      try {
         Query isDuplicateQuery = entityManager
               .createQuery("SELECT COUNT(rf) FROM ReplicationFilter rf WHERE rf.source = '"
                     + source + "' AND rf.regex = '" + regex + "'");
         isDuplicate = ((Long) isDuplicateQuery.getSingleResult()) > 0;
      } catch (NoResultException e) {
         isDuplicate = false;
      } finally {
      }

      return isDuplicate;
   }

   /**
       * Removes a filter on the ID of the metadata to ingest.
       *
       * @param type
       * @param source
       * @return {@code true} if the list of filters changed as a result of the call
       */
   @WebMethod(operationName = "removeReplicationFilter")
   public boolean removeReplicationFilter(@WebParam(name = "source") final String source,
         @WebParam(name = "regex") final String regex) {
      Query query = entityManager
            .createQuery("DELETE FROM ReplicationFilter rf WHERE rf.source = '" + source
                  + "' AND rf.regex = '" + regex + "'");
      int removed = query.executeUpdate();

      return (removed == 1);
   }

   /**
    * Description goes here.
    *
    * @param oldStatus
    * @param newStatus
    */
   private boolean updateReplicationServiceStatus(ManagedServiceStatus status) {
      // check arguments
      if (status == null) {
         return false;
      }

      String path = configService.getString(REPLICATION_STATUS_FOLDER);
      
      File fEnable = new File(path, REPLICATION_STATUS_FILE_NAME + FILE_STATUS_ENABLED);
      File fDisable = new File(path, REPLICATION_STATUS_FILE_NAME + FILE_STATUS_DISABLED);
      try {
         if (fEnable.exists() && ManagedServiceStatus.DISABLED.equals(status)) {
            fEnable.renameTo(fDisable);
            return true;
         } else if (fDisable.exists() && ManagedServiceStatus.ENABLED.equals(status)) {
            fDisable.renameTo(fEnable);
            return true;
         }
      } catch (Exception e) {
         logger.warn("Unable to update replication status file", e);
      }
      return false;
   }

   /**
    * Description goes here.
    *
    * @param oldStatus
    * @param newStatus
    */
   private boolean updateServiceStatus(ManagedServiceIdentifier serviceId,
         ManagedServiceStatus status) {
      // check arguments
      if (status == null) {
         return false;
      }

      String path = configService.getString(DATASERVICE_SERVICE_STATUS_FOLDER);
      File fEnabled = new File(path, serviceId.name() + FILE_STATUS_ENABLED);
      File fDisabled = new File(path, serviceId.name() + FILE_STATUS_DISABLED);

      if (!fEnabled.exists() && !fDisabled.exists()) {
         // File not found. We will try to create a file with enabled status
         try {
            if (!fEnabled.createNewFile()) {
               return false;
            }
         } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
         }
      }

      try {
         if (fEnabled.exists() && ManagedServiceStatus.DISABLED.equals(status)) {
            fEnabled.renameTo(fDisabled);
            return true;
         } else if (fDisabled.exists() && ManagedServiceStatus.ENABLED.equals(status)) {
            fDisabled.renameTo(fEnabled);
            return true;
         }
      } catch (Exception e) {
         logger.warn("Unable to update " + serviceId + " status file", e);
      }
      return false;
   }
}