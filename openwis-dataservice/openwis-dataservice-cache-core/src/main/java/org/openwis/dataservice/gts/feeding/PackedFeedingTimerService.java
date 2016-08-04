package org.openwis.dataservice.gts.feeding;

import javax.ejb.Local;
import javax.ejb.Timer;
import javax.jms.Message;

@Local
public interface PackedFeedingTimerService{
	
	public void onMessage(Message message);
	
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
}