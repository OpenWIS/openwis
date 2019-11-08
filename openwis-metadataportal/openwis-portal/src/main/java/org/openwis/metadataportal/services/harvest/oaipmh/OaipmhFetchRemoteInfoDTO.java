/**
 * 
 */
package org.openwis.metadataportal.services.harvest.oaipmh;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class OaipmhFetchRemoteInfoDTO {

   private String url;
   
   private boolean synchronization = false;

   /**
    * Gets the url.
    * @return the url.
    */
   public String getUrl() {
      return url;
   }

   /**
    * Sets the url.
    * @param url the url to set.
    */
   public void setUrl(String url) {
      this.url = url;
   }

   /**
    * Gets the synchronization.
    * @return the synchronization.
    */
   public boolean isSynchronization() {
      return synchronization;
   }

   /**
    * Sets the synchronization.
    * @param synchronization the synchronization to set.
    */
   public void setSynchronization(boolean synchronization) {
      this.synchronization = synchronization;
   }
}
