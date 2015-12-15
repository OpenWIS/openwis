package org.openwis.management.service;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.openwis.management.config.PropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the ConfigService.
 * 
 * @author lmika
 *
 */
@Stateless(name="ConfigService")
@Remote(ConfigService.class)
public class ConfigServiceImpl implements ConfigService {
   
   private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);
   
   private PropertySource propertySource;
   
   @PostConstruct
   public void initialize() {
      propertySource = new PropertySource(PropertySource.OPENWIS_DATA_SERVICE);
//      // !!TEMP!!
//      File configFile = new File(SystemUtils.getUserDir(), "conf/openwis-dataservice.properties");
//      // !!END TEMP!!
//      
//      InputStream inStream = null;
//      try {
//         inStream = new FileInputStream(configFile);
//         
//         configProperties = new Properties();
//         configProperties.load(inStream);
//      } catch (IOException e) {
//         throw new RuntimeException("Cannot load configuration from '" + configFile + "'", e);
//      } finally {
//         IOUtils.closeQuietly(inStream);
//      }
   }

   @Override
   public String getString(String key) {
//      String value = JndiUtils.getString(key);
      String value = propertySource.getProperties().getProperty(key);
      return value;
   }
}
