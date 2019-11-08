/**
 * 
 */
package org.openwis.metadataportal.model.availability;

/**
 * An object to list all modules of data service with corresponding availability. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataServiceAvailability {

   /**
    * The replication process.
    */
   private Availability replicationProcess;

   /**
    * The ingestion module.
    */
   private Availability ingestion;

   /**
    * The incoming data queue.
    */
   private Availability subscriptionQueue;

   /**
    * The dissemination queue.
    */
   private Availability disseminationQueue;

   /**
    * Gets the replicationProcess.
    * @return the replicationProcess.
    */
   public Availability getReplicationProcess() {
      return replicationProcess;
   }

   /**
    * Sets the replicationProcess.
    * @param replicationProcess the replicationProcess to set.
    */
   public void setReplicationProcess(Availability replicationProcess) {
      this.replicationProcess = replicationProcess;
   }

   /**
    * Gets the ingestion.
    * @return the ingestion.
    */
   public Availability getIngestion() {
      return ingestion;
   }

   /**
    * Sets the ingestion.
    * @param ingestion the ingestion to set.
    */
   public void setIngestion(Availability ingestion) {
      this.ingestion = ingestion;
   }

   /**
    * Gets the subscriptionQueue.
    * @return the subscriptionQueue.
    */
   public Availability getSubscriptionQueue() {
      return subscriptionQueue;
   }

   /**
    * Sets the subscriptionQueue.
    * @param subscriptionQueue the subscriptionQueue to set.
    */
   public void setSubscriptionQueue(Availability subscriptionQueue) {
      this.subscriptionQueue = subscriptionQueue;
   }

   /**
    * Gets the disseminationQueue.
    * @return the disseminationQueue.
    */
   public Availability getDisseminationQueue() {
      return disseminationQueue;
   }

   /**
    * Sets the disseminationQueue.
    * @param disseminationQueue the disseminationQueue to set.
    */
   public void setDisseminationQueue(Availability disseminationQueue) {
      this.disseminationQueue = disseminationQueue;
   }
}
