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
 */
public class ExtractionTimerListener implements ServletContextListener {

   /**
    * {@inheritDoc}
    * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextDestroyed(ServletContextEvent event) {
      //      InitialContext initialContext;
      //      try {
      //         initialContext = new InitialContext();
      //         InitTimerService customTimer = (InitTimerService) initialContext
      //               .lookup("dataservice/ExtractTimerService/remote");
      //         customTimer.destroy();
      //      } catch (NamingException e) {
      //         e.printStackTrace();
      //      }
   }

   /**
    * {@inheritDoc}
    * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
    */
   @Override
   public void contextInitialized(ServletContextEvent event) {
      //      InitialContext initialContext;
      //      try {
      //         initialContext = new InitialContext();
      //         InitTimerService customTimer = (InitTimerService) initialContext
      //               .lookup("dataservice/ExtractTimerService/remote");
      //         customTimer.start(3000);
      //      } catch (NamingException e) {
      //         e.printStackTrace();
      //      }

   }
}
