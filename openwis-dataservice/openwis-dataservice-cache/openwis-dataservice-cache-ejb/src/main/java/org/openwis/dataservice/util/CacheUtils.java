/**
 *
 */
package org.openwis.dataservice.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public final class CacheUtils implements ConfigurationInfo {

   /** Logging tool. */
   private static final Logger LOG = LoggerFactory.getLogger(CacheUtils.class);

   /**
    * Default constructor.
    * Builds a CacheUtils.
    */
   private CacheUtils() {
      // helper class
   }

   // -------------------------------------------------------------------------
   // Configuration Info
   // -------------------------------------------------------------------------

   /**
    * Gets the JNDI name for the incoming data queue.
    *
    * @return the resource name for the incoming data queue.
    */
   public static String getIncomingDataQueueName() {
      return INCOMING_DATA_QUEUE_NAME;
   }

   /**
    * Gets the JNDI name for the request queue.
    *
    * @return the resource name for the request queue.
    */
   public static String getRequestQueueName() {
      return REQUEST_QUEUE_NAME;
   }

   // -------------------------------------------------------------------------
   // Metadata URN Helpers
   // -------------------------------------------------------------------------

   /**
    * Description goes here.
    * @param pathname
    * @return
    */
   public static String parseMetadataURN(final String pathname) {
      return pathname;
   }

   // -------------------------------------------------------------------------
   // DirectoryScanner Helpers
   // -------------------------------------------------------------------------

   // -------------------------------------------------------------------------
   // JMS Helpers
   // -------------------------------------------------------------------------

   /**
    * Emits a new message to the incoming message queue reporting the arrival of the specified file.
    *
    * @param message
    */
   public static final void postTextMessage(final String queueName, final String text) {
      Connection connection = null;
      try {
         // obtain the initial JNDI context
         Context initCtx = new InitialContext();

         // perform JNDI lookup to obtain resource manager connection factory
         ConnectionFactory factory = (ConnectionFactory) initCtx.lookup("java:/JmsXA");

         Queue queue = (Queue) initCtx.lookup(queueName);

         // Step 4.Create a JMS Connection
         connection = factory.createConnection();
         // Step 5. Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Step 6. Create a JMS Message Producer
         MessageProducer sender = session.createProducer(queue);

         TextMessage message = session.createTextMessage(text);

         message.setJMSType(ProcessedRequest.class.getSimpleName());

         if (LOG.isTraceEnabled()) {
            LOG.trace("Sending message: type={}\n{}", message.getJMSType(), message.getText());
         }
         sender.send(message);
      }
      catch (Exception e) {
         LOG.error("Failed to post incoming data queue message due to {}", e);
      }
      finally {
         if (connection != null) {
            try {
               connection.close();
            }
            catch (Exception ignored) {
            }
         }
      }
   }

}
