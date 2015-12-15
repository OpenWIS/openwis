package org.openwis.dataservice.gts.collection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.tools.ant.util.FileUtils;
import org.jboss.ejb3.annotation.Depends;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.cache.CacheManager;
import org.openwis.dataservice.common.domain.entity.cache.CacheConfiguration;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.gts.GTSTimerService;
import org.openwis.dataservice.util.GlobalDataCollectionUtils;
import org.openwis.dataservice.util.WMOFTP;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.AlertService;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.ManagedServiceIdentifier;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Depends({"jboss.ha:service=HASingletonDeployer,type=Barrier"})
@Stateless(name = "SplittingTimerService")
@TransactionTimeout(18000)
public class SplittingTimerServiceImpl implements GTSTimerService, ConfigurationInfo {

   private final Logger LOG = LoggerFactory.getLogger(SplittingTimerServiceImpl.class);

   private final String splittingInUseKey = "splittingInUse";

   private long splittingInitialDelay = 0;

   private long splittingPeriod = 0;

   private int maxNumberOfIncludedNonpackedFiles = 0;

   @Resource
   private TimerService timerService;

   private String sourceDirectory;

   private String workingDirectory;

   private String fromReplicationDirectoryName;

   private String replicationWorkingDirectory;

   private String[] includePatterns;

   private String[] excludePatterns;

   @PersistenceContext
   private EntityManager entityManager;

   @EJB
   private CacheManager cacheManager;

   /**
   * injection ConnectionFactory
   */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory cf;

   private Connection connection;

   private Session session;

   private MessageProducer messageProducer;

   /**
    * injection queue
    */
   @Resource(mappedName = "java:/queue/CollectionQueue")
   private Queue queue;

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

