package org.openwis.dataservice.common.domain.entity.request.dissemination;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * The dissemination job entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "disseminationJob")
@Entity
@Table(name = "OPENWIS_DISSEMINATION_JOB")
@SequenceGenerator(name = "DISSEMINATION_JOB_GEN", sequenceName = "DISSEMINATION_JOB_SEQ", initialValue = 1, allocationSize = 1)
public class DisseminationJob implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DISSEMINATION_JOB_GEN")
   @Column(name = "DISSEMINATION_JOB_ID")
   private Long id;

   /** The primary dissemination */
   @Column(name = "PRIMARY_DISSEMINATION", length = 255, nullable = false, unique = false)
   private String primaryDissemination;

   /** The secondary dissemination */
   @Column(name = "SECONDARY_DISSEMINATION", length = 255, nullable = false, unique = false)
   private String secondaryDissemination;

   /** The primary state */
   @Column(name = "PRIMARY_STATE", length = 255, nullable = false, unique = false)
   private String primaryState;

   /** The secondary state */
   @Column(name = "SECONDARY_STATE", length = 255, nullable = false, unique = false)
   private String secondaryState;

   /** The final state */
   @Column(name = "FINAL_STATE", length = 255, nullable = false, unique = false)
   private String finalState;

   /** The creation time stamp */
   @Column(name = "TIMESTAMP", nullable = false, unique = false)
   private long timeStamp;

   /** The processed request ID */
   @Column(name = "REQUEST_ID", nullable = false, unique = false)
   private long requestId;

   /** The file URI */
   @Column(name = "FILE_URI", nullable = false, unique = false)
   private String fileURI;
   
   @Column(name = "NUMBER_OF_FILES")
   private int numberOfFiles;
   
   @Column(name = "TOTAL_SIZE")
   private long totalSize;
   
   /**
    * Default constructor.
    */
   public DisseminationJob() {
      super();
   }

   /**
    * @return the id
    */
   public Long getId() {
      return id;
   }

   
   /**
    * Sets the primaryDissemination.
    * @param primaryDissemination the primaryDissemination to set.
    */
   public void setPrimaryDissemination(String primaryDissemination) {
      this.primaryDissemination = primaryDissemination;
   }

   /**
    * Gets the primaryDissemination.
    * @return the primaryDissemination.
    */
   public String getPrimaryDissemination() {
      return primaryDissemination;
   }
   
   /**
    * Sets the secondaryDissemination.
    * @param secondaryDissemination the secondaryDissemination to set.
    */
   public void setSecondaryDissemination(String secondaryDissemination) {
      this.secondaryDissemination = secondaryDissemination;
   }

   /**
    * Gets the secondaryDissemination.
    * @return the secondaryDissemination.
    */
   public String getSecondaryDissemination() {
      return secondaryDissemination;
   }

   /**
    * Sets the primaryState.
    * @param primaryState the primaryState to set.
    */
   public void setPrimaryState(String primaryState) {
      this.primaryState = primaryState;
   }

   /**
    * Gets the primaryState.
    * @return the primaryState.
    */
   public String getPrimaryState() {
      return primaryState;
   }
   
   /**
    * Sets the secondaryState.
    * @param secondaryState the secondaryState to set.
    */
   public void setSecondaryState(String secondaryState) {
      this.secondaryState = secondaryState;
   }

   /**
    * Gets the secondaryState.
    * @return the secondaryState.
    */
   public String getSecondaryState() {
      return secondaryState;
   }

   /**
    * Sets the finalState.
    * @param finalState the finalState to set.
    */
   public void setFinalState(String finalState) {
      this.finalState = finalState;
   }

   /**
    * Gets the finalState.
    * @return the finalState.
    */
   public String getFinalState() {
      return finalState;
   }
   
   /**
    * Gets the timeStamp.
    *
    * @return the timeStamp
    */
   public long getTimeStamp() {
      return timeStamp;
   }

   /**
    * Sets the timeStamp.
    *
    * @param timeStamp the timeStamp to set
    */
   public void setTimeStamp(long timeStamp) {
      this.timeStamp = timeStamp;
   }
   
   /**
    * Gets the requestId.
    *
    * @return the requestId
    */
   public long getRequestId() {
      return requestId;
   }

   /**
    * Sets the requestId.
    *
    * @param requestId the requestId to set
    */
   public void setRequestId(long requestId) {
      this.requestId = requestId;
   }

   /**
    * Sets the fileURI.
    * @param fileURI the fileURI to set.
    */
   public void setFileURI(String fileURI) {
      this.fileURI = fileURI;
   }

   /**
    * Gets the fileURI.
    * @return the fileURI.
    */
   public String getFileURI() {
      return fileURI;
   }

public int getNumberOfFiles() {
	return numberOfFiles;
}

public void setNumberOfFiles(int numberOfFiles) {
	this.numberOfFiles = numberOfFiles;
}

public long getTotalSize() {
	return totalSize;
}

public void setTotalSize(long totalSize) {
	this.totalSize = totalSize;
}

}
