package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ProductMetadataColumn enumeration. <P>
 */
@XmlType(name = "ProductMetadataColumn")
@XmlEnum
public enum ProductMetadataColumn {

   /** The URN. */
   URN("urn"),
   /** The FED. */
   FED("fed"),
   /** The GTS category. */
   GTS_CATEGORY("gtsCategory"),
   /** The INGESTED. */
   INGESTED("ingested"),
   /** The local datasource. */
   LOCAL_DATASOURCE("localDataSource"),
   /** The ORIGINATOR. */
   ORIGINATOR("originator"),
   /** The PROCESS. */
   PROCESS("process"),
   /** The TITLE. */
   TITLE("title"),
   /** The update frequency. */
   UPDATE_FREQUENCY("updateFrequency");

   /** The attribute. */
   private final String attribute;

   /**
    * Default constructor.
    * Builds a ProductMetadataColumn.
    *
    * @param attribute the attribute
    */
   private ProductMetadataColumn(String attribute) {
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
