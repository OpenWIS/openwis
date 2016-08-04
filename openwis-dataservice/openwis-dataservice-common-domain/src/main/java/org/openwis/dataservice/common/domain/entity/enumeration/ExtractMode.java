package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The mail attachment mode enumeration. <P>
 */
@XmlType(name = "extractMode")
@XmlEnum
public enum ExtractMode {

   /** */
   NOT_IN_LOCAL_CACHE,

   /** */
   GLOBAL

}
