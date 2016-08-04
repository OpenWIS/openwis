package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ProductMetadataColumn enumeration. <P>
 */
@XmlType(name = "ProcessedRequestColumn")
@XmlEnum
public enum ProcessedRequestColumn {

   /** The Creation date. */
   CREATION_DATE("pr.creationDate"),
   /** The Status of the result. */
   STATUS("pr.requestResultStatus"),
   /** The update frequency. */
   VOLUME("pr.size");

   /** The attribute. */
   private final String attribute;

   /**
    * Default constructor.
    * Builds a ProductMetadataColumn.
    *
    * @param attribute the attribute
    */
   private ProcessedRequestColumn(String attribute) {
      this.attribute = attribute;
   }

   /**
    * Gets the attribute.
    * @return the attribute.
    */
   public String getAttribute() {
      return attribute;
   }

}
