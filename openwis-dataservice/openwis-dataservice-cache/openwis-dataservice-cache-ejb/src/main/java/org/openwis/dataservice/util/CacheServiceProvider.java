package org.openwis.dataservice.util;

import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.service.CacheExtraService;

/**
 * Provides access to the cache service beans.
 */
public interface CacheServiceProvider {

   /**
    * Provides access to the cache extra service.
    */
   CacheExtraService getCacheSrv();

   /**
    * Provides access to the cache index.
    */
   CacheIndex getCacheIndex();
}
