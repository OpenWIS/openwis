/**
 * 
 */
package org.fao.oaipmh.configuration;

import java.util.ResourceBundle;

/**
 * The configuration. <P>
 * This utility class is used to access the properties in the external properties file. <P>
 * 
 */
public class OaipmhConfig {

   /**
    * The resource bundle.
    */
   private static ResourceBundle ressourceBundle = ResourceBundle
         .getBundle("oaipmh");

   /**
   * Default constructor.
   * Builds a OaipmhConfig.
   */
   private OaipmhConfig() {
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
}
