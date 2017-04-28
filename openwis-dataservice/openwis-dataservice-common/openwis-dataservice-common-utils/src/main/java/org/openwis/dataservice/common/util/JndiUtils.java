/**
 *
 */
package org.openwis.dataservice.common.util;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 * @deprecated Do not use for configuration values.  Please use ConfigServiceFacade.
 *
 */
public final class JndiUtils {

   /** The Constant JNDI_CONFIGURATION_LOCATION. */
   public static final String JNDI_CONFIGURATION_LOCATION = "conf/openwis-dataservice";

   /** The instance. */
   private static JndiUtils instance = new JndiUtils();

   /** The logger. */
   private Logger logger = LoggerFactory.getLogger(JndiUtils.class);

   /** properties. */
   private Properties prop;

   /**
    * Instantiates a new jndi utils.
    */
   private JndiUtils() {
      try {
         InitialContext ctx = new InitialContext();
         prop = (Properties) ctx.lookup(JNDI_CONFIGURATION_LOCATION);
         logger.info("JNDI configuration : {}", prop);
      } catch (NamingException e1) {
         logger.error("Can not initialize the initial context JNDI.", e1);
      }
   }

   /**
    * Get the value for the given key. Try first in system properties then in vod resource bundle
    *
    * @param key
    *            the key
    * @return the String value
    */
   public static String getString(String key) {
      String result;
      String value = System.getProperty(key);
      if (value != null) {
         result = value.trim();
      } else if (instance != null && instance.prop != null) {
         result = StringUtils.trim(instance.prop.getProperty(key));
      } else {
         result = "NOT_FOUND";
      }
      return result;
   }

   /**
    * Return a parsed property as a double.
    *
    * @param key property key to parse
    * @return Return a parsed property as a double
    */
   public static double getDouble(String key) {
      return Double.parseDouble(JndiUtils.getString(key));
   }

   /**
    * Return a parsed property as an int.
    *
    * @param key property key to parse
    * @return Return a parsed property as an int
    */
   public static int getInt(String key) {
      return Integer.parseInt(JndiUtils.getString(key));
   }

   /**
    * Return a parsed property as a short.
    *
    * @param key property key to parse
    * @return Return a parsed property as a short
    */
   public static short getShort(String key) {
      return Short.parseShort(JndiUtils.getString(key));
   }

   /**
    * Return a parsed property as a long.
    *
    * @param key property key to parse
    * @return Return a parsed property as a long
    */
   public static long getLong(String key) {
      return Long.parseLong(JndiUtils.getString(key));
   }

}
