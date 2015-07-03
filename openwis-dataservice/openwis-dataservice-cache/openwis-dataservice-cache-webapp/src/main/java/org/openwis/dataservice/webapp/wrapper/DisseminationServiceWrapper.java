package org.openwis.dataservice.webapp.wrapper;

import javax.naming.InitialContext;
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
		InitialContext initCtx;
		try {
			initCtx = new InitialContext();
			// TODO: Unify
//			disseminationManagerTimerService = (DisseminationManagerTimerService) initCtx.lookup("openwis-dataservice/DisseminationManagerTimerService/local");			
//			disseminationStatusMonitor = (DisseminationStatusMonitor) initCtx.lookup("openwis-dataservice/DisseminationStatusMonitor/local");
			disseminationManagerTimerService = (DisseminationManagerTimerService) initCtx.lookup("ejb:openwis-dataservice/openwis-dataservice-cache-ejb/DisseminationManagerTimerService!org.openwis.dataservice.dissemination.DisseminationManagerTimerService");
			disseminationStatusMonitor = (DisseminationStatusMonitor) initCtx.lookup("ejb:openwis-dataservice/openwis-dataservice-cache-ejb/DisseminationStatusMonitor!org.openwis.dataservice.dissemination.DisseminationStatusMonitor");
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