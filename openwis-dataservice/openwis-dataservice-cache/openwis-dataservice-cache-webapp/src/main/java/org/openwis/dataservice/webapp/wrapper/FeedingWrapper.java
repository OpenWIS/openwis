package org.openwis.dataservice.webapp.wrapper;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.dataservice.gts.feeding.PackedFeedingTimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedingWrapper implements ConfigurationInfo {
	
	private PackedFeedingTimerService packedFeedingService;

   private final Logger LOG = LoggerFactory.getLogger(FeedingWrapper.class);
	
	public void start(){		
		InitialContext context;
		try {
			context = new InitialContext();
			packedFeedingService = (PackedFeedingTimerService) context.lookup(JndiUtils.getString(PACKED_FEEDING_TIMER_SERVICE_URL_KEY));		
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