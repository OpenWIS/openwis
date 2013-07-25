/**
 * 
 */
package org.openwis.metadataportal.common.utils;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class that provides some methods. <P>
 * Explanation goes here. <P>
 * 
 */
public final class Utils {

   /**
    * Default constructor.
    * Builds a Utils.
    */
   private Utils() {

   }

   /**
    * Formats the request ID by adding some 7 '0' to the left side.
    * @param id the id of the request.
    * @return the request id formatted.
    */
   public static String formatRequestID(Long id) {
      return StringUtils.leftPad(id.toString(), 7, '0');
   }
   
}
