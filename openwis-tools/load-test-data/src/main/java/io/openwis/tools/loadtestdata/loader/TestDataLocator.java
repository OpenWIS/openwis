package io.openwis.tools.loadtestdata.loader;

import java.net.URL;
import java.util.List;

/**
 * Will load resources based on a specific profile
 */
public interface TestDataLocator {

   /**
    * Find metadata records for a specific profile.
    * 
    * @param profile
    * @return
    */
   public List<URL> findMetadata();
}
