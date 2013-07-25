/**
 * 
 */
package org.openwis.usermanagement.model.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "classOfService")
public enum ClassOfService {

   /**
    * @member: SILVER
    */
   SILVER,

   /**
    * @member: GOLD
    */
   GOLD,

   /**
    * @member: BRONZE
    */
   BRONZE

}
