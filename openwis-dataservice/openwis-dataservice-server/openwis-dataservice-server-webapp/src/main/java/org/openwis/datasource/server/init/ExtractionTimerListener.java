/**
 * 
 */
package org.openwis.datasource.server.init;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * The listener interface for receiving extractionTimer events.
 *
 * @see ExtractionTimerEvent
 * 
 * XXX - To delete
 */
public class ExtractionTimerListener implements ServletContextListener {

   /**
    * {@inheritDoc}
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(ServletContextEvent event) {
   }

   /**
    * {@inheritDoc}
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
   }
}
