package org.openwis.dataservice.replication.ftp;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openwis.dataservice.replication.ftp.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Status Monitoring: monitor status file to start/stop replication or stop the whole FTPReplicator process.
 */
public class StatusMonitor implements Runnable {

   private static final String REPLICATION_STATUS_FILE_NAME = "replication-status";

   private static final String REPLICATION_STATUS_ENABLED = ".enabled";

   private static final String REPLICATION_STATUS_DISABLED = ".disabled";

   private static final String STOP_REPLICATOR_FILE_NAME = "stop-replicator";

   private static final int STATUS_MONITOR_PERIOD = 3000;

   private static final Logger LOG = LoggerFactory.getLogger(StatusMonitor.class);

   private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

   /**
    * Start status monitoring.
    * 
    * @param ftpReplicator the {@link FTPReplicator}
    */
   public static void startStatusMonitor(FTPReplicator ftpReplicator) throws IOException {
      LOG.info("Starting Status Monitor");
      StatusMonitor statusMonitor = new StatusMonitor(ftpReplicator);
      statusMonitor.init();
      // Schedule monitoring task
      LOG.debug("Scheduling StatusMonitor execution with period " + STATUS_MONITOR_PERIOD + " ms");
      executor.scheduleAtFixedRate(statusMonitor, 0L, STATUS_MONITOR_PERIOD, TimeUnit.MILLISECONDS);
   }

   private boolean enabled;

   private final FTPReplicator ftpReplicator;

   private File disabledStatusFile = new File(Config.getInstance().getReplicationStatusFolder(),
         REPLICATION_STATUS_FILE_NAME + REPLICATION_STATUS_DISABLED);

   private File enabledStatusFile = new File(Config.getInstance().getReplicationStatusFolder(),
         REPLICATION_STATUS_FILE_NAME + REPLICATION_STATUS_ENABLED);

   private File stopReplicatorFile = new File(Config.getInstance().getReplicationStatusFolder(),
         STOP_REPLICATOR_FILE_NAME);

   /**
    * Builds a StatusMonitor.
    * @param ftpReplicator {@link FTPReplicator} instance
    */
   public StatusMonitor(FTPReplicator ftpReplicator) {
      this.ftpReplicator = ftpReplicator;
   }

   /**
    * Status Monitor initialization.
    * @throws IOException if any IO occurs
    */
   private void init() throws IOException {
      // clean stop replicator file if any
      stopReplicatorFile.delete();

      if (enabledStatusFile.exists()) {
         setReplicationState(true);
      } else if (!disabledStatusFile.exists()) {
         // on first start, no status file exists -> enabled by default
         LOG.info("No status file found, creating enabled status (" + enabledStatusFile + ")");
         enabledStatusFile.createNewFile();
         setReplicationState(true);
      }
   }

   @Override
   public void run() {
      LOG.debug("Monitoring status - current status: " + enabled);

      LOG.debug("Check if stop file exists for " + stopReplicatorFile);
      if (stopReplicatorFile.exists()) {
         // Stop the process
         ftpReplicator.stopFTPReplicator();

         LOG.debug("Shutdown StatusMonitor execution");
         executor.shutdown();
      } else {
         if (enabled) {
            check(enabledStatusFile, disabledStatusFile);
         } else {
            check(disabledStatusFile, enabledStatusFile);
         }
      }
   }

   private void check(File currentStateFile, File otherStateFile) {
      LOG.debug("Check status file exists " + otherStateFile);
      if (otherStateFile.exists()) {
         // Switch replication state
         setReplicationState(!enabled);
      } else {
         LOG.debug("Update last modified time of status file " + currentStateFile);

         // touch file to notify replication is still alive
         try {
            if (!currentStateFile.exists()) {
               currentStateFile.createNewFile();
            } else {
               currentStateFile.setLastModified(System.currentTimeMillis());
            }
         } catch (IOException e) {
            LOG.error("Unable to touch state file " + currentStateFile, e);
         }
      }
   }

   private void setReplicationState(boolean enabled) {
      if (enabled) {
         ftpReplicator.enableReplication();
      } else {
         ftpReplicator.disableReplication();
      }
      this.enabled = enabled;
   }
}