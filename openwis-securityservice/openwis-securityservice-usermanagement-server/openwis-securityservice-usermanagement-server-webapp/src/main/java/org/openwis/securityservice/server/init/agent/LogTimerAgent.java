package org.openwis.securityservice.server.init.agent;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.securityservice.server.init.ServerAgent;
import org.openwis.usermanagement.LogTimerService;
import org.openwis.usermanagement.util.JNDIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTimerAgent implements ServerAgent {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(LogTimerAgent.class);

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
         logger.info("Log Timer Agent is shutting down.");

         InitialContext initialContext;
         try {
            initialContext = new InitialContext();
            LogTimerService customTimer = (LogTimerService) initialContext
                  .lookup(JNDIUtils.getInstance().getLogTimerUrl());
            customTimer.destroy();
         } catch (NamingException e) {
            logger.error("Context not found.", e);
         }
         logger.info("Log Timer Agent shutdown completed."); //$NON-NLS-1$
      } catch (Throwable t) {
         logger.error("Error during Log Timer Agent shutdown.", t); //$NON-NLS-1$
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
         logger.info("Log Timer Agent started, looking for {}.", JNDIUtils.getInstance().getLogTimerUrl());

         InitialContext initialContext;
         try {
            initialContext = new InitialContext();
            LogTimerService customTimer = (LogTimerService) initialContext
                  .lookup(JNDIUtils.getInstance().getLogTimerUrl());
            customTimer.start(JNDIUtils.getInstance().getLogTimerPeriod());
         } catch (NamingException e) {
            logger.error("Context not found.", e);
         }
      } catch (Throwable t) {
         logger.error("Error during Log Timer initialisation.", t); //$NON-NLS-1$
      }
   }
}