package org.openwis.dataservice.replication.ftp.queue;

public interface FileInProgressListMBean {

   /**
    * Returns the number of elements in the queue
    * @return the number of elements in the queue
    */
   int getSize();

   /**
    * Clear the queue by removing all the elements.
    */
   void clear();
   
   /**
    * Get content.
    */
   String getContent();
}
