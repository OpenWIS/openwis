/**
 *
 */
package org.openwis.datasource.server.init.agent;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.common.timer.LocalDataSourcePollingTimerService;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
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
public class LocalDataSourcePollingTimerAgent implements ServerAgent {

   /** logger. */
   private static Logger logger = LoggerFactory.getLogger(LocalDataSourcePollingTimerAgent.class);

   /** The Constant SERVICE_URL. */
   private final String serviceUrl;

   /** The Constant TIME_PERIOD. */
   private final long timePeriod;
   
   public LocalDataSourcePollingTimerAgent() {
      serviceUrl = ConfigServiceFacade.getInstance().getString(DataServiceTimerConfiguration.LOCAL_DATA_SOURCE_POLLING_URL_KEY);
      timePeriod = ConfigServiceFacade.getInstance().getLong(DataServiceTimerConfiguration.LOCAL_DATA_SOURCE_POLLING_PERIOD_KEY);
   }

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
         logger.info("LocalDataSource Polling Timer Agent is shutting down.");

         InitialContext initialContext;
         try {
            initialContext = new InitialContext();
            LocalDataSourcePollingTimerService customTimer = (LocalDataSourcePollingTimerService) initialContext
                  .lookup(serviceUrl);
            customTimer.destroy();
         } catch (NamingException e) {
            logger.error("Context not found.", e);
         }
         logger.info("LocalDataSource Polling Timer Agent shutdown completed."); //$NON-NLS-1$
      } catch (Throwable t) {
         logger.error("Error during LocalDataSource Polling Timer Agent shutdown.", t); //$NON-NLS-1$
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
         logger.info("Subscription Timer Agent started, looking for {}.", serviceUrl);

         InitialContext initialContext;
         try {
            initialContext = new InitialContext();
            LocalDataSourcePollingTimerService customTimer = (LocalDataSourcePollingTimerService) initialContext
                  .lookup(serviceUrl);
            customTimer.start(timePeriod);
         } catch (NamingException e) {
            logger.error("Context not found.", e);
         }
      } catch (Throwable t) {
         logger.error("Error during Subscription Timer initialisation.", t); //$NON-NLS-1$
      }
   }
}
