/**
 *
 */
package org.openwis.management.entity;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The IngestedDataColumn enumeration.
 */
@XmlType(name = "IngestedDataColumn ")
@XmlEnum
public enum IngestedDataColumn {

   /** The date. */
   DATE("date"),

   /** The size. */
   SIZE("size");

   /** The attribute. */
   private final String attribute;

   /**
    * Instantiates a new ingested data column.
    *
    * @param attribute the attribute
    */
   private IngestedDataColumn(String attribute) {
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
