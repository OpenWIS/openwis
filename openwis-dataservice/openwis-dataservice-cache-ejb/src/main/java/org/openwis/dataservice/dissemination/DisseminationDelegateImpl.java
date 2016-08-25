package org.openwis.dataservice.dissemination;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.ws.BindingProvider;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.domain.entity.enumeration.ClassOfService;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Request;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.request.dissemination.DisseminationJob;
import org.openwis.dataservice.common.domain.entity.request.dissemination.FTPDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MSSFSSDissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MailDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.PublicDissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.RMDCNDissemination;
import org.openwis.dataservice.common.domain.entity.request.dissemination.ShoppingCartDissemination;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmBuilder;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmRequestType;
import org.openwis.dataservice.common.service.MailSender;
import org.openwis.dataservice.common.service.UserAlarmManagerLocal;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.dataservice.util.DisseminationRequestInfo;
import org.openwis.dataservice.util.DisseminationUtils;
import org.openwis.dataservice.util.FilePacker;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.StatisticsMessage;
import org.openwis.harness.dissemination.Diffusion;
import org.openwis.harness.dissemination.Dissemination;
import org.openwis.harness.dissemination.DisseminationImplService;
import org.openwis.harness.dissemination.DisseminationInfo;
import org.openwis.harness.dissemination.DisseminationStatus;
import org.openwis.harness.dissemination.RequestStatus;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.AlertService;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.ManagedServiceIdentifier;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegate for dissemination MDB.
 */
@Stateless(name = "DisseminationDelegate")
@Local(DisseminationDelegate.class)
public class DisseminationDelegateImpl implements ConfigurationInfo, DisseminationDelegate {

   private static Logger logger = LoggerFactory.getLogger(DisseminationDelegateImpl.class);

   private static int zipBufferSize = 2048;

   private String primaryDissMeth;

   private String secondaryDissMeth;

   private static String PUBLIC_DISS_METHOD = "PUBLIC";

   private static String PRIVATE_DISS_METHOD = "PRIVATE";

   private static String SP_DISS_METHOD = "SP";

   private static String NONE_DISS_METHOD = "NONE";

   // Dissemination states
   private static String NONE_DISS_STATE = "NONE";

   private static String ONGOING_DISS_STATE = "ONGOING_DISSEMINATION";

   private static String SUCCESS_DISS_STATE = "DISSEMINATED";

   private static String FAILURE_DISS_STATE = "FAILED";

   // Dissemination harness URLs
   private String disseminationHarnessPublicURL; // = JndiUtils.getString(DISSEMINATION_HARNESS_PUBLIC_URL_KEY);

   private String disseminationHarnessRMDCNURL; // = JndiUtils.getString(DISSEMINATION_HARNESS_RMDCN_URL_KEY);

   // Staging Post items
   private static String stagingPostDirectory; // = JndiUtils.getString(STAGING_POST_DIRECTORY_KEY);
   private static final String STAGING_POST_MAIL_PROPERTIES = "openwis-sp-mail-message";
   private static final String STAGING_POST_MAIL_SUBJECT_KEY = "dataservice.dissemination.stagingPostMessage.subject";
   private static final String STAGING_POST_MAIL_CONTENT_KEY = "dataservice.dissemination.stagingPostMessage.content";
   private static String stagingPostUrl; // = JndiUtils.getString(STAGING_POST_URL_KEY);

   @EJB
   private UserAlarmManagerLocal userAlarmManager;

   @PersistenceContext
   protected EntityManager entityManager;

   /**
    * injection ConnectionFactory
    */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory cf;

   /**
    * injection queue
    */
   @Resource(mappedName = "java:/queue/StatisticsQueue")
   private Queue queue;
   
   @PostConstruct
   public void initialize() {
      disseminationHarnessPublicURL = ConfigServiceFacade.getInstance().getString(DISSEMINATION_HARNESS_PUBLIC_URL_KEY);
      disseminationHarnessRMDCNURL = ConfigServiceFacade.getInstance().getString(DISSEMINATION_HARNESS_RMDCN_URL_KEY);
      stagingPostDirectory = ConfigServiceFacade.getInstance().getString(STAGING_POST_DIRECTORY_KEY);
      stagingPostUrl = ConfigServiceFacade.getInstance().getString(STAGING_POST_URL_KEY);
   }

   private ControlService controlService;

   private ControlService getControlService() {
      if (controlService == null) {
         try {
            controlService = ManagementServiceBeans.getInstance().getControlService();
         } catch (NamingException e) {
            controlService = null;
         }
      }
      return controlService;
   }
   
   /**
    * Process JMS message.
    * @param entityManager the {@link EntityManager}
    * @param message the message to process
    */
   @Override
   public void processMessage(DisseminationRequestInfo dissRequestInfo) {
      if (!isDisseminationEnabled()) return;

         // retrieve the ProcessedRequest from the database, using the id from the request info just taken from the queue
         ProcessedRequest processedRequest = getProcessedRequest(dissRequestInfo
               .getProcessedRequestId());

         if (processedRequest == null || processedRequest.getRequest() == null) {
            logger.error("No process request found with request id {}",
                  dissRequestInfo.getProcessedRequestId());
         } else {
            // Create a dissemination job, using the information contained in the processedRequest
            DisseminationJob dissJob = createDisseminationJob(processedRequest);

            // Create the entry in the database
            entityManager.persist(dissJob);

            // start dissemination
            startDissemination(dissJob, true);
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
            logger.error("Retrieved processed request id: " + processedRequest.getId()
                  + " differs from requested one: " + requestId);
         }
      } catch (EntityNotFoundException e) {
         logger.error("EntityNotFoundException: " + e.getMessage());
         processedRequest = null;
      }

