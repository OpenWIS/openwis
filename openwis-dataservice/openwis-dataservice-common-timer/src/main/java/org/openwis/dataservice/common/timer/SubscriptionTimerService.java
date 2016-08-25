package org.openwis.dataservice.common.timer;

import java.util.Date;

import javax.ejb.Timer;

/**
 * The Interface SubscriptionTimerService. <P>
 * Explanation goes here. <P>
 */
public interface SubscriptionTimerService {

   /**
    * Start.
    *
    * @param interval the interval
    */
   void start(long interval);

   /**
    * On timeout.
    *
    * @param timer the timer
    */
   void onTimeout(Timer timer);

   /**
    * Destroy.
    */
   void destroy();

   /**
    * Process all activated recurrent subscriptions against the reference date.
    *
    * @param date the date
    */
   public void processRecurrentSubscription(Date date);

}
