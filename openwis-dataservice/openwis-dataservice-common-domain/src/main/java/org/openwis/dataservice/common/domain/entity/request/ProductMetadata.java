package org.openwis.dataservice.common.domain.entity.request;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The product metadata entity. <P>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "productMetadata")
@Entity
@Table(name = "OPENWIS_PRODUCT_METADATA")
@SequenceGenerator(name = "PRODUCT_METADATA_GEN", sequenceName = "PRODUCT_METADATA_SEQ", initialValue = 1, allocationSize = 1)
@NamedQueries({
      @NamedQuery(name = "ProductMetadata.FindByUrn", query = "SELECT pm FROM ProductMetadata pm WHERE lower(pm.urn) = lower(:urn)"),
      @NamedQuery(name = "ProductMetadata.count", query = "SELECT COUNT(*) FROM ProductMetadata pm"),
      @NamedQuery(name = "ProductMetadata.getLastStopGap", query = "SELECT pm FROM ProductMetadata pm WHERE pm.creationDate >= :since AND pm.stopGap IS TRUE"),
      @NamedQuery(name = "ProductMetadata.getAllStopGap", query = "SELECT pm FROM ProductMetadata pm WHERE pm.stopGap IS TRUE")})
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class ProductMetadata implements Serializable {

   /** The generated id. */
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_METADATA_GEN")
   @Column(name = "PRODUCT_METADATA_ID")
   private Long id;

   /** The creation date. */
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "CREATION_DATE", nullable = true)
   private Date creationDate;

   /** The URN */
   @Column(name = "URN", length = 255, nullable = false, unique = true)
   private String urn;

   /** */
   @Column(name = "DATA_POLICY", length = 255, nullable = false, unique = false)
   private String dataPolicy;

   /** */
   @Column(name = "IS_FED", nullable = false, unique = false)
   private Boolean fed;

   /** */
   @Column(name = "FNC_PATTERN", length = 1024, nullable = true, unique = false)
   private String fncPattern;

   /** */
   @Column(name = "GTS_CATEGORY", length = 255, nullable = false, unique = false)
   private String gtsCategory;

   /** */
   @Column(name = "IS_INGESTED", nullable = false, unique = false)
   private Boolean ingested;

   /** */
   @Column(name = "LOCAL_DATA_SOURCE", length = 255, nullable = false, unique = false)
   private String localDataSource;

   /** */
   @Column(name = "ORIGINATOR", length = 255, nullable = false, unique = false)
   private String originator;

   /** */
   @Column(name = "OVERRIDEN_DATA_POLICY", length = 255, nullable = true, unique = false)
   private String overridenDataPolicy;

   /** */
   @Column(name = "OVERRIDEN_GTS_CATEGORY", length = 255, nullable = true, unique = false)
   private String overridenGtsCategory;

   /** */
   @Column(name = "OVERRIDEN_FNC_PATTERN", length = 1024, nullable = true, unique = false)
   private String overridenFncPattern;

   /** */
   @Column(name = "OVERRIDEN_PRIORITY", nullable = true, unique = false)
   private Integer overridenPriority;

   /** */
   @Column(name = "PRIORITY", nullable = false, unique = false)
   private Integer priority;

   /** */
   @Column(name = "PROCESS", length = 255, nullable = false, unique = false)
   private String process;

   /** */
   @Column(name = "TITLE", length = 255, nullable = false, unique = false)
   private String title;

   /** */
   @Column(name = "FILE_EXTENSION", length = 255, nullable = true, unique = false)
   private String fileExtension;

   /** */
   @Column(name = "OVERRIDEN_FILE_EXTENSION", length = 255, nullable = true, unique = false)
   private String overridenFileExtension;

   /** */
   @OneToOne(optional = true, cascade = CascadeType.ALL)
   @JoinColumn(name = "UPDATE_FREQUENCY_ID", referencedColumnName = "UPDATE_FREQUENCY_ID")
   private UpdateFrequency updateFrequency;

   /** The stop gap. */
   @Column(name = "STOP_GAP", nullable = false, unique = false)
   private Boolean stopGap = false;

   /**
    * Default constructor.
    */
   public ProductMetadata() {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[{0}] {1}", urn, title);
   }

   /**
    * @return the id
    */
   public Long getId() {
      return id;
   }

   /**
    * @return the dataPolicy
    */
   public String getDataPolicy() {
      return dataPolicy;
   }

   /**
    * @param dataPolicy
    *            the dataPolicy to set
    */
   public void setDataPolicy(String dataPolicy) {
      this.dataPolicy = dataPolicy;
   }

   /**
    * @return the fed
    */
   public Boolean isFed() {
      return fed;
   }

   /**
    * @param fed
    *            the fed to set
    */
   public void setFed(boolean fed) {
      this.fed = Boolean.valueOf(fed);
   }

   /**
    * @return the fncPattern
    */
   public String getFncPattern() {
      return fncPattern;
   }

   /**
    * @param fncPattern
    *            the fncPattern to set
    */
   public void setFncPattern(String fncPattern) {
      this.fncPattern = fncPattern;
   }

   /**
    * @return the gtsCategory
    */
   public String getGtsCategory() {
      return gtsCategory;
   }

   /**
    * @param gtsCategory
    *            the gtsCategory to set
    */
   public void setGtsCategory(String gtsCategory) {
      this.gtsCategory = gtsCategory;
   }

   /**
    * @return the ingested
    */
   public Boolean isIngested() {
      return ingested;
   }

   /**
    * @param ingested
    *            the ingested to set
    */
   public void setIngested(boolean ingested) {
      this.ingested = Boolean.valueOf(ingested);
   }

   /**
    * @return the localDataSource
    */
   public String getLocalDataSource() {
      return localDataSource;
   }

   /**
    * @param localDataSource
    *            the localDataSource to set
    */
   public void setLocalDataSource(String localDataSource) {
      this.localDataSource = localDataSource;
   }

   /**
    * @return the originator
    */
   public String getOriginator() {
      return originator;
   }

   /**
    * @param originator
    *            the originator to set
    */
   public void setOriginator(String originator) {
      this.originator = originator;
   }

   /**
    * @return the overridenDataPolicy
    */
   public String getOverridenDataPolicy() {
      return overridenDataPolicy;
   }

   /**
    * @param overridenDataPolicy
    *            the overridenDataPolicy to set
    */
   public void setOverridenDataPolicy(String overridenDataPolicy) {
      this.overridenDataPolicy = overridenDataPolicy;
   }

   /**
    * @return the overridenGtsCategory
    */
   public String getOverridenGtsCategory() {
      return overridenGtsCategory;
   }

   /**
    * @param overridenGtsCategory
    *            the overridenGtsCategory to set
    */
   public void setOverridenGtsCategory(String overridenGtsCategory) {
      this.overridenGtsCategory = overridenGtsCategory;
   }

   /**
    * @return the overridenFncPattern
    */
   public String getOverridenFncPattern() {
      return overridenFncPattern;
   }

   /**
    * @param overridenFncPattern
    *            the overridenFncPattern to set
    */
   public void setOverridenFncPattern(String overridenFncPattern) {
      this.overridenFncPattern = overridenFncPattern;
   }

   /**
    * @return the overridenPriority
    */
   public Integer getOverridenPriority() {
      return overridenPriority;
   }

   /**
    * @param overridenPriority
    *            the overridenPriority to set
    */
   public void setOverridenPriority(int overridenPriority) {
      this.overridenPriority = Integer.valueOf(overridenPriority);
   }

   /**
    * @return the priority
    */
   public Integer getPriority() {
      return priority;
   }

   /**
    * @param priority
    *            the priority to set
    */
   public void setPriority(int priority) {
      this.priority = Integer.valueOf(priority);
   }

   /**
    * @return the process
    */
   public String getProcess() {
      return process;
   }

   /**
    * @param process
    *            the process to set
    */
   public void setProcess(String process) {
      this.process = process;
   }

   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title
    *            the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * @return the updateFrequency
    */
   public UpdateFrequency getUpdateFrequency() {
      return updateFrequency;
   }

   /**
    * Gets the fed.
    * @return the fed.
    */
   public Boolean getFed() {
      return fed;
   }

   /**
    * Sets the fed.
    * @param fed the fed to set.
    */
   public void setFed(Boolean fed) {
      this.fed = fed;
   }

   /**
    * Gets the ingested.
    * @return the ingested.
    */
   public Boolean getIngested() {
      return ingested;
   }

   /**
    * Sets the ingested.
    * @param ingested the ingested to set.
    */
   public void setIngested(Boolean ingested) {
      this.ingested = ingested;
   }

   /**
    * Gets the urn.
    * @return the urn.
    */
   public String getUrn() {
      return urn;
   }

   /**
    * Sets the urn.
    * @param urn the urn to set.
    */
   public void setUrn(String urn) {
      this.urn = urn;
   }

   /**
    * Sets the overridenPriority.
    * @param overridenPriority the overridenPriority to set.
    */
   public void setOverridenPriority(Integer overridenPriority) {
      this.overridenPriority = overridenPriority;
   }

   /**
    * Sets the priority.
    * @param priority the priority to set.
    */
   public void setPriority(Integer priority) {
      this.priority = priority;
   }

   /**
    * @param updateFrequency
    *            the updateFrequency to set
    */
   public void setUpdateFrequency(UpdateFrequency updateFrequency) {
      this.updateFrequency = updateFrequency;
   }

   /**
    * Gets the file extension.
    *
    * @return the file extension
    */
   public String getFileExtension() {
      return fileExtension;
   }

   /**
    * Sets the file extension.
    *
    * @param fileExtension the new file extension
    */
   public void setFileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
   }

   /**
    * Gets the overriden file extension.
    *
    * @return the overriden file extension
    */
   public String getOverridenFileExtension() {
      return overridenFileExtension;
   }

   /**
    * Sets the overriden file extension.
    *
    * @param overridenFileExtension the new overriden file extension
    */
   public void setOverridenFileExtension(String overridenFileExtension) {
      this.overridenFileExtension = overridenFileExtension;
   }

   /**
    * Checks if is stop gap.
    *
    * @return true, if is stop gap
    */
   public boolean isStopGap() {
      return stopGap;
   }

   /**
    * Sets the stop gap.
    *
    * @param stopGap the new stop gap
    */
   public void setStopGap(boolean stopGap) {
      this.stopGap = stopGap;
   }

   /**
    * Gets the creation date.
    *
    * @return the creation date
    */
   public Date getCreationDate() {
      return creationDate;
   }

   /**
    * Sets the creation date.
    *
    * @param creationDate the new creation date
    */
   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

}
