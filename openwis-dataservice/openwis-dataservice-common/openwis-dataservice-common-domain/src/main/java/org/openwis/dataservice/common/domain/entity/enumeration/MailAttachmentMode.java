package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The mail attachment mode enumeration. <P>
 */
@XmlType(name = "mailAttachmentMode")
@XmlEnum
public enum MailAttachmentMode {

   /** */
   EMBEDDED_IN_BODY,

   /** */
   AS_ATTACHMENT

}
