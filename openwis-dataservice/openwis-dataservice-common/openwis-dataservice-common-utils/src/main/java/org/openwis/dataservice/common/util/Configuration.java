package org.openwis.dataservice.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration. This class handles all configuration attributes for the Communication Gateway. 
 * <br>
 * 
 * @author AKKA Technologies
 */
public class Configuration {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(Configuration.class);

   /** resource bundle. */
   private static Properties prop;

   /**
    * Constructor.
    * Builds a Configuration.
    *
    * @param propertiesFile the properties file
    */
   public Configuration(String propertiesFile) {
      prop = new Properties();
      try {
         prop.load(getClass().getResourceAsStream("/" + propertiesFile));
      } catch (FileNotFoundException e) {
         logger.error("Error loading configuration file: " + e.getMessage(), e);
      } catch (IOException e) {
         logger.error("Error loading configuration file:  " + e.getMessage(), e);
      }
   }

   /**
    * Get the value for the given key. Try first in system properties then in vod resource bundle
    * 
    * @param key
    *            the key
    * @return the String value
    */
   public String getString(String key) {
      String result;
      String value = System.getProperty(key);
      if (value != null) {
         result = value;
      } else {
         result = prop.getProperty(key);
      }
      return result;
   }

   /**
    * Return a parsed property as a double.
    *
    * @param key property key to parse
    * @return Return a parsed property as a double
    */
   public double getDouble(String key) {
      return Double.parseDouble(getString(key).trim());
   }

   /**
    * Return a parsed property as an int.
    *
    * @param key property key to parse
    * @return Return a parsed property as an int
    */
   public int getInt(String key) {
      return Integer.parseInt(getString(key).trim());
   }

   /**
    * Return a parsed property as a short.
    *
    * @param key property key to parse
    * @return Return a parsed property as a short
    */
   public short getShort(String key) {
      return Short.parseShort(getString(key).trim());
   }

   /**
    * Return a parsed property as a long.
    *
    * @param key property key to parse
    * @return Return a parsed property as a long
    */
   public long getLong(String key) {
      return Long.parseLong(getString(key).trim());
   }
}
