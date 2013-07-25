package org.openwis.dataservice.replication.ftp.scanner;

import java.io.File;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openwis.dataservice.replication.ftp.config.Config;
import org.openwis.dataservice.replication.ftp.queue.FileInProgressList;

/**
 * Manual impl of SendingScanner: a thread will monitor of file system.
 */
public class ManualSendingScanner extends SendingScanner {

   private ScheduledExecutorService executor;

   /**
    * Builds a ManualSendingScanner.
    */
   public ManualSendingScanner(String path, BlockingDeque<File> queue,
         FileInProgressList inProgressSet) {
      super(path, queue, inProgressSet);
   }

   @Override
   public void addWatch() {
      LOG.info("Starting manual watch for directory=" + watchPath);
      int period = Config.getInstance().getSendingScannerPeriod();
      executor = Executors.newSingleThreadScheduledExecutor();
      executor.scheduleAtFixedRate(new ScannerTask(), 0, period, TimeUnit.MILLISECONDS);
   }

   public void removeWatch() {
      LOG.info("Stopping manual watch for directory=" + watchPath);
      if (executor != null) {
         executor.shutdown();
      }
   }

   private class ScannerTask implements Runnable {
      @Override
      public void run() {
         LOG.debug("Scanning folder: " + watchPath);

         ReplicationFileFilter filter = new ReplicationFileFilter();
         listAllFilesIncludingSubdirectoriesFilteredSorted(watchPath, filter);

         for (File filename : filter.getSortedFiles()) {
            if (!queue.contains(filename) && !inProgressList.contains(filename)) {
               if (!filename.exists()) {
                  LOG.warn("!!! File does not exist anymore: " + filename);
                  continue;
               }
               
               LOG.debug("Adding file to queue: " + filename);
               queue.add(filename);
            }
         }

      }
   }

}
