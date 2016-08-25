package org.openwis.datasource.server.mdb;

import java.io.StringReader;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;

import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.DisseminationMessage;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.openwis.datasource.server.mdb.delegate.ExtractionDelegate;
import org.openwis.datasource.server.utils.QueueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author AKKA Technologies / racaru
 */
@MessageDriven(activationConfig = {
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/RequestQueue"),
      @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5")})
public class ExtractManager implements MessageListener {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ExtractManager.class);

   /**
    * injection Message Driven Context
    */
   @Resource
   private MessageDrivenContext mdc;

   /**
    * The entity manager.
    */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The extraction delegate. */
   @EJB
   private ExtractionDelegate extractionDelegate;

   /**
    * injection ConnectionFactory
    */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory connectionFactory;

   /**
    * injection queue
    */
   @Resource(mappedName = "java:/queue/DisseminationQueue")
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
            logger.info("Received message: {}", message);
            logger.info("Message text: {}", messageReceived.getText());

            // parse received message
            StringReader sr = new StringReader(messageReceived.getText());
            ProcessedRequestMessage requestMessageDeserialize = Serializer.deserialize(
                  ProcessedRequestMessage.class, sr);

            Status status = extractionDelegate.processMessage(requestMessageDeserialize);

            // Send request to dissemination if necessary
            if (Status.EXTRACTED.equals(status) || Status.NO_RESULT_FOUND.equals(status)) {
               sendDisseminationMessage(requestMessageDeserialize.getId());
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
    * @param processedRequest the processed request
    */
   private void sendDisseminationMessage(Long prId) {

      Connection connection = null;
      try {
         // Create queue connection
         // Step 4.Create a JMS Connection
         connection = connectionFactory.createConnection();

         // Step 5. Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

         // Step 6. Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);

         // Step 7. Create the dissemination message and send it
         // Create the XML Request...
         DisseminationMessage dissMessage = QueueUtils.createDisseminationMessage(prId);
         String textMessage = QueueUtils.createXMLDisseminationMessage(dissMessage);
         TextMessage disseminationMessage = session.createTextMessage(textMessage);

         // retrieve the processedRequest
         ProcessedRequest processedRequest = entityManager.getReference(ProcessedRequest.class,
               prId);

         int priority = QueueUtils.getJMSPriorityForProcessedRequest(processedRequest);
         logger.info("Sending dissemination event for processed request "
               + processedRequest.getId() + " with priority " + priority);

         messageProducer.send(disseminationMessage, DeliveryMode.PERSISTENT, priority, 0);
      } catch (JMSException jmse) {
         extractionDelegate.setFailProcessedRequest(prId);
         logger.error("Unable to send the message to the queue", jmse);
      } catch (JAXBException jaxbe) {
         extractionDelegate.setFailProcessedRequest(prId);
         logger.error("Unable to serialize the message to the queue", jaxbe);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (JMSException e) {
               logger.error("Unable to properly close connection to the queue");
            }
         }
      }
   }

}
