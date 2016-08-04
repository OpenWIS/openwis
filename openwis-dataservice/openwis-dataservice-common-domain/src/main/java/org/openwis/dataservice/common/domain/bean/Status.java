/**
 *
 */
package org.openwis.dataservice.common.domain.bean;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
@XmlType(name = "msgStatus")
@XmlEnum
public enum Status {

   /** The ERROR. */
   ERROR,

   /** The ONGOIN g_ extraction. */
   ONGOING_EXTRACTION,

   /** The No Result Found. */
   NO_RESULT_FOUND,

   /** The EXTRACTED. */
   EXTRACTED;

   /**
    * Value.
    *
    * @return the string
    */
   public String value() {
      return name();
   }

   /**
    * From value.
    *
    * @param v the v
    * @return the status
    */
   public static Status fromValue(String v) {
      return valueOf(v);
   }

}
