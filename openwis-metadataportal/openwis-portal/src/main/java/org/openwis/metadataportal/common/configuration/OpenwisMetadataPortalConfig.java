/**
 * 
 */
package org.openwis.metadataportal.common.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The configuration. <P>
 * This utility class is used to access the properties in the external properties file. <P>
 * 
 */
public final class OpenwisMetadataPortalConfig {

   /**
    * The resource bundle.
    */
   private static ResourceBundle ressourceBundle = ResourceBundle
         .getBundle("openwis-metadataportal");

   /**
    * Default constructor.
    * Builds a OpenwisMetadataPortalConfig.
    */
   private OpenwisMetadataPortalConfig() {
      super();
   }

   /**
    * Get the value for the given key. Try first in system properties then in vod resource bundle
    * @param key the key.
    * @return the String value.
    */
   public static String getString(String key) {
      String value = System.getProperty(key);
      if (value == null) {
         value = ressourceBundle.getString(key);
      }
      return value;
   }

   /**
    * Return a parsed property as a boolean.
    * @param key property key to parse.
    * @return Return a parsed property as a boolean.
    * @throws NumberFormatException if an error occurs.
    */
   public static boolean getBoolean(String key) throws NumberFormatException {
      return Boolean.parseBoolean(getString(key));
   }

   /**
    * Return a parsed property as a double.
    * @param key property key to parse.
    * @return Return a parsed property as a double.
    * @throws NumberFormatException if an error occurs.
    */
   public static double getDouble(String key) throws NumberFormatException {
      return Double.parseDouble(getString(key));
   }

   /**
    * Return a parsed property as an int.
    * @param key property key to parse.
    * @return Return a parsed property as an int.
    * @throws NumberFormatException if an error occurs.
    */
   public static int getInt(String key) throws NumberFormatException {
      return Integer.parseInt(getString(key));
   }

   /**
    * Return a parsed property as a short.
    * @param key property key to parse.
    * @return Return a parsed property as a short.
    * @throws NumberFormatException if an error occurs.
    */
   public static short getShort(String key) throws NumberFormatException {
      return Short.parseShort(getString(key));
   }

   /**
    * Return a parsed property as a list of (trimmed and non empty) strings.
    * @param key property key to parse.
    * @return Return a parsed property as a list of string.
    */
   public static List<String> getList(String key) {
      String s = getString(key);
      String[] valArray = s.split(",");
      List<String> values = new ArrayList<String>(valArray.length);
      for (String val : valArray) {
         String trimmedVal = val.trim();
         if (trimmedVal.length() > 0) {
            values.add(trimmedVal);
         }
      }
      return values;
   }
}
