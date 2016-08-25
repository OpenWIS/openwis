package org.openwis.management.service;

/**
 * Provides access to the application configuration.
 * 
 * @author lmika
 *
 */
public interface ConfigService {

   /**
    * Returns the value of a configuration key.  If the key is undefined, returns <code>null</code>.
    * 
    * @param key
    *       The configuration key.
    * @return
    *       The configured value, or <code>null</code>.
    */
   public String getString(String key);
}
