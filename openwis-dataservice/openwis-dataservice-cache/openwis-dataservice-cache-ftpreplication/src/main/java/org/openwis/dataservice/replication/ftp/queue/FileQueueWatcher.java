package org.openwis.dataservice.replication.ftp.queue;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openwis.dataservice.replication.ftp.DestinationReplicator;
import org.openwis.dataservice.replication.ftp.FTPConnectionHandler;
import org.openwis.dataservice.replication.ftp.config.Destination;
import org.openwis.dataservice.replication.ftp.pool.FTPConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consume the files of the blocking queue to send them to their destination.
 */
public class FileQueueWatcher extends Thread {
   private static final Logger LOG = LoggerFactory.getLogger(FileQueueWatcher.class);

   /** Thread pool to execute sending task */
   private static ExecutorService executor = Executors.newCachedThreadPool();

   /** the replication info associated to the destination */
   private DestinationReplicator destinationReplicator;

   private boolean stopped;

   /**
    * Builds a FileQueueWatcher.
    * @param destinationReplicator the replication info associated to the destination
    */
   public FileQueueWatcher(DestinationReplicator destinationReplicator) {
      this.destinationReplicator = destinationReplicator;
   }

   public void stopWatcher() {
      stopped = true;
      // force queue blocking operation to quit
      this.interrupt();
   }

   /**
    * Shutdown the ExecutorService (who prevents the application to stop otherwise)
    */
   public static void stopWatcherExecutorService() {
      executor.shutdown();
   }

   @Override
   public void run() {
      Destination destination = destinationReplicator.getDestination();
      BlockingDeque<File> fileQueue = destinationReplicator.getQueue();
      FTPConnectionPool pool = destinationReplicator.getConnectionPool();
      FileInProgressList inProgressSet = destinationReplicator.getInProgressList();

      LOG.info("Starting FileQueue watcher for " + destination.getLocalPath());

      while (!stopped) {

         try {
            // Wait for new file in the queue
            File file = fileQueue.take();
            LOG.debug("Taking file " + file + " from FileQueue " + destination.getLocalPath()
                  + ", adding it in progress list");
            inProgressSet.add(file);

            // Get FTP connection
            FTPConnectionHandler ftpConnection;
            try {
               ftpConnection = pool.borrowObject();
            } catch (NoSuchElementException e) {
               // No connection available since timeout
               ftpConnection = null;
            }

            if (ftpConnection == null) {
               LOG.warn("Timeout when trying to get FTP Connection for destination "
                     + destination.getLocalPath() + ": " + FTPConnectionPool.MAX_WAIT / 1000
                     + "s exceeded");
               // Put the file again the queue
               fileQueue.push(file);
            } else {
               // Send file to destination
               executor.execute(new SendTask(file, pool, ftpConnection));
            }
         } catch (InterruptedException e) {
            LOG.debug("Watcher interrupted for FileQueue " + destination.getLocalPath());
         } catch (Exception e) {
            LOG.error(
                  "Unexpected error in FileQueueWatcher for destination "
                        + destination.getLocalPath(), e);
         }
      }

      LOG.info("Stopping FileQueue watcher for " + destination.getLocalPath());
   }

   /**
    * Task executed on thread pool to send the file.
    */
   private class SendTask implements Runnable {
      private File file;

      private FTPConnectionPool pool;

      private FTPConnectionHandler ftpConnection;

      public SendTask(File file, FTPConnectionPool pool, FTPConnectionHandler ftpConnection) {
         this.file = file;
         this.pool = pool;
         this.ftpConnection = ftpConnection;
      }

      public void run() {
         boolean sent = false;
         try {
            if (ftpConnection.upload(file)) {
               // file has been uploaded, delete local file
               LOG.debug("Delete local file " + file);
               file.delete();

               sent = true;
            }
         } catch (Exception e) {
            LOG.error("Unable to send file " + file + " to server "
                  + destinationReplicator.getDestination().getServer());
            LOG.debug("Push the file back into the queue");
         } finally {
            // in any case, release connection on pool
            try {
               pool.returnObject(ftpConnection);
            } catch (Exception e) {
               LOG.error("Unable to return connection to pool "
                     + destinationReplicator.getDestination().getLocalPath(), e);
            }
         }
         if (!sent) {
            // In case the file has not been sent (any errors), push the file back into the queue
            // for re-sending
            destinationReplicator.getQueue().push(file);
         }
         // remove file from in progress set
         destinationReplicator.getInProgressList().remove(file);
      }
   }
}
