package org.openwis.dataservice.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.security.auth.login.Configuration;

import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.lang.text.StrLookup;

/**
 * A config loader which uses Apache Commons Configuration to load the properties file.
 */
public class CommonsConfigLoader implements ConfigLoader {
   
   @Override
   public Properties loadConfig(URL url) throws IOException {

      try {
         PropertiesConfiguration config = new PropertiesConfiguration();
         setupInterpolator(config.getInterpolator());
         config.setDelimiterParsingDisabled(true);
         config.load(url);
         
         return ConfigurationConverter.getProperties(config.interpolatedConfiguration());
      } catch (ConfigurationException e) {
         throw new IOException("Failed to load configuration from resource: " + url.toString(), e);
      }
   }

   /**
    * Add a few missing interpolator lookups that are missing.
    *  
    * @param interpolator
    */
   private void setupInterpolator(ConfigurationInterpolator interpolator) {
      // the env prefix, which looks up the value of a variable based on the environment variable
      interpolator.registerLookup("env", new StrLookup() {
         @Override
         public String lookup(String name) {
            return System.getenv(name);
         }
      });
   }
}
