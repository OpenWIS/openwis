package org.openwis.dataservice.replication.ftp.scanner;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingDeque;

import org.openwis.dataservice.replication.ftp.queue.FileInProgressList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendingScanner {

   protected static final Logger LOG = LoggerFactory.getLogger(SendingScanner.class);

   // Directory path to watch
   protected String watchPath = null;

   // Queue used to add event watched on the directory
   protected BlockingDeque<File> queue;
   
   // List of files in progress of being set
   protected FileInProgressList inProgressList;
   
   /**
    * Builds a SendingScanner.
    * @param pathToScan
    */
   public SendingScanner(String path, BlockingDeque<File> queue, FileInProgressList inProgressSet) {
      File pf = new File(path);
      if (!pf.isDirectory()) {
         throw new RuntimeException(path + " is not a directory !!!");
      }

      this.watchPath = path;
      this.queue = queue;
      this.inProgressList = inProgressSet;
   }

   /**
    * Add watch on the directory
    */
   public abstract void addWatch();

   /**
    * Remove watch on the directory.
    */
   public abstract void removeWatch();

   protected void listAllFilesIncludingSubdirectoriesFilteredSorted(String path, FileFilter filter) {
      File fileDir = new File(path);
      if (fileDir == null || !fileDir.exists())
         return;
      if (fileDir.isDirectory()) {
         File[] listedFiles = fileDir.listFiles(filter);
         if (listedFiles != null) {
            for (File newPath : listedFiles) {
               listAllFilesIncludingSubdirectoriesFilteredSorted(
                     newPath.getPath().replace('\\', '/'), filter);
            }
         }
         return;
      }
   }

}
