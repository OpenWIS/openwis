package org.openwis.usermanagement;

import javax.ejb.Timer;

/**
 * Control the authentication log. <P>
 * 
 */
public interface LogTimerService {

   public void start(long interval);

   /**
    * The action to be executed when the timer triggers.
    * @param timer the timer.
    */
   public void timeout(Timer timer);

   public void destroy();
}
