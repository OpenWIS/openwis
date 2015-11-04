package org.openwis.dataservice.config;

import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.apache.commons.lang.Validate;

/**
 * An object factory which will return the configuration loaded as a properties file.
 * 
 * This implementation takes inspiration from, but does not use any material from:
 * 
 * jndi-properties-factory, written by Ingo Dueppe.
 * https://github.com/crowdcode-de/jndi-properties-factory
 *    
 */
public class PropertiesFactory implements ObjectFactory {
   
   private static final Logger log = Logger.getLogger(PropertiesFactory.class.getName());
   
   private final ResourceResolver resourceResolver;
   private final ConfigLoader configLoader;
   
   public PropertiesFactory() {
      this.resourceResolver = new SystemPropertyFileResourceResolver();
      this.configLoader = new CommonsConfigLoader();
   }

   @Override
   public Object getObjectInstance(Object obj, Name name, Context nameCtx,
         Hashtable<?, ?> environment) throws Exception {
      Validate.notNull(obj);
      String jndiName = obj.toString();      // The name this object factory is bound to
                                             // TODO: name and nameCtx might be a better way to get this property
      
      // Resolve the URL
      URL resourceUrl = resourceResolver.resolveFromMappedName(jndiName);
      if (resourceUrl == null) {
         throw new RuntimeException("No config resource mapped to JNDI name: " + jndiName + ".\n" +
               "  Set a system property with the key '" + jndiName + "' to the path of a properties file.");
      }
      
      // Load the properties
      Properties props = configLoader.loadConfig(resourceUrl);
      
      return props;
   }

}
