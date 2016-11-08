package org.openwis.datasource.server.service.impl;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.bean.DataPolicyOperations;
import org.openwis.dataservice.common.domain.bean.Operation;
import org.openwis.dataservice.common.domain.bean.UserDataPolicyOperations;
import org.openwis.dataservice.common.domain.dao.subscription.SubscriptionDao;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.enumeration.SubscriptionColumn;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Diffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.FTPDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MailDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.PublicDissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.RMDCNDissemination;
import org.openwis.dataservice.common.domain.entity.subscription.Frequency;
import org.openwis.dataservice.common.domain.entity.subscription.RecurrentFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionState;
import org.openwis.dataservice.common.exception.OpenWisException;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.SubscriptionService;
import org.openwis.dataservice.common.service.UserAlarmManagerLocal;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.dataservice.util.FileNameParser;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.openwis.datasource.server.utils.RequestUtils;
import org.openwis.datasource.server.utils.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class SubscriptionServiceImpl
 */
@WebService(targetNamespace = "http://dataservice.openwis.org/", name = "SubscriptionService", portName = "SubscriptionServicePort", serviceName = "SubscriptionService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Remote(SubscriptionService.class)
@Stateless(name = "SubscriptionService")
public class SubscriptionServiceImpl implements SubscriptionService {

   /** The Constant ID_PARAMETER. @member: ID2 */
   private static final String ID_PARAMETER = "id";

   /** The logger */
   private final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

   /**
    * The entity manager.
    */
   @PersistenceContext
   protected EntityManager entityManager;

   /** The processed request service. */
   @EJB(name = "ProcessedRequestService")
   private ProcessedRequestService processedRequestService;

   /** The user alarm manager. */
   @EJB(name = "UserAlarmManager")
   private UserAlarmManagerLocal userAlarmManager;

   /** The connection factory. */
   @Resource(mappedName = "java:/ConnectionFactory")
   private ConnectionFactory cf;

   /** The request queue. */
   @Resource(mappedName = "java:/queue/RequestQueue")
   private Queue queue;

   /** The subscription dao. */
   @EJB(name = "SubscriptionDao")
   private SubscriptionDao subscriptionDao;

   /** The product metadata service. */
   @EJB(name = "ProductMetadataService")
   private ProductMetadataService productMetadataService;

   /** The blacklist service. */
   @EJB
   private BlacklistService blacklistService;

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#
    * createSubscription(org.openwis.dataservice.common.domain.entity.subscription.Subscription)
    */
   @Override
   public Long createSubscription(@WebParam(name = "subscription") Subscription subscription,
         @WebParam(name = "metadataUrn") String metadataURN) {
      if (metadataURN == null || "".equals(metadataURN)) {
         throw new OpenWisException(
               "Invalid metadaURN sent to the createSubscription service. Sent metadataURN="
                     + metadataURN);
      }
      String user = subscription.getUser();
      if (blacklistService.isUserBlacklisted(user)) {
         throw new OpenWisException("Could not create AdHoc request, the user " + user
               + " is blacklisted");
      }

      // retrieve product metadata
      ProductMetadata productMetadata = productMetadataService.getProductMetadataByUrn(metadataURN);
      if (productMetadata == null) {
         throw new OpenWisException(
               "Unknown metadaURN sent to the createSubscription service. Sent metadataURN="
                     + metadataURN);
      }

      // Associate product metadata to subscription request
      subscription.setProductMetadata(productMetadata);

      // Set initial frequency next date
      if (subscription.getFrequency() instanceof RecurrentFrequency) {
         RecurrentFrequency frequency = (RecurrentFrequency) subscription.getFrequency();
         frequency.setNextDate(subscription.getStartingDate());
      }

      // if backup subscription, ensure state is suspended backup
      if (subscription.isBackup()) {
         subscription.setState(SubscriptionState.SUSPENDED_BACKUP);
      }

      logger.info("Created new subscription {}", subscription);

      return makePersistent(subscription);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#deleteSubscription(java.lang.Long)
    */
   @Override
   public void deleteSubscription(@WebParam(name = "subscriptionId") Long id) {
      // Deletes the user alarms associated with the request
      userAlarmManager.deleteAlarmsOfRequest(id);

      Subscription subscription = this.entityManager.find(Subscription.class, id);
      if (subscription != null) {
         // delete process request
         Query query = this.entityManager
               .createQuery("Delete ProcessedRequest pr Where pr.request = :request");
         query.setParameter("request", subscription);
         query.executeUpdate();

         // delete subscription
         this.entityManager.remove(subscription);
         this.entityManager.flush();
      } else {
         throw new IllegalArgumentException("Subscription [" + id
               + "] not found! Could not delete this subscription");
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#getSubscription(java.lang.Long)
    */
   @Override
   public Subscription getSubscription(@WebParam(name = "subscriptionId") Long id) {
      logger.debug("Get subscription {}", id);
      return subscriptionDao.findById(id);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#
    * resumeSubscription(org.openwis.dataservice.common.domain.entity.subscription.Subscription)
    */
   @Override
   public void resumeSubscription(@WebParam(name = "subscription") Long id) {
      Subscription existingSubscription = subscriptionDao.findById(id);
      if (SubscriptionState.SUSPENDED.equals(existingSubscription.getState())) {
         existingSubscription.setState(SubscriptionState.ACTIVE);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#
    * suspendSubscription(org.openwis.dataservice.common.domain.entity.subscription.Subscription)
    */
   @Override
   public void suspendSubscription(@WebParam(name = "subscription") Long id) {
      Subscription existingSubscription = subscriptionDao.findById(id);
      if (SubscriptionState.ACTIVE.equals(existingSubscription.getState())) {
         existingSubscription.setState(SubscriptionState.SUSPENDED);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#setBackup(java.lang.String, boolean)
    */
   @SuppressWarnings("unchecked")
   @Override
   public void setBackup(@WebParam(name = "deploymentName") String deployment,
         @WebParam(name = "activateBackup") boolean activateBackup,
         @WebParam(name = "dateFrom") String dateFrom) {
      logger.info("[Backup] set {} with status: {} and retro-process date: {}", new Object[] {
            deployment, activateBackup, dateFrom});
      logger.debug("[Backup] > Update backup subscription");
      // Handle subscription status
      Query querySelectSubscriptionBackuped = entityManager
            .createQuery("SELECT DISTINCT subscription.id FROM Subscription subscription "
                  + "JOIN subscription.subscriptionBackup backup "
                  + "WHERE backup.deployment = :deployment");

      querySelectSubscriptionBackuped.setParameter("deployment", deployment);
      List<Long> ids = querySelectSubscriptionBackuped.getResultList();

      if (!ids.isEmpty()) {
         Query query = entityManager
               .createQuery("UPDATE Subscription SET state = :state WHERE id IN (:ids)");

         query.setParameter("ids", ids);
         SubscriptionState state;
         if (activateBackup) {
            state = SubscriptionState.ACTIVE;
         } else {
            state = SubscriptionState.SUSPENDED_BACKUP;
         }
         query.setParameter("state", state);
         logger.debug("[Backup] >> Subscription {} should been {}", new Object[] {ids, state});
         query.executeUpdate();

         if (activateBackup) {
            logger.debug("[Backup] Retro-process Subscription");
            Date from = null;
            Calendar to = DateTimeUtils.getUTCCalendar();
            try {
               from = DateTimeUtils.parseDateTime(dateFrom);

               // Handle OnProductArrival subscription retro processing
               handleOnProductArrivalRetroProcessing(ids, from, to);

               // handle Recurrent subscription
               handleRecurrentRetroProcessing(ids, from, to);
            } catch (Exception e) {
               logger.error("Could not parse the date: " + dateFrom, e);
               logger.error("[Backup] Could not parse the date: " + dateFrom, e);
            }
         }
      }
   }

   /**
    * Handle recurrent retro processing.
    *
    * @param ids the subscription id subset
    * @param from the from
    * @param to the to
    */
   @SuppressWarnings("unchecked")
   private void handleRecurrentRetroProcessing(List<Long> ids, Date from, Calendar to) {
      logger.info("[Backup] > Retro-process recurrent Subscriptions");
      Query query;
      Calendar calFrom = DateTimeUtils.getUTCCalendar();
      calFrom.setTime(from);
      while (calFrom.before(to)) {
         query = entityManager
               .createQuery("SELECT s FROM Subscription s JOIN s.frequency f"
                     + " WHERE s.id IN (:ids) AND s.startingDate <=:date AND (f.nextDate IS NULL OR f.nextDate <= :date) AND f.reccurentScale IS NOT NULL");
         query.setParameter("ids", ids);
         query.setParameter("date", calFrom.getTime());
         List<Subscription> subscriptions = query.getResultList();

         if (subscriptions != null && subscriptions.size() > 0) {
            logger.info("[Backup] >> Retro-process: {} subscriptions to processed at {}",
                  new Object[] {subscriptions.size(), calFrom.getTime()});
            processRecurrentSubscriptions(subscriptions, calFrom.getTime());
         }
         calFrom.add(Calendar.MILLISECOND, ConfigServiceFacade.getInstance()
               .getInt(DataServiceConfiguration.SUBSCRIPTION_RECURRENT_RETROPROCESS_INCREMENT));
      }
   }

   /**
    * Handle on product arrival retro processing.
    *
    * @param ids the subscription subset id
    * @param from the from
    * @param to the to
    * @throws ParseException the parse exception
    */
   @SuppressWarnings("unchecked")
   private void handleOnProductArrivalRetroProcessing(List<Long> ids, Date from, Calendar to)
         throws ParseException {
      logger.info("[Backup] > Retro-process OnProductArrival Subscriptions");
      // Get URN from recurrent subscription
      Query query = entityManager
            .createQuery("SELECT DISTINCT s.productMetadata FROM Subscription s JOIN s.frequency f"
                  + " WHERE s.id IN (:ids) AND f.reccurentScale IS NULL");
      query.setParameter("ids", ids);
      List<ProductMetadata> productMetadataList = query.getResultList();

      // Retrieve matching product
      CacheIndex cache = ServiceProvider.getCacheIndex();
      List<CachedFile> files = null;
      String urn;
      String filename;
      Date productDate;
      Collection<ProcessedRequest> prs;
      for (ProductMetadata pm : productMetadataList) {
         urn = pm.getUrn();
         logger.debug("[Backup] >> Retro-process ProductMetadata {}", urn);
         // Only handle gisc subscription
         files = cache.listFilesByMetadataUrn(urn, DateTimeUtils.formatUTC(from),
               DateTimeUtils.formatUTC(to.getTime()));
         for (CachedFile cf : files) {
            filename = cf.getFilename();
            logger.debug("[Backup] >>> Retro-process Product {}", filename);
            productDate = FileNameParser.parseFileName(filename).getProductDate();
            // Retrieve valid subscriptions
            query = entityManager
                  .createQuery("SELECT DISTINCT s FROM Subscription s JOIN s.frequency f JOIN s.productMetadata meta "
                        + "WHERE s.id IN (:ids) AND meta.urn = :metadataurn AND s.startingDate <= :productDate AND s.state = :state AND s.valid = true AND s.extractMode = :mode AND f.reccurentScale IS NULL");
            query.setParameter("metadataurn", urn);
            query.setParameter("state", SubscriptionState.ACTIVE);
            query.setParameter("productDate", productDate);
            query.setParameter("mode", ExtractMode.GLOBAL);
            query.setParameter("ids", ids);
            prs = processEventSubscriptions(query.getResultList(),
                  DateTimeUtils.formatUTC(productDate));
            sendRequests(prs, productDate, Long.toString(cf.getId()));
         }
      }
   }

   /**
    * Process listof recurrent subscriptions (open jms connection for all the sent events)
    */
   @Override
   @WebMethod(exclude = true)
   public void processRecurrentSubscriptions(Collection<Subscription> subscriptions, Date date) {
      Connection connection = null;
      try {
         // Create queue connection
         connection = cf.createConnection();
         // Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);

         for (Subscription sub : subscriptions) {
            processRecurrentSubscription(messageProducer, session, sub, date);
         }
      } catch (Throwable t) {
         logger.error("Unable to process message using frequency visitor ", t);
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
    * Process recurrent subscription.
    *
    * @param subscription the subscription
    * @param date the reference date for the processing
    */
   private void processRecurrentSubscription(MessageProducer messageProducer, Session session,
         Subscription subscription, Date date) {
      logger.debug("Process subscription: {}", subscription.getId());

      Date now = DateTimeUtils.getUTCTime();

      // New processedRequest
      ProcessedRequest processedRequest = new ProcessedRequest();
      processedRequest.setCreationDate(now);
      processedRequest.setRequestResultStatus(RequestResultStatus.CREATED);

      // Create the process Request
      Long id = processedRequestService.addProcessedRequestToSubscription(subscription,
            processedRequest);
      entityManager.merge(subscription);
      logger.info("Subscription: {} - Generate new request {}.", new Object[] {
            subscription.getId(), id});

      // Send
      sendRequest(messageProducer, session, processedRequest, date, null);

      // Update next subscription date
      RecurrentFrequency frequency = (RecurrentFrequency) subscription.getFrequency();
      Date next = frequency.getNextDate();
      if (next == null) {
         frequency.setNextDate(subscription.getStartingDate());
      } else {
         Calendar calNext = DateTimeUtils.getUTCCalendar();
         calNext.setTime(date);
         // Increment next date
         switch (frequency.getReccurentScale()) {
         case HOUR:
            calNext.add(Calendar.HOUR_OF_DAY, frequency.getReccurencePeriod());
            break;
         case DAY:
            calNext.add(Calendar.DAY_OF_MONTH, frequency.getReccurencePeriod());
            break;
         default:
            break;
         }
         frequency.setNextDate(calNext.getTime());
      }
      entityManager.merge(subscription);
   }

   /**
    * Send request events for the given processed requests.
    */
   private void sendRequests(Collection<ProcessedRequest> prs, Date date, String productId) {
      Connection connection = null;
      try {
         // Create queue connection
         connection = cf.createConnection();
         // Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);

         for (ProcessedRequest pr : prs) {
            logger.info("[Backup] >>>> Retro-process subscription {}", pr.getRequest());
            sendRequest(messageProducer, session, pr, date, productId);
         }
      } catch (Throwable t) {
         logger.error("Unable to process message using frequency visitor ", t);
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
    * Send request event to the request queue.
    *
    * @param processedRequest the processed request
    * @param date the date
    */
   private void sendRequest(MessageProducer messageProducer, Session session,
         ProcessedRequest processedRequest, Date date, String productId) {
      try {
         // Create the XML Request...
         ProcessedRequestMessage requestMessage = createRequestMessage(processedRequest.getId(),
               DateTimeUtils.formatUTC(date), productId);
         String textMessage = createXMLRequest(requestMessage);
         TextMessage messageToSend = session.createTextMessage(textMessage);
         // Send message in the request queue
         messageProducer.send(messageToSend);
      } catch (Throwable t) {
         logger.error("Unable to create message for the request queue ", t);
         if (processedRequest != null) {
            processedRequest.setRequestResultStatus(RequestResultStatus.FAILED);
         }
      }
   }

   /**
    * Create the RequestMessage to serialize.
    *
    * @param id the id
    * @param productDate the product date
    * @return the RequestMessage
    */
   private ProcessedRequestMessage createRequestMessage(Long id, String productDate,
         String productId) {
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
   private String createXMLRequest(ProcessedRequestMessage requestMessage) throws JAXBException {
      StringWriter sw;
      sw = new StringWriter();
      Serializer.serialize(requestMessage, sw);
      return sw.toString();
   }

   /**
    * Process on product arrival subscriptions.
    *
    * @param subscriptions the subscriptions to process
    * @param productDate the new product date
    * @return the collection of ProcessedRequest generated by the processing
    */
   @Override
   @WebMethod(exclude = true)
   public Collection<ProcessedRequest> processEventSubscriptions(List<Subscription> subscriptions,
         String productDate) {
      try {
         Collection<ProcessedRequest> result = new ArrayList<ProcessedRequest>();
         ProcessedRequest processedRequest = null;
         for (Subscription subscription : subscriptions) {
            // check availability
            if (!subscription.isValid()) {
               logger.warn("The subscription {} is not valid ", subscription);
            } else {
               // Process subscription
               processedRequest = processEventSubscription(subscription, productDate);
               if (processedRequest != null) {
                  result.add(processedRequest);
               }
            }
         }
         return result;
      } catch (RuntimeException e) {
         logger.error("Error while processing subscriptions", e);
         throw e;
      }
   }

   /**
    * Process on product arrival subscriptions.
    *
    * @param productDate the new product date
    * @param subscription the subscription
    * @return the processed request
    */
   private ProcessedRequest processEventSubscription(Subscription subscription, String productDate) {
      // Check event subscription on cache will give a processed request
      if (!checkEventSubscriptionOnCache(subscription, productDate)) {
         return null;
      }

      ProcessedRequest processedRequest = null;

      // Create process request
      Date now = Calendar.getInstance().getTime();

      processedRequest = new ProcessedRequest();
      processedRequest.setCreationDate(now);
      processedRequest.setRequestResultStatus(RequestResultStatus.CREATED);

      //  Long id = processedRequestService.addProcessedRequestToSubscription(subscription,
      //            processedRequest);
      // Remark: duplicate of ProcessedRequestToSubscription without sub-transaction
      logger.debug("Adding processed request to subscription {}", subscription.getId());
      processedRequest.setRequest(subscription);
      entityManager.persist(processedRequest);
      processedRequest.setUri(RequestUtils.composeUriForSubscription(processedRequest));
      Long id = processedRequest.getId();

      entityManager.merge(subscription);
      logger.info("Subscription: {} - Generate new request {}.", new Object[] {
            subscription.getId(), id});
      return processedRequest;
   }

   private boolean checkEventSubscriptionOnCache(Subscription subscription, String productDate) {
      // only test event subscription on cache with time interval parameter
      if (subscription.getExtractMode() != ExtractMode.GLOBAL
            || subscription.getParameters().size() != 1) {
         return true;
      }
      Parameter parameter = subscription.getParameters().iterator().next();
      if (!ParameterCode.TIME_INTERVAL.equalsIgnoreCase(parameter.getCode())) {
         return true;
      }

      Calendar productCalendar;
      try {
         productCalendar = DateTimeUtils.getUTCCalendar(productDate);
      } catch (Exception e) {
         throw new IllegalArgumentException("Invalid product date in incoming message: "
               + productDate);
      }

      // parse parameter value
      for (Value value : parameter.getValues()) {
         if (value == null || value.getValue() == null || value.getValue().trim().isEmpty()
               || value.getValue().indexOf('/') == -1) {
            continue;
         }

         // do the real work
         String parameterValue = value.getValue().trim();
         String[] intervalValues = parameterValue.split("/");
         if (intervalValues.length != 2) {
            continue;
         }
         Calendar start = parseTimeFields(productCalendar, intervalValues[0]);
         Calendar end = parseTimeFields(productCalendar, intervalValues[1]);
         if (productCalendar.equals(start)
               || (productCalendar.after(start) && productCalendar.before(end))) {
            return true;
         }
      }
      return false;
   }

   private Calendar parseTimeFields(final Calendar productDate, final String timeExpr) {
      // return value
      Calendar calendar = null;

      // time interval
      try {
         String value = timeExpr.substring(0, 2);
         int hourOfDay = Integer.parseInt(value);
         if (hourOfDay < 0 || hourOfDay > 23) {
            throw new IllegalArgumentException("Invalid hour field in time expression specified: "
                  + value);
         }

         value = timeExpr.substring(3, 5);
         int minute = Integer.parseInt(value);
         if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(
                  "Invalid minute field in time expression specified: " + value);
         }

         calendar = (Calendar) productDate.clone();
         calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
         calendar.set(Calendar.MINUTE, minute);
      } catch (Exception e) {
         throw new IllegalArgumentException("Failed to parse time fields: " + e.getMessage());
      }

      return calendar;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#
    * updateSubscription(org.openwis.dataservice.common.domain.entity.subscription.Subscription)
    */
   @Override
   public Subscription updateSubscription(@WebParam(name = "subscription") Subscription subscription) {
      Subscription s = entityManager.merge(subscription);
      return s;
   }

   /**
   * {@inheritDoc}
   * @see org.openwis.dataservice.common.service.SubscriptionService#updateSubscription(java.lang.Long, java.util.Set, org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination, org.openwis.dataservice.common.domain.entity.request.dissemination.Dissemination)
   */
   @Override
   public Subscription updateSubscriptionConfig(
         @WebParam(name = "subscriptionId") Long subscriptionId,
         @WebParam(name = "ssp") Set<Parameter> parameters,
         @WebParam(name = "primaryDissemination") Dissemination primaryDissemination,
         @WebParam(name = "secondaryDissemination") Dissemination secondaryDissemination,
         @WebParam(name = "frequency") Frequency frequency,
         @WebParam(name = "startingDate") String startingDate) {
      Subscription subscription = getFullSubscription(subscriptionId);

      //Delete insert SSP
      deleteParameters(subscription);
      if (parameters != null) {
    	  subscription.getParameters().addAll(parameters);
      }

      //Delete insert 1st diss / 2nd Diss
      deleteDisseminations(subscription);
      subscription.setPrimaryDissemination(primaryDissemination);
      subscription.setSecondaryDissemination(secondaryDissemination);

      //Delete insert frequency.
      entityManager.remove(subscription.getFrequency());
      subscription.setFrequency(frequency);

      //Delete insert startingDate.
      try {
         subscription.setStartingDate(DateTimeUtils.parseDateTime(startingDate));
      } catch (ParseException e) {
         throw new OpenWisException(
               "Invalid startingDate sent to the updateSubscriptionConfig service. Sent startingDate="
                     + startingDate);
      }

      return entityManager.merge(subscription);
   }

   /**
    * Clear parameters and delete orphans
    * @param subscription the {@link Subscription}
    */
   private void deleteParameters(Subscription subscription) {
      for (Parameter parameter : subscription.getParameters()) {
         entityManager.remove(parameter);
      }
      subscription.getParameters().clear();
   }

   /**
    * Delete orphans disseminations of the subscription (before updating)
    * @param subscription the {@link Subscription}
    */
   private void deleteDisseminations(Subscription subscription) {
      if (subscription.getPrimaryDissemination() != null) {
         entityManager.remove(subscription.getPrimaryDissemination());
      }
      if (subscription.getSecondaryDissemination() != null) {
         entityManager.remove(subscription.getSecondaryDissemination());
      }
   }

   /**
       * {@inheritDoc}
       * @see org.openwis.dataservice.common.service.SubscriptionService#getFullSubscription(java.lang.Long)
       */
   @Override
   public Subscription getFullSubscription(@WebParam(name = "subscriptionId") Long id) {
      Query q = entityManager.createNamedQuery("Subscription.FindBySusbcriptionId").setParameter(
            ID_PARAMETER, id);
      Subscription s = (Subscription) q.getSingleResult();
      return s;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#getSubscriptionsByUsers(java.util.Collection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public List<Subscription> getSubscriptionsByUsers(
         @WebParam(name = "userNames") Collection<String> users,
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults,
         @WebParam(name = "column") SubscriptionColumn column,
         @WebParam(name = "sortDirection") SortDirection sortDirection) {

      if (firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be ≥ 0!");
      }
      if (maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0!");
      }

      List<Subscription> result;
      if (users != null && users.isEmpty()) {
         result = new ArrayList<Subscription>();
      } else {

         // Default column is URN
         SubscriptionColumn col = (SubscriptionColumn) ObjectUtils.defaultIfNull(column,
               SubscriptionColumn.URN);

         // Default direction is Ascending.
         SortDirection dir = (SortDirection) ObjectUtils.defaultIfNull(sortDirection,
               SortDirection.ASC);

         String userConstraint = "";
         if (users != null) {
            userConstraint = "WHERE subscription.user IN (:users) ";
         }
         String q = MessageFormat.format(
               "SELECT DISTINCT subscription FROM Subscription subscription "
                     + "JOIN FETCH subscription.productMetadata pm "
                     + "LEFT JOIN FETCH subscription.parameters " + userConstraint
                     + "ORDER BY {0} {1}", col.getAttribute(), dir);
         Query query = entityManager.createQuery(q);

         if (users != null) {
            query.setParameter("users", users);
         }
         query.setFirstResult(firstResult);
         query.setMaxResults(maxResults);

         result = query.getResultList();
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#getSubscriptionsByUsersCount(java.util.Collection)
    */
   @Override
   public int getSubscriptionsByUsersCount(Collection<String> users) {
      int result;
      if (users != null && users.isEmpty()) {
         result = 0;
      } else {
         String userConstraint = "";
         if (users != null) {
            userConstraint = " WHERE user IN (:users)";
         }
         Query query = entityManager.createQuery("SELECT COUNT(*) FROM Subscription"
               + userConstraint);
         if (users != null) {
            query.setParameter("users", users);
         }

         Number res = (Number) query.getSingleResult();
         result = res.intValue();
      }
      return result;
   }

   /**
    * Make persistent.
    * @param request the request
    * @return the long
    */
   private Long makePersistent(Subscription request) {
      subscriptionDao.persist(request);
      entityManager.flush();
      return request.getId();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#findLastProcessedRequest(java.lang.Long)
    */
   @Override
   public ProcessedRequest findLastProcessedRequest(@WebParam(name = "subscriptionId") Long id) {
      ProcessedRequest pr = null;
      Query query = entityManager.createNamedQuery("ProcessedRequest.FindLastByDate").setParameter(
            ID_PARAMETER, id);
      @SuppressWarnings("unchecked")
      List<ProcessedRequest> result = query.getResultList();
      if (result != null && result.size() > 0) {
         pr = result.get(0);
      }
      return pr;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#checkSubscription(java.util.Map)
    */
   @Override
   public void checkUsersSubscription(
         @WebParam(name = "operationsAllowed") Set<UserDataPolicyOperations> operationsAllowed) {
      logger.debug("checkUsersSubscription with {} ", operationsAllowed);
      if (operationsAllowed != null) {
         // Retrieve Subscription
         Map<String, Set<DataPolicyOperations>> usersOperationsAllowed = new HashMap<String, Set<DataPolicyOperations>>();
         StringBuffer users = new StringBuffer();
         boolean isFirst = true;
         String user;
         Set<DataPolicyOperations> operations;
         for (UserDataPolicyOperations udpo : operationsAllowed) {
            user = udpo.getUser();
            if (user != null) {
               if (isFirst) {
                  isFirst = false;
               } else {
                  users.append(", ");
               }
               users.append('\'');
               users.append(StringEscapeUtils.escapeSql(user));
               users.append('\'');

               operations = usersOperationsAllowed.get(user);
               if (operations == null) {
                  operations = new HashSet<DataPolicyOperations>();
                  usersOperationsAllowed.put(user, operations);
               }
               operations.addAll(udpo.getDataPolicyOperations());
            }
         }
         // Retrieve subscription
         Query query = entityManager.createQuery(MessageFormat.format(
               "SELECT s FROM Subscription s WHERE s.user IN ({0})", users));
         @SuppressWarnings("unchecked")
         List<Subscription> subs = query.getResultList();

         // Checks each subscription
         Set<DataPolicyOperations> userOperationsAllowed;
         for (Subscription subscription : subs) {
            userOperationsAllowed = usersOperationsAllowed.get(subscription.getUser());
            updateSubscriptionState(userOperationsAllowed, subscription);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.SubscriptionService#checkSubscription(java.lang.String, org.openwis.dataservice.common.domain.bean.DataPolicyOperations)
    */
   @Override
   @TransactionAttribute(TransactionAttributeType.REQUIRED)
   public void checkUserSubscription(@WebParam(name = "user") String user,
         @WebParam(name = "userOperationsAllowed") Set<DataPolicyOperations> userOperationsAllowed) {
      logger.debug("checkUserSubscription for {} with {} ", new Object[] {user,
            userOperationsAllowed});
      // Retrieve Subscription
      Query query = entityManager.createNamedQuery("Subscription.FindByUser");
      query.setParameter("user", user);
      @SuppressWarnings("unchecked")
      List<Subscription> subs = query.getResultList();

      // Checks each subscription
      for (Subscription subscription : subs) {
         updateSubscriptionState(userOperationsAllowed, subscription);
      }
   }

   /**
    * Update subscription state.
    *
    * @param userOperationsAllowed the user operations allowed
    * @param subscription the subscription
    */
   private void updateSubscriptionState(Set<DataPolicyOperations> userOperationsAllowed,
         Subscription subscription) {
      ProductMetadata pm;
      DataPolicyOperations dpo;
      Dissemination dissemination;
      pm = subscription.getProductMetadata();
      dpo = findDataPolicyOperations(pm, userOperationsAllowed);

      boolean valid;
      ArrayList<String> checkMessages = new ArrayList<String>();
      if (dpo == null) {
         checkMessages.add("NO Operation allowed");
         valid = false;
      } else {
         logger.trace("\tCheck {} with Operations: {}",
               new Object[] {subscription, dpo.getOperations()});
         // Check Download
         if (dpo.getOperations().contains(Operation.Download)) {
            checkMessages.add("Download authorized");
            valid = true;
         } else {
            checkMessages.add("Download NOT authorized");
            valid = false;
         }
         // Check dissemination
         dissemination = subscription.getPrimaryDissemination();
         if (!checkDissemination(dissemination, dpo.getOperations())) {
            checkMessages.add("Primary dissemination NOT authorized");
            valid = false;
         }

         dissemination = subscription.getSecondaryDissemination();
         if (!checkDissemination(dissemination, dpo.getOperations())) {
            checkMessages.add("Secondary dissemination NOT authorized");
            valid = false;
         }
      }

      if (subscription.isValid() != valid) {
         String action;
         if (valid) {
            action = "Enable";
         } else {
            action = "Disable";
         }
         logger.info(MessageFormat.format(
               "Checking User Subscription: {0} subscription {1} - cause: {2}", action,
               subscription, checkMessages));

         subscription.setValid(valid);
         entityManager.merge(subscription);
      }
   }

   /**
    * Check dissemination.
    *
    * @param dissemination the dissemination
    * @param operations the operations
    */
   private boolean checkDissemination(Dissemination dissemination, Set<Operation> operations) {
      boolean result = true;
      if (dissemination instanceof PublicDissemination) {
         PublicDissemination diss = (PublicDissemination) dissemination;
         Diffusion diffusion = diss.getDiffusion();
         if (diffusion instanceof MailDiffusion) {
            result = operations.contains(Operation.PublicEmail);
         } else if (diffusion instanceof FTPDiffusion) {
            result = operations.contains(Operation.PublicFTP);
         } else {
            result = false;
         }
      } else if (dissemination instanceof RMDCNDissemination) {
         RMDCNDissemination diss = (RMDCNDissemination) dissemination;
         Diffusion diffusion = diss.getDiffusion();
         if (diffusion instanceof MailDiffusion) {
            result = operations.contains(Operation.RMDCNEmail);
         } else if (diffusion instanceof FTPDiffusion) {
            result = operations.contains(Operation.RMDCNFTP);
         } else {
            result = false;
         }
      }
      return result;
   }

   /**
    * Find data policy operations.
    *
    * @param pm the product metadata
    * @param userOperationsAllowed the user operations allowed
    * @return the data policy operations matching the product metadata data policy
    */
   private DataPolicyOperations findDataPolicyOperations(ProductMetadata pm,
         Set<DataPolicyOperations> userOperationsAllowed) {
      DataPolicyOperations result = null;
      if (userOperationsAllowed != null && pm != null) {
         String dp;
         for (DataPolicyOperations dpo : userOperationsAllowed) {
            dp = dpo.getDataPolicy();
            if (dp != null
                  && (dp.equals(pm.getOverridenDataPolicy()) || (pm.getOverridenDataPolicy() == null && dp
                        .equals(pm.getDataPolicy())))) {
               result = dpo;
               break;
            }
         }
      }
      return result;
   }
}