      return processedRequest;
   }

   /**
    * Create a DisseminationJob object using a processed request entry from the database.
    * The DisseminationJob contains information which dissemination methods were selected for the request.
    *
    * @param processedRequest the processedRequest containing the relevant dissemination information
    * @return the generated DisseminationJob object
    */
   private DisseminationJob createDisseminationJob(ProcessedRequest processedRequest) {
      Request request = processedRequest.getRequest();

      setDisseminationMethods(request);

      // Fill the dissemination job
      DisseminationJob dissJob = new DisseminationJob();

      dissJob.setPrimaryDissemination(primaryDissMeth);
      dissJob.setSecondaryDissemination(secondaryDissMeth);

      dissJob.setPrimaryState(NONE_DISS_STATE);
      dissJob.setSecondaryState(NONE_DISS_STATE);
      dissJob.setFinalState(NONE_DISS_STATE);

      dissJob.setRequestId(processedRequest.getId());
      dissJob.setFileURI(processedRequest.getUri());

      dissJob.setTimeStamp(System.currentTimeMillis());

      return dissJob;
   }

   /**
    * Set the primary and secondary dissemination methods for a certain request.
    *
    * @param request the request from a processedRequest containing the relevant dissemination information
    */
   private void setDisseminationMethods(Request request) {
      // The primary dissemination method has to be set in any case
      // The secondary one may be left as none, in case that both disseminations from the request go to the same harness
      secondaryDissMeth = NONE_DISS_METHOD;

      // The primary dissemination shall go to the public harness
      if (request.getPrimaryDissemination() instanceof PublicDissemination) {
         primaryDissMeth = PUBLIC_DISS_METHOD;

         // A secondary dissemination is defined
         if (request.getSecondaryDissemination() != null) {
            // The same as the primary -> no secondary dissemination method necessary
            if (request.getSecondaryDissemination() instanceof PublicDissemination) {
               secondaryDissMeth = NONE_DISS_METHOD;
            }
            // Private harness selected
            else if (request.getSecondaryDissemination() instanceof RMDCNDissemination) {
               secondaryDissMeth = PRIVATE_DISS_METHOD;
            }
            // Dissemination via the StagingPost was selected
            else if (request.getSecondaryDissemination() instanceof ShoppingCartDissemination) {
               secondaryDissMeth = SP_DISS_METHOD;
            }
         }
      }
      // The primary dissemination shall go to the private harness
      else if (request.getPrimaryDissemination() instanceof RMDCNDissemination) {

         primaryDissMeth = PRIVATE_DISS_METHOD;

         // A secondary dissemination is defined
         if (request.getSecondaryDissemination() != null) {
            // The same as the primary -> no secondary dissemination method necessary
            if (request.getSecondaryDissemination() instanceof RMDCNDissemination) {
               secondaryDissMeth = NONE_DISS_METHOD;
            }
            // Public harness selected
            else if (request.getSecondaryDissemination() instanceof PublicDissemination) {
               secondaryDissMeth = PUBLIC_DISS_METHOD;
            }
            // Dissemination via the StagingPost was selected
            else if (request.getSecondaryDissemination() instanceof ShoppingCartDissemination) {
               secondaryDissMeth = SP_DISS_METHOD;
            }
         }
      }
      // The primary dissemination shall go via the StagingPost
      else if (request.getPrimaryDissemination() instanceof ShoppingCartDissemination) {

         primaryDissMeth = SP_DISS_METHOD;

         // A secondary dissemination is defined
         if (request.getSecondaryDissemination() != null) {
            // Private harness selected
            if (request.getSecondaryDissemination() instanceof RMDCNDissemination) {
               secondaryDissMeth = PRIVATE_DISS_METHOD;
            }
            // Public harness selected
            else if (request.getSecondaryDissemination() instanceof PublicDissemination) {
               secondaryDissMeth = PUBLIC_DISS_METHOD;
            }
            // As StagingPost dissemination is not going to any harness, set secondary dissemination method,
            // although it is the same as the primary
            else if (request.getSecondaryDissemination() instanceof ShoppingCartDissemination) {
               secondaryDissMeth = SP_DISS_METHOD;
            }
         }
      }
      // The primary dissemination is set to MSSFSSDissemination -> this should not hanppen
      else if (request.getPrimaryDissemination() instanceof MSSFSSDissemination) {

         primaryDissMeth = NONE_DISS_METHOD;

         logger.error(
               "MSSFSS was specified as primary dissemination in request {} -> this is not supported!",
               request.getId());

         // A secondary dissemination is defined
         if (request.getSecondaryDissemination() != null) {
            // Private harness selected
            if (request.getSecondaryDissemination() instanceof RMDCNDissemination) {
               secondaryDissMeth = PRIVATE_DISS_METHOD;
            }
            // Public harness selected
            else if (request.getSecondaryDissemination() instanceof PublicDissemination) {
               secondaryDissMeth = PUBLIC_DISS_METHOD;
            }
            // As StagingPost dissemination is not going to any harness, set secondary dissemination method,
            // although it is the same as the primary
            else if (request.getSecondaryDissemination() instanceof ShoppingCartDissemination) {
               secondaryDissMeth = SP_DISS_METHOD;
            }
         }
      }
   }

   /**
    * Process the jobs from the DisseminationJob database table.
    *
    * The database table is scanned. For the oldest entry that is not yet "on-going" or successfully processed
    * and that still has a dissemination target left it will start a dissemination and update the job entry to be "on-going".
    *
    * For those entries that are not "on-going" and not yet successfully processed, but that have no possible dissemination target left,
    * it will try the StagingPost dissemination (if not yet done already as a "normal" dissemination attempt),
    * and then set the final dissemination state in the job entry.
    *
    * For those entries that are finished (either successful or not) it will set the final state of the dissemination job. In addition
    * it will set the RequestResultStatus of the processed request in this case.
    */
   @SuppressWarnings("unchecked")
   @Override
   public void processJobs() {
      List<DisseminationJob> dissJobs = new ArrayList<DisseminationJob>();

      Query jobQuery = entityManager
            .createQuery("SELECT dj FROM DisseminationJob dj WHERE dj.finalState = '"
                  + NONE_DISS_STATE + "' OR dj.finalState = '" + ONGOING_DISS_STATE
                  + "' ORDER BY TIMESTAMP ASC");
      dissJobs = jobQuery.getResultList();

      if (logger.isDebugEnabled()) {
         logger.debug("Number of jobs that are onging or not yet started: {}", dissJobs.size());
      }

      for (DisseminationJob dJob : dissJobs) {
         boolean found = false;
         boolean startPrimary = true;

         if (logger.isDebugEnabled()) {
            logger.debug("Unfinished dissemination job: request id " + dJob.getRequestId()
                  + " | primaryState: " + dJob.getPrimaryState() + " | primaryDissemination: "
                  + dJob.getPrimaryDissemination());
         }

         if (dJob.getPrimaryState().equals(NONE_DISS_STATE)) {
            found = true;
         } else if (dJob.getPrimaryState().equals(FAILURE_DISS_STATE)) {
            if (!dJob.getSecondaryDissemination().equals(NONE_DISS_METHOD)
                  && dJob.getSecondaryState().equals(NONE_DISS_STATE)) {
               found = true;
               startPrimary = false;
            }
         }

         if (found) {
            startDissemination(dJob, startPrimary);
         }
      }


      for (DisseminationJob dissJob : dissJobs) {
         if (dissJob.getPrimaryState().equals(SUCCESS_DISS_STATE)
               || dissJob.getSecondaryState().equals(SUCCESS_DISS_STATE)) {
            // update dissJob final status to success
            logger.info("Final dissemination state SUCCESS for request: " + dissJob.getRequestId());

            mergeDissJobFinalState(dissJob, SUCCESS_DISS_STATE);

            mergeProcessedRequestFinalStatus(dissJob, RequestResultStatus.DISSEMINATED);
         } else if (dissJob.getPrimaryState().equals(FAILURE_DISS_STATE)) {
            if (dissJob.getSecondaryDissemination().equals(NONE_DISS_METHOD)
                  || dissJob.getSecondaryState().equals(FAILURE_DISS_STATE)) {
               ProcessedRequest processedRequest = getProcessedRequest(dissJob.getRequestId());

               // try SP dissemination as a fallback, if not yet tried; update final state of diss job according the result
               if (!dissJob.getPrimaryDissemination().equals(SP_DISS_METHOD)
                     && !dissJob.getSecondaryDissemination().equals(SP_DISS_METHOD)) {
                  // alert if primary or secondary dissemination failed
                  String errorMsg = "Primary (non staging post) dissamination "
                        + dissJob.getPrimaryDissemination()
                        + " failed and "
                        + (dissJob.getSecondaryDissemination().equals(NONE_DISS_METHOD) ? " there was no secondary dissemination. "
                              : " secondary (non staging post) dissemination "
                                    + dissJob.getSecondaryDissemination() + " also failed. ")
                        + "Trying staging post dissemination.";
                  raiseDeliveryFailedEvent(processedRequest, dissJob, errorMsg);

                  processedRequest
                        .setMessage("Primary/Secondary (non-staging-post-) Dissemination failed. Falling back to Staging Post Dissemination.");

                  boolean success = disseminateViaSP(dissJob, true);

                  if (success) {
                     logger.info("Final dissemination state SUCCESS for request: "
                           + dissJob.getRequestId());

                     mergeDissJobFinalState(dissJob, SUCCESS_DISS_STATE);

                     mergeProcessedRequestFinalStatus(dissJob, RequestResultStatus.DISSEMINATED);
                  } else {
                     logger.error("Final dissemination state FAILURE for request: "
                           + dissJob.getRequestId());

                     processedRequest.setMessage("Backup-Staging-Post-Dissemination failed.");

                     mergeDissJobFinalState(dissJob, FAILURE_DISS_STATE);

                     mergeProcessedRequestFinalStatus(dissJob, RequestResultStatus.FAILED);
                  }
               } else {
                  logger.error("Final dissemination state FAILURE for request: "
                        + dissJob.getRequestId());

                  processedRequest.setMessage("Primary/Secondary Dissemination failed.");

                  mergeDissJobFinalState(dissJob, FAILURE_DISS_STATE);

                  mergeProcessedRequestFinalStatus(dissJob, RequestResultStatus.FAILED);
               }
            }
         }
      }
   }

   /**
    * Start the dissemination, depending on the selected dissemination method.
    *
    * @param dJob the DisseminationJob containing the relevant dissemination information
    * @param startPrimary a flag saying if the primary dissemination is to be started or the secondary one
    */
   private void startDissemination(DisseminationJob dJob, boolean startPrimary) {
      boolean started;

      if (startPrimary && dJob.getPrimaryDissemination().equals(SP_DISS_METHOD)) {
         started = disseminateViaSP(dJob, startPrimary);

         if (started) {
            if (logger.isInfoEnabled()) {
               logger.info("Primary dissemination state SUCCESS for request: "
                     + dJob.getRequestId());
               logger.info("Final dissemination state SUCCESS for request: "
                     + dJob.getRequestId());
            }
            mergeDissJobPrimaryState(dJob, SUCCESS_DISS_STATE);
            mergeDissJobFinalState(dJob, SUCCESS_DISS_STATE);
            mergeProcessedRequestFinalStatus(dJob, RequestResultStatus.DISSEMINATED);
         } else {
            logger.error("Primary dissemination state FAILURE for request: " + dJob.getRequestId());
            mergeDissJobPrimaryState(dJob, FAILURE_DISS_STATE);
         }
      } else if (!startPrimary && dJob.getSecondaryDissemination().equals(SP_DISS_METHOD)) {
         started = disseminateViaSP(dJob, startPrimary);

         if (started) {
            if (logger.isInfoEnabled()) {
               logger.info("Secondary dissemination state SUCCESS for request: "
                     + dJob.getRequestId());
            }
            mergeDissJobSecondaryState(dJob, SUCCESS_DISS_STATE);
         } else {
            logger.error("Secondary dissemination state FAILURE for request: "
                  + dJob.getRequestId());
            mergeDissJobSecondaryState(dJob, FAILURE_DISS_STATE);
         }
      } else if (startPrimary && dJob.getPrimaryDissemination().equals(NONE_DISS_METHOD)) {
         logger.error("Primary dissemination was not specified correctly -> set primary dissemination state to FAILURE");
         mergeDissJobPrimaryState(dJob, FAILURE_DISS_STATE);
      } else {
         started = disseminateViaHarness(dJob, startPrimary);

         // update dissJob status accordingly
         if (startPrimary) {
            if (started) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Primary dissemination state to ONGOING for request: "
                        + dJob.getRequestId());
               }
               mergeDissJobPrimaryState(dJob, ONGOING_DISS_STATE);
            } else {
               logger.error("Failed to start primary dissemination for request: "
                     + dJob.getRequestId());
               mergeDissJobPrimaryState(dJob, FAILURE_DISS_STATE);
            }
         } else {
            if (started) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Secondary dissemination state to ONGOING for request: "
                        + dJob.getRequestId());
               }
               mergeDissJobSecondaryState(dJob, ONGOING_DISS_STATE);
            } else {
               logger.error("Failed to start secondary dissemination for request: "
                     + dJob.getRequestId());
               mergeDissJobSecondaryState(dJob, FAILURE_DISS_STATE);
            }
         }
      }
   }

   /**
    * Merge a DisseminationJob back to the database, setting the final state.
    *
    * @param dissJob the DisseminationJob to be merged
    * @param finalState the finalState to be set
    */
   private void mergeDissJobFinalState(DisseminationJob dissJob, String finalState) {
      dissJob.setFinalState(finalState);

      // Write back to database
      entityManager.merge(dissJob);
   }

   /**
    * Merge a DisseminationJob back to the database, setting the primary state.
    *
    * @param dissJob the DisseminationJob to be merged
    * @param primaryState the primaryState to be set
    */
   private void mergeDissJobPrimaryState(DisseminationJob dissJob, String primaryState) {
      dissJob.setPrimaryState(primaryState);

      // Write back to database
      entityManager.merge(dissJob);
   }

   /**
    * Merge a DisseminationJob back to the database, setting the secondary state.
    *
    * @param dissJob the DisseminationJob to be merged
    */
   private void mergeDissJobSecondaryState(DisseminationJob dissJob, String secondaryState) {
      dissJob.setSecondaryState(secondaryState);

      // Write back to database
      entityManager.merge(dissJob);
   }

   /**
    * Merge a processed request back to the database, setting the RequestResultStatus.
    *
    * @param processedRequest the ProcessedRequest to be merged
    * @param resultStatus the RequestResultStatus to be set in the processed request
    */
   private void mergeProcessedRequestStatus(ProcessedRequest processedRequest,
         RequestResultStatus resultStatus) {
      if (processedRequest != null) {
         processedRequest.setRequestResultStatus(resultStatus);

         // Write back to database
         entityManager.merge(processedRequest);
      }
   }

   /**
    * Merge a processed request back to the database, setting the final RequestResultStatus and the completion date.
    *
    * @param dissJob the DisseminationJob containing the relevant dissemination information
    * @param resultStatus the RequestResultStatus to be set in the processed request
    */
   private void mergeProcessedRequestFinalStatus(DisseminationJob dissJob,
         RequestResultStatus resultStatus) {
      // Get the processed request
      ProcessedRequest processedRequest = getProcessedRequest(dissJob.getRequestId());

      if (processedRequest != null) {
         processedRequest.setRequestResultStatus(resultStatus);
         processedRequest.setCompletedDate(new Date(System.currentTimeMillis()));

         if (RequestResultStatus.DISSEMINATED == resultStatus) {
            if ((SUCCESS_DISS_STATE.equals(dissJob.getPrimaryState()) && !SP_DISS_METHOD
                  .equals(dissJob.getPrimaryDissemination()))
                  || (SUCCESS_DISS_STATE.equals(dissJob.getSecondaryState()) && !SP_DISS_METHOD
                        .equals(dissJob.getSecondaryDissemination()))) {
               updateDisseminatedDataStatistics(dissJob, processedRequest);
            }
         }

         if (RequestResultStatus.FAILED == resultStatus) {
            String errorMsg = "Dissemination failed completely.";
            raiseDeliveryFailedEvent(processedRequest, dissJob, errorMsg);
         }

         // Write back to database
         entityManager.merge(processedRequest);
         
         
         // In case of subscription, update last event date (if necessary)
         if (processedRequest.getRequest() instanceof Subscription) {
            Subscription subscription = (Subscription) processedRequest.getRequest();
            if (subscription.getLastEventDate() == null || 
                  processedRequest.getCompletedDate().after(subscription.getLastEventDate())) {
               subscription.setLastEventDate(processedRequest.getCompletedDate());
               entityManager.merge(subscription);
            }
         }
      }
   }

   private void updateDisseminatedDataStatistics(DisseminationJob dissJob,
         ProcessedRequest processedRequest) {

      String userId = processedRequest.getRequest().getUser();
      String date = DateTimeUtils.formatUTC(new Date(System.currentTimeMillis()));
      int nbFiles = dissJob.getNumberOfFiles();
      long totalSize = dissJob.getTotalSize();
      
      // Send update statistics message
      StatisticsMessage message = new StatisticsMessage();
      message.setUserId(userId);
      message.setDate(date);
      message.setNbFiles(nbFiles);
      message.setTotalSize(totalSize);
      message.setCommand(StatisticsMessage.CMD_UPDATE_USER_DISSEMINATED_BY_TOOL_DATA);
      sendStatisticsUpdate(message);

      //disseminationStatistics.updateUserDisseminatedByToolData(userId, date, nbFiles, totalSize);
   }
   
   /**
    * Send statistics update message to the dedicated JMS queue.
    *
    * @param statisticsMessage the statistics message
    */
   private void sendStatisticsUpdate(StatisticsMessage statisticsMessage) {
      Connection connection = null;
      try {
         // Create queue connection
         // Create a JMS Connection
         connection = cf.createConnection();
         // Create a JMS Session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         // Create a JMS Message Producer
         MessageProducer messageProducer = session.createProducer(queue);

         // Create XML message
         StringWriter sw;
         sw = new StringWriter();
         Serializer.serialize(statisticsMessage, sw);
         String textMessage = sw.toString();
         TextMessage messageToSend = session.createTextMessage(textMessage);
         // Send message in the request queue
         messageProducer.send(messageToSend);

      } catch (Throwable t) {
         logger.error("Unable to create message for the statistics queue", t);
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

   private void raiseDeliveryFailedEvent(ProcessedRequest processedRequest,
         DisseminationJob dissJob, String errorMessage) {
      AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
      if (alertService == null) {
         logger.error("Could not get hold of the Alert Service. No alert was passed.");
      } else {
         String source = "openwis-dataservice-cache-ejb-DisseminationManagerTimerServiceImpl";
         String location = "Dissemination";
         String severity = "ERROR";
         String eventId = DataServiceAlerts.DELIVERY_FAILS.getKey();

         Object user = processedRequest.getRequest().getUser();
         Object productInfo = dissJob.getFileURI();
         Object cause = errorMessage;

         List<Object> arguments = Arrays.asList(user, productInfo, cause);

         alertService.raiseEvent(source, location, severity, eventId, arguments);
      }
   }

   private boolean isDisseminationEnabled() {
      return getControlService().isServiceEnabled(ManagedServiceIdentifier.DISSEMINATION_SERVICE);
   }

   private long getMailDiffusionThreshold() {
      long value;
      value = ConfigServiceFacade.getInstance().getLong(MAIL_DIFFUSION_THRESHOLD_KEY);
      return value;
   }

   private long getFTPDiffusionThreshold() {
      long value;
      value = ConfigServiceFacade.getInstance().getLong(FTP_DIFFUSION_THRESHOLD_KEY);
      return value;
   }

   private boolean isDisseminationThresholdExceeded(DisseminationInfo disseminationInfo,
         DisseminationJob dissJob, String user) {
      // check threshold for Mail/FTP-Diffusion (see JNDI properties
      Diffusion diffusion = disseminationInfo.getDiffusion();
      long size = dissJob.getTotalSize();
      logger.info("Size of file to be disseminated " + size);
      long threshold = Integer.MAX_VALUE;
      if (diffusion instanceof org.openwis.harness.dissemination.MailDiffusion) {
         // check if size of file(s) is not bigger then the mail diffusion threshold
         threshold = getMailDiffusionThreshold();
         logger.info("Diffusion is via Mail. Threshold = " + threshold);
      }
      if (diffusion instanceof org.openwis.harness.dissemination.FTPDiffusion) {
         // check if size of file(s) is not bigger then the ftp diffusion threshold
         threshold = getFTPDiffusionThreshold();
         logger.info("Diffusion is via FTP. Threshold = " + threshold);
      }
      if (size > threshold) {
         // raise alarm: threshold for chosen distribution system exceeded
         raiseThresholdExceededAlarm(Long.valueOf(threshold), Long.valueOf(size), user,
               disseminationInfo.getDiffusion().getClass().getSimpleName());
         return true;
      }

      return false;
   }

   /**
    * Start a dissemination via harness. The files to be disseminated are zipped / packed prior to the dissemination, if required.
    *
    * @param dissJob the DisseminationJob containing the relevant dissemination information
    * @param startPrimary a flag saying if the primary dissemination is to be started or the secondary one
    * @return true if dissemination was started successful, false otherwise
    */
   private boolean disseminateViaHarness(DisseminationJob dissJob, boolean startPrimary) {
      if (disseminationHarnessPublicURL == null || disseminationHarnessPublicURL.length() == 0
            || disseminationHarnessRMDCNURL == null || disseminationHarnessRMDCNURL.length() == 0) {
         logger.error("No dissemination harness URLs defined!");
         return false;
      }

      if (logger.isDebugEnabled()) {
         logger.debug("Now disseminate via harness");
      }

      boolean dissStatus = false;

      DisseminationInfo disseminationInfo = getDisseminationInfo(dissJob, startPrimary);

      if (disseminationInfo == null) {
         logger.error("Could not create dissemination info structure!");
      } else {
         DisseminationUtils.logDissInfo(disseminationInfo);

         ProcessedRequest processedRequest = getProcessedRequest(dissJob.getRequestId());

         if (processedRequest != null) {
            String actualURL = null;

            String requestId = processedRequest.getId().toString();

            String fileURI = processedRequest.getUri();

            // Get zip mode from the processed request and perform zipping / packing prior to dissemination

            String zipMode = getZipMode(processedRequest, startPrimary);
            // TODO: decide what to do in case that zipping / packing fails
            if (!prepareForDelivery(fileURI, zipMode, dissJob, processedRequest)) {
               logger.error("Zipping / packing of " + fileURI + " failed for request " + requestId);
            } else {
               isDisseminationThresholdExceeded(disseminationInfo, dissJob, processedRequest
                     .getRequest().getUser());

               if (startPrimary) {
                  if (dissJob.getPrimaryDissemination().equals(PUBLIC_DISS_METHOD))
                     actualURL = disseminationHarnessPublicURL;
                  else
                     actualURL = disseminationHarnessRMDCNURL;
               } else {
                  if (dissJob.getSecondaryDissemination().equals(PUBLIC_DISS_METHOD))
                     actualURL = disseminationHarnessPublicURL;
                  else
                     actualURL = disseminationHarnessRMDCNURL;
               }

               try {
                  URL url = new URL(actualURL);

                  if (logger.isDebugEnabled()) {
                     logger.debug("Connecting to " + actualURL);
                  }

                  DisseminationImplService disseminationImplService = new DisseminationImplService(
                        url);
                  Dissemination disseminationHarness = disseminationImplService
                        .getDisseminationImplPort();

                  if (actualURL.endsWith("?wsdl")) {
                     // Set endpoint
                     ((BindingProvider) disseminationHarness).getRequestContext().put(
                           BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                           actualURL.replace("?wsdl", ""));
                  }

                  if (disseminationHarness != null) {
                     DisseminationStatus status = disseminationHarness.disseminate(requestId,
                           fileURI, disseminationInfo);

                     if (logger.isDebugEnabled()) {
                        logger.debug("Returned dissemination status: "
                              + status.getRequestStatus().value() + " " + status.getRequestId()
                              + " " + status.getMessage());
                     }

                     if (status.getRequestStatus() == RequestStatus.FAILED) {
                        logger.error("Start of dissemination via harness failed: " + actualURL);

                        // XXX - lmika: Raise an alarm
	                    raiseUserAlarm(processedRequest, status);
                        // XXX - lmika: End modified code
                     } else {
                        if (logger.isInfoEnabled()) {
                           logger.info("Start of dissemination via harness succeeded: " + actualURL);
                        }

                        // Update the processed request to "ONGOING_DISSEMINATION"
                        mergeProcessedRequestStatus(processedRequest,
                              RequestResultStatus.ONGOING_DISSEMINATION);

                        dissStatus = true;
                     }
                  } else {
                     logger.error("DisseminationHarness not initialized: " + actualURL);
                  }
               } catch (MalformedURLException e) {
                  logger.error("Can not initialize the wsdl: " + actualURL);
               } catch (Throwable t) {
                  logger.error(t.getMessage(), t);
               }
            }
         }
      }

      return dissStatus;
   }

   /**
    * Raise a new user alarm.
    *
    * @param processedRequest
    * @param status
    */
   private void raiseUserAlarm(ProcessedRequest processedRequest,
			DisseminationStatus status) {
		long processedReqId = 0;
		long requestId = 0;
		UserAlarmRequestType reqType = null;

		if (processedRequest.getRequest() instanceof AdHoc) {
			reqType = UserAlarmRequestType.REQUEST;
		} else if (processedRequest.getRequest() instanceof Subscription) {
			reqType = UserAlarmRequestType.SUBSCRIPTION;
		}

		String user = processedRequest.getRequest().getUser();
      processedReqId = processedRequest.getId();
      requestId = processedRequest.getRequest().getId();

		UserAlarm alarm = new UserAlarmBuilder(user)
							.request(reqType, processedReqId, requestId)
							.message(status.getMessage())
							.getUserAlarm();

		userAlarmManager.raiseUserAlarm(alarm);
   }

   private void raiseThresholdExceededAlarm(Object threshold, Object value, Object user,
         Object dissMethod) {
      AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
      if (alertService == null) {
         logger.error("Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "openwis-dataservice-cache-ejb-DisseminationManagerTimerServiceImpl";
      String location = "Dissemination";
      String severity = "WARN";
      String eventId = DataServiceAlerts.DISSEMINATION_THRESHOLD_EXCEEDED.getKey();

      List<Object> arguments = Arrays.asList(threshold, value, user, dissMethod);

      alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

   /**
    * Get the zipping / packing information from a processed request from the database.
    *
    * @param processedRequest the processedRequest containing the relevant information
    * @param startPrimary a flag saying if the primary dissemination is to be started or the secondary one
    * @return the retrieved zip mode
    */
   private String getZipMode(ProcessedRequest processedRequest, boolean startPrimary) {
      Request request = processedRequest.getRequest();

      String zipMode = "NONE";

      if (startPrimary) {
         zipMode = request.getPrimaryDissemination().getZipMode().toString();
      } else {
         zipMode = request.getSecondaryDissemination().getZipMode().toString();
      }

      return zipMode;
   }

   /**
    * Depending on a packing option perform zipping / packing on a set of files prior to delivery
    *
    * @param fileURI the file URI
    * @param zipMode the option for the zipping / packing: NONE, ZIPPED, WMO_FTP
    * @return true if preparation was successful, false otherwise
    */
   private boolean prepareForDelivery(String fileURI, String zipMode, DisseminationJob dissJob, ProcessedRequest processedRequest) {
      boolean result = true;

      try {
         String srcFilePath = stagingPostDirectory + File.separator + fileURI;

         // fill infos for DisseminatedDataStatistics
         File srcFile = new File(srcFilePath);
         int numberOfFiles = 0;
         long totalSize = 0;

         if (srcFile.isFile()) {
            totalSize = srcFile.length();
            numberOfFiles = 1;

         } else {
            File[] fileList = srcFile.listFiles();
            if (fileList != null && fileList.length > 0) {
               numberOfFiles = fileList.length;
               for (File file : fileList) {
                  totalSize += file.length();
               }
            }
         }
         dissJob.setNumberOfFiles(numberOfFiles);
         dissJob.setTotalSize(totalSize);

         if (zipMode.equals("NONE")) {
            if (logger.isDebugEnabled()) {
               logger.debug("No zipping / packing required!");
            }
         } else if (zipMode.equals("ZIPPED")) {
            String zipFilename = srcFilePath + File.separator + "tmp.zip";

            result = zipFiles(zipFilename, srcFilePath);
         } else if (zipMode.equals("WMO_FTP")) {
            result = wmoFtpPackFiles(srcFilePath);
            if (!result) {
               processedRequest.setMessage("Warning: Extracted file is not a valid FNC, could not pack as WMO-FTP");
            }
         }
      } catch (Exception exception) {
         logger.error("URI string <" + fileURI + "> could not be zipped / packed");
         result = false;
      }
      return result;
   }

   /**
    * Zip files to a zip archive and deleting the individual files
    *
    * @param zipFilename the name of the destination zip file
    * @param srcFilePath the source file path
    * @return true if zipping was successful, false otherwise
    */
   private boolean zipFiles(String zipFilename, String srcFilePath) {
      if (!checkSourceFilePath(srcFilePath)) {
         return false;
      }

      if (logger.isInfoEnabled()) {
         logger.info("Zipping files from " + srcFilePath + " to " + zipFilename);
      }

      boolean zipSuccessful = true;

      try {
         BufferedInputStream origin = null;

         byte data[] = new byte[zipBufferSize];

         // Get a list of files from source directory
         File sourceFile = new File(srcFilePath);
         String files[] = sourceFile.list();

         // Output stream
         FileOutputStream dest = new FileOutputStream(zipFilename);
         ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

         // Step through file list
         for (String filename : files) {
            File f = new File(srcFilePath, filename);

            // Regular file
            if (f.isFile()) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Adding " + filename + " to zip file");
               }

               FileInputStream fi = new FileInputStream(f);
               origin = new BufferedInputStream(fi, zipBufferSize);

               ZipEntry entry = new ZipEntry(filename);
               out.putNextEntry(entry);

               int count;
               while ((count = origin.read(data, 0, zipBufferSize)) != -1) {
                  out.write(data, 0, count);
               }
               origin.close();

               // Delete original file
               f.delete();
            }
         }
         out.close();
      } catch (FileNotFoundException fe) {
         zipSuccessful = false;
         logger.error("FileNotFoundException: " + fe.getMessage());
      } catch (Exception e) {
         zipSuccessful = false;
         logger.error(e.getMessage(), e);
      }

      return zipSuccessful;
   }

   /**
    * Pack files into a WMOFTP format file and delete the individual files
    *
    * @param srcFilePath the source file path
    * @return true if zipping was successful, false otherwise
    */
   private boolean wmoFtpPackFiles(String srcFilePath) {
      if (!checkSourceFilePath(srcFilePath)) {
         return false;
      }

      if (logger.isInfoEnabled()) {
         logger.info("WMO-FTP packing files from " + srcFilePath);
      }

      boolean packSuccessful = true;

      try {
         
         // Get a list of files from source directory
         File sourceFile = new File(srcFilePath);
         String files[] = sourceFile.list();

         // File packer
         FilePacker filePacker = FilePacker.getDisseminationFilePacker(srcFilePath);

         int fileCnt = 0;

         // Step through file list
         for (String filename : files) {
            if ('A' != filename.charAt(0)) {
               logger.warn("Not a valid FNC file, cannot pack file " + filename);
               packSuccessful = false;
               break;
            } else {
               File f = new File(srcFilePath, filename);
   
               // Regular file
               if (f.isFile()) {
                  fileCnt++;
   
                  if (logger.isDebugEnabled()) {
                     logger.debug("Adding " + filename + " to wmoftp file");
                  }
   
                  filePacker.appendBulletinToPackedFile(f, filename);
   
                  // Delete original file
                  f.delete();
               }
            }
         }

         int includedBulletinCnt = filePacker.getNumberOfIncludedBulletins();

         if (includedBulletinCnt == 0) {
            logger.error("Failed to add any file to wmoftp file: "
                  + filePacker.getPackedFile().getPath());
            packSuccessful = false;
         } else if (includedBulletinCnt != fileCnt) {
            logger.error("Failed to add all files to wmoftp file: "
                  + filePacker.getPackedFile().getPath());
         }

         filePacker.flush();
      } catch (Exception e) {
         packSuccessful = false;
         logger.error(e.getMessage(), e);
      }

      return packSuccessful;
   }

   /**
    * Checks whether a source file path points to an existing, non-empty directory.
    *
    * @param srcFilePath the source file path to check
    * @return true if the source file path points to an existing, non-empty directory, false otherwise
    */
   private boolean checkSourceFilePath(String srcFilePath) {
      boolean srcFilePathOK = true;

      File sourceFile = new File(srcFilePath);

      if (!sourceFile.exists()) {
         logger.error("Source file path not existing: " + srcFilePath);
         srcFilePathOK = false;
      } else if (!sourceFile.isDirectory()) {
         logger.error("Source file path does not name a directory: " + srcFilePath);
         srcFilePathOK = false;
      } else if (sourceFile.listFiles().length == 0) {
         logger.error("Source file path contains no files: " + srcFilePath);
         srcFilePathOK = false;
      }
      return srcFilePathOK;
   }

   /**
    * Start a dissemination via staging post.
    *
    * @param dissJob the DisseminationJob containing the relevant dissemination information
    * @return true if dissemination was started successful, false otherwise
    */
   private boolean disseminateViaSP(DisseminationJob dissJob, boolean startPrimary) {
      if (logger.isInfoEnabled()) {
         logger.info("Now disseminate via Staging Post");
      }

      // For the moment just set the processed request to "ONGOING_DISSEMINATION" and return success
      ProcessedRequest processedRequest = getProcessedRequest(dissJob.getRequestId());
      prepareForDelivery(dissJob.getFileURI(), getZipMode(processedRequest, startPrimary), dissJob, processedRequest);

      String fromMailAddress = ConfigServiceFacade.getInstance().getString(ConfigurationInfo.MAIL_FROM);
      String userMailAddress = processedRequest.getRequest().getEmail();
      if (userMailAddress == null) {
         logger.error("No user e-mail address was provided. Could not send a mail to user for staging post dissemination.");
         return false;
      }

      // prepare message content
      String subject = getStagingPostMessageSubject();
      String content = getStagingPostMessageContent(dissJob.getFileURI());

      // XXX
//      try {
//         InitialContext context = new InitialContext();
//         MailSender mailSender = (MailSender) context.lookup(ConfigServiceFacade.getInstance()
//               .getString(MAIL_SENDER_URL_KEY));
//         logger.info("Sending mail to " + userMailAddress);
//         mailSender.sendMail(fromMailAddress, userMailAddress, subject, content);
//      } catch (NamingException e) {
//         logger.error("Could not get hold of the MailSender EJB.");
//         return false;
//      } catch (Exception e1) {
//         logger.error("Could not send message to user " + userMailAddress + ". " + e1);
//         return false;
//      }

      mergeProcessedRequestStatus(processedRequest, RequestResultStatus.ONGOING_DISSEMINATION);

      return true;
   }

   private String getStagingPostMessageSubject(){
	   ResourceBundle bundle = ResourceBundle.getBundle(STAGING_POST_MAIL_PROPERTIES);
	   String subject = null;
	   try {
	      subject = bundle.getString(STAGING_POST_MAIL_SUBJECT_KEY);
	   }
	   catch (Exception e) {
	      return null;
	   }
	   return subject;
   }

   private String getStagingPostMessageContent(String fileUrl){
	   String contentTemplate = null;
	   try {
		   ResourceBundle bundle = ResourceBundle.getBundle(STAGING_POST_MAIL_PROPERTIES);
		   contentTemplate = bundle.getString(STAGING_POST_MAIL_CONTENT_KEY);
	   }
	   catch (Exception e) {
	      return null;
	   }

	   String content = MessageFormat.format(contentTemplate, stagingPostUrl, fileUrl);

	   return content;
   }

   /**
    * Create a DisseminationInfo object needed for the dissemination via harness.
    *
    * @param dissJob the DisseminationJob containing the relevant dissemination information
    * @param startPrimary a flag saying if the primary dissemination is to be started or the secondary one
    * @return the generated DisseminationInfo object
    */
   private DisseminationInfo getDisseminationInfo(DisseminationJob dissJob, boolean startPrimary) {
      // retrieve the processedRequest
      ProcessedRequest processedRequest = getProcessedRequest(dissJob.getRequestId());

      if (processedRequest == null || processedRequest.getRequest() == null) {
         logger.error("No process request found with request id {}", dissJob.getRequestId());
         return null;
      }

      Request request = processedRequest.getRequest();
      int userSLA = getSLA(request.getClassOfService());
      logger.info("The user SLA is " + userSLA);

      ProductMetadata metadata = request.getProductMetadata();

      DisseminationInfo disseminationInfo = DisseminationUtils.createDisseminationInfo(metadata
            .getPriority().intValue(), userSLA, metadata.getDataPolicy());

      if (startPrimary) {
         if (request.getPrimaryDissemination() instanceof PublicDissemination) {
            if (((PublicDissemination) request.getPrimaryDissemination()).getDiffusion() instanceof MailDiffusion) {
               MailDiffusion mailDiffusion = (MailDiffusion) ((PublicDissemination) request
                     .getPrimaryDissemination()).getDiffusion();

               setMailDiffusion(disseminationInfo, mailDiffusion);
            } else if (((PublicDissemination) request.getPrimaryDissemination()).getDiffusion() instanceof FTPDiffusion) {
               FTPDiffusion ftpDiffusion = (FTPDiffusion) ((PublicDissemination) request
                     .getPrimaryDissemination()).getDiffusion();

               setFtpDiffusion(disseminationInfo, ftpDiffusion);
            }
         } else if (request.getPrimaryDissemination() instanceof RMDCNDissemination) {
            if (((RMDCNDissemination) request.getPrimaryDissemination()).getDiffusion() instanceof MailDiffusion) {
               MailDiffusion mailDiffusion = (MailDiffusion) ((RMDCNDissemination) request
                     .getPrimaryDissemination()).getDiffusion();

               setMailDiffusion(disseminationInfo, mailDiffusion);
            } else if (((RMDCNDissemination) request.getPrimaryDissemination()).getDiffusion() instanceof FTPDiffusion) {
               FTPDiffusion ftpDiffusion = (FTPDiffusion) ((RMDCNDissemination) request
                     .getPrimaryDissemination()).getDiffusion();

               setFtpDiffusion(disseminationInfo, ftpDiffusion);
            }
         }

         // Set alternative diffusion, if necessary
         if (dissJob.getPrimaryDissemination().equals(dissJob.getSecondaryDissemination())) {
            if (request.getSecondaryDissemination() instanceof PublicDissemination) {
               if (((PublicDissemination) request.getSecondaryDissemination()).getDiffusion() instanceof MailDiffusion) {
                  MailDiffusion mailDiffusion = (MailDiffusion) ((PublicDissemination) request
                        .getSecondaryDissemination()).getDiffusion();

                  setAlternativeMailDiffusion(disseminationInfo, mailDiffusion);
               } else if (((PublicDissemination) request.getSecondaryDissemination())
                     .getDiffusion() instanceof FTPDiffusion) {
                  FTPDiffusion ftpDiffusion = (FTPDiffusion) ((PublicDissemination) request
                        .getSecondaryDissemination()).getDiffusion();

                  setAlternativeFtpDiffusion(disseminationInfo, ftpDiffusion);
               }
            } else if (request.getSecondaryDissemination() instanceof RMDCNDissemination) {
               if (((RMDCNDissemination) request.getSecondaryDissemination()).getDiffusion() instanceof MailDiffusion) {
                  MailDiffusion mailDiffusion = (MailDiffusion) ((RMDCNDissemination) request
                        .getSecondaryDissemination()).getDiffusion();

                  setAlternativeMailDiffusion(disseminationInfo, mailDiffusion);
               } else if (((RMDCNDissemination) request.getSecondaryDissemination()).getDiffusion() instanceof FTPDiffusion) {
                  FTPDiffusion ftpDiffusion = (FTPDiffusion) ((RMDCNDissemination) request
                        .getSecondaryDissemination()).getDiffusion();

                  setAlternativeFtpDiffusion(disseminationInfo, ftpDiffusion);
               }
            }
         }
      } else {
         if (request.getSecondaryDissemination() instanceof PublicDissemination) {
            if (((PublicDissemination) request.getSecondaryDissemination()).getDiffusion() instanceof MailDiffusion) {
               MailDiffusion mailDiffusion = (MailDiffusion) ((PublicDissemination) request
                     .getSecondaryDissemination()).getDiffusion();

               setMailDiffusion(disseminationInfo, mailDiffusion);
            } else if (((PublicDissemination) request.getSecondaryDissemination()).getDiffusion() instanceof FTPDiffusion) {
               FTPDiffusion ftpDiffusion = (FTPDiffusion) ((PublicDissemination) request
                     .getSecondaryDissemination()).getDiffusion();

               setFtpDiffusion(disseminationInfo, ftpDiffusion);
            }
         } else if (request.getSecondaryDissemination() instanceof RMDCNDissemination) {
            if (((RMDCNDissemination) request.getSecondaryDissemination()).getDiffusion() instanceof MailDiffusion) {
               MailDiffusion mailDiffusion = (MailDiffusion) ((RMDCNDissemination) request
                     .getSecondaryDissemination()).getDiffusion();

               setMailDiffusion(disseminationInfo, mailDiffusion);
            } else if (((RMDCNDissemination) request.getSecondaryDissemination()).getDiffusion() instanceof FTPDiffusion) {
               FTPDiffusion ftpDiffusion = (FTPDiffusion) ((RMDCNDissemination) request
                     .getSecondaryDissemination()).getDiffusion();

               setFtpDiffusion(disseminationInfo, ftpDiffusion);
            }
         }
      }

      return disseminationInfo;
   }

   /**
    * Get the user SLA value.
    *
    * @return the user SLA value
    */
   private int getSLA(ClassOfService classOfService) {
      // default value : silver
      int sla = 1;

      if (classOfService != null) {
         switch (classOfService) {
         case BRONZE: {
            sla = 0;
            break;
         }
         case SILVER: {
            sla = 1;
            break;
         }
         case GOLD: {
            sla = 2;
            break;
         }
         }
      }

      return sla;
   }

   /**
    * Fill the MailDiffusion in a DisseminationInfo object needed for the dissemination via harness.
    *
    * @param disseminationInfo the DisseminationInfo object
    * @param mailDiffusion the MailDiffusion originated from the ProcessedRequest
    */
   private void setMailDiffusion(DisseminationInfo disseminationInfo, MailDiffusion mailDiffusion) {
      DisseminationUtils.setMailDiffusion(disseminationInfo, mailDiffusion.getFileName(),
            mailDiffusion.getAddress(), mailDiffusion.getSubject(), mailDiffusion.getHeaderLine(),
            mailDiffusion.getMailAttachmentMode().toString(), mailDiffusion.getMailDispatchMode()
                  .toString());
   }

   /**
    * Fill the alternative MailDiffusion in a DisseminationInfo object needed for the dissemination via harness.
    *
    * @param disseminationInfo the DisseminationInfo object
    * @param mailDiffusion the MailDiffusion originated from the ProcessedRequest
    */
   private void setAlternativeMailDiffusion(DisseminationInfo disseminationInfo,
         MailDiffusion mailDiffusion) {
      DisseminationUtils.setAlternativeMailDiffusion(disseminationInfo,
            mailDiffusion.getFileName(), mailDiffusion.getAddress(), mailDiffusion.getSubject(),
            mailDiffusion.getHeaderLine(), mailDiffusion.getMailAttachmentMode().toString(),
            mailDiffusion.getMailDispatchMode().toString());
   }

   /**
    * Fill the FTPDiffusion in a DisseminationInfo object needed for the dissemination via harness.
    *
    * @param disseminationInfo the DisseminationInfo object
    * @param ftpDiffusion the FTPDiffusion originated from the ProcessedRequest
    */
   private void setFtpDiffusion(DisseminationInfo disseminationInfo, FTPDiffusion ftpDiffusion) {
      DisseminationUtils.setFtpDiffusion(disseminationInfo, ftpDiffusion.getFileName(),
            ftpDiffusion.getHost(), ftpDiffusion.getPort(), ftpDiffusion.getUser(),
            ftpDiffusion.getPassword(), ftpDiffusion.getPassive(), ftpDiffusion.getPath(),
            ftpDiffusion.getCheckFileSize(), ftpDiffusion.getEncrypted());
   }

   /**
    * Fill the alternative FTPDiffusion in a DisseminationInfo object needed for the dissemination via harness.
    *
    * @param disseminationInfo the DisseminationInfo object
    * @param ftpDiffusion the FTPDiffusion originated from the ProcessedRequest
    */
   private void setAlternativeFtpDiffusion(DisseminationInfo disseminationInfo,
         FTPDiffusion ftpDiffusion) {
      DisseminationUtils.setAlternativeFtpDiffusion(disseminationInfo, ftpDiffusion.getFileName(),
            ftpDiffusion.getHost(), ftpDiffusion.getPort(), ftpDiffusion.getUser(),
            ftpDiffusion.getPassword(), ftpDiffusion.getPassive(), ftpDiffusion.getPath(),
            ftpDiffusion.getCheckFileSize(), ftpDiffusion.getEncrypted());
   }


}
