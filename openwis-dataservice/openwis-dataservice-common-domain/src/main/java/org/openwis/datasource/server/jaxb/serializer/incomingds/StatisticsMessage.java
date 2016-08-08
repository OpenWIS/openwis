package org.openwis.datasource.server.jaxb.serializer.incomingds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class StatisticsMessage.
 * <P>
 * Explanation goes here.
 * <P>
 */
@XmlRootElement(name = "statisticsmessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsMessage {

   public static final String CMD_UPDATE_USER_EXTRACTED_DATA = "updateUserExtractedData";
   public static final String CMD_UPDATE_USER_DISSEMINATED_BY_TOOL_DATA = "updateUserDisseminatedByToolData";
   public static final String CMD_UPDATE_INGESTED_DATA = "updateIngestedData";
   public static final String CMD_UPDATE_REPLICATED_DATA = "updateReplicatedData";
   
   
   public String getCommand() {
      return command;
   }

   public void setCommand(String command) {
      this.command = command;
   }

   @XmlAttribute
   private String command;
   
   @XmlAttribute
   private String userId;

   @XmlAttribute
   private String date;

   @XmlAttribute
   private int nbFiles;

   @XmlAttribute
   private long totalSize;

   @XmlAttribute
   private String source;

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public int getNbFiles() {
      return nbFiles;
   }

   public void setNbFiles(int nbFiles) {
      this.nbFiles = nbFiles;
   }

   public long getTotalSize() {
      return totalSize;
   }

   public void setTotalSize(long totalSize) {
      this.totalSize = totalSize;
   }

   /**
    * Instantiates a new dissemination message.
    */
   public StatisticsMessage() {
      // Default Constructor
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return super.hashCode();
   }

}
