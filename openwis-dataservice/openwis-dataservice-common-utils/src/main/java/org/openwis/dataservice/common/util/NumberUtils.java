package org.openwis.dataservice.common.util;

/**
 * 
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public final class NumberUtils {

   /**
    * Default constructor.
    * Builds a NumberUtils.
    */
   private NumberUtils() {
      super();
   }

   /**
    * Description goes here.
    *
    * @param s the s
    * @return the double
    */
   public static double parseDouble(String s) {
      double result;
      try {
         result = Double.parseDouble(s);
      } catch (NumberFormatException e) {
         result = 0;
      } catch (NullPointerException e) {
         result = 0;
      }
      return result;
   }

   /**
    * Description goes here.
    *
    * @param s the s
    * @return the int
    */
   public static int parseInteger(String s) {
      int result;
      try {
         result = Integer.parseInt(s);
      } catch (NumberFormatException e) {
         result = 0;
      } catch (NullPointerException e) {
         result = 0;
      }
      return result;
   }

}
