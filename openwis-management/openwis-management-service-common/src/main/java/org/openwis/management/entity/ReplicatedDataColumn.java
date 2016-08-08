/**
 *
 */
package org.openwis.management.entity;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ReplicatedDataColumn enumeration.
 */
@XmlType(name = "ReplicatedDataColumn ")
@XmlEnum
public enum ReplicatedDataColumn {

   /** The date. */
   DATE("date"),

   /** The size. */
   SIZE("size"),

   /** The source. */
   SOURCE("source");

   /** The attribute. */
   private final String attribute;

   /**
    * Instantiates a new replicated data column.
    *
    * @param attribute the attribute
    */
   private ReplicatedDataColumn(String attribute) {
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
