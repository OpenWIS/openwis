package org.openwis.management.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieve the Properties instance for a configuration file.
 * 
 * @author lmika
 *
 */
public class PropertySource {
   
   private static final Logger log = LoggerFactory.getLogger(PropertySource.class);
   
   public static final String OPENWIS_DATASERVICE_CONFIGDIR = "openwis.dataService.configDir";
   
   /**
    * The data service property file.
    */
   public static final String OPENWIS_DATA_SERVICE = "openwis-dataservice";
   
   /**
    * The local data source property file.
    */
   public static final String LOCAL_DATA_SOURCE = "localdatasourceservice";
   
   

   private final String name;
   private Properties properties;
   
   public PropertySource(String name) {
      this.name = name;
      this.properties = null;
   }
   
   /**
    * Retrieve the properties from this property source.
    * 
    * @return
    */
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
      final String propertiesFileName = name + ".properties";
      
      CompositeConfiguration compositeConfig = new CompositeConfiguration();
      
      // The configuration files on the file system.  Read the configuration
      String configDir = System.getProperty(OPENWIS_DATASERVICE_CONFIGDIR, new File(System.getProperty("user.home"), "conf").toString());
      File configFile = new File(configDir, propertiesFileName);
      
      if ((configFile.exists()) && (configFile.canRead())) {
         try {
            log.info("Using properties file: " + configFile);
            PropertiesConfiguration fileSystemConfig = readPropertiesConfiguration(configFile.toURI().toURL());
            compositeConfig.addConfiguration(fileSystemConfig);
         } catch (ConfigurationException e) {
            log.error("Failed to read properties from file system: " + configFile, e);
         } catch (MalformedURLException e) {
            log.error("Failed to read properties from file system: " + configFile, e);
         }
      } else {
         log.info("No properties file found at " + configFile + ".  Using embedded properties.");
      }
      
      
      // The embedded configuration files
      String embeddedPropertiesResourceName = "/conf/" + propertiesFileName;
      try {
         PropertiesConfiguration defaultConfig = readPropertiesConfiguration(getClass().getResource(embeddedPropertiesResourceName));
         compositeConfig.addConfiguration(defaultConfig);
      } catch (ConfigurationException e) {
         log.error("Failed to read embedded properties resource: " + embeddedPropertiesResourceName, e);
      }
      
      return ConfigurationConverter.getProperties(compositeConfig);
   }
   
   private PropertiesConfiguration readPropertiesConfiguration(URL resource) throws ConfigurationException {
      PropertiesConfiguration propertiesConfig = new PropertiesConfiguration();
      propertiesConfig.setDelimiterParsingDisabled(true);
      propertiesConfig.load(resource);
      
      return propertiesConfig;
   }
}
