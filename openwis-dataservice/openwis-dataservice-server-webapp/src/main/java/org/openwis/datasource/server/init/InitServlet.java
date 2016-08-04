package org.openwis.datasource.server.init;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class InitServlet extends javax.servlet.http.HttpServlet {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(InitServlet.class);

   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
   }

   /**
    * {@inheritDoc}
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
   }

   /**
    * {@inheritDoc}
    * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
    */
   @Override
   public void init(ServletConfig cfg) throws ServletException {
      super.init(cfg);
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
   @Override
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
