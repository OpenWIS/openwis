package org.openwis.datasource.server.jaxb.serializer.incomingds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ProcessedRequestMessage. <P>
 * Explanation goes here. <P>
 */
@XmlRootElement(name = "processedrequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessedRequestMessage {

   /** The id. */
   @XmlAttribute
   private Long id;

   /** The product date. */
   @XmlElement
   private String productDate;

   /** The product id */
   @XmlElement
   private String productId;

   /**
    * Instantiates a new processed request message.
    */
   public ProcessedRequestMessage() {
      //Default Constructor
   }

   /**
    * Gets the productDate.
    * @return the productDate.
    */
   public String getProductDate() {
      return productDate;
   }

   /**
    * Sets the productDate.
    * @param productDate the productDate to set.
    */
   public void setProductDate(String productDate) {
      this.productDate = productDate;
   }

   /**
    * Gets the productId.
    * @return the productId.
    */
   public String getProductId() {
      return productId;
   }

   /**
    * Sets the productId.
    * @param productId the productId to set.
    */
   public void setProductId(String productId) {
      this.productId = productId;
   }

   /**
    * Gets the id.
    *
    * @return the id
    */
   public Long getId() {
      return id;
   }

   /**
    * Sets the id.
    *
    * @param id the new id
    */
   public void setId(Long id) {
      this.id = id;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object o) {
      return super.equals(o);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return super.hashCode();
   }

}
