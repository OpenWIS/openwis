package org.openwis.dataservice.replication.ftp;

import java.io.File;
import java.util.concurrent.BlockingDeque;

import org.openwis.dataservice.replication.ftp.config.Destination;
import org.openwis.dataservice.replication.ftp.pool.FTPConnectionPool;
import org.openwis.dataservice.replication.ftp.queue.FileInProgressList;
import org.openwis.dataservice.replication.ftp.queue.FileQueueWatcher;
import org.openwis.dataservice.replication.ftp.scanner.SendingScanner;

/**
 * Holds references for replication on a given destination.
 */
public class DestinationReplicator {

   /** The destination of the replication */
   private Destination destination;

   /** Pool of FTP Connection associated to this destination */
   private FTPConnectionPool connectionPool;

   /** Queue used to add event watched on the directory */
   private BlockingDeque<File> queue;

   /** List of files in progress of being sent */
   private FileInProgressList inProgressList;

   /** Directory scanner associated to the destination */
   private SendingScanner sendingScanner;

   /** Watcher of the file queue */
   private FileQueueWatcher fileQueueWatcher;

   public FileQueueWatcher getFileQueueWatcher() {
      return fileQueueWatcher;
   }

   public void setFileQueueWatcher(FileQueueWatcher fileQueueWatcher) {
      this.fileQueueWatcher = fileQueueWatcher;
   }

   public Destination getDestination() {
      return destination;
   }

   public void setDestination(Destination destination) {
      this.destination = destination;
   }

   public FTPConnectionPool getConnectionPool() {
      return connectionPool;
   }

   public void setConnectionPool(FTPConnectionPool connectionPool) {
      this.connectionPool = connectionPool;
   }

   public BlockingDeque<File> getQueue() {
      return queue;
   }

   public void setQueue(BlockingDeque<File> queue) {
      this.queue = queue;
   }

   public SendingScanner getSendingScanner() {
      return sendingScanner;
   }

   public void setSendingScanner(SendingScanner sendingScanner) {
      this.sendingScanner = sendingScanner;
   }

   public FileInProgressList getInProgressList() {
      return inProgressList;
   }

   public void setInProgressList(FileInProgressList inProgressList) {
      this.inProgressList = inProgressList;
   }

}
