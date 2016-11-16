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
import org.openwis.dataservice.extraction.ExtractFromCache;
import org.openwis.management.service.DisseminatedDataStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ServiceProvider. <P>
 * Explanation goes here. <P>
 * 
 * FIXME - This needs to be separated.
 */
public final class ServiceProvider {
	
   private static final String CACHE_SERVICE_JNDI_PREFIX = "ejb:openwis-dataservice/openwis-dataservice-cache-ejb";
	

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
         return getRemoteBean("ExtractFromCache", ExtractFromCache.class);
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
         return getRemoteBean("CacheIndex", CacheIndex.class);
      } catch (NamingException e) {
         logger.error("Unable to locate the CacheIndex", e);
      }
      return result;
   }

   /**
    * Provides access to a remote bean. This uses the standard remote EJB naming
    * protocol used by JBoss AS 7.1 See:
    * https://docs.jboss.org/author/display/AS71
    * /EJB+invocations+from+a+remote+client+using+JNDI
    * 
    * @param class1
    * @return
    * @throws NamingException
    */
   private static <T> T getRemoteBean(String beanName, Class<T> remoteInterfaceClass)
         throws NamingException {
      InitialContext initialContext = new InitialContext();
      String jndiName = String.format("%s/%s!%s", CACHE_SERVICE_JNDI_PREFIX, beanName, 
            remoteInterfaceClass.getName());
      
      logger.info("*** Getting remote bean: " + jndiName);
      return remoteInterfaceClass.cast(initialContext.lookup(jndiName));
   }
}
