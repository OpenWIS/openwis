/**
 * 
 */
package org.openwis.metadataportal.model.metadata.source;

/**
 * Site source class for locally created or imported metadata. <P>
 * 
 */
public class SiteSource extends AbstractSource {
   
   private String userName;
   
   private String sourceId;
   
   private String sourceName;
   
   /**
    * Default constructor.
    * Builds a SiteSource.
    */
   public SiteSource() {
      super();
      setProcessType(ProcessType.LOCAL);
   }
   
   /**
    * Default constructor.
    * Builds a SiteSource.
    */
   public SiteSource(String userName, String sourceId, String sourceName) {
      this();
      this.userName = userName;
      this.sourceId = sourceId;
      this.sourceName = sourceName;
   }

   /**
    * Gets the userName.
    * @return the userName.
    */
   public String getUserName() {
      return userName;
   }

   /**
    * Sets the userName.
    * @param userName the userName to set.
    */
   public void setUserName(String userName) {
      this.userName = userName;
   }

   /**
    * Gets the sourceId.
    * @return the sourceId.
    */
   public String getSourceId() {
      return sourceId;
   }

   /**
    * Sets the sourceId.
    * @param sourceId the sourceId to set.
    */
   public void setSourceId(String sourceId) {
      this.sourceId = sourceId;
   }

   /**
    * Gets the sourceName.
    * @return the sourceName.
    */
   public String getSourceName() {
      return sourceName;
   }

   /**
    * Sets the sourceName.
    * @param sourceName the sourceName to set.
    */
   public void setSourceName(String sourceName) {
      this.sourceName = sourceName;
   }
   
}
