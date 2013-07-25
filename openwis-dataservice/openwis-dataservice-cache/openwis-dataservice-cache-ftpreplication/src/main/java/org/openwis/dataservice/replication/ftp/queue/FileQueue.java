package org.openwis.dataservice.replication.ftp.queue;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Class used to expose JMX interface of the file watcher queue
 *
 * @param <E>
 */
public class FileQueue<E> extends LinkedBlockingDeque<E> implements
      FileQueueMBean {

   @Override
   public int getSize() {
      return size();
   }
   
   @Override
   public String getContent() {
      return toString();
   }

}
