/**
 * 
 */
package org.openwis.dataservice.common.timer;

import javax.ejb.Timer;

/**
 * Control the Dissemination Timer polling. <P>
 * Explanation goes here. <P>
 * 
 */
public interface DisseminationTimerService {

   public void start(long interval);

   /**
    * The action to be executed when the timer triggers.
    * @param timer the timer.
    */
   public void timeout(Timer timer);

   public void destroy();

}
