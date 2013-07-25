package org.openwis.dataservice.webapp.wrapper;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.dataservice.gts.GTSTimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wraps the Splitting of packed files and the Collection process. 
 */
public class GlobalDataCollectionWrapper implements ConfigurationInfo{
	
	private GTSTimerService splittingTimerService;
	
	private CacheIndex cacheIndex;
	
   private final Logger LOG = LoggerFactory.getLogger(GlobalDataCollectionWrapper.class);

	public void start(){
		InitialContext initCtx;
		try {
			initCtx = new InitialContext();
			splittingTimerService = (GTSTimerService) initCtx.lookup(JndiUtils.getString(SPLITTING_TIMER_SERVICE_URL_KEY));
			cacheIndex = (CacheIndex) initCtx.lookup(JndiUtils.getString(CACHE_INDEX_URL_KEY));
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