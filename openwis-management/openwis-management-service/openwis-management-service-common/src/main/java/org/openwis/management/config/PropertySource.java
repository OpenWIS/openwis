package org.openwis.management.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Retrieve the Properties instance for a configuration file.
 * 
 * @author lmika
 *
 */
public class PropertySource {
   
   public static final String OPENWIS_DATA_SERVICE = "openwis-dataservice";
   public static final String LOCAL_DATA_SOURCE = "localdatasourceservice";

   private final String name;
   private Properties properties;
   
   public PropertySource(String name) {
      this.name = name;
      this.properties = null;
   }
   
   public Properties getProperties() {
      if (properties == null) {
         synchronized(this) {
            if (properties == null) {
               properties = readProperties();
            }
         }
      }
      return properties;
   }
   
   private Properties readProperties() {
      // !!TEMP!!
      File homeDirectory = new File(System.getProperty("user.home"));
      File configDir = new File(homeDirectory, "conf");
      File configFile = new File(configDir, name + ".properties");
      
      System.err.println("Using configuration from '" + configFile + "'");
      // !!END TEMP!!
      
      InputStream inStream = null;
      Properties configProperties = null;
      try {
         inStream = new FileInputStream(configFile);
         
         try {
            configProperties = new Properties();
            configProperties.load(inStream);
            
            return configProperties;
         } finally {
            inStream.close();
         }
      } catch (IOException e) {
         throw new RuntimeException("Cannot load configuration from '" + configFile + "'", e);
      }
   }
}
