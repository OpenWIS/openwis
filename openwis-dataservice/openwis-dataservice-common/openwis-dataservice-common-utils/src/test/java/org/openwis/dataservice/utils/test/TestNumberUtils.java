/**
 * 
 */
package org.openwis.dataservice.utils.test;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.dataservice.common.util.NumberUtils;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class TestNumberUtils {

   /** The Constant INTEGER. */
   private final static String[] INTEGER = {"10", "-10", "0", null};

   /** The Constant PARSED_INT. */
   private final static int[] PARSED_INT = {10, -10, 0, 0};

   /** The Constant DOUBLE. */
   private final static String[] DOUBLE = {"10.10", "-10.10", "0", null};

   /** The Constant PARSED_DOUBLE. */
   private final static double[] PARSED_DOUBLE = {10.10d, -10.10d, 0d, 0d};

   /**
    * Test parse double.
    */
   @Test
   public void TestParseDouble() {
      int index = 0;
      for (String value : DOUBLE) {
         Assert.assertEquals(PARSED_DOUBLE[index++], NumberUtils.parseDouble(value),
               Double.MIN_VALUE);
      }
   }

   /**
    * Test parse integer.
    */
   @Test
   public void TestParseInteger() {
      int index = 0;
      for (String value : INTEGER) {
         Assert.assertEquals(PARSED_INT[index++], NumberUtils.parseInteger(value));
      }
   }

}
