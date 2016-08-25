package org.openwis.dataservice.cache;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.apache.tools.ant.util.FileUtils;
import org.jboss.ejb3.annotation.Depends;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.domain.entity.cache.CacheConfiguration;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.dataservice.gts.feeding.Feeder;
import org.openwis.dataservice.util.CacheUtils;
import org.openwis.dataservice.util.ChecksumCalculator;
import org.openwis.dataservice.util.FileInfo;
import org.openwis.dataservice.util.GlobalDataCollectionUtils;
import org.openwis.dataservice.util.WMOFNC;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage;
import org.openwis.datasource.server.jaxb.serializer.incomingds.StatisticsMessage;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.entity.IngestedData;
import org.openwis.management.entity.ReplicatedData;
import org.openwis.management.entity.ReplicationFilter;
import org.openwis.management.service.AlertService;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.IngestedDataStatistics;
import org.openwis.management.service.ReplicatedDataStatistics;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Depends({"jboss.ha:service=HASingletonDeployer,type=Barrier"})
@Stateless(name = "CacheManager")
@TransactionTimeout(18000)
public class CacheManagerImpl implements CacheManager, ConfigurationInfo {

	private String cacheDirectoryPath;
	private String harnessOutgoingDirectoryPath;
	private String tempDirectoryPath;
	private String workingDirectoryPath;

	private long purgingTimerPeriod = 0;
	private long purgingTimerInitialDelay = 0;
	private long alertCleanerTimerPeriod = 0;
	private static final long DEFAULT_ALERT_CLEANER_TIMER_PERIOD = 300000;
	private long alertCleanerTimerInitialDelay = 0;
	private static final long DEFAULT_ALERT_CLEANER_TIMER_INITIAL_DELAY = 30000;
	private long alertCleanerExpirationWindow = 0;
	private static final long DEFAULT_ALERT_CLEANER_EXPIRATION_WINDOW = 7;
	private long housekeepingTimerPeriod = 0;
	private long housekeepingTimerInitialDelay = 0;
	private long housekeepingExpirationWindow = 0;
	private long purgingExpirationWindow = 0;
	private final long MILLISECONDS_PER_DAY = 86400000;

	private final String productDateFormatString = "dd.MM.yyyy_HH-mm";
	private final SimpleDateFormat productDateFormat = new SimpleDateFormat(productDateFormatString);
	private final String insertionDateFormatString = "dd.MM.yyyy_HH-mm-ss-SSS_zzz";
	private final SimpleDateFormat insertionDateFormat = new SimpleDateFormat(insertionDateFormatString);

	private final long LARGE_PRODUCT_THRESHOLD = 1073741824; // 1 GB (in bytes)

	private final Logger LOG = LoggerFactory.getLogger(CacheManagerImpl.class);

	@EJB
	private Feeder feeder;

	@EJB
	private CacheIndex cacheIndex;

	@PersistenceContext
	private EntityManager entityManager;

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@Resource
	private TimerService timerService;

	/**
    * injection queue
    */
   @Resource(mappedName = "java:/queue/StatisticsQueue")
   private Queue statisticsQueue;
   
   /**
    * injection ConnectionFactory
    */
	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory cf;

	private ControlService controlService;
   
   private ControlService getControlService() {
      if (controlService == null) {
         try {
            controlService = ManagementServiceBeans.getInstance().getControlService();
         } catch (NamingException e) {
            controlService = null;
         }
      }
      return controlService;
   }

   private ReplicatedDataStatistics replicatedStatistics;

   private ReplicatedDataStatistics getReplicatedDataStatistics() {
      if (replicatedStatistics == null) {
         try {
            replicatedStatistics = ManagementServiceBeans.getInstance().getReplicatedDataStatistics();
         } catch (NamingException e) {
            replicatedStatistics = null;
         }
      }
      return replicatedStatistics;
   }
   private IngestedDataStatistics ingestionStatistics;
   
   private IngestedDataStatistics getIngestedDataStatistics() {
      if (ingestionStatistics == null) {
         try {
            ingestionStatistics = ManagementServiceBeans.getInstance().getIngestedDataStatistics();
         } catch (NamingException e) {
            ingestionStatistics = null;
         }
      }
      return ingestionStatistics;
   }
   
