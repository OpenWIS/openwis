package org.openwis.dataservice.common.domain.entity.enumeration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The SortDirection enumeration. <P>
 */
@XmlType(name = "SortDirection")
@XmlEnum
public enum SortDirection {

   /** The ASCENDING. */
   ASC,

   /** The DESCENDING. */
   DESC;

}
