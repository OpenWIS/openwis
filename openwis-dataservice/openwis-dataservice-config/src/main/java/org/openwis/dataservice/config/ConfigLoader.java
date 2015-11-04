package org.openwis.dataservice.config;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * A class that can load a configuration resource from a URL.
 */
public interface ConfigLoader {

   /**
    * Loads a config resource from a URL and returns it as a Properties instance.
    * 
    * @param url
    *       The URL to load
    * @return
    *       A Properties instance containing the configuration values
    * @throws IOException
    *       Thrown if there was an error loading the configuration file.
    */
   public Properties loadConfig(URL url) throws IOException;
}
