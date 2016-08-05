package io.openwis.tools.loadtestdata.loader;

import java.net.URL;
import java.util.List;

/**
 * Manages the loading of metadata into the application.
 */
public interface MetadataLoader {
   /**
    * Uploads a set of metadata records identified with the given URL to OpenWIS.
    * 
    * @param resourceName
    * @return
    */
   public void uploadMetadata(List<URL> urls);
}
