/**
 * ExchangedDataDTO
 */
package org.openwis.metadataportal.services.catalog.dto;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openwis.management.monitoring.ExchangedData;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ExchangedDataDTO {

   private Long id;

   private XMLGregorianCalendar date;

   private Long totalSize;

   private Long nbMetadata;

   private String source;

   public ExchangedDataDTO() {
      super();
   }

   public ExchangedDataDTO(ExchangedData exchangedData) {
      this.setId(exchangedData.getId());
      this.setDate(exchangedData.getDate());
      this.setTotalSize(exchangedData.getTotalSize());
      this.setNbMetadata(exchangedData.getNbMetadata());
      this.setSource(exchangedData.getSource());
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
   public XMLGregorianCalendar getDate() {
      return date;
   }

   /**
    * Sets the date.
    * @param date the date to set.
    */
   public void setDate(XMLGregorianCalendar date) {
      this.date = date;
   }

   /**
    * Gets the totalSize.
    * @return the totalSize.
    */
   public Long getTotalSize() {
      return totalSize;
   }

   /**
    * Sets the totalSize.
    * @param totalSize the totalSize to set.
    */
   public void setTotalSize(Long totalSize) {
      this.totalSize = totalSize;
   }

   /**
    * Gets the nbMetadata.
    * @return the nbMetadata.
    */
   public Long getNbMetadata() {
      return nbMetadata;
   }

   /**
    * Sets the nbMetadata.
    * @param nbMetadata the nbMetadata to set.
    */
   public void setNbMetadata(Long nbMetadata) {
      this.nbMetadata = nbMetadata;
   }

   /**
    * Gets the source.
    * @return the source.
    */
   public String getSource() {
      return source;
   }

   /**
    * Sets the source.
    * @param source the source to set.
    */
   public void setSource(String source) {
      this.source = source;
   }
}
