/**
 *
 */
package org.openwis.datasource.server.utils;

/**
 * Pattern ServiceLocator<P>
 * Explanation goes here. <P>
 *
 */

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.service.CacheExtraService;
import org.openwis.dataservice.common.util.JndiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ServiceProvider. <P>
 * Explanation goes here. <P>
 */
public final class ServiceProvider {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

   /** The cache extract service. */
   private static CacheExtraService cacheExtractService;

   /** The cache index. */
   private static CacheIndex cacheIndex;

   /**
    * Default constructor.
    * Builds a ServiceProvider.
    */
   private ServiceProvider() {
      super();
   }

   /**
    * Description goes here.
    *
    * @return the cache service
    */
   public static CacheExtraService getCacheSrv() {
      if (cacheExtractService == null) {
         cacheExtractService = loadCacheService();
      } else {
         // Check cache service, is dynamic case
         try {
            cacheExtractService.toString();
         } catch (RuntimeException e) {
            // Bad Karma, try reload
            cacheExtractService = loadCacheService();
         }
      }
      return cacheExtractService;
   }

   /**
    * Gets the cache index.
    *
    * @return the cache index
    */
   public static CacheIndex getCacheIndex() {
      if (cacheIndex == null) {
         cacheIndex = loadCacheIndex();
      } else {
         // Check cache service, is dynamic case
         try {
            cacheIndex.toString();
         } catch (RuntimeException e) {
            // Bad Karma, try reload
            cacheIndex = loadCacheIndex();
         }
      }
      return cacheIndex;
   }

   /**
    * Load cache service.
    *
    * @return the cache extra service
    */
   private static CacheExtraService loadCacheService() {
      CacheExtraService result = null;
      try {
         InitialContext context = new InitialContext();
         String cacheUrl = JndiUtils.getString(DataServiceConfiguration.CACHE_URL_KEY);
         result = (CacheExtraService) context.lookup(cacheUrl);
      } catch (NamingException e) {
         logger.error("Unable to locate the CacheExtraService", e);
      }
      return result;
   }

   /**
    * Load cache index.
    *
    * @return the cache index
    */
   private static CacheIndex loadCacheIndex() {
      CacheIndex result = null;
      try {
         InitialContext context = new InitialContext();
         String cacheUrl = JndiUtils.getString(DataServiceConfiguration.CACHE_INDEX_URL_KEY);
         result = (CacheIndex) context.lookup(cacheUrl);
      } catch (NamingException e) {
         logger.error("Unable to locate the CacheExtraService", e);
      }
      return result;
   }

}
