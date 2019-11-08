/**
 * 
 */
package org.openwis.metadataportal.model.metadata;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class MetadataResource {
   
   private String name;
   
   private Long changeDate;
   
   private byte[] data;
   
   /**
    * Gets the name.
    * @return the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * @param name the name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the changeDate.
    * @return the changeDate.
    */
   public Long getChangeDate() {
      return changeDate;
   }

   /**
    * Sets the changeDate.
    * @param changeDate the changeDate to set.
    */
   public void setChangeDate(Long changeDate) {
      this.changeDate = changeDate;
   }

   /**
    * Gets the data.
    * @return the data.
    */
   public byte[] getData() {
      return data;
   }

   /**
    * Sets the data.
    * @param data the data to set.
    */
   public void setData(byte[] data) {
      this.data = data;
   }
   
   

}
