package org.openwis.dataservice.webapp.wrapper;

import javax.naming.NamingException;

import org.openwis.dataservice.gts.feeding.PackedFeedingTimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedingWrapper {
	
	private PackedFeedingTimerService packedFeedingService;

   private final Logger LOG = LoggerFactory.getLogger(FeedingWrapper.class);
	
	public void start(){		
		try {
			packedFeedingService = DataServiceCacheBeans.getInstance().getPackedFeedingTimerService();		
		}
		catch (NamingException e) {
         LOG.error(e.getMessage(), e);
		}							
		packedFeedingService.stop();
		packedFeedingService.start();
	}		
	
	public void stop(){
		packedFeedingService.stop();
	}
}