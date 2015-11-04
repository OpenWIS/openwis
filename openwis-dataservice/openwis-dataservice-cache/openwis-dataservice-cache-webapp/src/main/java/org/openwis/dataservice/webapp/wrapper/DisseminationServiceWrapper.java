package org.openwis.dataservice.webapp.wrapper;

import javax.naming.NamingException;

import org.openwis.dataservice.dissemination.DisseminationManagerTimerService;
import org.openwis.dataservice.dissemination.DisseminationStatusMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DisseminationServiceWrapper {
	
	private DisseminationManagerTimerService disseminationManagerTimerService;	
	private DisseminationStatusMonitor disseminationStatusMonitor;	
	
   private final Logger LOG = LoggerFactory.getLogger(DisseminationServiceWrapper.class);

	public void start(){
		try {
         disseminationManagerTimerService = DataServiceCacheBeans.getInstance().getDisseminationManagerTimerService();
         disseminationStatusMonitor = DataServiceCacheBeans.getInstance().getDisseminationStatusMonitor();
		} catch (NamingException e) {
         LOG.error(e.getMessage(), e);
		}					
		disseminationManagerTimerService.start();
		disseminationStatusMonitor.start();
	}
	
	public void stop(){
		disseminationManagerTimerService.stop();
		disseminationStatusMonitor.stop();
	}
}