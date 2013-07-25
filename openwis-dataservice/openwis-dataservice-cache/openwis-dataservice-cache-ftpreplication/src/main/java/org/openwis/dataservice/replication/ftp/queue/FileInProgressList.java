package org.openwis.dataservice.replication.ftp.queue;

import java.io.File;
import java.util.Vector;

/**
 * List of files currently being sent.
 */
public class FileInProgressList extends Vector<File> implements FileInProgressListMBean {

   @Override
   public int getSize() {
      return size();
   }

   @Override
   public String getContent() {
      return toString();
   }
}
