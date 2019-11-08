package org.openwis.datasource.server.jaxb.serializer.incomingds;

import java.text.MessageFormat;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class IncomingDSMessage. <P>
 * Explanation goes here. <P>
 */
@XmlRootElement(name = "incomingds")
@XmlAccessorType(XmlAccessType.FIELD)
public class IncomingDSMessage {

   /** The metadata urn. */
   @XmlElement
   private List<String> metadataURNs;

   /** The product date. */
   @XmlElement
   private String productDate;
   
   /** The product id */
   @XmlElement
   private String productId;

   /**
    * Instantiates a new incoming ds message.
    */
   public IncomingDSMessage() {
      //Default Constructor
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

   /**
    * Gets the metadataURN.
    * @return the metadataURN.
    */
   public List<String> getMetadataURNs() {
      return metadataURNs;
   }

   /**
    * Sets the metadataURN.
    * @param metadataURN the metadataURN to set.
    */
   public void setMetadataURNs(List<String> metadataURNs) {
      this.metadataURNs = metadataURNs;
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
    * To string.
    *
    * @return the string
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[URNs: {0}, Date: {1}, ProductId: {2}]", getMetadataURNs(), getProductDate(), getProductId());
   }

}
