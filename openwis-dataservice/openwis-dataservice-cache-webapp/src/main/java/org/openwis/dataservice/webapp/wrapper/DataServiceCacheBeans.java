package org.openwis.dataservice.webapp.wrapper;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.cache.CacheManager;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.dissemination.DisseminationManagerTimerService;
import org.openwis.dataservice.dissemination.DisseminationStatusMonitor;
import org.openwis.dataservice.gts.GTSTimerService;
import org.openwis.dataservice.gts.feeding.PackedFeedingTimerService;

/**
 * Facade to the data service cache EJBs
 * 
 * TODO: Some of these services are exposed to the configuration file.  Not sure this is ideal.
 */
public class DataServiceCacheBeans {
   
   private static final String DISSEMINATION_MANAGER_TIMER_SERVICE_JNDI_NAME = "ejb:openwis-dataservice/openwis-dataservice-cache-ejb/DisseminationManagerTimerService!org.openwis.dataservice.dissemination.DisseminationManagerTimerService";
   private static final String DISSEMINATION_STATUS_MONITOR_JNDI_NAME = "ejb:openwis-dataservice/openwis-dataservice-cache-ejb/DisseminationStatusMonitor!org.openwis.dataservice.dissemination.DisseminationStatusMonitor";
   
   public DisseminationManagerTimerService getDisseminationManagerTimerService() throws NamingException {
      return (DisseminationManagerTimerService) lookupBean(DISSEMINATION_MANAGER_TIMER_SERVICE_JNDI_NAME);
   }

   public DisseminationStatusMonitor getDisseminationStatusMonitor() throws NamingException {
      return (DisseminationStatusMonitor) lookupBean(DISSEMINATION_STATUS_MONITOR_JNDI_NAME);
   }
   
   public PackedFeedingTimerService getPackedFeedingTimerService() throws NamingException {
      final String packedFeedingTimerServiceJndiName = ConfigServiceFacade.getInstance().getString(ConfigurationInfo.PACKED_FEEDING_TIMER_SERVICE_URL_KEY);
      return (PackedFeedingTimerService) lookupBean(packedFeedingTimerServiceJndiName);
   }
   
   public GTSTimerService getSplittingTimerService() throws NamingException {
      final String gtsTimerServiceJndiName = ConfigServiceFacade.getInstance().getString(ConfigurationInfo.SPLITTING_TIMER_SERVICE_URL_KEY);
      return (GTSTimerService) lookupBean(gtsTimerServiceJndiName);
   }
   
   public CacheIndex getCacheIndex() throws NamingException {
      final String cacheIndexJndiName = ConfigServiceFacade.getInstance().getString(ConfigurationInfo.CACHE_INDEX_URL_KEY);
      return (CacheIndex) lookupBean(cacheIndexJndiName);
   }
   
   public CacheManager getCacheManager() throws NamingException {
      final String cacheManagerJndiName = ConfigServiceFacade.getInstance().getString(ConfigurationInfo.CACHE_MANAGER_URL_KEY);
      return (CacheManager) lookupBean(cacheManagerJndiName);
   }   
   
   public static DataServiceCacheBeans getInstance() {
      return new DataServiceCacheBeans();
   }
   
   private Object lookupBean(String jndiName) throws NamingException {
      InitialContext initCtx = new InitialContext();
      return initCtx.lookup(jndiName);
   }
}
