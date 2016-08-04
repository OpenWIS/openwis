package org.openwis.datasource.server.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.ObjectUtils;
import org.openwis.dataservice.common.domain.dao.adhoc.AdHocDao;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Request;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.exception.OpenWisException;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.RequestService;
import org.openwis.dataservice.common.service.UserAlarmManagerLocal;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.openwis.datasource.server.utils.QueueUtils;
import org.openwis.datasource.server.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class RequestServiceImpl.
 */
@WebService(targetNamespace = "http://dataservice.openwis.org/", name = "RequestService", portName = "RequestServicePort", serviceName = "RequestService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(RequestService.class)
@Stateless(name = "RequestService")
public class RequestServiceImpl implements RequestService {

   /** The logger. */
   private final Logger logger = LoggerFactory.getLogger(RequestServiceImpl.class);

   /**
    * The entity manager.
    */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The request dao. */
   @EJB(name = "AdHocDao")
   private AdHocDao requestDao;

   /** The processed request service. */
   @EJB(name = "ProcessedRequestService")
   private ProcessedRequestService processedRequestService;

   /** The product metadata service. */
   @EJB(name = "ProductMetadataService")
   private ProductMetadataService productMetadataService;

   /** The user alarm manager. */
   @EJB(name = "UserAlarmManager")
   private UserAlarmManagerLocal userAlarmManager;

   /** injection ConnectionFactory. */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory connectionFactory;

   /** injection queue. */
   @Resource(mappedName = "java:/queue/RequestQueue")
   private Queue queue;

   /** The blacklist service. */
   @EJB
   private BlacklistService blacklistService;

   /**
    * Default constructor.
    * Builds a RequestServiceImpl.
    */
   public RequestServiceImpl() {
      //
   }

   /**
    * Creates the request.
    *
    * @param adHoc the ad hoc
    * @param metadataURN the metadata urn
    * @return the long
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.RequestService#createRequest(org.openwis.dataservice.common.domain.entity.request.ProcessedRequest)
    */
   @Override
   public Long createRequest(@WebParam(name = "adHoc") AdHoc adHoc,
         @WebParam(name = "metadataURN") String metadataURN) {
      String user = adHoc.getUser();
      if (blacklistService.isUserBlacklisted(user)) {
         throw new OpenWisException("Could not create AdHoc request, the user " + user
               + " is blacklisted");
      }
      //Persist request.
      ProcessedRequest processedRequest = persistAdhocRequest(adHoc, metadataURN);

      //Send request to extraction.
      sendRequestToExtraction(processedRequest);

      //Return adhoc ID.
      return adHoc.getId();
   }

   /**
    * Description goes here.
    * @return
    */
   @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
   private ProcessedRequest persistAdhocRequest(AdHoc adHoc, String metadataURN) {
      // Associate product metadata to AdHoc request
      if (metadataURN == null || "".equals(metadataURN)) {
         throw new OpenWisException(
               "Invalid metadaURN sent to the createRequest service. Sent metadataURN="
                     + metadataURN);
      }

      ProductMetadata productMetadata = productMetadataService.getProductMetadataByUrn(metadataURN);
      if (productMetadata == null) {
         throw new OpenWisException(
               "Unknown metadaURN sent to the createRequest service. Sent metadataURN="
                     + metadataURN);
      }
      adHoc.setProductMetadata(productMetadata);

      //Open transaction
      entityManager.persist(adHoc);

      // Create the processedRequest
      ProcessedRequest processedRequest = new ProcessedRequest();
      processedRequest.setCreationDate(Calendar.getInstance().getTime());
      processedRequest.setRequestResultStatus(RequestResultStatus.CREATED);
      processedRequest.setRequest(adHoc);
      entityManager.persist(processedRequest);
      Long prId = processedRequest.getId();
      processedRequest.setUri(RequestUtils.composeUriForRequest(processedRequest));
      processedRequest = entityManager.merge(processedRequest);
      entityManager.flush();

      logger.info("Create new Request {} -> ProcessedRequest {}", adHoc.getId(), prId);
      return processedRequest;
   }

   /**
    * Description goes here.
    *
    * @param processedRequest the processed request
    */
   private void sendRequestToExtraction(ProcessedRequest processedRequest) {
      Connection connection = null;
      try {
         // Send the message XML Request to the Queue
         // Create queue connection
         // Step 1.Create a JMS Connection
         logger.info("Create JMS connection");
         connection = connectionFactory.createConnection();
         // Step 2. Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

         // Create the XML Request...
         ProcessedRequestMessage requestMessage = QueueUtils.createRequestMessage(processedRequest);
         String textMessage = QueueUtils.createXMLRequestMessage(requestMessage);
         TextMessage message = session.createTextMessage(textMessage);
         // Step 3. Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);
         logger.info("Send the processed request message [id:{}] to queue:{}",
               processedRequest.getId(), queue.getQueueName());
         messageProducer.send(message);
      } catch (JAXBException e) {
         processedRequest.setRequestResultStatus(RequestResultStatus.FAILED);
         logger.error("Unable to send the processed request message [id:{}] to queue:RequestQueue",
               new Object[] {processedRequest.getId()}, e);
         throw new OpenWisException("Can not serialize the processed request message with id:"
                     + processedRequest.getId());
      } catch (JMSException e) {
         processedRequest.setRequestResultStatus(RequestResultStatus.FAILED);
         logger.error("Unable to send the processed request message [id:{}] to queue:RequestQueue",
               new Object[] {processedRequest.getId()}, e);
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
    * Delete request.
    *
    * @param id the id
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.RequestService#deleteRequest(java.lang.Long)
    */
   @Override
   public void deleteRequest(@WebParam(name = "requestId") Long id) {
      // Deletes the user alarms associated with the request
      userAlarmManager.deleteAlarmsOfRequest(id);

      processedRequestService.deleteProcessedRequestsByRequest(id);
      AdHoc request = requestDao.findById(id);
      AdHoc mergeRequest = entityManager.merge(request);
      entityManager.remove(mergeRequest);
   }

   /**
    * Gets the request.
    *
    * @param id the id
    * @return the request
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.RequestService#getRequest(java.lang.Long)
    */
   @Override
   public AdHoc getRequest(@WebParam(name = "requestId") Long id) {
      return requestDao.findById(id);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.RequestService#
    * getRequestsByUsers(java.util.Collection, int, int,
    * org.openwis.dataservice.common.domain.entity.enumeration.RequestColumn,
    * org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<ProcessedRequest> getRequestsByUsers(
         @WebParam(name = "userNames") Collection<String> users,
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults,
         @WebParam(name = "column") RequestColumn column,
         @WebParam(name = "sortDirection") SortDirection sortDirection) {
      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be = 0!");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0!");
      }

      List<ProcessedRequest> result;
      if (users.isEmpty()) {
         result = new ArrayList<ProcessedRequest>();
      } else {

         // Default column is URN
         RequestColumn col = (RequestColumn) ObjectUtils.defaultIfNull(column, RequestColumn.URN);

         // Default direction is Ascending.
         SortDirection dir = (SortDirection) ObjectUtils.defaultIfNull(sortDirection,
               SortDirection.ASC);

         String q = MessageFormat
               .format(
                     "SELECT pr FROM ProcessedRequest pr "
                           + "JOIN FETCH pr.request request "
                           + "JOIN FETCH request.productMetadata pm "
                           + "LEFT JOIN FETCH request.parameters "
                           + "WHERE request.requestType LIKE :requestType AND request.user IN (:users) ORDER BY {0} {1}",
                     col.getAttribute(), dir);
         Query query = entityManager.createQuery(q);
         query.setParameter("users", users);
         query.setParameter("requestType", "ADHOC");
         query.setFirstResult(firstResult);
         query.setMaxResults(maxResults);

         result = query.getResultList();
      }
      return result;
   }

   /**
   * {@inheritDoc}
   * @see org.openwis.dataservice.common.service.RequestService#getRequestsByUsersCount(java.util.Collection)
   */
   @Override
   public int getRequestsByUsersCount(Collection<String> users) {
      int result;
      if (users.isEmpty()) {
         result = 0;
      } else {
         Query query = entityManager
               .createQuery("SELECT COUNT(*) FROM AdHoc WHERE user IN (:users)");
         query.setParameter("users", users);

         Number res = (Number) query.getSingleResult();
         result = res.intValue();
      }
      return result;
   }

   /**
       * Gets the last processed request.
       *
       * @param user the user
       * @param maxRequest the max request
       * @return the last processed request
       * {@inheritDoc}
       * @see org.openwis.dataservice.common.service.RequestService#getLastProcessedRequest(java.lang.String, int)
       */
   @SuppressWarnings("unchecked")
   @Override
   public List<ProcessedRequest> getLastProcessedRequest(@WebParam(name = "userName") String user,
         @WebParam(name = "maxRequests") int maxRequest) {
      Query query = entityManager.createNamedQuery("ProcessedRequest.FindLastRequests");
      query.setParameter("user", user);
      query.setMaxResults(maxRequest);
      List<ProcessedRequest> result = query.getResultList();
      return result;

   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.RequestService#deleteRequestByUser(java.lang.String)
    */
   @Override
   @TransactionAttribute(TransactionAttributeType.REQUIRED)
   public int deleteRequestByUser(@WebParam(name = "user") String user) {
      int result;
      logger.debug("Delete all request owned by '{}'", user);

      // Delete all the user alarms
      userAlarmManager.deleteAlarmsOfUser(user);

      Query query;
      // Retrieve request
      query = entityManager.createNamedQuery("ProcessedRequest.getByUser");
      query.setParameter("user", user);

      @SuppressWarnings("unchecked")
      List<Request> requests = query.getResultList();
      result = requests.size();
      for (Request r : requests) {
         // Remove all associated ProcessedRequest
         query = entityManager.createNamedQuery("ProcessedRequest.deleteByRequest");
         query.setParameter("request", r);
         query.executeUpdate();
         entityManager.remove(r);
      }
      entityManager.flush();
      return result;
   }
}
