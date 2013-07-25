/**
 *
 */
package org.openwis.metadataportal.model.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.jdom.Element;
import org.openwis.metadataportal.model.category.Category;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
@JsonIgnoreProperties({"data"})
public class Metadata extends AbstractMetadata {

   private String localImportDate;

   private Category category;

   private Boolean fed;

   private String fncPattern;

   private String gtsCategory;

   private Boolean ingested;

   private String localDataSource;

   private String originator;

   private String overridenGtsCategory;

   private String overridenDataPolicy;

   private String overridenFncPattern;

   private Integer overridenPriority;

   private Integer priority;

   private String process;

   private String fileExtension;

   private String overridenFileExtension;

   private Collection<MetadataResource> privateDocs;

   private Collection<MetadataResource> publicDocs;

   private Map<Metadata, Boolean> relatedMetadatas;
   
   private boolean stopGap;

   /**
    * Default constructor.
    * Builds a Metadata.
    */
   public Metadata() {
      super();
   }

   /**
    * Default constructor.
    * Builds a Metadata.
    * @param urn
    */
   public Metadata(String urn) {
      super(urn);
   }

   /**
    * Default constructor.
    * Builds a Metadata.
    * @param id
    * @param urn
    */
   public Metadata(Integer id, String urn) {
      super(id, urn);
   }

   /**
    * Default constructor.
    * Builds a Metadata.
    * @param e
    */
   public Metadata(Element e) {
      super();
      setData(e);
   }

   /**
    * Gets the localImportDate.
    * @return the localImportDate.
    */
   public String getLocalImportDate() {
      return localImportDate;
   }

   /**
    * Sets the localImportDate.
    * @param localImportDate the localImportDate to set.
    */
   public void setLocalImportDate(String localImportDate) {
      this.localImportDate = localImportDate;
   }

   /**
    * Gets the category.
    * @return the category.
    */
   public Category getCategory() {
      return category;
   }

   /**
    * Sets the category.
    * @param category the category to set.
    */
   public void setCategory(Category category) {
      this.category = category;
   }

