package org.openwis.datasource.server.service.impl;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import org.openwis.dataservice.common.exception.OpenWisException;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProductArrivalHandler {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ProductArrivalHandler.class);

   /** The connection factory. */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory cf;

   /** Incoming product JMS queue. */
   @Resource(mappedName = "java:/queue/IncomingDataQueue")
   private Queue queue;

   public boolean sendProductArrival(String productDate, List<String> urns) {
      boolean result = false;
      if (urns == null || urns.isEmpty()) {
         throw new OpenWisException("Product URN should not being null !");
      }
      if (productDate == null) {
         throw new OpenWisException(MessageFormat.format("ProductDate {0} is not valid!",
               productDate));
      }

      logger.info("Product incoming {} at {}", new Object[] {urns, productDate});

      // Build Message
      IncomingDSMessage msg = new IncomingDSMessage();
      msg.setMetadataURNs(urns);
      msg.setProductDate(productDate);
      msg.setProductId(productDate);

      // Send Message
      result = sendMessage(msg);
      return result;
   }

   /**
    * Send product arrival.
    *
    * @param productDate the product date
    * @param urns the urns
    */
   public boolean sendProductArrival(String productDate, String urn) {
      List<String> urns = new ArrayList<String>();
      urns.add(urn);
      return this.sendProductArrival(productDate, urns);
   }

   /**
    * Send message.
    *
    * @param msg the incoming product message
    */
   private boolean sendMessage(IncomingDSMessage msg) {
      boolean result = false;
      Connection connection = null;
      try {
         // Get JMS Session
         connection = cf.createConnection();
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

         // Create Text Message (Serialize message  with JAXB)
         MessageProducer messageProducer = session.createProducer(queue);
         StringWriter sw = new StringWriter();
         Serializer.serialize(msg, sw);
         TextMessage messageToSend = session.createTextMessage(sw.toString());

         // Send message in the request queue
         messageProducer.send(messageToSend);
         result = true;
      } catch (JMSException e) {
         logger.error("Could not send product arrival message: " + msg, e);
      } catch (JAXBException e) {
         logger.error("Could not serialize product arrival message: " + msg, e);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (JMSException jme) {
               logger.error("Unable to properly close connection to the queue", jme);
            }
         }
      }
      return result;
   }
}
