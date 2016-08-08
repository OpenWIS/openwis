package org.openwis.dataservice.gts;

import javax.ejb.Local;
import javax.ejb.Timer;

@Local
public interface GTSTimerService {
	
	/*
	 * Starts the timer service.
	 */
	public void start();
	
	/*
	 * Stops the timer service.
	 */
	public void stop();
	
	/*
	 * Do what is supposed to do, when the timer/timer period runs out.
	 */
	public void timeout(final Timer timer);	
	
	public boolean isTimerServiceRunning();
	
	public void setTimerServiceStatus(boolean status);
}