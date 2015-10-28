package org.openwis.dataservice.config;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is responsible for locating the property resource to load from the JNDI name of this
 * object factory. 
 */
public interface ResourceResolver {

   /**
    * Given the JNDI name this ObjectFactory is mapped to, return a URL to a properties file that can be loaded.
    * If the JNDI name cannot be resolved, <code>null</code> is returned.
    * 
    * @param jndiName
    *       The JNDI name of the object factory.
    * @return
    *       A URL which references the properties file to load, or <code>null</code>.
    * @throws MalformedURLException
    *       Thrown if there was an error converting the resolved name into a URL
    */
   public URL resolveFromMappedName(String jndiName) throws MalformedURLException;
}
