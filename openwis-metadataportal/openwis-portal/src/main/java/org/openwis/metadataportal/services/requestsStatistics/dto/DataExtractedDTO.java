/**
 * DataExtractedDTO
 */
package org.openwis.metadataportal.services.requestsStatistics.dto;

import java.util.Date;

import org.openwis.management.monitoring.UserDisseminationData;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class DataExtractedDTO {

   private Long id;

   private Date date;

   private Long size;

   private Long dissToolSize;

   public DataExtractedDTO() {
      super();
   }

   public DataExtractedDTO(UserDisseminationData userDisseminatedData) {
      setId(userDisseminatedData.getId());
      setDate(userDisseminatedData.getDate().toGregorianCalendar().getTime());
      setSize(userDisseminatedData.getSize());
      setDissToolSize(userDisseminatedData.getDissToolSize());
   }

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
    * Gets the date.
    * @return the date.
    */
   public Date getDate() {
      return date;
   }

   /**
    * Sets the date.
    * @param date the date to set.
    */
   public void setDate(Date date) {
      this.date = date;
   }

   /**
    * Gets the extracted.
    * @return the extracted.
    */
   public Long getSize() {
      return size;
   }

   /**
    * Sets the extracted.
    * @param extracted the extracted to set.
    */
   public void setSize(Long size) {
      this.size = size;
   }

   /**
    * Gets the disseminated.
    * @return the disseminated.
    */
   public Long getDissToolSize() {
      return dissToolSize;
   }

   /**
    * Sets the disseminated.
    * @param disseminated the disseminated to set.
    */
   public void setDissToolSize(Long dissToolSize) {
      this.dissToolSize = dissToolSize;
   }
}
