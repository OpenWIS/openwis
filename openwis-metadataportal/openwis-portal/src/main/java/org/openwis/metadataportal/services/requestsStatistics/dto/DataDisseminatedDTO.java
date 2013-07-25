/**
 * DataDisseminatedDTO
 */
package org.openwis.metadataportal.services.requestsStatistics.dto;

import java.util.Date;

import org.openwis.management.monitoring.UserDisseminationData;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class DataDisseminatedDTO {

   private Long id;

   private String userId;

   private Date date;

   private Integer dissToolNbFiles;

   private Long dissToolSize;

   public DataDisseminatedDTO() {
      super();
   }

   public DataDisseminatedDTO(UserDisseminationData userDisseminatedData) {
      setId(userDisseminatedData.getId());
      setUserId(userDisseminatedData.getUserId());
      setDate(userDisseminatedData.getDate().toGregorianCalendar().getTime());
      setDissToolNbFiles(userDisseminatedData.getNbFiles());
      setDissToolSize(userDisseminatedData.getSize());
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
    * Gets the userId.
    * @return the userId.
    */
   public String getUserId() {
      return userId;
   }

   /**
    * Sets the userId.
    * @param userId the userId to set.
    */
   public void setUserId(String userId) {
      this.userId = userId;
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
    * Gets the dissToolNbFiles.
    * @return the dissToolNbFiles.
    */
   public Integer getDissToolNbFiles() {
      return dissToolNbFiles;
   }

   /**
    * Sets the dissToolNbFiles.
    * @param dissToolNbFiles the dissToolNbFiles to set.
    */
   public void setDissToolNbFiles(Integer dissToolNbFiles) {
      this.dissToolNbFiles = dissToolNbFiles;
   }

   /**
    * Gets the dissToolSize.
    * @return the dissToolSize.
    */
   public Long getDissToolSize() {
      return dissToolSize;
   }

   /**
    * Sets the dissToolSize.
    * @param dissToolSize the dissToolSize to set.
    */
   public void setDissToolSize(Long dissToolSize) {
      this.dissToolSize = dissToolSize;
   }

}
