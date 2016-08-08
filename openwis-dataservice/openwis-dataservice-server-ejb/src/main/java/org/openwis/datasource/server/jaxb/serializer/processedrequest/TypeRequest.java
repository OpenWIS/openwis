/**
 * 
 */
package org.openwis.datasource.server.jaxb.serializer.processedrequest;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
@XmlType
@XmlEnum(String.class)
public enum TypeRequest {

   /** */
   AD_HOC,

   /** */
   SUBSCRIPTION

}
