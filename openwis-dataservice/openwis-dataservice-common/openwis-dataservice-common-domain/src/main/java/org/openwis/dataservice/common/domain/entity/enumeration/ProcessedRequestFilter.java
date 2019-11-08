package org.openwis.dataservice.common.domain.entity.enumeration;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * The ProcessedRequestFilter enumeration. <P>
 */
@XmlType(name = "ProcessedRequestFilter")
@XmlEnum
public enum ProcessedRequestFilter {

   
   ADHOC("ADHOC"),
   SUBSCRIPTION("SUBSCRIPTION"),
   BOTH(Arrays.asList("ADHOC", "SUBSCRIPTION"));
   

   /** The attribute (used as query filter). */
   private final Object attribute;

   /**
    * Builds a ProcessedRequestFilter.
    *
    * @param attribute the attribute
    */
   private ProcessedRequestFilter(Object attribute) {
      this.attribute = attribute;
   }

   /**
    * Gets the attribute.
    * @return the attribute.
    */
   public Object getAttribute() {
      return attribute;
   }


}
