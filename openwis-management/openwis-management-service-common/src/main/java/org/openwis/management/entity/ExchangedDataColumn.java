/**
 *
 */
package org.openwis.management.entity;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ExchangedDataColumn enumeration.
 */
@XmlType(name = "ExchangedDataColumn ")
@XmlEnum
public enum ExchangedDataColumn {

   /** The date. */
   DATE("date"),

   /** The total size. */
   TOTAL_SIZE("totalSize"),

   /** The number of metadata. */
   METADATA_NUMBER("nbMetadata"),

   /** The source. */
   SOURCE("source");

   /** The attribute. */
   private final String attribute;

   /**
    * Instantiates a new exchanged data column.
    *
    * @param attribute the attribute
    */
   private ExchangedDataColumn(String attribute) {
      this.attribute = attribute;
   }

   /**
    * Gets the attribute.
    *
    * @return the attribute
    */
   public String getAttribute() {
      return attribute;
   }
}
