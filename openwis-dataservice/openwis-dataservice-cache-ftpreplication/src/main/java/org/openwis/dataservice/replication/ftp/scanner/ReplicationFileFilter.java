package org.openwis.dataservice.replication.ftp.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplicationFileFilter implements FileFilter {

   private final static Logger LOG = LoggerFactory.getLogger(ReplicationFileFilter.class);

   private SortedSet<File> sortedFiles;

   // --------------------------------------------

   private static class FileTimestampComparator implements Comparator<File>, Serializable {

      @Override
      public int compare(File f1, File f2) {
         return (f1.lastModified() < f2.lastModified()) ? -1 : (f1.lastModified() == f2
               .lastModified() ? (f1.getPath().equals(f2.getPath()) ? 0 : 1) : 1);

         //       return (f1.lastModified() < f2.lastModified()) ? 1 : (f1.lastModified() == f2
         //             .lastModified() ? 0 : -1);
      }
   };

   public ReplicationFileFilter() {
      this.sortedFiles = new TreeSet<File>(new FileTimestampComparator());
   }

   @Override
   public boolean accept(File file) {
      if (file.isDirectory())
         return true;

      sortedFiles.add(file);
      LOG.debug(file.getName() + " added to sortedFiles");
      return false;
   }

   public List<File> getSortedFiles() {
      return getSortedFiles(Integer.MAX_VALUE);
   }

   public List<File> getSortedFiles(int lengthOfHead) {
      List<File> sortedFileList = new ArrayList<File>(sortedFiles);

      int maxIndex = lengthOfHead;
      if (maxIndex > sortedFiles.size()) {
         maxIndex = sortedFiles.size();
      }

      return sortedFileList.subList(0, maxIndex);
   }
}
