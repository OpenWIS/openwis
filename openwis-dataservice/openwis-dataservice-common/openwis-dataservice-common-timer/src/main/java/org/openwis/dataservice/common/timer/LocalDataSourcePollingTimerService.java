package org.openwis.dataservice.common.timer;

import java.util.Date;

import javax.ejb.Timer;

/**
 * The Interface SubscriptionTimerService. <P>
 * Explanation goes here. <P>
 */
public interface LocalDataSourcePollingTimerService {

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
    * Process recurrent subscription.
    * @param date the date
    */
   public void processPolling(Date date);

}
