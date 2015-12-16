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
   
   private PropertySource propertySource;
   
   @PostConstruct
   public void initialize() {
      propertySource = new PropertySource(PropertySource.OPENWIS_DATA_SERVICE);
   }

   @Override
   public String getString(String key) {
      String value = propertySource.getProperties().getProperty(key);
      return value;
   }
}