   public void start() {
      timerService.createTimer(getSplittingInitialDelay(), getSplittingPeriod(), null);
      LOG.info("Timer was successfully started, with " + splittingPeriod + " ms delay!");

      // Should some files remains in working directory after a JBoss reboot. 
      // All these files are notified to the CollectionMDB. 
      File working = new File(getWorkingDirectory());
      File[] filesInWorking = working.listFiles();
      if (filesInWorking != null && filesInWorking.length > 0) {
         try {
            // Create a JMS Connection
            connection = cf.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);

            for (File file : filesInWorking) {
               if (!file.isDirectory()) {
                  sendCollectionMessage(file);
                  LOG.info("*** Send collection message for file " + file);
               }
            }
         } catch (Exception e) {
            LOG.error(e.getMessage(), e);
         }
      }
   }

   @SuppressWarnings("unchecked")
   public void stop() {
      Iterator<Timer> it = timerService.getTimers().iterator();
      while (it.hasNext()) {
         Timer timer = it.next();
         if (timer != null)
            timer.cancel();
      }
   }

   public boolean isTimerServiceRunning() {
      return isSplittingServiceAlreadyRunning();
   }

   public void setTimerServiceStatus(boolean status) {
      setSplittingRunning(status);
   }

   private boolean isSplittingServiceAlreadyRunning() {
      // check if splittingInUse key is already in the database, create it otherwise
      Long splittingInUse = null;
      try {
         Query query = entityManager
               .createQuery("SELECT cc.value FROM CacheConfiguration cc WHERE cc.key = '"
                     + splittingInUseKey + "'");
         splittingInUse = (Long) query.getSingleResult();
      } catch (Exception e) {
      } finally {
         if (splittingInUse == null) {
            LOG.warn("Could not find " + splittingInUseKey
                  + " key in the OPENWIS_CACHE_CONFIGURATION table. Creating it.");
            CacheConfiguration cc = new CacheConfiguration();
            cc.setKey(splittingInUseKey);
            splittingInUse = Long.valueOf(0);
            cc.setValue(splittingInUse);
            entityManager.persist(cc);
         }
      }
      return (Long.valueOf(1).equals(splittingInUse));
   }

   private void setSplittingRunning(boolean value) {
      Query query = entityManager
            .createQuery("SELECT cc FROM CacheConfiguration cc WHERE cc.key = '"
                  + splittingInUseKey + "'");
      try {
         CacheConfiguration cacheConfiguration = (CacheConfiguration) query.getSingleResult();
         cacheConfiguration.setValue((value ? Long.valueOf(1) : Long.valueOf(0)));
         cacheConfiguration = entityManager.merge(cacheConfiguration);
      } catch (Exception e) {
      }
   }

   private boolean isIngestionEnabled() {
      return getControlService().isServiceEnabled(ManagedServiceIdentifier.INGESTION_SERVICE);
   }

   @Timeout
   public void timeout(Timer timer) {
      if (!isIngestionEnabled())
         return;
      boolean isRunning = isSplittingServiceAlreadyRunning();
      if (isRunning)
         return;

      List<File> sortedFiles = null;
      try {
         LOG.debug("Starting new Splitting timer at " + new Date(System.currentTimeMillis()));

         sortedFiles = scanForNewFiles(getSourceDirectory(), getIncludePatterns(),
               getExcludePatterns());

         // Create a JMS Connection
         connection = cf.createConnection();
         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         messageProducer = session.createProducer(queue);

         String targetPath = getWorkingDirectory();

         for (File sortedFile : sortedFiles) {
            LOG.debug("File " + sortedFile.getName() + " last modified at "
                  + sortedFile.lastModified());
            String fileNamePath = sortedFile.getPath().replace('\\', '/')
                  .replace(getSourceDirectory(), "");
            String relativePath = fileNamePath.substring(0, fileNamePath.lastIndexOf('/'));

            String filename = fileNamePath.substring(fileNamePath.lastIndexOf('/') + 1);
            if (relativePath.contains(getFromReplicationDirectoryName())) {
               targetPath = getReplicationWorkingDirectory();
            } else {
               targetPath = getWorkingDirectory();
            }

            if (GlobalDataCollectionUtils.isPacked(filename)) {
               handlePackedFile(getSourceDirectory(), targetPath, relativePath, filename);
            } else {
               handleUnpackedFile(getSourceDirectory(), targetPath, fileNamePath, filename);
            }
         }
         LOG.debug("Splitting timer done. New splitting timer will start in "
               + getSplittingPeriod() + " ms.");
      } catch (Exception e) {
         LOG.error("Error in Splitting timer", e);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (JMSException jme) {
               LOG.error("Unable to properly close connection to the queue", jme);
            }
         }

         LOG.debug("Splitting timer ended at " + new Date(System.currentTimeMillis()));

         // cleanup
         sortedFiles = null;
      }
   }

   private void sendCollectionMessage(File file) {
      try {
         // Send message in the request queue
         String textMessage = file.getAbsolutePath();
         TextMessage messageToSend = session.createTextMessage(textMessage);
         messageProducer.send(messageToSend);
      } catch (JMSException e) {
         LOG.error("Unable to send message for the Collection queue", e);
      }
   }

   public void handleUnpackedFile(String sourceDirectory, String workingDirectory,
         String fileNamePath, String filename) {
      File sourceFile = new File(sourceDirectory, fileNamePath);
      if (sourceFile == null || !sourceFile.exists()) {
         LOG.error("File " + sourceFile.getPath() + " does not exist.");
         return;
      }

      File targetFile = new File(workingDirectory, filename);

      try {
         FileUtils.getFileUtils().rename(sourceFile, targetFile);
      } catch (IOException e) {
         LOG.error("Could not rename " + sourceFile.getAbsolutePath() + " to "
               + targetFile.getAbsolutePath());
      }

      sendCollectionMessage(targetFile);
   }

   public void handlePackedFile(String sourceDirectory, String workingDirectory,
         String relativePath, String filename) {
      File packedFile = new File(sourceDirectory + relativePath, filename);
      if (packedFile == null || !packedFile.exists()) {
         LOG.error("Packed file " + packedFile.getPath() + " does not exist.");
         return;
      }

      LOG.info("+++ begin analyzing packed file : " + filename);
      WMOFTP wmoftp = new WMOFTP(packedFile, workingDirectory, session, messageProducer);

      int numberOfBulletins = wmoftp.getNumberOfContainingBulletins();
      if (numberOfBulletins == 0) {
         raiseCorruptedDataAlert(filename);
         LOG.error("Packed file does not contain any valid bulletins. Archiving file to temporary directory.");
         if (cacheManager != null) {
            cacheManager.archiveFileToTemporaryDirectory(packedFile, false);
         } else {
            LOG.error("Could not archive file " + packedFile.getAbsolutePath()
                  + " to the temporary directory.");
         }
         return;
      }

      LOG.info("+++ extraction of " + numberOfBulletins
            + " bulletins complete. deleting package : " + filename);
      packedFile.delete();

      // clean up
      wmoftp = null;
   }

   private void raiseCorruptedDataAlert(String filename) {
      AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
      if (alertService == null) {
         LOG.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-SplittingTimerServiceImpl";
      String location = "Ingestion";
      String severity = "ERROR";
      String eventId = DataServiceAlerts.CORRUPTED_DATA_RECEIVED.getKey();

      List<Object> arguments = new ArrayList<Object>();
      Object cause = "Packed file " + filename + " does not contain any valid bulletins.";
      arguments.add(source);
      arguments.add(filename);
      arguments.add(cause);

      alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

   private List<File> scanForNewFiles(final String searchDirectory, final String[] include,
         final String[] exclude) {
      Pattern[] includePatterns = GlobalDataCollectionUtils.getPatternsFromStrings(include);
      Pattern[] excludePatterns = GlobalDataCollectionUtils.getPatternsFromStrings(exclude);

      IngestionFilenameFilter iff = new IngestionFilenameFilter(includePatterns, excludePatterns);
      GlobalDataCollectionUtils.listAllFilesIncludingSubdirectoriesFilteredSorted(searchDirectory,
            iff);

      int maximumNumberOfIncludedFiles = getMaxNumberOfIncludedNonpackedFiles();
      return iff.getSortedFiles(maximumNumberOfIncludedFiles);
   }

   public int getMaxNumberOfIncludedNonpackedFiles() {
      if (maxNumberOfIncludedNonpackedFiles == 0) {
         maxNumberOfIncludedNonpackedFiles = ConfigServiceFacade.getInstance().getInt(MAX_NUMBER_INCLUDED_UNPACKED_FILES);
      }
      return maxNumberOfIncludedNonpackedFiles;
   }

   public String[] getExcludePatterns() {
      if (excludePatterns == null) {
         String excludePatternString = ConfigServiceFacade.getInstance().getString(EXCLUDE_PATTERNS_KEY);
         excludePatternString = excludePatternString.replace(" ", "");
         excludePatterns = excludePatternString.split(";");
      }
      return excludePatterns;
   }

   public String[] getIncludePatterns() {
      if (includePatterns == null) {
         String includePatternString = ConfigServiceFacade.getInstance().getString(INCLUDE_PATTERNS_KEY);
         includePatternString = includePatternString.replace(" ", "");
         includePatterns = includePatternString.split(";");
      }
      return includePatterns;
   }

   public String getSourceDirectory() {
      if (sourceDirectory == null) {
         sourceDirectory = ConfigServiceFacade.getInstance().getString(HARNESS_INGESTING_DIRECTORY_KEY);
      }
      return sourceDirectory;
   }

   public String getWorkingDirectory() {
      if (workingDirectory == null) {
         workingDirectory = ConfigServiceFacade.getInstance().getString(HARNESS_WORKING_DIRECTORY_KEY);
      }
      return workingDirectory;
   }
   
   public String getFromReplicationDirectoryName() {
      if (fromReplicationDirectoryName == null) {
         fromReplicationDirectoryName = ConfigServiceFacade.getInstance().getString(REPLICATION_CONFIG_FROM_REPLICATION_FOLDER_KEY);
      }
      return fromReplicationDirectoryName;
   }

   public String getReplicationWorkingDirectory() {
      if (replicationWorkingDirectory == null) {
         replicationWorkingDirectory = new File(getWorkingDirectory(), getFromReplicationDirectoryName()).getPath();
      }
      return replicationWorkingDirectory;
   }

   public long getSplittingPeriod() {
      if (splittingPeriod == 0) {
         splittingPeriod = ConfigServiceFacade.getInstance().getLong(SPLITTING_TIMER_PERIOD_KEY);
      }
      return splittingPeriod;
   }

   public long getSplittingInitialDelay() {
      if (splittingInitialDelay == 0) {
         splittingInitialDelay = ConfigServiceFacade.getInstance().getLong(SPLITTING_TIMER_INITIAL_DELAY_KEY);
      }
      return splittingInitialDelay;
   }
}