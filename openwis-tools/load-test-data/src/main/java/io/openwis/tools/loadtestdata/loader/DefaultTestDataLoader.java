package io.openwis.tools.loadtestdata.loader;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The default test data loader.
 */
public class DefaultTestDataLoader implements TestDataLoader {

   private final TestDataLocator profileLoader;
   private final MetadataLoader metadataLoader;
   private final CacheDataLoader cacheDataLoader;

   public DefaultTestDataLoader(TestDataLocator profileLocator, MetadataLoader metadataLoader, CacheDataLoader cacheDataLoader) {
      super();
      this.profileLoader = profileLocator;
      this.metadataLoader = metadataLoader;
      this.cacheDataLoader = cacheDataLoader;
   }

   @Override
   public void loadAll() {
      List<URL> metadatas = profileLoader.findMetadata();
      
      // Sort the metadatas alphabetically by the path's basename
      Collections.sort(metadatas, new Comparator<URL>() {
         @Override
         public int compare(URL o1, URL o2) {
            String baseName1 = new File(o1.getPath()).getName();
            String baseName2 = new File(o2.getPath()).getName();
            return baseName1.compareTo(baseName2);
         }
      });
      
      System.err.println("Loading test metadata");
      metadataLoader.uploadMetadata(metadatas);
      
      System.err.println("Loading test cache data");
      cacheDataLoader.loadCacheData();
   }
}
