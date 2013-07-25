/**
 *
 */
package org.openwis.metadataportal.kernel.availability;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.management.control.ControlService;
import org.openwis.management.control.ManagedServiceIdentifier;
import org.openwis.management.control.ManagedServiceStatus;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.common.AbstractManager;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.kernel.external.SecurityServiceProvider;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.model.availability.Availability;
import org.openwis.metadataportal.model.availability.AvailabilityLevel;
import org.openwis.metadataportal.model.availability.AvailabilityStatistics;
import org.openwis.metadataportal.model.availability.AvailabilityStatisticsItem;
import org.openwis.metadataportal.model.availability.DataServiceAvailability;
import org.openwis.metadataportal.model.availability.HostAvailabilityHelper;
import org.openwis.metadataportal.model.availability.MetadataServiceAvailability;
import org.openwis.metadataportal.model.availability.SecurityServiceAvailability;
import org.openwis.metadataportal.model.harvest.HarvestingStatistics;
import org.openwis.metadataportal.model.harvest.HarvestingTaskStatus;
import org.openwis.metadataportal.services.login.SessionCounter;
import org.openwis.metadataportal.services.login.TokenUtilities;
import org.openwis.metadataportal.services.util.DateTimeUtils;

/**
 * Availability Manager. <P>
 *
 */
public class AvailabilityManager extends AbstractManager implements IAvailabilityManager {
   
   public static final String SRV_PORTAL_SESSIONS = "Portal Sessions";

