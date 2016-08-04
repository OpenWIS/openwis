package org.openwis.datasource.server.mdb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.IncomingDSMessage;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.openwis.datasource.server.mdb.delegate.SubscriptionDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author AKKA Technologies / racaru
 */
@MessageDriven(activationConfig = {
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/IncomingDataQueue"),
      @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5")})
public class SubscriptionDataManager implements MessageListener {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(SubscriptionDataManager.class);

   /** The subscription delegate. */
   @EJB(name = "SubscriptionDelegate")
   private SubscriptionDelegate subscriptionDelegate;

   /**
   * injection Message Driven Context
   */
   @Resource
   private MessageDrivenContext mdc;

   /**
    * injection ConnectionFactory
    */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory cf;

   /**
    * injection queue
    */
   @Resource(mappedName = "java:/queue/RequestQueue")
   private Queue queue;

   /**
    * {@inheritDoc}
    * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
    */
   @Override
   public void onMessage(Message message) {
      if (message instanceof TextMessage) {
         TextMessage messageReceived = (TextMessage) message;
         try {
            logger.info("Received message:  {}", messageReceived.getText());

            // parse received message
            StringReader sr = new StringReader(messageReceived.getText());
            IncomingDSMessage incomingDSMessage = Serializer.deserialize(IncomingDSMessage.class,
                  sr);

            // process received message
            Collection<ProcessedRequest> processedRequests = subscriptionDelegate
                  .processMessage(incomingDSMessage);
            if (processedRequests != null && !processedRequests.isEmpty()) {
               sendRequests(processedRequests, incomingDSMessage);
            }
         } catch (JMSException e) {
            logger.error("Unable to read the message from the queue", e);
            mdc.setRollbackOnly();
         } catch (JAXBException e) {
            logger.error("Unable to deseialize the message from the queue", e);
            mdc.setRollbackOnly();
         } catch (Throwable t) {
            logger.error("Unexpected error !!", t);
            mdc.setRollbackOnly();
         }
      }
   }

   /**
    * Description goes here.
    *
    * @param processedRequests the processed messages
    * @param incomingDSMessage the incoming ds message
    */
   private void sendRequests(Collection<ProcessedRequest> processedRequests,
         IncomingDSMessage incomingDSMessage) {
      Connection connection = null;
      ProcessedRequest processedRequest = null;
      try {
         // Create queue connection
         // Step 4.Create a JMS Connection
         connection = cf.createConnection();
         // Step 5. Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Step 6. Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);
         for (ProcessedRequest pr : processedRequests) {
            processedRequest = pr;

            // Create the XML Request...
            ProcessedRequestMessage requestMessage = createRequestMessage(processedRequest.getId(),
                  incomingDSMessage.getProductDate(), incomingDSMessage.getProductId());
            String textMessage = createXMLRequest(requestMessage);
            TextMessage messageToSend = session.createTextMessage(textMessage);
            // Send message in the request queue
            messageProducer.send(messageToSend);
         }
      } catch (Throwable t) {
         logger.error("Unable to create message for the request queue", t);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (JMSException jme) {
               logger.error("Unable to properly close connection to the queue", jme);
            }
         }
      }
   }

   /**
    * Create the RequestMessage to serialize.
    *
    * @param id the id
    * @param productDate the product date
    * @param productId the product id
    * @return the RequestMessage
    */
   private static ProcessedRequestMessage createRequestMessage(Long id, String productDate, String productId) {
      ProcessedRequestMessage requestMessage = new ProcessedRequestMessage();
      requestMessage.setId(id);
      requestMessage.setProductDate(productDate);
      requestMessage.setProductId(productId);
      return requestMessage;
   }

   /**
    * Create the request message with the request id and serialize this object to xml.
    *
    * @param requestMessage the request message
    * @return the xml string.
    * @throws JAXBException exception if an error occurs.
    */
   private static String createXMLRequest(ProcessedRequestMessage requestMessage)
         throws JAXBException {
      StringWriter sw;
      sw = new StringWriter();
      Serializer.serialize(requestMessage, sw);
      return sw.toString();
   }

}
