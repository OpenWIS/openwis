/**
 * 
 */
package org.openwis.usermanagement.util;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ldap Data Source Connection. <P>
 * 
 */
public class JNDIConnectionUtils {
   
   /**
    * logger
    */
   private static Logger logger = LoggerFactory.getLogger(JNDIConnectionUtils.class);
   
   /**
    * @member: jndiBindingName
    */
   private final String jndiBindingName = "ws/ldapdatasourceservice";
   
   /**
    * @member: properties
    */
   private Properties properties;
   
   /**
    * Default constructor.
    * Builds a LdapDataSourceConnection.
    */
   public JNDIConnectionUtils() {
      initialize();
   }
   
   /**
    * Initialize.
    */
   private void initialize() {
      InitialContext ctx;
      try {
         ctx = new InitialContext();
         properties = (Properties) ctx.lookup(jndiBindingName);
      } catch (NamingException e1) {
         logger.error("Can not initialize the initial context JNDI", e1);
      }
   }
   
   /**
    * Get the value for the given key.
    * 
    * @param key
    *            the key
    * @return the String value
    */
   public String getString(String key) {
      return properties.getProperty(key);
   }
   
   /**
    * Get the value for the given key.
    * 
    * @param key
    *            the key
    * @return the Int value
    */
   public int getInt(String key) {
      return Integer.valueOf(properties.getProperty(key));
   }
   
   /**
    * Get the value for the given key.
    * 
    * @param key
    *            the key
    * @return the Long value
    */
   public Long getLong(String key) {
      return Long.valueOf(properties.getProperty(key));
   }

}
