package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The request result status enumeration. <P>
 */
@XmlType(name = "requestResultStatus")
@XmlEnum

public enum RequestResultStatus {

   /** */
   INITIAL,

   /** */
   CREATED,

   /** */
   EXTRACTED,

   /** */
   ONGOING_DISSEMINATION,

   /** */
   ONGOING_EXTRACTION,

   /** */
   DISSEMINATED,

   /** */
   FAILED,

   /** */
   WAITING_FOR_DISSEMINATION

}
