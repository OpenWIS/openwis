package org.openwis.dataservice.util;

/**
 * Singleton which provides access to the cache service provider.  Based on various deployment configurations
 * of the data and management services, the cache EJBs can either be retrieved locally or remotely.
 *
 */
public class CacheServiceProviderFactory {

   private static CacheServiceProviderFactory instance;
   
   private CacheServiceProvider provider;
   
   private CacheServiceProviderFactory() {
      // TEMP: If using a local setup, create a local cache service provider.
      this.provider = new RemoteCacheServiceProvider();
   }
   
   /**
    * Retrieves an instance of the cache service provider factory.
    */
   public static CacheServiceProviderFactory getInstance() {
      if (instance == null) {
         synchronized(CacheServiceProviderFactory.class) {
            if (instance == null) {
               CacheServiceProviderFactory.instance = new CacheServiceProviderFactory();
            }
         }
      }
      
      return CacheServiceProviderFactory.instance;
   }
   
   /**
    * Retrieve an instance of a cache service provider.
    */
   public CacheServiceProvider getProvider() {
      return provider;
   }
}