	@Override
	public void copyFileToHarness(File file) {
		File tempFile = new File(getHarnessOutgoingDirectory(),file.getName() + ".tmp");
		File targetFile = new File(getHarnessOutgoingDirectory(),file.getName());
		try {
			getFileUtils().copyFile(file,tempFile);
			getFileUtils().rename(tempFile, targetFile);
		}
		catch (IOException e) {
         LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void archiveFileToTemporaryDirectory(File file, boolean keepOriginal){
		String insertionDateFormatString = "dd.MM.yyyy_HH-mm-ss-SSS_zzz";
		SimpleDateFormat insertionDateFormat = new SimpleDateFormat(insertionDateFormatString);
		String insertionDateString = null;
		Date insertionDate = new Date(System.currentTimeMillis());
		insertionDateString = insertionDateFormat.format(insertionDate);
		Random random = new Random();
		try{
			File targetFile = new File(getTempDirectory() + "/received " + insertionDateString + "_" + random.nextInt(),file.getName());
			if (keepOriginal)
				getFileUtils().copyFile(file, targetFile);
			else
				getFileUtils().rename(file, targetFile);
		}
		catch(Exception e){
			LOG.error("Could not " + (keepOriginal? "copy":"move") + " file " + file.getName() + " to the temporary directory.");
		}
	}

	@Override
	public void start(){
		LOG.info("+++ start CleanUp timer with period " + getPurgingTimerPeriod());
		timerService.createTimer(getPurgingTimerInitialDelay(),getPurgingTimerPeriod(),"CleanUp");

		LOG.info("+++ start Housekeeping timer with period " + getHousekeepingTimerPeriod());
		timerService.createTimer(getHousekeepingTimerInitialDelay(),getHousekeepingTimerPeriod(),"Housekeeping");

		LOG.info("+++ start AlertCleaner timer with period " + getAlertCleanerTimerPeriod());
		timerService.createTimer(getAlertCleanerTimerInitialDelay(),getAlertCleanerTimerPeriod(),"AlertCleaner");
	}

	@SuppressWarnings("unchecked")
   @Override
	public void stop(){
		Iterator<Timer> it = timerService.getTimers().iterator();
		while (it.hasNext()){
			Timer timer = it.next();
			if (timer != null) timer.cancel();
		}
	}

	@Override
	public boolean isServiceAlreadyRunning(String serviceKey){
		// check if service key is already in the database, create it otherwise
		Long serviceInUse = null;
		try{
			Query query = entityManager.createQuery("SELECT cc.value FROM CacheConfiguration cc WHERE key = '" + serviceKey + "'");
			serviceInUse = (Long) query.getSingleResult();
		}
		catch(Exception e){
		}
		finally{
			if (serviceInUse == null){
				LOG.warn("Could not find " + serviceKey + " key in the OPENWIS_CACHE_CONFIGURATION table. Creating it.");
				CacheConfiguration cc = new CacheConfiguration();
				cc.setKey(serviceKey);
				serviceInUse = Long.valueOf(0);
				cc.setValue(serviceInUse);
				entityManager.persist(cc);
			}
		}
		return (Long.valueOf(1).equals(serviceInUse));
	}

	@Override
	public void setServiceRunning(String serviceKey, boolean value){
		Query query = entityManager.createQuery("SELECT cc FROM CacheConfiguration cc WHERE key = '" + serviceKey + "'");
		try{
			CacheConfiguration cacheConfiguration = (CacheConfiguration) query.getSingleResult();
			cacheConfiguration.setValue((value ? Long.valueOf(1) : Long.valueOf(0)));
			entityManager.merge(cacheConfiguration);
		}
		catch (Exception e){
		}
	}

	/**
	 * (non-Javadoc)
	 * @see org.openwis.dataservice.cache.CacheManager#timeout(javax.ejb.Timer)
	 * Implementing the CleanUp service, which purges the temporary directory.
	 * Also implementing the cache Housekeeping service, which takes care of expiration and synchronization of data in the cache directory and the cache index.
	 */
	@Override
	@Timeout
	public void timeout(Timer timer){
		if ("CleanUp".equals(timer.getInfo())){
			if (isServiceAlreadyRunning(cleanupInUseKey)) return;
			try{
				setServiceRunning(cleanupInUseKey, true);
				LOG.debug("Starting new Cleanup timer at " + new Date(System.currentTimeMillis()));
				startCleanUp();
			}
			finally{
				setServiceRunning(cleanupInUseKey, false);
				LOG.debug("Cleanup timer ended at " + new Date(System.currentTimeMillis()));
			}
		} else
		if ("Housekeeping".equals(timer.getInfo())){
			if (isServiceAlreadyRunning(housekeepingInUseKey)) return;
			try{
				setServiceRunning(housekeepingInUseKey, true);
				LOG.debug("Starting new Housekeeping timer at " + new Date(System.currentTimeMillis()));
				startHousekeeping();
			}
			finally{
				setServiceRunning(housekeepingInUseKey, false);
				LOG.debug("Housekeeping timer ended at " + new Date(System.currentTimeMillis()));
			}
		}  else
			if ("AlertCleaner".equals(timer.getInfo())){
				if (isServiceAlreadyRunning(alertCleanerInUseKey)) return;
				try{
					setServiceRunning(alertCleanerInUseKey, true);
					LOG.debug("Starting new Alert Cleaner timer at " + new Date(System.currentTimeMillis()));
					startAlertCleaner();
				}
				finally{
					setServiceRunning(alertCleanerInUseKey, false);
					LOG.debug("Alert Cleaner timer ended at " + new Date(System.currentTimeMillis()));
				}
			}
	}

	private void startAlertCleaner() {
		Date expirationDate = new Date(System.currentTimeMillis() - getAlertCleanerExpirationWindow());
		LOG.info("+++ Start Alert Cleaner since " + expirationDate);
		String queryString = "DELETE FROM OPENWIS_ALARMS alarm WHERE alarm.DATE < (:expirationDate)";
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("expirationDate", expirationDate);
		query.executeUpdate();
		LOG.info("+++ Finished Alert Cleaner since " + expirationDate);
	}

	private void startCleanUp(){
		LOG.info("+++ purging temporary directory");
		ArrayList<String> fileList = new ArrayList<String>();
		GlobalDataCollectionUtils.listAllFilesIncludingSubdirectories(getTempDirectory(), getTempDirectory(), fileList, Integer.MAX_VALUE);
		for (String filename : fileList){
			File fileToDelete = new File(getTempDirectory(),filename);

			if (fileToDelete.isFile()){
				long lastModifiedDateInMilliseconds = fileToDelete.lastModified();
				long expiryDateInMilliseconds = System.currentTimeMillis() - getPurgingExpirationWindow();

				if (lastModifiedDateInMilliseconds < expiryDateInMilliseconds){ // only delete files older than configured in the JNDI
					fileToDelete.delete();
					String path = filename.substring(0,filename.lastIndexOf('/'));
					GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(getTempDirectory()+ path, getTempDirectory());
					LOG.info("+++ Deleted File " + filename + " from temporary directory " + getTempDirectory());
				}
			}
		}

        // check for staging post max size, too big --> raise alert
        checkStagingPostSize();

		// clean up
		fileList = null;
	}

	/**
	 * Starts the Housekeeping for the cache and removes all expired files from the file system and the database.
	 *
	 *  Opens a new transaction and uses its own EntityManager, since the latter is cleared during the housekeeping operation for performance reasons.
	 */
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void startHousekeeping(){
		long nowInMilliseconds = System.currentTimeMillis();
		long expiryDateInMilliseconds = nowInMilliseconds - getHousekeepingExpirationWindow();
		Date expiryDate = new Date(expiryDateInMilliseconds);
		LOG.info("+++ Start housekeeping of cache since " + expiryDate);
		
		// Version <=3.12, suppression dans le File System, désormais, supprimer les fichiers avec un cron
      // delete mapped metadata entries in the database
      LOG.info("+++ expiration +++ deleting mapped metadata");
      Query deleteExpiredMappedMetadataQuery = entityManager
            .createNativeQuery("DELETE FROM openwis_mapped_metadata where cached_file_id in (SELECT cached_file_id FROM openwis_cached_file WHERE insertion_date < (:expiryDate))");
      deleteExpiredMappedMetadataQuery.setParameter("expiryDate", expiryDate);
      deleteExpiredMappedMetadataQuery.executeUpdate();

      // delete cached files entries in the database
      LOG.info("+++ expiration +++ deleting cached files");
      Query deleteExpiredFileFromCachedFilesQuery = entityManager
            .createNativeQuery("DELETE FROM openwis_cached_file WHERE insertion_date < (:expiryDate)");
      deleteExpiredFileFromCachedFilesQuery.setParameter("expiryDate", expiryDate);
      int deletedFiles = deleteExpiredFileFromCachedFilesQuery.executeUpdate();
      LOG.info("+++ expiration +++ deleted : " + deletedFiles);

		testForMaximumCacheSize();

		LOG.info("+++ Finished housekeeping of cache since " + expiryDate);
		
	}

	/**
	 * Deletes a file with the given name and checksum from the cache folder and index.
	 */
	@SuppressWarnings("unchecked")
   @Override
	public void removeFileFromCache(String filename, String checksum){
		Query filesToDeleteQuery = entityManager.createQuery("SELECT cf FROM CachedFile cf WHERE cf.filename = '" + filename + "' and cf.checksum = '" + checksum + "'");
		List<CachedFile> filesToDelete = filesToDeleteQuery.getResultList();
		for (CachedFile fileToDelete : filesToDelete){
			// stop sharing and delete files (and torrents) from cache
			String path = fileToDelete.getPath();
			LOG.info("+++ expiration +++ deleting file : " + filename);
         File obsoleteFile = new File(path, filename);
         obsoleteFile.delete();

			LOG.info("+++ expiration +++ delete file with id " + fileToDelete.getId() + " from cache index");

			Long fileId = fileToDelete.getId();

			// delete mapped metadata entries in the database
			Query deleteMappedMetadataQuery = entityManager.createQuery("DELETE FROM MappedMetadata mm WHERE mm.id = '" + fileId + "'");
			deleteMappedMetadataQuery.executeUpdate();

			// delete cached files entries in the database
			Query deleteFilesFromCacheQuery = entityManager.createQuery("DELETE FROM CachedFile cf WHERE cf.id = '" + fileId + "'");
			deleteFilesFromCacheQuery.executeUpdate();
		}
	}

	/**
	 * (non-Javadoc)
	 * @see org.openwis.dataservice.cache.CacheManager#isDuplicate(java.lang.String, java.lang.String, int)
	 * Tests if a duplicate of the given file(name) is already in the cache. Using filename + checksum as a primary key.
	 * Recalculates checksum if the number of bytes on which the checksum was calculated does not match the currently configured number of bytes.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean isDuplicate(String filename, String checksum, int numberOfChecksumBytes){
		Query query = entityManager.createQuery("SELECT cf FROM CachedFile cf WHERE cf.filename = " + "'" + filename + "'");
      List<CachedFile> resultList = query.getResultList();
		boolean duplicateExists = false;

		for (CachedFile cachedFile : resultList){
			if (numberOfChecksumBytes == cachedFile.getNumberOfChecksumBytes()){
				if (checksum.equals(cachedFile.getChecksum())) {
					duplicateExists = true;
					break;
				}
			} else {
				File file = new File(cachedFile.getPath(),cachedFile.getInternalFilename());
				String newChecksum = ChecksumCalculator.calculateChecksumOnFile(file, numberOfChecksumBytes);
				if (checksum.equals(newChecksum)) {
					duplicateExists = true;
					break;
				}
			}
		}

		return duplicateExists;
	}

	@Override
   public CachedFile moveFileIntoCache(FileInfo fileInfo) {
      String originator = fileInfo.getOriginator();
      Date productDate = fileInfo.getProductDate();
      Date insertionDate = fileInfo.getInsertionDate();

      String productDateString = null;
      String insertionDateString = null;

      productDateString = productDateFormat.format(productDate);
      insertionDateString = insertionDateFormat.format(insertionDate);

      // Check file to move into cache
      File sourceFileWithSuffix = new File(fileInfo.getFileURLwithSuffix());

      if (sourceFileWithSuffix == null || !sourceFileWithSuffix.exists()) {
         LOG.error("The file " + sourceFileWithSuffix.getPath() + " to be ingested does not exist.");
         return null;
      }

      long filesize = sourceFileWithSuffix.length();
      fileInfo.setSize(filesize);

      if (filesize > LARGE_PRODUCT_THRESHOLD) {
         raiseLargeProductAlert(fileInfo);
      }

      Random random = new Random(this.hashCode());
      String path = new StringBuilder(getCacheDirectory()).append("/").append(originator)
            .append("/").append(productDateString).toString();

      // Retrieve product filename with the URL without suffix
      String productFilename = new File(fileInfo.getFileURL()).getName();
      
      if (fileInfo.getExtension() != null && productFilename.endsWith(".bin")) {
         productFilename = productFilename.substring(0, productFilename.length() - 3)
               + fileInfo.getExtension();
         LOG.info("+++ enforce file extension for " + productFilename);
      }

      String filename = new StringBuilder(productFilename).append(insertionDateString)
            .append(random.nextInt()).toString();

      File targetFile = new File(path, filename);
      fileInfo.setFileURL(targetFile.getAbsolutePath());

      fileInfo.setProductFilename(productFilename);

      // Add CachedFile in DB
      CachedFile cachedFile = addCacheIndexEntry(fileInfo);

      if (cachedFile == null) {
         LOG.error(
               "File {} could not be ingested. An error occured during persisting. Trying again during next ingestion process.",
               fileInfo.getProductFilename());
         return null;
      }

      LOG.debug("Trying to move file " + sourceFileWithSuffix + " into cache folder " + targetFile);
      try {
         moveFileInCacheFolder(sourceFileWithSuffix, targetFile);
         LOG.debug("Moving of file " + sourceFileWithSuffix + " into cache folder " + targetFile
               + " done.");
      } catch (IOException e) {
         LOG.error("Error while moving file into cache", e);
         return null;
      }

      if (!fileInfo.isReceivedFromGTS()) {
         // publish file via GTS Feeding (since the file must have arrived from replication, there is no need in creating or saving a new product advertisement)
         if (feeder != null) {
            try {
               feeder.add(fileInfo);
            } catch (Exception e) {
               LOG.error("File {} could not be fed due to {}", fileInfo.getProductFilename(), e);
            }
         } else {
            LOG.error("Could not feed file " + fileInfo.getProductFilename()
                  + ". Feeder is not available.");
         }
      }

      updateStatistics(fileInfo);

      return cachedFile;
   }
	
	/**
	 * Synchronized renaming of file to avoid exception while creating sub-directories in 
	 * multithread.
	 * @throws IOException
	 */
   private static synchronized void moveFileInCacheFolder(File sourceFile, File targetFile)
         throws IOException {
      FileUtils.getFileUtils().rename(sourceFile, targetFile);
   }

	private String findSourceForReplicatedFile(FileInfo fileInfo) {
      List<String> metadataUrnList = fileInfo.getMetadataURNList();

      List<ReplicationFilter> replicationFilters = getControlService().getReplicationFilters();
      for (ReplicationFilter filter : replicationFilters) {
         if (!filter.isActive()){
            continue;
         }
         Pattern pattern = Pattern.compile(filter.getRegex());
         for (String metadataUrn : metadataUrnList) {
            if (pattern.matcher(metadataUrn).matches()){
               return filter.getSource();
            }
         }
      }

      return null;
   }

   private void updateStatistics(FileInfo fileInfo) {
      // statistics info
      String date = DateTimeUtils.formatUTC(fileInfo.getInsertionDate());
      long size = fileInfo.getSize();

      if (!fileInfo.isReceivedFromGTS()) {
         // add statistics for replicated file
         String source = findSourceForReplicatedFile(fileInfo);

         if (source != null) {
            try {
               // Send update statistics message
               StatisticsMessage message = new StatisticsMessage();
               message.setSource(source);
               message.setDate(date);
               message.setTotalSize(size);
               message.setCommand(StatisticsMessage.CMD_UPDATE_REPLICATED_DATA);
               sendStatisticsUpdate(message);
            } catch (Exception e) {
               LOG.error("Replication Statistics could not be updated due to {}", e);
            }
         } else {
            LOG.error("Could not find a source for " + fileInfo.getProductFilename());
         }
      } else {
         // add statistics for ingested file
         try {
            // Send update statistics message
            StatisticsMessage message = new StatisticsMessage();
            message.setDate(date);
            message.setTotalSize(size);
            message.setCommand(StatisticsMessage.CMD_UPDATE_INGESTED_DATA);
            sendStatisticsUpdate(message);
         } catch (Exception e) {
            LOG.error("Ingestion Statistics could not be updated due to {}", e);
         }
      }
   }
   
   /**
    * Send statistics update message to the dedicated JMS queue.
    *
    * @param statisticsMessage the statistics message
    */
   private void sendStatisticsUpdate(StatisticsMessage statisticsMessage) {
      Connection connection = null;
      try {
         // Create queue connection
         // Create a JMS Connection
         connection = cf.createConnection();
         // Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(statisticsQueue);

         // Create XML message
         StringWriter sw;
         sw = new StringWriter();
         Serializer.serialize(statisticsMessage, sw);
         String textMessage = sw.toString();
         TextMessage messageToSend = session.createTextMessage(textMessage);
         // Send message in the request queue
         messageProducer.send(messageToSend);

      } catch (Throwable t) {
         LOG.error("Unable to create message for the statistics queue", t);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (JMSException jme) {
               LOG.error("Unable to properly close connection to the queue", jme);
            }
         }
      }
   }
	
	private void raiseLargeProductAlert(FileInfo fileInfo){
		AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
		if (alertService == null){
			LOG.error("Could not get hold of the AlertService. No alert was passed!");
			return;
		}

		String source = "openwis-dataservice-cache-ejb-CacheManagerImpl";
		String location = "Ingestion";
		String severity = "WARN";
		String eventId = DataServiceAlerts.LARGE_PRODUCT_INGESTION.getKey();

		List<Object> arguments = new ArrayList<Object>();
		arguments.add(source);
		arguments.add(fileInfo.getMetadataURNList().get(0));
		arguments.add(Long.valueOf(fileInfo.getSize()));

		try {
			alertService.raiseEvent(source, location, severity, eventId, arguments);
		}
		catch (Exception e){
			LOG.error("Could not raise event " + DataServiceAlerts.LARGE_PRODUCT_INGESTION);
		}
	}

	@Override
	public boolean isValidForReplication(List<String> metadataUrnList){
		LOG.debug("Testing metadata for replication validation (null/empty)?: " + (metadataUrnList == null) + "-" + (metadataUrnList.isEmpty()));
		if (metadataUrnList != null && metadataUrnList.isEmpty()) return true; // handling products with stop-gap metadata (those files will be replicated regardless of replication filters)

		ControlService controlService = getControlService();
		if (controlService == null) return false;

		List<ReplicationFilter> replicationFilters = controlService.getReplicationFilters();
		for (ReplicationFilter filter : replicationFilters){
			LOG.debug("Filter Regex = " + filter.getRegex());
			if (!filter.isActive()) continue;
			Pattern pattern = Pattern.compile(filter.getRegex());
			for (String metadataUrn : metadataUrnList){
				if (pattern.matcher(metadataUrn).matches()) return true;
			}
		}

		return false;
	}

	private CachedFile addCacheIndexEntry(FileInfo file){
		CachedFile cachedFile = null;
		if (cacheIndex != null){
			cachedFile = cacheIndex.addCacheIndexEntry(file);
		}
		return cachedFile;
	}

	/**
	 * (non-Javadoc)
	 * @see org.openwis.dataservice.cache.CacheManager#createNewIncomingDataMessage(org.openwis.dataservice.util.WMOFNC, org.openwis.dataservice.util.FileInfo)
	 * Creates a new IncomingDSMessage for the IncomingDataQueue.
	 */
	@Override
	public void createNewIncomingDataMessage(WMOFNC wmofnc, FileInfo fileInfo, Long cachedFileId) {
		List<String> metadataURNs = fileInfo.getMetadataURNList();
        Date productDate = wmofnc.getProductDate();

        String body = null;
	      try {
	         // create message object
	         IncomingDSMessage message = new IncomingDSMessage();
	         message.setProductDate(DateTimeUtils.formatUTC(productDate));
	         message.setMetadataURNs(metadataURNs);
	         message.setProductId(String.valueOf(cachedFileId));

	         // XML transformation
	         StringWriter writer = new StringWriter();
	         Serializer.serialize(message, writer);
	         body = writer.toString();
	      } catch (Exception e) {
	         LOG.error("Failed to create incoming data queue message for: {} due to {}", new File(fileInfo.getFileURL()), e);
	         body = null;
	      }
	      if (body != null) {
	           // notify incoming data message queue
	           CacheUtils.postTextMessage(INCOMING_DATA_QUEUE_NAME, body);
	      }
	}
	
	// TEMP
	private long getConfigValue(String configValue) {
	   try {
         return Long.parseLong(ManagementServiceBeans.getInstance().getConfigService().getString(configValue));
      } catch (NumberFormatException e) {
         throw new RuntimeException("Cannot get config value: " + configValue, e);
      } catch (NamingException e) {
         throw new RuntimeException("Cannot get config value: " + configValue, e);
      }
	}
	// END TEMP

	public long getPurgingTimerPeriod(){
		if (purgingTimerPeriod == 0){
			purgingTimerPeriod = getConfigValue(CACHE_MANAGER_TEMPORARY_DIRECTORY_PURGE_TIMER_PERIOD_KEY);
		}
		return purgingTimerPeriod;
	}

	public long getPurgingTimerInitialDelay(){
		if (purgingTimerInitialDelay == 0){
			purgingTimerInitialDelay = getConfigValue(CACHE_MANAGER_TEMPORARY_DIRECTORY_PURGE_TIMER_INITIAL_DELAY_KEY);
		}
		return purgingTimerInitialDelay;
	}

	public long getAlertCleanerTimerPeriod(){
		if (alertCleanerTimerPeriod == 0){
			try {
				alertCleanerTimerPeriod = getConfigValue(CACHE_MANAGER_ALERT_CLEANER_TIMER_PERIOD_KEY);
			}
			catch (NumberFormatException e){
				// catch if no entry can be found in the JNDI
				LOG.warn("Could not find " + CACHE_MANAGER_ALERT_CLEANER_TIMER_PERIOD_KEY + " in JNDI. Taking default value of " + DEFAULT_ALERT_CLEANER_TIMER_PERIOD);
				alertCleanerTimerPeriod = DEFAULT_ALERT_CLEANER_TIMER_PERIOD;
			}
		}
		return alertCleanerTimerPeriod;
	}

	public long getAlertCleanerTimerInitialDelay(){
		if (alertCleanerTimerInitialDelay == 0){
			try {
				alertCleanerTimerInitialDelay = getConfigValue(CACHE_MANAGER_ALERT_CLEANER_TIMER_INITIAL_DELAY_KEY);
			}
			catch (NumberFormatException e){
				// catch if no entry can be found in the JNDI
				LOG.warn("Could not find " + CACHE_MANAGER_ALERT_CLEANER_TIMER_INITIAL_DELAY_KEY + " in JNDI. Taking default value of " + DEFAULT_ALERT_CLEANER_TIMER_INITIAL_DELAY);
				alertCleanerTimerInitialDelay = DEFAULT_ALERT_CLEANER_TIMER_INITIAL_DELAY;
			}
		}
		return alertCleanerTimerInitialDelay;
	}

	public long getHousekeepingTimerPeriod(){
		if (housekeepingTimerPeriod == 0){
			housekeepingTimerPeriod = getConfigValue(CACHE_MANAGER_HOUSEKEEPING_TIMER_PERIOD_KEY);
		}
		return housekeepingTimerPeriod;
	}

	public long getHousekeepingTimerInitialDelay(){
		if (housekeepingTimerInitialDelay == 0){
			housekeepingTimerInitialDelay = getConfigValue(CACHE_MANAGER_HOUSEKEEPING_TIMER_INITIAL_DELAY_KEY);
		}
		return housekeepingTimerInitialDelay;
	}

	public long getHousekeepingExpirationWindow(){
		if (housekeepingExpirationWindow == 0){
			housekeepingExpirationWindow = MILLISECONDS_PER_DAY * getConfigValue(CACHE_MANAGER_HOUSEKEEPING_EXPIRATION_WINDOW_KEY);
		}
		return housekeepingExpirationWindow;
	}

	public long getAlertCleanerExpirationWindow(){
		if (alertCleanerExpirationWindow == 0){
			try {
				alertCleanerExpirationWindow = MILLISECONDS_PER_DAY * getConfigValue(CACHE_MANAGER_ALERT_CLEANER_EXPIRATION_WINDOW_KEY);
			}
			catch (NumberFormatException e){
				// catch if no entry can be found in the JNDI
				LOG.warn("Could not find " + CACHE_MANAGER_ALERT_CLEANER_EXPIRATION_WINDOW_KEY + " in JNDI. Taking default value of " + DEFAULT_ALERT_CLEANER_EXPIRATION_WINDOW);
				alertCleanerExpirationWindow = MILLISECONDS_PER_DAY * DEFAULT_ALERT_CLEANER_EXPIRATION_WINDOW;
			}
		}
		return alertCleanerExpirationWindow;
	}

	public long getPurgingExpirationWindow(){
		if (purgingExpirationWindow == 0){
			purgingExpirationWindow = getConfigValue(CACHE_MANAGER_PURGING_EXPIRATION_WINDOW_KEY);
		}
		return purgingExpirationWindow;
	}

	public String getCacheDirectory(){
		if (cacheDirectoryPath == null){
			cacheDirectoryPath = ConfigServiceFacade.getInstance().getString(CACHE_DIRECTORY_KEY);
		}
		return cacheDirectoryPath;
	}

	public String getHarnessOutgoingDirectory(){
		if (harnessOutgoingDirectoryPath == null){
			harnessOutgoingDirectoryPath = ConfigServiceFacade.getInstance().getString(HARNESS_OUTGOING_DIRECTORY_KEY);
		}
		return harnessOutgoingDirectoryPath;
	}

	public String getTempDirectory(){
		if (tempDirectoryPath == null){
			tempDirectoryPath = ConfigServiceFacade.getInstance().getString(TEMP_DIRECTORY_KEY);
		}
		return tempDirectoryPath;
	}

	public String getWorkingDirectory(){
		if (workingDirectoryPath == null){
			workingDirectoryPath = ConfigServiceFacade.getInstance().getString(HARNESS_WORKING_DIRECTORY_KEY);
		}
		return workingDirectoryPath;
	}

	public FileUtils getFileUtils(){
		return FileUtils.getFileUtils();
	}

	private void checkStagingPostSize(){
		Query query = entityManager.createQuery("SELECT SUM(pr.size) FROM ProcessedRequest pr WHERE NOT (pr.uri = NULL)");
		long stagingPostSize = 0;
		try {
			stagingPostSize = ((Long) query.getSingleResult()).longValue();
		}
		catch (Exception e){
			// No result
			return;
		}

		long stagingPostMaximumSize = ConfigServiceFacade.getInstance().getLong(STAGING_POST_MAXIMUM_SIZE);
		if (stagingPostSize >= stagingPostMaximumSize){
			raiseStagingPostMaximumSizeReachedEvent(Long.valueOf(stagingPostMaximumSize), Long.valueOf(stagingPostSize));
		}
	}
	
	private void testForMaximumCacheSize() {
	   // test for cache maximum size --> raise alert
      String date = DateTimeUtils.formatUTC(System.currentTimeMillis());

      IngestedData ingestedData = getIngestedDataStatistics().getIngestedData(date);
      ReplicatedData replicatedData = getReplicatedDataStatistics().getReplicatedData(date);

      if (ingestedData != null && replicatedData != null) {
         long cacheMaximumSize = ConfigServiceFacade.getInstance().getLong(CACHE_MAXIMUM_SIZE);
         long actualSize = ingestedData.getSize() + replicatedData.getSize();

         if (actualSize >= cacheMaximumSize) {
            raiseCacheMaximumSizeReachedAlert(cacheMaximumSize, actualSize);
         }
      }
   }
	
	private void raiseCacheMaximumSizeReachedAlert(long maximumSize, long actualSize) {
      AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
      if (alertService == null) {
         LOG.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-CacheManagerImpl";
      String location = "Ingestion";
      String severity = "WARN";
      String eventId = DataServiceAlerts.CACHE_MAXIMUM_SIZE_REACHED.getKey();
      List<Object> arguments = Arrays.asList(
            (Object) Long.valueOf(maximumSize),
            (Object) Long.valueOf(actualSize));

      alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

	private void raiseStagingPostMaximumSizeReachedEvent(Object stagingPostMaximumSize, Object stagingPostCurrentSize){
		AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
	   if (alertService == null){
		   LOG.error("Could not get hold of the AlertService. No alert was passed!");
		   return;
	   }

	   String source = "openwis-dataservice-cache-ejb-ExtractFromCacheImpl";
	   String location = "Extraction";
	   String severity = "WARN";
	   String eventId = DataServiceAlerts.STAGING_POST_MAXIMUM_SIZE_REACHED.getKey();

	   List<Object> arguments = Arrays.asList(stagingPostMaximumSize,stagingPostCurrentSize);

	   try {
		   alertService.raiseEvent(source, location, severity, eventId, arguments);
	   }
	   catch(Exception e){
		   LOG.error("Could not raise event " + DataServiceAlerts.STAGING_POST_MAXIMUM_SIZE_REACHED);
	   }
   }
}