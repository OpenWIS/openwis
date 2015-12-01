/**
 * 
 */
package org.openwis.usermanagement.util;

import java.util.ResourceBundle;

/**
 * Ldap Data Source Connection. <P>
 * 
 */
public class JNDIConnectionUtils {

   private static ResourceBundle resourceBundle = ResourceBundle
         .getBundle("openwis-securityservice");

   /**
    * Default constructor.
    * Builds a LdapDataSourceConnection.
    */
   public JNDIConnectionUtils() {
   }

   /**
    * Get the value for the given key.
    * 
    * @param key
    *            the key
    * @return the String value
    */
   public String getString(String key) {
      return resourceBundle.getString(key);
   }

   /**
    * Get the value for the given key.
    * 
    * @param key
    *            the key
    * @return the Int value
    */
   public int getInt(String key) {
      return Integer.valueOf(resourceBundle.getString(key));
   }

   /**
    * Get the value for the given key.
    * 
    * @param key
    *            the key
    * @return the Long value
    */
   public Long getLong(String key) {
      return Long.valueOf(resourceBundle.getString(key));
   }

}
