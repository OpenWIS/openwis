package org.openwis.dataservice.dissemination;

import javax.ejb.Local;

/**
 * Encapsulate the service that listens to the 4 dissemination request queues that are filled by the DisseminationManager.
 * It triggers the dissemination by using the corresponding wsdl call of the Dissemination harness.
 *
 * @author <a href="mailto:christoph.bortlisz@vcs.de">Christoph Bortlisz</a>
 */
@Local
public interface DisseminationManagerTimerService {		
	
	public void start();		
	
	public void stop();
}