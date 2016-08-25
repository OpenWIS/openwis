package org.openwis.dataservice.common.util;

import javax.naming.NamingException;

import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to the configuration service.
 * 
 * @author lmika
 *
 */
public class ConfigServiceFacade {
   
   private static final Logger log = LoggerFactory.getLogger(ConfigServiceFacade.class);

   private static ConfigServiceFacade instance;
   
   private final ConfigService configService;
   
   private ConfigServiceFacade() {
      try {
         this.configService = ManagementServiceBeans.getInstance().getConfigService();
      } catch (NamingException e) {
         throw new RuntimeException("Error getting reference to ConfigService", e);
      }
   }
   
   
   public static ConfigServiceFacade getInstance() {
      if (instance == null) {
         synchronized (ConfigServiceFacade.class) {
            if (instance == null) {
               instance = new ConfigServiceFacade();
            }
         }
      }
      return instance;
   }

   /**
    * Returns the string value of a configuration property.
    * 
    * @param key
    *       The configuration property.
    * @return
    *       The property value.
    */
   public String getString(String key) {
      String value = configService.getString(key);
      log.debug("Using ConfigServiceFacade to look up '" + key + "'.  The value was '" + value + "'");
      return value;
   }
   
   public int getInt(String key) {
      return Integer.parseInt(getString(key));
   }
   
   public long getLong(String key) {
      return Long.parseLong(getString(key));
   }
}
