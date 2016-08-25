package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ProductMetadataColumn enumeration. <P>
 */
@XmlType(name = "BlacklistInfoColumn")
@XmlEnum
public enum BlacklistInfoColumn {

   /** The USER. */
   USER("user"),
   /** The nbDisseminationWarnThreshold. */
   NB_WARN("nbDisseminationWarnThreshold"),
   /** The nbDisseminationBlacklistThreshold. */
   NB_BLACKLIST("nbDisseminationBlacklistThreshold"),
   /** The volDisseminationWarnThreshold. */
   VOL_WARN("volDisseminationWarnThreshold"),
   /** The volDisseminationBlacklistThreshold. */
   VOL_BLACKLIST("volDisseminationBlacklistThreshold"),
   /** The STATUS. */
   STATUS("status");

   /** The attribute. */
   private final String attribute;

   /**
    * Default constructor.
    * Builds a ProductMetadataColumn.
    *
    * @param attribute the attribute
    */
   private BlacklistInfoColumn(String attribute) {
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
