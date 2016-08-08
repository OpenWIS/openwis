package org.openwis.dataservice.dissemination;

import javax.ejb.Local;


@Local
public interface DisseminationStatusMonitor {		
	
	public void start();		
	
	public void stop();
	
	public boolean isPurgeStagingPostAlreadyRunning();
	
	public void setPurgeStagingPostRunning(boolean value);
}