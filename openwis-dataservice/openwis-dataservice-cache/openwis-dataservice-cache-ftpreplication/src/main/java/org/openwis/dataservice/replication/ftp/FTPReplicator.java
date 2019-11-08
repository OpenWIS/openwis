package org.openwis.dataservice.replication.ftp;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.openwis.dataservice.replication.ftp.config.Config;
import org.openwis.dataservice.replication.ftp.config.Destination;
import org.openwis.dataservice.replication.ftp.pool.FTPConnectionPool;
import org.openwis.dataservice.replication.ftp.queue.FileInProgressList;
import org.openwis.dataservice.replication.ftp.queue.FileQueue;
import org.openwis.dataservice.replication.ftp.queue.FileQueueWatcher;
import org.openwis.dataservice.replication.ftp.scanner.JNofitySendingScanner;
import org.openwis.dataservice.replication.ftp.scanner.ManualSendingScanner;
import org.openwis.dataservice.replication.ftp.scanner.SendingScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP Replication main class.
 */
public class FTPReplicator {
   private static final Logger LOG = LoggerFactory.getLogger(FTPReplicator.class);

   /** Map of DestinationReplicator, where keys are a destination local path */
   private Map<String, DestinationReplicator> destinationReplicators = new Hashtable<String, DestinationReplicator>();

   private MBeanServer mbs = null;

   /**
    * Initialize FTP Replication for each destination.
    */
   private void init() {
      LOG.info("---------- Start ----------");
      LOG.info("Initializing FTP Replicator");
      try {
         // Load configuration
         Config config = Config.getInstance();

         // Initialize replication for each destination
         Collection<Destination> destinations = config.getDestinations();
         for (Destination destination : destinations) {
            String destLocalPath = destination.getLocalPath();
            LOG.info("Initializing Replication for Destination: " + destLocalPath);
            DestinationReplicator destRepli = new DestinationReplicator();
            destinationReplicators.put(destLocalPath, destRepli);

            destRepli.setDestination(destination);

            LOG.info("Creating connection pool for " + destLocalPath);
            FTPConnectionPool pool = new FTPConnectionPool(destination);
            destRepli.setConnectionPool(pool);

            // create the blocking queue and inprogress set
            BlockingDeque<File> queue = new FileQueue<File>();
            destRepli.setQueue(queue);
            FileInProgressList inProgressList = new FileInProgressList();
            destRepli.setInProgressList(inProgressList);

            destRepli.setSendingScanner(createSendingScanner(config, destination, queue,
                  inProgressList));

            // Register BlockingDeque MBean
            registerMBean(destRepli.getQueue(), "openwis:name=QueueMBean-"
                  + destRepli.getDestination().getLocalPath());

            // Register FileInProgressList MBean
            registerMBean(destRepli.getInProgressList(), "openwis:name=InProgressMBean-"
                  + destRepli.getDestination().getLocalPath());

            // Register FTPConnectionPool MBean
            registerMBean(destRepli.getConnectionPool(), "openwis:name=FTPConnectionPoolMBean-"
                  + destRepli.getDestination().getLocalPath());

         }

         // Start status monitoring; enable/disable replication will be done in this monitor
         StatusMonitor.startStatusMonitor(this);

         LOG.info("FTP Replicator initialized");
      } catch (Exception e) {
         LOG.error("Error while initializing FTP Replicator", e);
      }
   }

   private SendingScanner createSendingScanner(Config config, Destination destination,
         BlockingDeque<File> queue, FileInProgressList inProgressList) {
      String pathToScan = config.getDestinationAbsoluteLocalFolder(destination);
      LOG.info("Creating Manual Sending Scanner; watching folder " + pathToScan);
      SendingScanner sendingScanner;
      if (config.isUseJNotifyScanner()) {
         sendingScanner = new JNofitySendingScanner(pathToScan, queue, inProgressList);
      } else {
         sendingScanner = new ManualSendingScanner(pathToScan, queue, inProgressList);
      }
      return sendingScanner;
   }

   /**
    * Enable replication.
    */
   public void enableReplication() {
      LOG.info("Enabling OpenWIS FTP Replication");
      try {
         for (DestinationReplicator destinationReplicator : destinationReplicators.values()) {
            // Create and start the File Queue Watcher
            FileQueueWatcher fileQueueWatcher = new FileQueueWatcher(destinationReplicator);
            destinationReplicator.setFileQueueWatcher(fileQueueWatcher);
            fileQueueWatcher.start();
            destinationReplicator.getSendingScanner().addWatch();
         }
      } catch (Exception e) {
         LOG.error("Unable to start replication; force disable", e);
         disableReplication();
      }
   }

   /**
    * Stop (disable) replication.
    */
   public void disableReplication() {
      LOG.info("Disabling OpenWIS FTP Replication");
      for (DestinationReplicator destinationReplicator : destinationReplicators.values()) {
         destinationReplicator.getSendingScanner().removeWatch();
         destinationReplicator.getFileQueueWatcher().stopWatcher();
      }
   }

   /**
    * Stop the process FTPReplicator.
    */
   public void stopFTPReplicator() {
      LOG.info("Stopping FTP Replicator");
      disableReplication();
      FileQueueWatcher.stopWatcherExecutorService();
   }

   private void registerMBean(Object mbeanPojo, String mbeanNameStr) {
      // Get the platform MBeanServer
      mbs = ManagementFactory.getPlatformMBeanServer();

      // Unique identification of MBeans
      ObjectName mbeanName = null;

      try {
         // Uniquely identify the MBeans and register them with the platform MBeanServer 
         mbeanName = new ObjectName(mbeanNameStr);

         mbs.registerMBean(mbeanPojo, mbeanName);
      } catch (Exception e) {
         LOG.error(e.getMessage(), e);
      }
   }

   public static void main(String[] args) {
      FTPReplicator replicator = new FTPReplicator();
      replicator.init();
   }
}
