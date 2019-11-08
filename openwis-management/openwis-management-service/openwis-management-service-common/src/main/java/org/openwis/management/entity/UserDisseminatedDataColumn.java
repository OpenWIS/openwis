/**
 *
 */
package org.openwis.management.entity;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ExchangedDataColumn enumeration.
 */
@XmlType(name = "UserDisseminatedDataColumn ")
@XmlEnum
public enum UserDisseminatedDataColumn {

   /** The date. */
   DATE("date"),

   /** The total size. */
   TOTAL_SIZE("size"),

   /** The number of files. */
   FILES_NUMBER("nbFiles"),

   /** The total size. */
   DISS_TOOL_TOTAL_SIZE("dissToolSize"),

   /** The number of files. */
   DISS_TOOL_FILES_NUMBER("dissToolNbFiles"),

   /** The user. */
   USER("userId");

   /** The attribute. */
   private final String attribute;

   /**
    * Instantiates a new user disseminated data column.
    *
    * @param attribute the attribute
    */
   private UserDisseminatedDataColumn(String attribute) {
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
