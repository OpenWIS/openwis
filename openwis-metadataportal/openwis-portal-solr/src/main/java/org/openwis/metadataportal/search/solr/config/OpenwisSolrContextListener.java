package org.openwis.metadataportal.search.solr.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openwis.metadataportal.search.solr.spatial.DataStoreFactory;
import org.openwis.metadataportal.search.solr.spatial.OpenwisGeometryTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialize OpenWIS Solr
 * @see ServletContextListener
 */
public class OpenwisSolrContextListener implements ServletContextListener {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(OpenwisSolrContextListener.class);

   /**
    * Context initialized.
    *
    * @param event the arg0
    * {@inheritDoc}
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
      logger.info("Initialize OpenWIS SolR webapp");

      // Configure Geometry Filter
      try {
         OpenwisGeometryTool.initialize();
      } catch (SecurityException e) {
         logger.error("Could not initialize FilterFactory !", e);
      } catch (NoSuchMethodException e) {
         logger.error("Could not initialize FilterFactory !", e);
      }

      // Configure SolR
      String solrHome = event.getServletContext().getRealPath("WEB-INF/classes/");
      logger.info("Set SolR Home to {}", solrHome);
      System.setProperty("solr.solr.home", solrHome);
      String solrDataDir = DataStoreFactory.bundle.getString("openwis.solr.data");
      logger.info("Set SolR Data Directory to {}", solrDataDir);
      System.setProperty("solr.data.dir", solrDataDir);
   }


   /**
    * Context destroyed.
    *
    * @param event the arg0
    * {@inheritDoc}
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(ServletContextEvent event) {
      logger.info("Destroy OpenWIS SolR webapp");
      try {
         OpenwisGeometryTool.getInstance().close();
      } catch (Exception e) {
         logger.error("Could not neatly close the spatial index writer", e);
      }
   }

}
