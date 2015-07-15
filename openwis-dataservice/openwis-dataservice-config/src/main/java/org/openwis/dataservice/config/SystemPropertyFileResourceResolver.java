package org.openwis.dataservice.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Interprets the JNDI name as a system property that is set to the path of the properties file to load
 * from the file system.  The file system path is resolved from the user's home directory, unless it starts
 * with a slash, in which case it will be treated as an absolute path.
 * 
 * To use this:
 * 
 * 1. Set a system property with a key set to JNDI name of this ObjectFactory, and a value
 *    set to the filename to load the properties from.
 * 2. Map this object factory to the same JNDI name.
 */
public class SystemPropertyFileResourceResolver implements ResourceResolver {

   @Override
   public URL resolveFromMappedName(String jndiName) throws MalformedURLException {
      String propertyValue = System.getProperty(jndiName);
      
      if (StringUtils.isBlank(propertyValue)) {
         return null;
      }
      
      // If it starts with a slash, treat it as absolute.  Otherwise, resolve it against the
      File propertyFile = new File(propertyValue);
      if (propertyFile.isAbsolute()) {
         return propertyFile.toURI().toURL();
      } else {
         return new File(FileUtils.getUserDirectory(), propertyFile.toString()).toURI().toURL();
      }
   }
}
