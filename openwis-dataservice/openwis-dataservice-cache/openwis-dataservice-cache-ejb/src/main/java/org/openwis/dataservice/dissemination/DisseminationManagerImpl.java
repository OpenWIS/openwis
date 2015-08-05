package org.openwis.dataservice.dissemination;

import java.io.StringReader;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.util.DisseminationRequestInfo;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.DisseminationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an implementation for the {@code DisseminationManager} class.
 *
 * @author <a href="mailto:christoph.bortlisz@vcs.de">Christoph Bortlisz</a>
 */
@MessageDriven(messageListenerInterface = MessageListener.class, name = "DisseminationManager", activationConfig = {
      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
      @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/DisseminationQueue"),
      @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "5")})
public class DisseminationManagerImpl implements DisseminationManager, MessageListener,
      ConfigurationInfo {

   // -------------------------------------------------------------------------
   // Instance Variables
   // -------------------------------------------------------------------------

   // Logging tool
   private final Logger LOG = LoggerFactory.getLogger(DisseminationManagerImpl.class);

   // Entity manager
   @PersistenceContext
   private EntityManager entityManager;

   // Message Driven Context.
   @Resource
   private MessageDrivenContext mdc;

   @EJB
   private DisseminationDelegate disseminationDelegate;

   // -------------------------------------------------------------------------
   // Message listener implementation
   // -------------------------------------------------------------------------
   @Override
   public void onMessage(Message message) {
      try {
         LOG.info("Received message:  {}", message);
         TextMessage messageReceived = (TextMessage) message;

         // parse received message
         StringReader sr = new StringReader(messageReceived.getText());
         DisseminationMessage disseminationMessage = Serializer.deserialize(
               DisseminationMessage.class, sr);

         Long id = disseminationMessage.getId();

         if (LOG.isDebugEnabled()) {
            LOG.debug("Checking processed request with id {}", id);
         }

         // retrieve the processedRequest
         ProcessedRequest processedRequest = getProcessedRequest(id);

         if (processedRequest == null) {
            LOG.error("No process request found with id {}", id);
         } else {
            DisseminationRequestInfo requestInfo = new DisseminationRequestInfo();
            requestInfo.setProcessedRequestId(processedRequest.getId());
            requestInfo.setTimeStamp(System.currentTimeMillis());

            LOG.info("Received dissemination event for processed request: " + id + " (request="
                  + processedRequest.getRequest().getId() + ")");
            disseminationDelegate.processMessage(requestInfo);
         }
      } catch (Throwable t) {
         LOG.error("Unexpected error while processing dissemination message", t);
         mdc.setRollbackOnly();
      }
   }

   /**
    * Retrieve a ProcessedRequest from the database
    *
    * @param requestId the ID of the processed request
    * @return the ProcessedRequest, if it is available, null otherwise
    */
   private ProcessedRequest getProcessedRequest(long requestId) {
      ProcessedRequest processedRequest = null;

      try {
         processedRequest = entityManager.getReference(ProcessedRequest.class, requestId);

         if (processedRequest.getId().longValue() != requestId) {
            LOG.error("Retrieved processed request id: " + processedRequest.getId()
                  + " differs from requested one: " + requestId);
         }
      } catch (EntityNotFoundException e) {
         LOG.error("EntityNotFoundException: " + e.getMessage());
         processedRequest = null;
      }

      return processedRequest;
   }
}
