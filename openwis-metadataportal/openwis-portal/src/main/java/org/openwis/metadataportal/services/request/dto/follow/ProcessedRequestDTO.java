/**
 *
 */
package org.openwis.metadataportal.services.request.dto.follow;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class ProcessedRequestDTO implements Comparable<ProcessedRequestDTO> {

   /**
    * The id of the processed request DTO.
    */
   private Long id;

   /**
    * The creation date.
    */
   private String creationDate;

   /**
    * The last update date.
    */
   private String submittedDisseminationDate;

   /**
    * The last update date.
    */
   private String completedDate;

   /**
    * The status.
    */
   private StatusDTO status;

   /**
    * The size.
    */
   private long size;

   /**
    * The URL.
    */
   private String url;

   /**
    * Gets the id.
    * @return the id.
    */
   public Long getId() {
      return id;
   }

   /**
    * Sets the id.
    * @param id the id to set.
    */
   public void setId(Long id) {
      this.id = id;
   }

   /**
    * Gets the creationDate.
    * @return the creationDate.
    */
   public String getCreationDate() {
      return creationDate;
   }

   /**
    * Sets the creationDate.
    * @param creationDate the creationDate to set.
    */
   public void setCreationDate(String creationDate) {
      this.creationDate = creationDate;
   }

   /**
    * Gets the submittedDisseminationDate.
    * @return the submittedDisseminationDate.
    */
   public String getSubmittedDisseminationDate() {
      return submittedDisseminationDate;
   }

   /**
    * Sets the submittedDisseminationDate.
    * @param submittedDisseminationDate the submittedDisseminationDate to set.
    */
   public void setSubmittedDisseminationDate(String submittedDisseminationDate) {
      this.submittedDisseminationDate = submittedDisseminationDate;
   }

   /**
    * Gets the completedDate.
    * @return the completedDate.
    */
   public String getCompletedDate() {
      return completedDate;
   }

   /**
    * Sets the completedDate.
    * @param completedDate the completedDate to set.
    */
   public void setCompletedDate(String completedDate) {
      this.completedDate = completedDate;
   }

   /**
    * Gets the status.
    * @return the status.
    */
   public StatusDTO getStatus() {
      return status;
   }

   /**
    * Sets the status.
    * @param status the status to set.
    */
   public void setStatus(StatusDTO status) {
      this.status = status;
   }

   /**
    * Gets the size.
    * @return the size.
    */
   public long getSize() {
      return size;
   }

   /**
    * Sets the size.
    * @param size the size to set.
    */
   public void setSize(long size) {
      this.size = size;
   }

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
    * {@inheritDoc}
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(ProcessedRequestDTO o) {
      return getCreationDate().compareTo(o.getCreationDate());
   }

}