   /**
    * Default constructor.
    * Builds a AvailabilityManager.
    * @param dbms the dbms element.
    */
   public AvailabilityManager(Dbms dbms) {
      super(dbms);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#startBackupMode(java.lang.String)
    */
   @Override
   public void switchBackupMode(boolean isSwitchedOn, String deploymentName, Date retroProcessDate,
         ServiceContext context) throws Exception {
      //Store Backup active.
      if (isSwitchedOn && !isLocalServerBackupingDeployment(deploymentName)) {
         String query = "INSERT INTO ActiveBackup VALUES(?)";
         getDbms().execute(query, deploymentName);
      } else if (!isSwitchedOn) {
         String query = "DELETE FROM ActiveBackup WHERE deploymentname like ?";
         getDbms().execute(query, deploymentName);
      }

      //Resume Subscription having Backup defined for deploymentName.
      SubscriptionService subscriptionService = DataServiceProvider.getSubscriptionService();
      subscriptionService.setBackup(deploymentName, isSwitchedOn,
            DateTimeUtils.format(retroProcessDate));

      //Change status and run harvesting task backups.
      HarvestingTaskManager harvestingTaskManager = new HarvestingTaskManager(getDbms());
      harvestingTaskManager.switchBackupMode(isSwitchedOn, deploymentName, context);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#isLocalServerBackupingDeployment(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   public boolean isLocalServerBackupingDeployment(String deploymentName) throws Exception {
      String checkPresent = "SELECT * FROM ActiveBackup WHERE deploymentname = ?";
      List<Element> records = getDbms().select(checkPresent, deploymentName).getChildren();
      return !records.isEmpty();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#startStopMetadataService(jeeves.server.context.ServiceContext, boolean, java.lang.String)
    */
   @Override
   public void startStopMetadataService(ServiceContext context, boolean isStarted,
         String metadataService) throws Exception {
      HarvestingTaskManager harvestingTaskManager = new HarvestingTaskManager(getDbms());

      if (isStarted) {
         if ("synchronization".equals(metadataService)) {
            //Stop Synchro tasks.
            harvestingTaskManager.suspendAllCurrentSynchroTasks();
         } else if ("harvesting".equals(metadataService)) {
            //Stop harvesting tasks.
            harvestingTaskManager.suspendAllCurrentHarvestingTasks();
         } else if ("userPortal".equals(metadataService)) {
            setUserPortalEnable(false);
         }
      } else {
         if ("synchronization".equals(metadataService)) {
            //Start Synchro tasks.
            harvestingTaskManager.startAllCurrentSynchroTasks(context);
         } else if ("harvesting".equals(metadataService)) {
            //Start harvesting tasks.
            harvestingTaskManager.startAllCurrentHarvestingTasks(context);
         } else if ("userPortal".equals(metadataService)) {
            setUserPortalEnable(true);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#startStopDataService(boolean, java.lang.String)
    */
   @Override
   public void startStopDataService(boolean isStarted, String dataService) {
      ControlService controlService = ManagementServiceProvider.getControlService();

      ManagedServiceIdentifier managedServiceIdentifier = null;

      if ("replication".equals(dataService)) {
         managedServiceIdentifier = ManagedServiceIdentifier.REPLICATION_SERVICE;
      } else if ("ingestion".equals(dataService)) {
         managedServiceIdentifier = ManagedServiceIdentifier.INGESTION_SERVICE;
      } else if ("dissemination".equals(dataService)) {
         managedServiceIdentifier = ManagedServiceIdentifier.DISSEMINATION_SERVICE;
      } else if ("subscriptionProcessing".equals(dataService)) {
         managedServiceIdentifier = ManagedServiceIdentifier.SUBSCRIPTION_SERVICE;
      }

      if (managedServiceIdentifier != null) {
         if (isStarted) {
            controlService
                  .setServiceStatus(managedServiceIdentifier, ManagedServiceStatus.DISABLED);
         } else {
            controlService.setServiceStatus(managedServiceIdentifier, ManagedServiceStatus.ENABLED);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#getDataServiceAvailability()
    */
   @Override
   public DataServiceAvailability getDataServiceAvailability() {
      DataServiceAvailability dataServiceAvailability = new DataServiceAvailability();

      // replication Process
      dataServiceAvailability = getReplicationAvailability(dataServiceAvailability);

      // ingestion
      dataServiceAvailability = getIngestionAvailability(dataServiceAvailability);

      // dissemination Queue
      dataServiceAvailability = getDisseminationAvailability(dataServiceAvailability);

      // subscription Queue
      dataServiceAvailability = getSubscriptionProcessingAvailability(dataServiceAvailability);

      return dataServiceAvailability;
   }

   @Override
   public DataServiceAvailability getDataServiceAvailability(String dataService) {
      DataServiceAvailability dataServiceAvailability = new DataServiceAvailability();

      if ("replication".equals(dataService)) {
         // replicationProcess
         dataServiceAvailability = getReplicationAvailability(dataServiceAvailability);
      } else if ("ingestion".equals(dataService)) {
         // ingestion
         dataServiceAvailability = getIngestionAvailability(dataServiceAvailability);
      } else if ("dissemination".equals(dataService)) {
         // disseminationQueue
         dataServiceAvailability = getDisseminationAvailability(dataServiceAvailability);
      } else if ("subscriptionProcessing".equals(dataService)) {
         // subscription queue
         dataServiceAvailability = getSubscriptionProcessingAvailability(dataServiceAvailability);
      }
      return dataServiceAvailability;
   }

   public DataServiceAvailability getSubscriptionProcessingAvailability(
         DataServiceAvailability dataServiceAvailability) {
      ManagedServiceIdentifier managedServiceIdentifier = ManagedServiceIdentifier.SUBSCRIPTION_SERVICE;
      dataServiceAvailability
            .setSubscriptionQueue(getDataServiceAvailability(managedServiceIdentifier));
      return dataServiceAvailability;
   }

   public DataServiceAvailability getDisseminationAvailability(
         DataServiceAvailability dataServiceAvailability) {
      ManagedServiceIdentifier managedServiceIdentifier = ManagedServiceIdentifier.DISSEMINATION_SERVICE;
      dataServiceAvailability
            .setDisseminationQueue(getDataServiceAvailability(managedServiceIdentifier));
      return dataServiceAvailability;
   }

   public DataServiceAvailability getIngestionAvailability(
         DataServiceAvailability dataServiceAvailability) {
      ManagedServiceIdentifier managedServiceIdentifier = ManagedServiceIdentifier.INGESTION_SERVICE;
      dataServiceAvailability.setIngestion(getDataServiceAvailability(managedServiceIdentifier));
      return dataServiceAvailability;
   }

   public DataServiceAvailability getReplicationAvailability(
         DataServiceAvailability dataServiceAvailability) {
      ManagedServiceIdentifier managedServiceIdentifier = ManagedServiceIdentifier.REPLICATION_SERVICE;
      dataServiceAvailability
            .setReplicationProcess(getDataServiceAvailability(managedServiceIdentifier));
      return dataServiceAvailability;
   }

   private Availability getDataServiceAvailability(ManagedServiceIdentifier managedServiceIdentifier) {
      Availability result = null;
      try {
         ControlService controlService = ManagementServiceProvider.getControlService();
         if (ManagedServiceStatus.DEGRADED.name().equals(
               controlService.getServiceStatus(managedServiceIdentifier))) {
            result = new Availability(AvailabilityLevel.WARN);
         } else if (ManagedServiceStatus.DISABLED.name().equals(
               controlService.getServiceStatus(managedServiceIdentifier))) {
            result = new Availability(AvailabilityLevel.STOPPED);
         } else if (ManagedServiceStatus.ENABLED.name().equals(
               controlService.getServiceStatus(managedServiceIdentifier))) {
            result = new Availability(AvailabilityLevel.UP);
         } else if (ManagedServiceStatus.UNKNOWN.name().equals(
               controlService.getServiceStatus(managedServiceIdentifier))) {
            result = new Availability(AvailabilityLevel.UNKNOWN);
         }
      } catch (Exception e) {
         result = new Availability(AvailabilityLevel.DOWN);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#getMetadataServiceAvailability(jeeves.resources.dbms.Dbms, org.fao.geonet.kernel.search.ISearchManager)
    */
   @Override
   public MetadataServiceAvailability getMetadataServiceAvailability(Dbms dbms,
         ISearchManager searchManager) {
      MetadataServiceAvailability metadataServiceAvailability = new MetadataServiceAvailability();

      //-- User Catalog.
      metadataServiceAvailability = getUserPortalMetadataServiceAvailability(metadataServiceAvailability);

      //-- Harvesting and synchro
      metadataServiceAvailability = getHarvestingSynchroMetadataServiceAvailability(dbms,
            metadataServiceAvailability);

      //-- Indexing.
      try {
         boolean isIndexAvailable = searchManager.isAvailable();
         if (isIndexAvailable) {
            metadataServiceAvailability.setIndexing(new Availability(AvailabilityLevel.UP));
         } else {
            metadataServiceAvailability.setIndexing(new Availability(AvailabilityLevel.DOWN));
         }
      } catch (Exception e) {
         metadataServiceAvailability.setIndexing(new Availability(AvailabilityLevel.DOWN));
      }

      return metadataServiceAvailability;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#getMetadataServiceAvailability(jeeves.resources.dbms.Dbms, java.lang.String)
    */
   @Override
   public MetadataServiceAvailability getMetadataServiceAvailability(Dbms dbms, String serviceName) {
      MetadataServiceAvailability metadataServiceAvailability = new MetadataServiceAvailability();
      if ("userPortal".equals(serviceName)) {
         metadataServiceAvailability = getUserPortalMetadataServiceAvailability(metadataServiceAvailability);
      } else if ("harvesting".equals(serviceName)) {
         metadataServiceAvailability = getHarvestingSynchroMetadataServiceAvailability(dbms,
               metadataServiceAvailability);
      } else if ("synchronization".equals(serviceName)) {
         metadataServiceAvailability = getHarvestingSynchroMetadataServiceAvailability(dbms,
               metadataServiceAvailability);
      }
      return metadataServiceAvailability;
   }

   /**
    * Get User Portal Metadata Service Availability.
    * @param metadataServiceAvailability The metadata service availability
    * @return The metadata service availability
    */
   public MetadataServiceAvailability getUserPortalMetadataServiceAvailability(
         MetadataServiceAvailability metadataServiceAvailability) {
      //-- User Catalog.
      String userPortalUrl = OpenwisMetadataPortalConfig
            .getString(ConfigurationConstants.MONITORING_USERPORTAL_URL);
      AvailabilityLevel userPortalAvailabilityLevel = null;
      if (StringUtils.isBlank(userPortalUrl)) {
         //Not specified. State the availability as UNKNOWN
         userPortalAvailabilityLevel = AvailabilityLevel.UNKNOWN;
      } else {
         boolean userPortableEnable = isUserPortalEnable();
         if (userPortableEnable) {
            //Perform a GET on URL.
            boolean isAvailable = HostAvailabilityHelper.isAvailable(userPortalUrl);

            if (!isAvailable) {
               userPortalAvailabilityLevel = AvailabilityLevel.DOWN;
            } else {
               userPortalAvailabilityLevel = AvailabilityLevel.UP;
            }
         } else {
            userPortalAvailabilityLevel = AvailabilityLevel.STOPPED;
         }
      }
      metadataServiceAvailability.setUserPortal(new Availability(userPortalAvailabilityLevel));

      return metadataServiceAvailability;
   }

   /**
    * Get the status of harvesting and synchro tasks.
    * @param dbms The dbms
    * @param metadataServiceAvailability The metadata status resume.
    * @return The metadata status resume.
    */
   public MetadataServiceAvailability getHarvestingSynchroMetadataServiceAvailability(Dbms dbms,
         MetadataServiceAvailability metadataServiceAvailability) {
      try {
         HarvestingTaskManager harvestingTaskManager = new HarvestingTaskManager(dbms);
         Map<Boolean, HarvestingStatistics> stats = harvestingTaskManager
               .getAllHarvestingStatistics(HarvestingTaskStatus.ACTIVE);

         // Harvesting.
         HarvestingStatistics harvestingTaskStats = stats.get(Boolean.FALSE);
         int warnLimit = OpenwisMetadataPortalConfig
               .getInt(ConfigurationConstants.MONITORING_HARVEST_WARN_LIMIT);
         metadataServiceAvailability.setHarvesting(computeAvailabilityForTasks(harvestingTaskStats,
               warnLimit));

         // Synchro.
         HarvestingStatistics synchroStats = stats.get(Boolean.TRUE);
         int synchroLimit = OpenwisMetadataPortalConfig
               .getInt(ConfigurationConstants.MONITORING_SYNCHRO_WARN_LIMIT);
         metadataServiceAvailability.setSynchronization(computeAvailabilityForTasks(synchroStats,
               synchroLimit));

      } catch (Exception e) {
         metadataServiceAvailability.setHarvesting(new Availability(AvailabilityLevel.UNKNOWN));
         metadataServiceAvailability
               .setSynchronization(new Availability(AvailabilityLevel.UNKNOWN));
      }
      return metadataServiceAvailability;
   }

   /**
    * Compute availability from the given harvesting task statistics.
    * @param harvestingTaskStats the task statistics
    * @param warnLimit the warn limit under which we consider the task as down
    * @return the {@link Availability}
    */
   private Availability computeAvailabilityForTasks(HarvestingStatistics harvestingTaskStats,
         int warnLimit) {
      AvailabilityLevel harvestingTaskAvailabilityLevel = null;
      if (harvestingTaskStats.getTotal() == 0) {
         harvestingTaskAvailabilityLevel = AvailabilityLevel.NONE;
      } else if (harvestingTaskStats.getActive() == 0) {
         harvestingTaskAvailabilityLevel = AvailabilityLevel.ALL_SUSPENDED;
      } else if (harvestingTaskStats.getSuccessRatioPrct() == 100) {
         harvestingTaskAvailabilityLevel = AvailabilityLevel.UP;
      } else if (harvestingTaskStats.getSuccessRatioPrct() < warnLimit) {
         harvestingTaskAvailabilityLevel = AvailabilityLevel.DOWN;
      } else {
         harvestingTaskAvailabilityLevel = AvailabilityLevel.WARN;
      }
      Availability harvestingTaskAvailability = new Availability(harvestingTaskAvailabilityLevel);
      harvestingTaskAvailability.getAdditionalInfo().put("active",
            Integer.toString(harvestingTaskStats.getActive()));
      harvestingTaskAvailability.getAdditionalInfo().put("failure",
            Integer.toString(harvestingTaskStats.getFailure()));
      return harvestingTaskAvailability;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#getSecurityServiceAvailability()
    */
   @Override
   public SecurityServiceAvailability getSecurityServiceAvailability() {
      SecurityServiceAvailability securityServiceAvailability = new SecurityServiceAvailability();

      //-- SSO -> Check Token
      try {
         String idpURL = OpenwisMetadataPortalConfig
               .getString(ConfigurationConstants.SSO_MANAGEMENT);
         TokenUtilities tokenUtilities = new TokenUtilities();

         //We check here if the IDP fails to check by throwing an exception. Invalid token does not matter.
         tokenUtilities.isTokenValid(idpURL, "");
         securityServiceAvailability.setSsoService(new Availability(AvailabilityLevel.UP));
      } catch (Exception e) {
         securityServiceAvailability.setSsoService(new Availability(AvailabilityLevel.DOWN));
      }

      //-- Security Service -> Call a method.
      try {
         boolean isAvailable = SecurityServiceProvider.getMonitoringService()
               .isSecurityServiceAvailable();
         if (isAvailable) {
            securityServiceAvailability.setSecurityService(new Availability(AvailabilityLevel.UP));
         } else {
            securityServiceAvailability
                  .setSecurityService(new Availability(AvailabilityLevel.DOWN));
         }
      } catch (Exception e) {
         securityServiceAvailability.setSecurityService(new Availability(AvailabilityLevel.DOWN));
      }

      return securityServiceAvailability;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#isUserPortalEnable()
    */
   @Override
   @SuppressWarnings("unchecked")
   public boolean isUserPortalEnable() {
      String query = "SELECT * FROM Availability WHERE task='userPortal'";
      try {
         List<Element> records = getDbms().select(query).getChildren();
         boolean result = true;
         for (Element e : records) {
            result = StringUtils.equals(e.getChildText("state"), "y");
         }
         return result;
      } catch (SQLException e1) {
         Log.error(Geonet.OPENWIS, "error isUserPortalEnable" + e1.getMessage());
         return true;
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.availability.IAvailabilityManager#setUserPortalEnable(boolean)
    */
   @Override
   public void setUserPortalEnable(boolean enable) {
      // Update the user portal availability.
      String query = "UPDATE Availability SET state=? WHERE task='userPortal'";
      try {
         getDbms().execute(query, BooleanUtils.toString(enable, "y", "n"));
      } catch (SQLException e) {
         Log.error(Geonet.OPENWIS, "error during setUserPortalEnable" + e.getMessage());
      }
   }
   
   /**
    * Schedule availability statistics.
    * 
    * @param settingMan the settings manager
    * @param searchMan the search manager
    * @param executor the {@link ScheduledExecutorService}
    */
   public void scheduleAvailabilityStatistics(final SettingManager settingMan, final ISearchManager searchMan, ScheduledExecutorService executor) {
      try {
         Integer period = settingMan.getValueAsInt("system/availablility/period");

         Runnable command = new Runnable() {
            @Override
            public void run() {
               checkLocalAvailability(searchMan);
            }
         };
         executor.scheduleAtFixedRate(command, period, period, TimeUnit.SECONDS);
      } catch (Exception e) {
         Log.error(
               Geonet.ADMIN,
               "Could not configure the availability synchonisation. Check that 'system/availablility/period' exists in Settings",
               e);
      }
   }
   
   
   private void checkLocalAvailability(ISearchManager searchManager) {
      Log.info(Geonet.ADMIN, "Local Availability Check for Statistics");

      try {
         MetadataServiceAvailability mdtaServiceAvailability = getMetadataServiceAvailability(getDbms(), searchManager);
         DataServiceAvailability dataServiceAvailability = getDataServiceAvailability();
         SecurityServiceAvailability securityServiceAvailability = getSecurityServiceAvailability();

         updateServiceAvailabilityStatistics("Harvesting", mdtaServiceAvailability.getHarvesting().getLevel());
         updateServiceAvailabilityStatistics("Synchronization", mdtaServiceAvailability.getSynchronization().getLevel());
         updateServiceAvailabilityStatistics("Indexing", mdtaServiceAvailability.getIndexing().getLevel());
         updateServiceAvailabilityStatistics("User Portal", mdtaServiceAvailability.getUserPortal().getLevel());
         updateServiceAvailabilityStatistics("Dissemination Queue", dataServiceAvailability.getDisseminationQueue().getLevel());
         updateServiceAvailabilityStatistics("Ingestion", dataServiceAvailability.getIngestion().getLevel());
         updateServiceAvailabilityStatistics("Replication", dataServiceAvailability.getReplicationProcess().getLevel());
         updateServiceAvailabilityStatistics("Subscription", dataServiceAvailability.getSubscriptionQueue().getLevel());
         updateServiceAvailabilityStatistics("Security Service", securityServiceAvailability.getSecurityService().getLevel());
         updateServiceAvailabilityStatistics("SSO Service", securityServiceAvailability.getSsoService().getLevel());
         
         // get session counters
         int[] sessionCounters = SessionCounter.getSessionsCreated();
         updateServiceAvailabilityStatistics(SRV_PORTAL_SESSIONS, sessionCounters[1], sessionCounters[0]);
         
      } catch (Exception e) {
         Log.error(Geonet.ADMIN, "Unable to update availability statistics", e);
      }
   }
   
   /**
    * Update Service availability statistics.
    * @param serviceName The service name.
    * @param level The availability level
    * @throws SQLException 
    */
   private void updateServiceAvailabilityStatistics(String serviceName, AvailabilityLevel level) throws SQLException {
      int available = 0;
      int notAvailable = 0;
      if (level == AvailabilityLevel.DOWN || level == AvailabilityLevel.STOPPED 
            || level == AvailabilityLevel.WARN || level == AvailabilityLevel.ALL_SUSPENDED) {
         notAvailable = 1;
         Log.warning(Geonet.ADMIN, serviceName + " is NOT fully availble - status: " + level);
      } else {
         available = 1;
         Log.info(Geonet.ADMIN, serviceName + " is available");
      }
      updateServiceAvailabilityStatistics(serviceName, available, notAvailable);
   }
   
   /**
    * Update Service availability statistics with new available/notAvailable values.
    * @param serviceName The service name.
    * @param available available value to increment
    * @param notAvailable notAvailable value to increment
    * @throws SQLException 
    */
   @SuppressWarnings("unchecked")
   private void updateServiceAvailabilityStatistics(String serviceName, int available, int notAvailable) throws SQLException {
      String date = DateFormatUtils.formatUTC(new Date(), DateFormatUtils.ISO_DATE_FORMAT.getPattern());
      String query = "SELECT * FROM Availability_Statistics WHERE date=? and task=?";
      List<Element> records = getDbms().select(query, date, serviceName).getChildren();

      if (records.isEmpty()) {
         // Create a new data
         String insert = "INSERT INTO Availability_Statistics (date, task, available, notAvailable) VALUES (?,?,?,?)";
         // Execute query
         getDbms().execute(insert, date, serviceName, available, notAvailable);
      } else {
         // Update data
         Element record = records.get(0);
         available += Integer.parseInt(record.getChildText("available"));
         notAvailable += Integer.parseInt(record.getChildText("notavailable"));
         String update = "UPDATE Availability_Statistics SET available=?, notAvailable=? WHERE date=? AND task=?";
         // Execute query
         getDbms().execute(update, available, notAvailable, date, serviceName);         
      }
   }
   
   public AvailabilityStatistics getAvailabilityStatistics(String serviceNameFilter, String sortingColumn, String sortDir, int offset, int rowcount) throws SQLException {
      return getAvailabilityStatistics(serviceNameFilter, SRV_PORTAL_SESSIONS, sortingColumn, sortDir, offset, rowcount);
   }
   
   public AvailabilityStatistics getSessionCountStatistics(String sortingColumn, String sortDir, int offset, int rowcount) throws SQLException {
      return getAvailabilityStatistics(SRV_PORTAL_SESSIONS, null, sortingColumn, sortDir, offset, rowcount);
   }
   
   @SuppressWarnings("unchecked")
   private AvailabilityStatistics getAvailabilityStatistics(String serviceNameFilter, String excludeService, String sortingColumn, String sortDir, int offset, int rowcount) throws SQLException {
      StringBuilder query = new StringBuilder(256);
      query.append("SELECT * FROM Availability_Statistics");
      StringBuilder queryCount = new StringBuilder(256);
      queryCount.append("SELECT count(date) FROM Availability_Statistics");
      
      // create where filter
      StringBuilder where = new StringBuilder();
      if (serviceNameFilter != null || excludeService != null) {
         where.append(" WHERE");
      }
      if (serviceNameFilter != null) {
         where.append(" lower(task) like lower(?)");
         serviceNameFilter += "%";
         if (excludeService != null) {
            where.append(" AND");
         }
      }
      if (excludeService != null) {
         where.append(" lower(task) not like lower(?)");
      }
      
      query.append(where);
      queryCount.append(where);
      
      if (sortingColumn != null) {
         query.append(" ORDER BY ").append(sortingColumn);
         if (sortDir != null) {
            query.append(" ").append(sortDir);
         }
      }
      if (rowcount != 0) {
         query.append(" LIMIT ").append(rowcount);
         query.append(" OFFSET ").append(offset);
      }

      List<Element> records;
      if (serviceNameFilter != null && excludeService != null) {
         records = getDbms().select(query.toString(), serviceNameFilter, excludeService).getChildren();  
      } else if (serviceNameFilter != null) {
         records = getDbms().select(query.toString(), serviceNameFilter).getChildren();
      } else if (excludeService != null) {
         records = getDbms().select(query.toString(), excludeService).getChildren();
      } else {
         records = getDbms().select(query.toString()).getChildren();  
      }
      
      List<AvailabilityStatisticsItem> items = new ArrayList<AvailabilityStatisticsItem>();
      for (Element record : records) {
         AvailabilityStatisticsItem item = new AvailabilityStatisticsItem();
         item.setDate(record.getChildText("date"));
         item.setTask(record.getChildText("task"));
         item.setAvailable(Integer.parseInt(record.getChildText("available")));
         item.setNotAvailable(Integer.parseInt(record.getChildText("notavailable")));
         items.add(item);
      }
      
      AvailabilityStatistics stats = new AvailabilityStatistics();
      stats.setItems(items);
      
      // count
      if (serviceNameFilter != null && excludeService != null) {
         records = getDbms().select(queryCount.toString(), serviceNameFilter, excludeService).getChildren();  
      } else if (serviceNameFilter != null) {
         records = getDbms().select(queryCount.toString(), serviceNameFilter).getChildren();  
      } else if (excludeService != null) {
         records = getDbms().select(queryCount.toString(), excludeService).getChildren();
      } else {
         records = getDbms().select(queryCount.toString()).getChildren();  
      }
      stats.setCount(Integer.parseInt(records.get(0).getValue()));
      
      return stats;
   }
 
}