   /**
    * Gets the fed.
    * @return the fed.
    */
   public Boolean isFed() {
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
    * Gets the fncPattern.
    * @return the fncPattern.
    */
   public String getFncPattern() {
      return fncPattern;
   }

   /**
    * Sets the fncPattern.
    * @param fncPattern the fncPattern to set.
    */
   public void setFncPattern(String fncPattern) {
      this.fncPattern = fncPattern;
   }

   /**
    * Gets the gtsCategory.
    * @return the gtsCategory.
    */
   public String getGtsCategory() {
      return gtsCategory;
   }

   /**
    * Sets the gtsCategory.
    * @param gtsCategory the gtsCategory to set.
    */
   public void setGtsCategory(String gtsCategory) {
      this.gtsCategory = gtsCategory;
   }

   /**
    * Gets the ingested.
    * @return the ingested.
    */
   public Boolean isIngested() {
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
    * Gets the localDataSource.
    * @return the localDataSource.
    */
   public String getLocalDataSource() {
      return localDataSource;
   }

   /**
    * Sets the localDataSource.
    * @param localDataSource the localDataSource to set.
    */
   public void setLocalDataSource(String localDataSource) {
      this.localDataSource = localDataSource;
   }

   /**
    * Gets the originator.
    * @return the originator.
    */
   public String getOriginator() {
      return originator;
   }

   /**
    * Sets the originator.
    * @param originator the originator to set.
    */
   public void setOriginator(String originator) {
      this.originator = originator;
   }
   
   /**
    * Gets the overridenGtsCategory.
    * @return the overridenGtsCategory.
    */
   public String getOverridenGtsCategory() {
      return overridenGtsCategory;
   }
   
   /**
    * Sets the overridenGtsCategory.
    * @param overridenGtsCategory the overridenGtsCategory to set.
    */
   public void setOverridenGtsCategory(String overridenGtsCategory) {
      this.overridenGtsCategory = overridenGtsCategory;
   }
   
   /**
    * Gets the overridenDataPolicy.
    * @return the overridenDataPolicy.
    */
   public String getOverridenDataPolicy() {
      return overridenDataPolicy;
   }

   /**
    * Sets the overridenDataPolicy.
    * @param overridenDataPolicy the overridenDataPolicy to set.
    */
   public void setOverridenDataPolicy(String overridenDataPolicy) {
      this.overridenDataPolicy = overridenDataPolicy;
   }

   /**
    * Gets the overridenFncPattern.
    * @return the overridenFncPattern.
    */
   public String getOverridenFncPattern() {
      return overridenFncPattern;
   }

   /**
    * Sets the overridenFncPattern.
    * @param overridenFncPattern the overridenFncPattern to set.
    */
   public void setOverridenFncPattern(String overridenFncPattern) {
      this.overridenFncPattern = overridenFncPattern;
   }

   /**
    * Gets the overridenPriority.
    * @return the overridenPriority.
    */
   public Integer getOverridenPriority() {
      return overridenPriority;
   }

   /**
    * Sets the overridenPriority.
    * @param overridenPriority the overridenPriority to set.
    */
   public void setOverridenPriority(Integer overridenPriority) {
      this.overridenPriority = overridenPriority;
   }

   /**
    * Gets the priority.
    * @return the priority.
    */
   public Integer getPriority() {
      return priority;
   }

   /**
    * Sets the priority.
    * @param priority the priority to set.
    */
   public void setPriority(Integer priority) {
      this.priority = priority;
   }

   /**
    * Gets the process.
    * @return the process.
    */
   public String getProcess() {
      return process;
   }

   /**
    * Sets the process.
    * @param process the process to set.
    */
   public void setProcess(String process) {
      this.process = process;
   }

   /**
    * Gets the fileExtension.
    * @return the fileExtension.
    */
   public String getFileExtension() {
      return fileExtension;
   }

   /**
    * Sets the fileExtension.
    * @param fileExtension the fileExtension to set.
    */
   public void setFileExtension(String fileExtension) {
      this.fileExtension = fileExtension;
   }

   /**
    * Gets the overridenFileExtension.
    * @return the overridenFileExtension.
    */
   public String getOverridenFileExtension() {
      return overridenFileExtension;
   }

   /**
    * Sets the overridenFileExtension.
    * @param overridenFileExtension the overridenFileExtension to set.
    */
   public void setOverridenFileExtension(String overridenFileExtension) {
      this.overridenFileExtension = overridenFileExtension;
   }

   /**
    * Gets the privateDocs.
    * @return the privateDocs.
    */
   public Collection<MetadataResource> getPrivateDocs() {
      if (privateDocs == null) {
         privateDocs = new ArrayList<MetadataResource>();
      }
      return privateDocs;
   }

   /**
    * Sets the privateDocs.
    * @param privateDocs the privateDocs to set.
    */
   public void setPrivateDocs(Collection<MetadataResource> privateDocs) {
      this.privateDocs = privateDocs;
   }

   /**
    * Gets the publicDocs.
    * @return the publicDocs.
    */
   public Collection<MetadataResource> getPublicDocs() {
      if (publicDocs == null) {
         publicDocs = new ArrayList<MetadataResource>();
      }
      return publicDocs;
   }

   /**
    * Sets the publicDocs.
    * @param publicDocs the publicDocs to set.
    */
   public void setPublicDocs(Collection<MetadataResource> publicDocs) {
      this.publicDocs = publicDocs;
   }

   /**
    * Gets the relatedMetadatas.
    * @return the relatedMetadatas.
    */
   public Map<Metadata, Boolean> getRelatedMetadatas() {
      if (relatedMetadatas == null) {
         relatedMetadatas = new HashMap<Metadata, Boolean>();
      }
      return relatedMetadatas;
   }

   /**
    * Sets the relatedMetadatas.
    * @param relatedMetadatas the relatedMetadatas to set.
    */
   public void setRelatedMetadatas(Map<Metadata, Boolean> relatedMetadatas) {
      this.relatedMetadatas = relatedMetadatas;
   }
   
   /**
    * Flag this metadata as stop gap. 
    * (flag not persistent, used in aligner process in case of stop gap md)
    */
   public void setStopGap(boolean stopGap) {
      this.stopGap = stopGap;
   }
   
   /**
    * Get stop-gap flag for this metadata. 
    * (flag not persistent, used in aligner process in case of stop gap md)
    */
   public boolean isStopGap() {
      return stopGap;
   }
}
