/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;

import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.timer.ExtractionTimerService;
import org.openwis.datasource.server.jaxb.serializer.incomingds.DisseminationMessage;
import org.openwis.datasource.server.utils.QueueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 */
@Local(ExtractionTimerService.class)
@Stateless(name = "ExtractionTimerService")
public class ExtractionTimerServiceImpl implements ExtractionTimerService {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ExtractionTimerServiceImpl.class);

   /** The timer service. */
   @Resource
   private TimerService timerService;

   /** The extract timer name. */
   public static final String NAME = "EXTRACT_TIMER";

   /**
    * The entity manager.
    */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The processed request service. */
   @EJB(name = "ProcessedRequestService")
   private ProcessedRequestService processedRequestService;

   /** injection ConnectionFactory. */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory connectionFactory;

   /** injection queue. */
   @Resource(mappedName = "java:/queue/DisseminationQueue")
   private Queue queue;

   /**
    * Destroy.
    *
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.timer.ExtractionTimerService#destroy()
    */
   @SuppressWarnings("unchecked")
   @Override
   public void destroy() {
      Collection<Timer> timersCollection = timerService.getTimers();
      // Generics way
      for (Timer timer : timersCollection) {
         if (timer.getInfo().equals(NAME)) {
            timer.cancel();
            logger.info("Timer: {} has been removed.", NAME);
         }
      }
   }

   /**
    * Start.
    *
    * @param interval the interval
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.timer.ExtractionTimerService#start(long)
    */
   @Override
   public void start(long interval) {
      destroy();
      timerService.createTimer(interval, interval, NAME);
      logger.info("Timer: {} created with period = {}", NAME, Long.valueOf(interval));
   }

   /**
    * Timeout.
    *
    * @param timer the timer
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.timer.ExtractionTimerService#timeout(javax.ejb.Timer)
    */
   @SuppressWarnings("unchecked")
   @Override
   @Timeout
   public void timeout(Timer timer) {
      try {
         // Check requests in ONGOING_EXTRACTION state
         logger.debug("Check requests in {} state.",
               RequestResultStatus.ONGOING_EXTRACTION.toString());
         Query query = entityManager.createNamedQuery("ProcessedRequest.FindByRequestResult")
               .setParameter("requestresult", RequestResultStatus.ONGOING_EXTRACTION);
         List<ProcessedRequest> resultList = query.getResultList();

         // Check if request status changed (call Local DataSource harness - ws)
         logger.debug("Check if request status changed.");
         
         //create and send a message to dissemination
         sendProcessedRequestToDisseminations(resultList);
            
      } catch (Throwable e) {
         logger.error("Error in Extraction timer: " + e.getMessage(), e);
      }
   }

   /**
    * Send pr to the diss queue for the after checking extraction status of the given pr list.
    */
   private void sendProcessedRequestToDisseminations(List<ProcessedRequest> resultList) {
      Connection connection = null;
      try {
         // Create a JMS Connection
         connection = connectionFactory.createConnection();
         // Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);
         
         for (ProcessedRequest processedRequest : resultList) {
            boolean result = processedRequestService.monitorExtraction(processedRequest.getId());
            if (result) {
               //create and send a message to dissemination
               sendProcessedRequestToDissemination(messageProducer, session, processedRequest);
            }
         }
         
      } catch (Throwable t) {
         logger.error("Unable to create message for the dissemination queue", t);
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
    * Send pr to the diss queue.
    *
    * @param processedRequest the processed request
    * @throws JMSException the jMS exception
    * @throws JAXBException the jAXB exception
    */
   private void sendProcessedRequestToDissemination(MessageProducer messageProducer, Session session, ProcessedRequest processedRequest)
         throws JMSException, JAXBException {
      
      // Step 7. Create the dissemination message and send it
      logger.info("Create the dissemination message and send it.");
      // Create the XML Request...
      DisseminationMessage dissMessage = QueueUtils.createDisseminationMessage(processedRequest
            .getId());
      String textMessage = QueueUtils.createXMLDisseminationMessage(dissMessage);
      TextMessage disseminationMessage = session.createTextMessage(textMessage);

      disseminationMessage.setJMSPriority(QueueUtils.getJMSPriorityForProcessedRequest(processedRequest));

      messageProducer.send(disseminationMessage);
   }
}
