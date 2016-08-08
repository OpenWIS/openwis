/**
 * 
 */
package org.openwis.datasource.server.init.agent;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.common.timer.SubscriptionTimerService;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.datasource.server.init.DataServiceTimerConfiguration;
import org.openwis.datasource.server.init.ServerAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SubscriptionTimerAgent implements ServerAgent {

   /** logger. */
   private static Logger logger = LoggerFactory.getLogger(SubscriptionTimerAgent.class);

   /** The Constant SERVICE_URL. */
   private static final String SERVICE_URL = JndiUtils
         .getString(DataServiceTimerConfiguration.SUBSCRUPTION_TIMER_URL_KEY);

   /** The Constant TIME_PERIOD. */
   private static final long TIME_PERIOD = JndiUtils
         .getLong(DataServiceTimerConfiguration.SUBSCRIPTION_TIMER_PERIOD_KEY);

   /**
    * Shutdown.
    *
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.openwis.datasource.server.init.ServerAgent#shutdown()
    */
   @Override
   public void shutdown() throws Exception {
      try {
         logger.info("Subscription Timer Agent is shutting down.");

         InitialContext initialContext;
         try {
            initialContext = new InitialContext();
            SubscriptionTimerService customTimer = (SubscriptionTimerService) initialContext
                  .lookup(SERVICE_URL);
            customTimer.destroy();
         } catch (NamingException e) {
            logger.error("Context not found.", e);
         }
         logger.info("Subscription Timer Agent shutdown completed."); //$NON-NLS-1$
      } catch (Throwable t) {
         logger.error("Error during Subscription Timer Agent shutdown.", t); //$NON-NLS-1$
      }
   }

   /**
    * Startup.
    *
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.openwis.datasource.server.init.ServerAgent#startup()
    */
   @Override
   public void startup() throws Exception {
      try {
         logger.info("Subscription Timer Agent started, looking for {}.", SERVICE_URL);

         InitialContext initialContext;
         try {
            initialContext = new InitialContext();
            SubscriptionTimerService customTimer = (SubscriptionTimerService) initialContext
                  .lookup(SERVICE_URL);
            customTimer.start(TIME_PERIOD);
         } catch (NamingException e) {
            logger.error("Context not found.", e);
         }
      } catch (Throwable t) {
         logger.error("Error during Subscription Timer initialisation.", t); //$NON-NLS-1$
      }
   }
}
