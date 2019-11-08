/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ObjectUtils;
import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.dto.LightProcessedRequestDTO;
import org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestFilter;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestColumn;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.enumeration.SortDirection;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Request;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.subscription.EventBasedFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.subscription.SubscriptionState;
import org.openwis.dataservice.common.exception.OpenWisException;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.service.CacheExtraService;
import org.openwis.dataservice.common.service.LocalDataSourceExtractService;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.bean.ProcessedRequestListResult;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.openwis.datasource.server.utils.RequestUtils;
import org.openwis.datasource.server.utils.ServiceProvider;
import org.openwis.management.service.AlertService;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
@WebService(targetNamespace = "http://dataservice.openwis.org/", name = "ProcessedRequestService", portName = "ProcessedRequestServicePort", serviceName = "ProcessedRequestService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@Local(ProcessedRequestService.class)
@Stateless(name = "ProcessedRequestService")
public class ProcessedRequestServiceImpl implements ProcessedRequestService {

   /** The logger. */
   private final Logger logger = LoggerFactory.getLogger(ProcessedRequestServiceImpl.class);

   /**
    * The stating post URI.
    */
   private String stagingPostUri;

   /**
    * The entity manager.
    */
   @PersistenceContext
   private EntityManager entityManager;

   /** The local data source service. */
   @EJB(name = "LocalDataSourceExtractService")
   private LocalDataSourceExtractService localDataSourceService;

   /** The blacklist service. */
   @EJB
   private BlacklistService blacklistService;
   
   @PostConstruct
   public void initialize() {
      stagingPostUri = ConfigServiceFacade.getInstance().getString(DataServiceConfiguration.STAGING_POST_URI_KEY);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#getProcessedRequest(java.lang.Long)
    */
   @Override
   public ProcessedRequest getProcessedRequest(@WebParam(name = "id") Long id) {
      return entityManager.find(ProcessedRequest.class, id);
   }
   

	/**
	 * Clear staging post.
	 */
	protected void clearStagingPost(ProcessedRequest pr) {
		if(pr!=null) {
			pr.clearStagingPost();
		}
	}

   /**
    * Adds the processed request to subscription.
    *
    * @param subscription the subscription
    * @param processedRequest the processed request
    * @return the long
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#
    * addProcessedRequest(org.openwis.dataservice.common.domain.entity.subscription.Subscription,
    * org.openwis.dataservice.common.domain.entity.request.ProcessedRequest)
    */
   @Override
   public Long addProcessedRequestToSubscription(
         @WebParam(name = "subscription") Subscription subscription,
         @WebParam(name = "processedRequest") ProcessedRequest processedRequest) {
      logger.debug("Adding processed request to subscription {}", subscription.getId());
      processedRequest.setRequest(subscription);
      entityManager.persist(processedRequest);
      entityManager.flush();
      processedRequest.setUri(RequestUtils.composeUriForSubscription(processedRequest));
      return processedRequest.getId();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#monitorExtraction(java.lang.Long)
    */
   @Override
   @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
   public boolean monitorExtraction(@WebParam(name = "processedRequestId") Long id) {
      boolean result = false;
      ProcessedRequest processedRequest = getProcessedRequest(id);

      // if the status has changed return
      if (processedRequest.getRequestResultStatus() == RequestResultStatus.ONGOING_EXTRACTION) {
         try {
            logger.debug("Call Local DataSource harness to monitor extraction - ws {}", id);
            final ProductMetadata productMetadata = processedRequest.getRequest()
                  .getProductMetadata();
            final MessageStatus extractionStatus = localDataSourceService.monitorExtraction(
                  productMetadata.getUrn(), processedRequest.getId(),
                  productMetadata.getLocalDataSource());

            logger.info(
                  "Message Status received for processed request id <{}> {}, {}",
                  new Object[] {id, extractionStatus.getStatus().name(),
                        extractionStatus.getMessage()});
            
            // Specific treatment in case of on product arrival with no result
            if (extractionStatus.getStatus() == Status.NO_RESULT_FOUND && 
                  isOnArrivalProductSubscription(processedRequest)) {
               entityManager.remove(processedRequest);
            } else {
               // Update request status ()
               RequestUtils.updateRequestStatus(processedRequest, extractionStatus);
            }

            // Add a message in the dissemination queue if extracted and ready to disseminate
            if (processedRequest.getRequestResultStatus() == RequestResultStatus.EXTRACTED) {
               //set submitted for dissemination date
               Date now = DateTimeUtils.getUTCCalendar().getTime();
               processedRequest.setSubmittedDisseminationDate(now);
               Request request = processedRequest.getRequest();
               result = true;
               updateExtractedDataStatistics(processedRequest, request.getUser());
            }
            //force to commit to the underlying database
            entityManager.flush();

         } catch (Exception e) {
            logger.error("Can not extract the processed request ", e);
            processedRequest.setRequestResultStatus(RequestResultStatus.FAILED);
            entityManager.flush();
            result = false;
         }
      } else {
         logger.warn("Processed Request <{}> has changed the status: {}", id,
               processedRequest.getRequestResultStatus());
      }
      return result;
   }

   /**
    * Description goes here.
    * @param extractedFiles
    */
   protected void updateExtractedDataStatistics(final ProcessedRequest processedRequest, final String userId) {
      File disseminatedFile = new File(stagingPostUri,processedRequest.getUri());
      if (disseminatedFile != null) {
         // get statistics parameter
         long size = 0;
         int nbFiles = 0;

         if (disseminatedFile.isDirectory()) {
            for (File child : disseminatedFile.listFiles()) {
               size += child.length();
               nbFiles++;
            }
         } else if (disseminatedFile.exists()) {
            size = disseminatedFile.length();
            nbFiles = 1;
         }

         processedRequest.setSize(size);
         //set completed date
         Date now = DateTimeUtils.getUTCCalendar().getTime();

         // Update statistics
         try {
            String email = processedRequest.getRequest().getEmail();
			blacklistService.checkAndUpdateDisseminatedData(userId,email ,
                  DateTimeUtils.formatUTC(now), nbFiles, size);
         } catch (Exception e) {
            logger.warn("Could no update statistics", e);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#
    * extract(org.openwis.dataservice.common.domain.entity.request.ProcessedRequest, java.lang.String)
    */
   @Override
   public Status extract(@WebParam(name = "processedRequest") ProcessedRequest processedRequest,
         @WebParam(name = "productDate") String productDate, String productId) {
      Status result = null;
      if (processedRequest == null) {
         throw new OpenWisException("The processed request shouldn't being null !");
      }
      // Load request
//      ProcessedRequest pr = entityManager.getReference(ProcessedRequest.class,
//            processedRequest.getId());
      // Changed from getReference() to find() is it is being used immediately
      ProcessedRequest pr = entityManager.find(ProcessedRequest.class, processedRequest.getId());

      logger.info("Check the processed request {}", pr.getId());

      // extract
      try {
         Request request = pr.getRequest();
         String user = request.getUser();
         if (blacklistService.isUserBlacklisted(user)) {
        	 invalidProcessRequest(pr);
        	 pr.setMessage(MessageFormat.format("Reject because [{0}] is blacklisted", user));
             result = Status.ERROR;
             return result;
         }
         
         if (request instanceof Subscription) {
            Subscription subscription = (Subscription) request;
            // check availability
            if (!SubscriptionState.ACTIVE.equals(subscription.getState())) {
               logger.info("Skip subscription {} because of the state: {} ", new Object[] {
                     subscription, subscription.getState()});
               invalidProcessRequest(pr);
               result = Status.ERROR;

            } else if (!subscription.isValid()) {
               logger.warn("The subscription {} is not valid ", subscription);
               invalidProcessRequest(pr);
               result = Status.ERROR;
            } else {
               // Process extract
               result = processExtraction(pr, request, productId);
            }
         } else {
            // Process extract
            result = processExtraction(pr, request, productId);
         }

         entityManager.flush();
      } catch (EJBException e) {
         logger.error("Can not extract the processed request ", e);
         invalidProcessRequest(pr);
         result = Status.ERROR;
      }
      return result;
   }

   /**
    * Invalid process request.
    *
    * @param pr the process
    */
   private void invalidProcessRequest(ProcessedRequest pr) {
      pr.setRequestResultStatus(RequestResultStatus.FAILED);
      pr.setUri(null);
      entityManager.merge(pr);
      entityManager.flush();
   }

   /**
    * Process extraction.
    *
    * @param pr the process request
    * @param request the request
    * @return the status
    */
   private Status processExtraction(ProcessedRequest pr, Request request, String productId) {
      Status result = null;

      MessageStatus messageStatus = null;
      switch (request.getExtractMode()) {
      case NOT_IN_LOCAL_CACHE:
         logger.info("Call local data source harness.");
         messageStatus = extractLocal(pr, productId);
         break;
      case GLOBAL:
         logger.info("Call cache harness.");
         messageStatus = extractCache(pr, productId);
         break;
      default:
         logger.error("Unknown Extraction Mode for the {}", request.getExtractMode());
         result = Status.ERROR;
         break;
      }

      // Refresh processed request as extractCache updates it (size)
      entityManager.refresh(pr);

      // Handle message status
      if (messageStatus != null) {
         result = messageStatus.getStatus();
         logger.info("Message Status received for processed request id <{}> {}, {}", new Object[] {
               pr.getId(), messageStatus.getStatus().name(), messageStatus.getMessage()});
         switch (result) {
         case EXTRACTED:
            // update request status
            RequestUtils.updateRequestStatus(pr, messageStatus);
            break;
         case NO_RESULT_FOUND:
            if (isOnArrivalProductSubscription(pr)) {
               entityManager.remove(pr);
            } else {
               RequestUtils.updateRequestStatus(pr, messageStatus);
            }
            break;
         case ERROR:
         case ONGOING_EXTRACTION:
         default:
            RequestUtils.updateRequestStatus(pr, messageStatus);
            break;
         }
      }
      return result;
   }
   
   /**
       * Checks if is on arrival product subscription.
       *
       * @param pr the process request
       * @return true, if is on arrival product subscription
       */
   private boolean isOnArrivalProductSubscription(ProcessedRequest pr) {
      Request request = pr.getRequest();
      return (request instanceof Subscription)
            && (((Subscription) request).getFrequency() instanceof EventBasedFrequency);
   }

   /**
    * Extract from cache.
    *
    * @param pr the process request
    * @return the message status
    */
   private MessageStatus extractCache(ProcessedRequest pr, String productId) {
      try {
         Request request = pr.getRequest();

         List<Parameter> params = new ArrayList<Parameter>();
         if (productId != null) {
            Parameter productIdParam = new Parameter();
            productIdParam.setCode(ParameterCode.PRODUCT_ID);
            Value v = new Value();
            v.setValue(productId);
            HashSet<Value> values = new HashSet<Value>();
            values.add(v);
            productIdParam.setValues(values);
            params.add(productIdParam);
         } else {
            params.addAll(request.getParameters());
         }

         logger.info("Perform cache extraction with parameters: " + params);

         // In case of an on product arrival subscription, avoid re-sending product instances already sent
         // by filtering out the file inserted before the last subscription event
         Calendar lowerBoundInsertionDate = null;
         if (isOnArrivalProductSubscription(pr)) {
            Subscription subscription = (Subscription) request;
            if (subscription.getLastEventDate() != null) {
               lowerBoundInsertionDate = Calendar.getInstance();
               lowerBoundInsertionDate.setTime(subscription.getLastEventDate());
            }
         }

         CacheExtraService cacheSrv = ServiceProvider.getCacheSrv();
         return cacheSrv.extract(request.getUser(), request.getProductMetadata().getUrn(), params,
               pr.getId(), pr.getUri(), lowerBoundInsertionDate);
      } catch (Throwable t) {
         logger.error("Error extracting from cache", t);
//         String msg = MessageFormat.format(
//               JndiUtils.getString(org.openwis.management.utils.DataServiceAlerts.EXTRACTION_FAILS
//                     .getKey()), t);
         // TODO: Read the message from a bundle (??)
         String msg = MessageFormat.format("Extraction failed: product={1}, error={2}", productId, t);
         MessageStatus result = new MessageStatus();
         result.setStatus(Status.ERROR);
         result.setMessage(msg);
         raiseExtractionFailedAlarm("Cache", pr.getUri(), t);
         return result;
      }
   }

   private void raiseExtractionFailedAlarm(String source, String product, Throwable t) {
      AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
      List<Object> args = new ArrayList<Object>();
      args.add(source);
      args.add(product);
      args.add(t.toString());
      alertService.raiseEvent("Data Service", "Extraction", null,
            DataServiceAlerts.EXTRACTION_FAILS.getKey(), args);
   }
   
   /**
    * Extract local.
    *
    * @param pr the process request
    * @return the message status
    */
   private MessageStatus extractLocal(ProcessedRequest pr, String productId) {
      Request request = pr.getRequest();
      ArrayList<Parameter> parameters = new ArrayList<Parameter>(request.getParameters());
      String urn = request.getProductMetadata().getUrn();
      String localDataSource = request.getProductMetadata().getLocalDataSource();
      return localDataSourceService.extract(parameters, urn, pr.getId(), localDataSource,
            pr.getUri(), productId);
   }

   /** Get native columns from ProcessedRequestColumn */
   private String getNativeColumnForProcessedRequestColumn(ProcessedRequestColumn column) {
      switch (column) {
      case CREATION_DATE:
         return "pr.creation_date";
      case STATUS:
         return "pr.request_result_status";
      case VOLUME:
         return "pr.size";
      default:
         return "pr.creation_date";
      }
   }
   
   /**
     * {@inheritDoc}
     * @see org.openwis.dataservice.common.service.ProcessedRequestService#
     * getAllProcessedRequestsForSubscription(java.lang.Long, int, int,
     * org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestColumn,
     * org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
     */
   @SuppressWarnings("unchecked")
   @Override
   public List<LightProcessedRequestDTO> getAllProcessedRequestsByRequest(
         @WebParam(name = "id") Long requestID,
         @WebParam(name = "firstResult") Integer firstResult,
         @WebParam(name = "maxResults") Integer maxResults,
         @WebParam(name = "column") ProcessedRequestColumn column,
         @WebParam(name = "dir") SortDirection sortDirection) {
      checkParams(firstResult, maxResults);

      // Default column is URN
      ProcessedRequestColumn col = (ProcessedRequestColumn) ObjectUtils.defaultIfNull(column,
            ProcessedRequestColumn.CREATION_DATE);
      String colNative = getNativeColumnForProcessedRequestColumn(col);
      
      // Default direction is Ascending.
      SortDirection dir = (SortDirection) ObjectUtils.defaultIfNull(sortDirection,
            SortDirection.ASC);

      // Create a native query to avoid JOIN FETCH performed by hibernate
      // otherwise hibernate fetch all, do all the sorting and 
      // compute limit/offset, everything in memory !!!
      String q = "SELECT * " +
            "FROM openwis_processed_request pr, openwis_request r " +
            "WHERE pr.request_id=r.request_id AND r.request_id = ?1 " +
            "ORDER BY " + colNative + " " + dir + " " +
            "LIMIT " + maxResults +
            " OFFSET " + firstResult;
      
      Query query = entityManager.createNativeQuery(q, ProcessedRequest.class);
      query.setParameter(1, requestID);

      List<ProcessedRequest> queryResults = query.getResultList();

      Collection<LightProcessedRequestDTO> dtos = CollectionUtils.collect(queryResults,
            new Transformer() {
               @Override
               public Object transform(Object pr) {
                  return LightProcessedRequestDTO.processedRequestToDTO(((ProcessedRequest) pr));
               }
            });
      return new ArrayList<LightProcessedRequestDTO>(dtos);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#
    * getAllProcessedRequestsForSubscription(java.lang.Long, int, int,
    * org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestColumn,
    * org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
    */
   @Override
   public int getAllProcessedRequestsByRequestCount(@WebParam(name = "id") Long requestID) {
      String q = "SELECT count(pr.processed_request_id) " +
      "FROM openwis_processed_request pr, openwis_request r " +
      "WHERE pr.request_id=r.request_id AND r.request_id = ?1 ";
      Query query = entityManager.createNativeQuery(q);
      query.setParameter(1, requestID);

      Number res = (Number) query.getSingleResult();
      return res.intValue();
   }

   /**
    * Check params.
    *
    * @param firstResult the first result
    * @param maxResults the max results
    */
   private void checkParams(Integer firstResult, Integer maxResults) {
      if (firstResult != null && firstResult < 0) {
         throw new IllegalArgumentException("FirstResult must be = 0!");
      }
      if (maxResults != null && maxResults <= 0) {
         throw new IllegalArgumentException("MaxResults must be > 0!");
      }
   }
   
   /** Get native columns from RequestColumn */
   private String getNativeColumnForRequestColumn(RequestColumn column) {
      switch (column) {
      case URN:
         return "pm.urn";
      case TITLE:
         return "pm.title";
      case LOCAL_DATASOURCE:
         return "pm.local_data_source";
      case USER:
         return "r.user_id";
      case ID:
         return "r.request_id";
      case CREATION_DATE:
         return "pr.creation_date";
      case STATUS:
         return "pr.request_result_status";
      case VOLUME:
         return "pr.size";
      default:
         return "pr.creation_date";
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#getAllProcessedRequestsByUsers(java.util.Collection, org.openwis.dataservice.common.domain.entity.enumeration.ProcessedRequestFilter, java.lang.Integer, java.lang.Integer, org.openwis.dataservice.common.domain.entity.enumeration.RequestColumn, org.openwis.dataservice.common.domain.entity.enumeration.SortDirection)
    */
   @SuppressWarnings("unchecked")
   @Override
   public ProcessedRequestListResult getAllProcessedRequestsByUsers(
         @WebParam(name = "userNames") Collection<String> userNames,
         @WebParam(name = "prfilter") ProcessedRequestFilter prfilter,
         @WebParam(name = "firstResult") Integer firstResult,
         @WebParam(name = "maxResults") Integer maxResults,
         @WebParam(name = "column") RequestColumn column,
         @WebParam(name = "dir") SortDirection sortDirection) {
      checkParams(firstResult, maxResults);
      
      ProcessedRequestListResult result = new ProcessedRequestListResult();
      if (userNames != null && userNames.isEmpty()) {
         result.setList(new ArrayList<ProcessedRequest>());
      } else {

         // Default column is URN
         RequestColumn col = (RequestColumn) ObjectUtils.defaultIfNull(column, RequestColumn.CREATION_DATE);
         String colNative = getNativeColumnForRequestColumn(col);
         
         // Default direction is Ascending.
         SortDirection dir = (SortDirection) ObjectUtils.defaultIfNull(sortDirection,
               SortDirection.ASC); 
         
         String userConstraint = "";
         if (userNames != null) {
            userConstraint = "r.user_id IN (?2) AND ";
         }

         // Create a native query to avoid JOIN FETCH performed by hibernate
         // otherwise hibernate fetch all, do all the sorting and 
         // compute limit/offset, everything in memory !!!
         String q = "SELECT * " +
         		"FROM openwis_processed_request pr, openwis_request r join openwis_product_metadata pm on r.product_metadata_id=pm.product_metadata_id " +
         		"WHERE pr.request_id=r.request_id AND " +
         		userConstraint +
         		"r.request_object_type in (?1) " +
         		"ORDER BY " + colNative + " " + dir + " " +
         		"LIMIT " + maxResults +
         		" OFFSET " + firstResult;
         
         Query query = entityManager.createNativeQuery(q, "allPRByUsers");
         
         if (userNames != null) {
            query.setParameter(2, userNames);
         }
         query.setParameter(1, prfilter.getAttribute());

         // SqlResultSetMapping "allPRByUsers" defined in ProcessedRequest entity
         // It returns a list of Object[2] <ProcessedRequest,ProductMetadata>
         List<Object[]> list = query.getResultList();
         List<ProcessedRequest> prList = new ArrayList<ProcessedRequest>(); 
         for (Object[] e : list) {
            prList.add((ProcessedRequest) e[0]);
         }
         
         result.setList(prList);

         // Count results
         String qCount = "SELECT count(pr.processed_request_id) " +
         "FROM openwis_processed_request pr, openwis_request r, openwis_product_metadata pm " +
         "WHERE pr.request_id=r.request_id AND r.product_metadata_id=pm.product_metadata_id AND " +
         userConstraint +
         "r.request_object_type in (?1) ";
         Query queryCount = entityManager.createNativeQuery(qCount);
         if (userNames != null) {
            queryCount.setParameter(2, userNames);
         }
         queryCount.setParameter(1, prfilter.getAttribute());

         Number res = (Number) queryCount.getSingleResult();
         result.setCount(res.intValue());
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#getAllProcessedRequestsByUsersCount(java.util.Collection)
    */
   @Override
   public int getAllProcessedRequestsByUsersCount(
         @WebParam(name = "userNames") Collection<String> userNames) {
      int result;
      if (userNames != null && userNames.isEmpty()) {
         result = 0;
      } else {
         String userConstraint = "";
         if (userNames != null) {
            userConstraint = "JOIN pr.request request WHERE request.user IN (:users)";
         }
         Query query = entityManager.createQuery("SELECT COUNT(*) FROM ProcessedRequest pr " + userConstraint);
         if (userNames != null) {
            query.setParameter("users", userNames);
         }
         
         Number res = (Number) query.getSingleResult();
         result = res.intValue();
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#getProcessedRequestForAdhoc(java.lang.Long)
    */
   @Override
   public ProcessedRequest getProcessedRequestForAdhoc(@WebParam(name = "id") Long adhocId) {
      Query query = entityManager.createQuery("SELECT pr FROM ProcessedRequest pr "
            + "LEFT JOIN FETCH pr.request adhoc "
            + "WHERE adhoc.id = :adhocId AND adhoc.requestType LIKE :requestType");

      query.setParameter("adhocId", adhocId);
      query.setParameter("requestType", "ADHOC");

      ProcessedRequest result = null;
      try {
         result = (ProcessedRequest) query.getSingleResult();
      } catch (NoResultException e) {
         result = null;
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#getProcessedRequestForAdhoc(java.lang.Long)
    */
   @Override
   public ProcessedRequest getFullProcessedRequestForAdhoc(@WebParam(name = "id") Long adhocId) {
      Query query = entityManager.createQuery("SELECT pr FROM ProcessedRequest pr "
            + "LEFT JOIN FETCH pr.request adhoc " + "JOIN FETCH adhoc.productMetadata pm "
            + "LEFT JOIN FETCH adhoc.parameters ssp " + "LEFT JOIN FETCH ssp.values "
            + "LEFT JOIN FETCH adhoc.primaryDissemination "
            + "WHERE adhoc.id = :adhocId AND adhoc.requestType LIKE :requestType");

      query.setParameter("adhocId", adhocId);
      query.setParameter("requestType", "ADHOC");

      ProcessedRequest result = null;
      try {
         result = (ProcessedRequest) query.getSingleResult();
      } catch (NoResultException e) {
         result = null;
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#getProcessedRequestForAdhoc(java.lang.Long)
    */
   @Override
   public ProcessedRequest getFullProcessedRequest(Long processedRequestID) {
      Query query = entityManager.createQuery("SELECT pr FROM ProcessedRequest pr "
            + "LEFT JOIN FETCH pr.request req " + "JOIN FETCH req.productMetadata pm "
            + "LEFT JOIN FETCH req.parameters ssp " + "LEFT JOIN FETCH ssp.values "
            + "LEFT JOIN FETCH req.primaryDissemination "
            + "WHERE pr.id = :id");

      query.setParameter("id", processedRequestID);

      ProcessedRequest result = null;
      try {
         result = (ProcessedRequest) query.getSingleResult();
      } catch (NoResultException e) {
         result = null;
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#deleteProcessedRequestsByRequest(java.lang.Long)
    */
   @Override
   public void deleteProcessedRequestsByRequest(@WebParam(name = "id") Long requestID) {
	   // delete process request
	   Query query = this.entityManager.createQuery("Select pr From ProcessedRequest pr Where pr.request.id = :id");
	   query.setParameter("id", requestID);
	   
	   @SuppressWarnings("unchecked")
      List<ProcessedRequest> queryResults = query.getResultList();
	   for (ProcessedRequest processedRequest : queryResults) {
         entityManager.remove(processedRequest);
      }
	   
	   entityManager.flush();
   }
   
   /* (non-Javadoc)
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#deleteProcessedRequests(java.util.List)
    */
   @Override
	public void deleteProcessedRequests(@WebParam(name="processedRequestIDs") List<Long> processedRequestIDs) {
	   if(processedRequestIDs!=null && !processedRequestIDs.isEmpty()) {
		   for(Long id : processedRequestIDs) {
			   deleteProcessedRequestWithAdHoc(id);
		   }
	   }
	}

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#deleteProcessedRequestWithAdHoc(java.lang.Long)
    */
   @Override
   public void deleteProcessedRequestWithAdHoc(@WebParam(name = "id") Long processedRequestId) {
      ProcessedRequest pr = getProcessedRequest(processedRequestId);
      if (pr != null) {
         Request request = pr.getRequest();
         if (request != null && request instanceof AdHoc) {
            entityManager.remove(request);
         }
         entityManager.remove(pr);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#
    * updateProcessedRequest(org.openwis.dataservice.common.domain.entity.request.ProcessedRequest)
    */
   @Override
   public void updateProcessedRequest(ProcessedRequest processedRequest) {
      entityManager.merge(processedRequest);
      entityManager.flush();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#updateProcessedRequestOnStagingPostCleaning()
    */
   @Override
   public void clearProcessedRequestStagingPost() {
      Query query = entityManager.createNamedQuery("ProcessedRequest.clearStagingPost");
      query.executeUpdate();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ProcessedRequestService#updateProcessedRequestOnStagingPostUpdate(java.lang.String)
    */
   @Override
   public void clearProcessedRequestStagingPostByUri(String uri) {
      // delete process requests
      Query query = this.entityManager.createQuery("Select pr From ProcessedRequest pr Where pr.uri = :uri");
      query.setParameter("uri", uri);

      // should be only one!
      @SuppressWarnings("unchecked")
      List<ProcessedRequest> queryResults = query.getResultList();
      for (ProcessedRequest processedRequest : queryResults) {
         deleteProcessedRequestWithAdHoc(processedRequest.getId());
      }
   }

}
