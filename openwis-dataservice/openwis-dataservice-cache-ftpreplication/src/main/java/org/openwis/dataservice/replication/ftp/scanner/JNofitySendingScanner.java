package org.openwis.dataservice.replication.ftp.scanner;

import java.io.File;
import java.util.concurrent.BlockingDeque;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.openwis.dataservice.replication.ftp.queue.FileInProgressList;

/**
 * SendingScanner impl based on JNotify.
 */
public class JNofitySendingScanner extends SendingScanner {

   // Watch mask, specify events you care about,
   // JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
   // or JNotify.FILE_ANY for all events.
   private int mask = JNotify.FILE_ANY;

   // watch subtree?
   private boolean watchSubtree = false;

   // internal id used to remove a watch previously created
   private int watchID;

   /**
    * Builds a SendingScanner.
    */
   public JNofitySendingScanner(String path, BlockingDeque<File> queue,
         FileInProgressList inProgressSet) {
      super(path, queue, inProgressSet);

      this.mask = JNotify.FILE_CREATED;
      this.watchSubtree = false;
   }

   /**
    * Add watch on the directory
    */
   public void addWatch() {

      // add directory watch
      try {
         watchID = JNotify.addWatch(watchPath, mask, watchSubtree, new Listener());

      } catch (JNotifyException e) {
         LOG.error("Error durig add watch for directory=" + watchPath + " with mask=" + mask
               + " and watchSubtree=" + watchSubtree, e);
      }

      LOG.info("Add watch for directory=" + watchPath + " with mask=" + mask + " and watchSubtree="
            + watchSubtree);

      //Add the list of file from the scanner directory
      ReplicationFileFilter filter = new ReplicationFileFilter();
      listAllFilesIncludingSubdirectoriesFilteredSorted(watchPath, filter);

      for (File filename : filter.getSortedFiles()) {
         if (!queue.contains(filename)) {
            queue.add(filename);
         }
      }

   }

   /**
    * Remove watch on the directory.
    */
   public void removeWatch() {
      // watchID to use to remove watch:
      try {
         LOG.info("Remove watch for directory=" + watchPath);
         boolean res = JNotify.removeWatch(watchID);
         if (!res) {
            // invalid watch ID specified.
         }
      } catch (JNotifyException e) {
         LOG.error(e.getMessage(), e);
      } finally {
         // Clean BlockingDeque
         queue.clear();
      }
   }

   class Listener implements JNotifyListener {
      /**
       * 
       * {@inheritDoc}
       * @see net.contentobjects.jnotify.JNotifyListener#fileRenamed(int, java.lang.String, java.lang.String, java.lang.String)
       */
      public void fileRenamed(int wd, String rootPath, String oldName, String newName) {
         LOG.debug("renamed " + rootPath + " : " + oldName + " -> " + newName);
      }

      /**
       * 
       * {@inheritDoc}
       * @see net.contentobjects.jnotify.JNotifyListener#fileModified(int, java.lang.String, java.lang.String)
       */
      public void fileModified(int wd, String rootPath, String name) {
         LOG.debug("modified " + rootPath + " : " + name);
      }

      /**
       * 
       * {@inheritDoc}
       * @see net.contentobjects.jnotify.JNotifyListener#fileDeleted(int, java.lang.String, java.lang.String)
       */
      public void fileDeleted(int wd, String rootPath, String name) {
         LOG.debug("deleted " + rootPath + " : " + name);
      }

      /**
       * 
       * {@inheritDoc}
       * @see net.contentobjects.jnotify.JNotifyListener#fileCreated(int, java.lang.String, java.lang.String)
       */
      public void fileCreated(int wd, String rootPath, String name) {
         LOG.debug("created " + rootPath + " : " + name);

         File file = new File(rootPath, name);
         if (file.isFile()) {
            LOG.debug(name + " is a file or a link");
            queue.add(file);
         }
      }
   }
}
