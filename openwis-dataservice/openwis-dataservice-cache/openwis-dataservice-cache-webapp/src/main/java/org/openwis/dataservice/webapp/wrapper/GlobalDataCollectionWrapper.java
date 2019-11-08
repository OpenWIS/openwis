package org.openwis.dataservice.webapp.wrapper;

import javax.naming.NamingException;

import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.gts.GTSTimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wraps the Splitting of packed files and the Collection process. 
 */
public class GlobalDataCollectionWrapper {
	
	private GTSTimerService splittingTimerService;
	
	private CacheIndex cacheIndex;
	
   private final Logger LOG = LoggerFactory.getLogger(GlobalDataCollectionWrapper.class);

	public void start(){
		try {
         splittingTimerService = DataServiceCacheBeans.getInstance().getSplittingTimerService();
         cacheIndex = DataServiceCacheBeans.getInstance().getCacheIndex();
		}
		catch (NamingException e) {
         LOG.error(e.getMessage(), e);
		}
		
		cacheIndex.backupLastCollectDate();
		
		splittingTimerService.stop();
		
		// this resets the flag if the server crashed while service was running
		if (splittingTimerService.isTimerServiceRunning()) splittingTimerService.setTimerServiceStatus(false);
		
		splittingTimerService.start();
	}
	
	public void stop(){
		splittingTimerService.stop();
	}
}