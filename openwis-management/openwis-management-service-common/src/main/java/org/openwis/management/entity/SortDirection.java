package org.openwis.management.entity;

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
