package org.openwis.datasource.server.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;

@Singleton
@Startup
public class InitServlet {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(InitServlet.class);

   @Resource
   TimerService timerService;

   @PostConstruct
   public void initialize() {
      logger.info(InitServlet.class.getSimpleName(), "Init timer");
      timerService.createTimer(20000, 0, "Wait 20s to allow management service to fully initialize");
   }
   /**
    * {@inheritDoc}
    * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
    */
   @Timeout
   public void init() {
      try {
         getInitService().init();
         logger.debug("InitServlet: System init completed."); //$NON-NLS-1$
      } catch (Throwable t) {
         logger.error("Error during InitServlet initialisation.", t); //$NON-NLS-1$
      }
   }

   /**
    * This method is called when the servlet container is shut down.
    */
   @PreDestroy
   public void destroy() {
      try {
         getInitService().destroy();
         logger.debug("InitServlet: System shutdown completed."); //$NON-NLS-1$
      } catch (Throwable t) {
         logger.error("Error during InitServlet shutdown.", t); //$NON-NLS-1$
      }

   }

   /**
    * Gets the init service instance.
    * @return  the init service instance.
    */
   private InitService getInitService() {
      return ServerAgentFactory.createInitService();
   }
}
