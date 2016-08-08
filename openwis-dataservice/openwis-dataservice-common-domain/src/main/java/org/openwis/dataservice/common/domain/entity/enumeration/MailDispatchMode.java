package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The mail dispatch mode enumeration. <P>
 */
@XmlType(name = "mailDispatchMode")
@XmlEnum

public enum MailDispatchMode {

   /** */
   TO,

   /** */
   CC,

   /** */
   BCC

}
