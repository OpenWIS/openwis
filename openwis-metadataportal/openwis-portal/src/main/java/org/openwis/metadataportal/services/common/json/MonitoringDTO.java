/**
 * 
 */
package org.openwis.metadataportal.services.common.json;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MonitoringDTO<T> {

   private T object;

   private boolean running;

   private double progress;

   /**
    * Default constructor.
    * Builds a MonitoringDTO.
    * @param object
    */
   public MonitoringDTO(T object) {
      super();
      this.object = object;
   }

   /**
    * Gets the object.
    * @return the object.
    */
   public T getObject() {
      return object;
   }

   /**
    * Sets the object.
    * @param object the object to set.
    */
   public void setObject(T object) {
      this.object = object;
   }

   /**
    * Gets the running.
    * @return the running.
    */
   public boolean isRunning() {
      return running;
   }

   /**
    * Sets the running.
    * @param running the running to set.
    */
   public void setRunning(boolean running) {
      this.running = running;
   }

   /**
    * Gets the progress.
    * @return the progress.
    */
   public double getProgress() {
      return progress;
   }

   /**
    * Sets the progress.
    * @param progress the progress to set.
    */
   public void setProgress(double progress) {
      this.progress = progress;
   }
}
