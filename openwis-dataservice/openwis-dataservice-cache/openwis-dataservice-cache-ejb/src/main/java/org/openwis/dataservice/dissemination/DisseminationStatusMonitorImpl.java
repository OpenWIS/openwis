package org.openwis.dataservice.dissemination;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.commons.io.FileUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.domain.entity.cache.CacheConfiguration;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.adhoc.AdHoc;
import org.openwis.dataservice.common.domain.entity.request.dissemination.DisseminationJob;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmBuilder;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmCategory;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmRequestType;
import org.openwis.dataservice.common.service.ProcessedRequestService;
import org.openwis.dataservice.common.service.UserAlarmManagerLocal;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.dataservice.util.GlobalDataCollectionUtils;
import org.openwis.harness.dissemination.Dissemination;
import org.openwis.harness.dissemination.DisseminationImplService;
import org.openwis.harness.dissemination.DisseminationStatus;
import org.openwis.harness.dissemination.RequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides an implementation for the {@code DisseminationStatusMonitor} service.
 *
 * @author <a href="mailto:christoph.bortlisz@vcs.de">Christoph Bortlisz</a>
 */
@Stateless(name = "DisseminationStatusMonitor")
@TransactionTimeout(18000)
public class DisseminationStatusMonitorImpl implements DisseminationStatusMonitor, ConfigurationInfo {

	// -------------------------------------------------------------------------
	// Instance Variables
	// -------------------------------------------------------------------------

	// Logging tool
	private final Logger LOG = LoggerFactory.getLogger(DisseminationStatusMonitorImpl.class);

	// Entity manager.
	@PersistenceContext
	protected EntityManager entityManager;

	// The processed request service.
	@EJB(name = "ProcessedRequestService")
	private ProcessedRequestService processedRequestService;

    @EJB
    private UserAlarmManagerLocal userAlarmManager;

	// Timer service
	@Resource
	private TimerService timerService;

	// Timer parameter
	private long disseminationPeriod;
	private long disseminationInitialDelay;

	// Dissemination harness URLs
	private String disseminationHarnessPublicURL;
	private String disseminationHarnessRMDCNURL;

	// Dissemination methods
	private static String PUBLIC_DISS_METHOD = "PUBLIC";
	private static String PRIVATE_DISS_METHOD = "PRIVATE";

	// Dissemination states
	//private static String NONE_DISS_STATE = "NONE";
	private static String ONGOING_DISS_STATE = "ONGOING_DISSEMINATION";
	private static String SUCCESS_DISS_STATE = "DISSEMINATED";
	private static String FAILURE_DISS_STATE = "FAILED";

	// Monitoring retries counter
	private int publicRetriesCounter;
	private int privateRetriesCounter;

	private int maxNumOfRetries;

	// Staging Post items
	private static String stagingPostDirectory;
	private long stagingPostPurgingTime;

	private final String purgeStagingPostInUseKey = "purgeStagingPostInUse";

   /**
    * Default constructor.
    * Builds a DisseminationStatusMonitorImpl.
    */
	public DisseminationStatusMonitorImpl()
	{
	   try
	   {
		   configure();
	   }
       catch (Throwable t)
       {
         LOG.error(t.getMessage(), t);
       }
	}

	// -------------------------------------------------------------------------
	// Timer implementation
	// -------------------------------------------------------------------------
	@Override
	public void start(){
		stop();
		configure();
		timerService.createTimer(disseminationInitialDelay,disseminationPeriod,"DisseminationStatusMonitor");
		if (LOG.isInfoEnabled()) {
            LOG.info("Timer was successfully started, with " + disseminationPeriod + " ms delay!");
        }
	}

	@SuppressWarnings("unchecked")
   @Override
	public void stop(){
		Iterator<Timer> it = timerService.getTimers().iterator();
		while (it.hasNext()){
			Timer timer = it.next();
			String name = (String) timer.getInfo();
			if (name.equals("DisseminationStatusMonitor")){
				timer.cancel();
				LOG.warn("Canceled Timer " + timer.toString());
			}
		}
	}

	@Timeout
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void timeout(final Timer timer)
	{
        try
        {
            processJobs();

            purgeStagingPost();
        }
        catch (Throwable t)
        {
         LOG.error(t.getMessage(), t);
        }
	}

	// -------------------------------------------------------------------------
	// Service functions
	// -------------------------------------------------------------------------
	/**
	 * Process the jobs from the DisseminationJob database table.
	 *
	 * For those jobs that are "on-going" and that have a "harness" dissemination (either public or private)
	 * it will ask the corresponding harness on the status of these requests. It then updates the job entries
	 * in the database accordingly.
	 * For those jobs that have their final state set, it will publish this state and delete the job entry
	 * in the database.
     *
	 */
	@SuppressWarnings("unchecked")
   private void processJobs()
	{
		// Check if harness URLs are available
		if (disseminationHarnessPublicURL == null || disseminationHarnessPublicURL.length() == 0 ||
			disseminationHarnessRMDCNURL == null || disseminationHarnessRMDCNURL.length() == 0)
		{
			LOG.debug("No dissemination harness URLs defined!");
			return;
		}

		// Define lists and maps
		List<String> publicRequestIds = new ArrayList<String>();
		List<String> privateRequestIds = new ArrayList<String>();

		List<DisseminationJob> primaryDissJobs = new ArrayList<DisseminationJob>();
		List<DisseminationJob> secondaryDissJobs = new ArrayList<DisseminationJob>();

		Map<String, DisseminationJob> primaryMap = new HashMap<String, DisseminationJob>();
		Map<String, DisseminationJob> secondaryMap = new HashMap<String, DisseminationJob>();

		// Query for those jobs that have an ongoing dissemination to the primary target
		Query jobQuery = entityManager.createQuery("SELECT dj FROM DisseminationJob dj WHERE dj.primaryState = '" + ONGOING_DISS_STATE + "'");
		primaryDissJobs = jobQuery.getResultList();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Number of jobs that are onging to the primary dissemination: {}", primaryDissJobs.size());
		}

		// Check if dissemination target is "harness" (either public or private)
		for (DisseminationJob dissJob : primaryDissJobs)
		{
			String requestId = Long.toString(dissJob.getRequestId());

         // Check ProcessedRequest still exists (may have been discarded)
			if (!ensureProcessedRequestExists(dissJob)) {
			   continue;
			}

			if (dissJob.getPrimaryDissemination().equals(PUBLIC_DISS_METHOD))
			{
				publicRequestIds.add(requestId);
				primaryMap.put(requestId, dissJob);
			}
			else if (dissJob.getPrimaryDissemination().equals(PRIVATE_DISS_METHOD))
			{
				privateRequestIds.add(requestId);
				primaryMap.put(requestId, dissJob);
			}
		}

		// Query for those jobs that have an ongoing dissemination to the secondary target
		jobQuery = entityManager.createQuery("SELECT dj FROM DisseminationJob dj WHERE dj.secondaryState = '" + ONGOING_DISS_STATE + "'");
		secondaryDissJobs = jobQuery.getResultList();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Number of jobs that are onging to the secondary dissemination: {}", secondaryDissJobs.size());
		}

		// Check if dissemination target is "harness" (either public or private)
		for (DisseminationJob dissJob : secondaryDissJobs)
		{
			String requestId = Long.toString(dissJob.getRequestId());

			// Check ProcessedRequest still exists (may have been discarded)
         if (!ensureProcessedRequestExists(dissJob)) {
            continue;
         }

			if (dissJob.getSecondaryDissemination().equals(PUBLIC_DISS_METHOD))
			{
				publicRequestIds.add(requestId);
				secondaryMap.put(requestId, dissJob);
			}
			else if (dissJob.getSecondaryDissemination().equals(PRIVATE_DISS_METHOD))
			{
				privateRequestIds.add(requestId);
				secondaryMap.put(requestId, dissJob);
			}
		}

		// Ask public harness on status of the requested dissemination ids
		if (publicRequestIds.size() != 0)
		{
			List<DisseminationStatus> publicStatusList = monitorHarness(disseminationHarnessPublicURL, publicRequestIds);

			if (publicStatusList != null && publicStatusList.size() > 0)
			{
				for (DisseminationStatus status : publicStatusList)
				{
					String requestId = status.getRequestId();

					if (status.getRequestStatus() == RequestStatus.FAILED)
					{
						if (primaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = primaryMap.get(requestId);

							// XXX - lmika: Raise user alarm message
							raiseUserAlarm(dJob, status);

							mergeDissJobPrimaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Primary public dissemination job failed: request id: " + dJob.getRequestId());
						}
						else if (secondaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = secondaryMap.get(requestId);

							// XXX - lmika: Raise user alarm message
							raiseUserAlarm(dJob, status);

							mergeDissJobSecondaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Secondary public dissemination job failed: request id: " + dJob.getRequestId());
						}
					}
					else if (status.getRequestStatus() == RequestStatus.DISSEMINATED)
					{
						// TODO - lmika: Maybe raise successfull delivery message here?

						if (primaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = primaryMap.get(requestId);
							mergeDissJobPrimaryState(dJob, SUCCESS_DISS_STATE);
							if (LOG.isInfoEnabled()) {
								LOG.info("Primary public dissemination job succeeded: request id: " + dJob.getRequestId());
							}
						}
						else if (secondaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = secondaryMap.get(requestId);
							mergeDissJobSecondaryState(dJob, SUCCESS_DISS_STATE);
							if (LOG.isInfoEnabled()) {
								LOG.info("Secondary public dissemination job succeeded: request id: " + dJob.getRequestId());
							}
						}
					}
				}

				publicRetriesCounter = 0;
			}
			else
			{
				publicRetriesCounter++;

				if (publicRetriesCounter > maxNumOfRetries)
				{
					publicRetriesCounter = 0;

					LOG.error("Public dissemination harness not reachable: " + disseminationHarnessPublicURL);

					for (String requestId : publicRequestIds)
					{
						if (primaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = primaryMap.get(requestId);
							mergeDissJobPrimaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Primary public dissemination job failed: request id: " + dJob.getRequestId());
						}
						else if (secondaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = secondaryMap.get(requestId);
							mergeDissJobSecondaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Secondary public dissemination job failed: request id: " + dJob.getRequestId());
						}
					}
				}
			}
		}

		// Ask private harness on status of the requested dissemination ids
		if (privateRequestIds.size() != 0)
		{
			List<DisseminationStatus> privateStatusList = monitorHarness(disseminationHarnessRMDCNURL, privateRequestIds);

			if (privateStatusList != null && privateStatusList.size() > 0)
			{
				for (DisseminationStatus status : privateStatusList)
				{
					String requestId = status.getRequestId();

					if (status.getRequestStatus() == RequestStatus.FAILED)
					{
						if (primaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = primaryMap.get(requestId);

							// XXX - lmika: Raise user alarm message
							raiseUserAlarm(dJob, status);

							mergeDissJobPrimaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Primary private dissemination job failed: request id: " + dJob.getRequestId());
						}
						else if (secondaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = secondaryMap.get(requestId);

							// XXX - lmika: Raise user alarm message
							raiseUserAlarm(dJob, status);

							mergeDissJobSecondaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Secondary private dissemination job failed: request id: " + dJob.getRequestId());
						}
					}
					else if (status.getRequestStatus() == RequestStatus.DISSEMINATED)
					{
						// XXX - Here the message should be recorded.

						if (primaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = primaryMap.get(requestId);
							mergeDissJobPrimaryState(dJob, SUCCESS_DISS_STATE);
							if (LOG.isInfoEnabled()) {
								LOG.info("Primary private dissemination job succeeded: request id: " + dJob.getRequestId());
							}
						}
						else if (secondaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = secondaryMap.get(requestId);
							mergeDissJobSecondaryState(dJob, SUCCESS_DISS_STATE);
							if (LOG.isInfoEnabled()) {
								LOG.info("Secondary private dissemination job succeeded: request id: " + dJob.getRequestId());
							}
						}
					}
				}

				privateRetriesCounter = 0;
			}
			else
			{
				privateRetriesCounter++;

				if (privateRetriesCounter > maxNumOfRetries)
				{
					privateRetriesCounter = 0;

					LOG.error("Private dissemination harness not reachable: " + disseminationHarnessRMDCNURL);

					for (String requestId : privateRequestIds)
					{
						if (primaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = primaryMap.get(requestId);
							mergeDissJobPrimaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Primary private dissemination job failed: request id: " + dJob.getRequestId());
						}
						else if (secondaryMap.containsKey(requestId))
						{
							DisseminationJob dJob = secondaryMap.get(requestId);
							mergeDissJobSecondaryState(dJob, FAILURE_DISS_STATE);
							LOG.error("Secondary private dissemination job failed: request id: " + dJob.getRequestId());
						}
					}
				}
			}
		}

		// Look for finished dissemination jobs and delete them from the database
		deleteDisseminationJobs();
	}

	/**
	 * Raises a user alarm identifying an error.
	 *
	 * XXX - lmika
	 *
	 * @param requestId
	 * @param dJob
	 * @param status
	 */
	private void raiseUserAlarm(DisseminationJob dJob, DisseminationStatus status) {
		ProcessedRequest processedRequest = processedRequestService.getProcessedRequest(dJob.getRequestId());

		UserAlarmRequestType reqType = null;
		long processedRequestId = 0;
		long requestId = 0;

		if (processedRequest.getRequest() instanceof AdHoc) {
			reqType = UserAlarmRequestType.REQUEST;
		} else if (processedRequest.getRequest() instanceof Subscription) {
			reqType = UserAlarmRequestType.SUBSCRIPTION;
		}

		processedRequestId = processedRequest.getId();
		requestId = processedRequest.getRequest().getId();

        String user = processedRequest.getRequest().getUser();
        UserAlarm alarm = new UserAlarmBuilder(user)
						.request(reqType, processedRequestId, requestId)
						.message(status.getMessage())
						.getUserAlarm();

        userAlarmManager.raiseUserAlarm(alarm);
	}

	/**
	 * Ensure the processed requests associated to the dissemination job still exists (may have been discarded).
	 * Otherwise, the DisseminationJob is set in failure mode and will then be removed.
	 *
	 * @param dissJob the {@link DisseminationJob}
	 * @return <code>true</code> if the processed still exists, else <code>false</code>
	 */
	private boolean ensureProcessedRequestExists(DisseminationJob dissJob) {
	   // Check ProcessedRequest still exists (may have been discarded)
      if (processedRequestService.getProcessedRequest(dissJob.getRequestId()) == null) {
         // delete the dissemination job in this case
         entityManager.remove(dissJob);
         LOG.error("Dissemination job removed because ProcessedRequest does not exist anymore: " + dissJob.getRequestId());
         return false;
      }
      return true;
	}

	/**
	 * Delete those jobs from the DisseminationJob database table that are finished.
	 */
	@SuppressWarnings("unchecked")
   private void deleteDisseminationJobs()
	{
		List<DisseminationJob> finalDissJobs = new ArrayList<DisseminationJob>();

		Query jobQuery = entityManager.createQuery("SELECT dj FROM DisseminationJob dj WHERE dj.finalState = '" + SUCCESS_DISS_STATE + "' OR dj.finalState = '" + FAILURE_DISS_STATE + "'");

		finalDissJobs = jobQuery.getResultList();

		for (DisseminationJob dissJob : finalDissJobs)
		{
			if (LOG.isInfoEnabled()) {
				LOG.info("Deleting finished dissemination job: {}", dissJob.getId());
			}

			entityManager.remove(dissJob);
		}

	}

   /**
    * Purge staging post and associated processed requests that have 
    * are older than a configurable amount of time will be deleted.
    */
   @SuppressWarnings("unchecked")
   private void purgeStagingPost()
	{
		boolean isRunning = isPurgeStagingPostAlreadyRunning();
		if (isRunning) return;

		try{
			setPurgeStagingPostRunning(true);

			// Query for those entries that have reached the configured purging time
			long purgeTime = stagingPostPurgingTime * 60 * 1000;
			int maxPurgedRequestsByCycle = 1000;

			Date compareDate = new Date(System.currentTimeMillis() - purgeTime);
			
			// Purge remaining processed requests (potentially processed request in error)
			Query prQuery = entityManager.createQuery("SELECT pr FROM ProcessedRequest pr WHERE pr.creationDate < '" + compareDate + "'");
         prQuery.setMaxResults(maxPurgedRequestsByCycle);
			List<ProcessedRequest> prEntries = prQuery.getResultList();
         for (ProcessedRequest pr : prEntries)
         {
            if (LOG.isInfoEnabled()) {
               LOG.info("Deleting processed request: {}", pr.getId());
            }
            String fileUri = pr.getUri();
            
            if (fileUri != null) {
               try {
                  LOG.info("Deleting staging post file: {}", pr.getUri());

                  File folder = new File(stagingPostDirectory, fileUri);

                  String parent = folder.getParent();

                  // Remove file from staging post
                  FileUtils.deleteDirectory(folder);

                  // ... and recursivly remove all empty parent folders up to the staging post folder
                  GlobalDataCollectionUtils.recursivelyDeleteEmptyParentDirectoriesUpToRoot(parent,
                        stagingPostDirectory);
               } catch (IOException ioe) {
                  LOG.error("Cannot delete staging post file : " + fileUri, ioe);
               }
            }

            processedRequestService.deleteProcessedRequestWithAdHoc(pr.getId());
         }
		}
		finally {
			setPurgeStagingPostRunning(false);
		}
	}

	@Override
	public boolean isPurgeStagingPostAlreadyRunning() {
		// check if purgeStagingPostInUse key is already in the database, create it otherwise
		Long purgeStagingPostInUse = null;
		try{
			Query query = entityManager.createQuery("SELECT cc.value FROM CacheConfiguration cc WHERE cc.key = '" + purgeStagingPostInUseKey + "'");
			purgeStagingPostInUse = (Long) query.getSingleResult();
		}
		catch(Exception e){
		}
		finally{
			if (purgeStagingPostInUse == null){
				LOG.warn("Could not find " + purgeStagingPostInUseKey + " key in the OPENWIS_CACHE_CONFIGURATION table. Creating it.");
				CacheConfiguration cc = new CacheConfiguration();
				cc.setKey(purgeStagingPostInUseKey);
				purgeStagingPostInUse = Long.valueOf(0);
				cc.setValue(purgeStagingPostInUse);
				entityManager.persist(cc);
				entityManager.flush();
			}
		}
		return (Long.valueOf(1).equals(purgeStagingPostInUse));
	}

	@Override
	public void setPurgeStagingPostRunning(boolean value) {
		Query query = entityManager.createQuery("SELECT cc FROM CacheConfiguration cc WHERE cc.key = '" + purgeStagingPostInUseKey + "'");
		try{
			CacheConfiguration cacheConfiguration = (CacheConfiguration) query.getSingleResult();
			cacheConfiguration.setValue((value ? Long.valueOf(1) : Long.valueOf(0)));
			entityManager.merge(cacheConfiguration);
			entityManager.flush();
		}
		catch (Exception e){
		}
	}

	/**
	 * Merge a DisseminationJob back to the database, setting the primary state.
	 *
	 * @param dissJob the DisseminationJob to be merged
	 * @param primaryState the primaryState to be set
	 */
	private void mergeDissJobPrimaryState(DisseminationJob dissJob, String primaryState)
	{
		dissJob.setPrimaryState(primaryState);

		// Write back to database
		entityManager.merge(dissJob);
	}
	/**
	 * Merge a DisseminationJob back to the database, setting the secondary state.
	 *
	 * @param dissJob the DisseminationJob to be merged
	 */
	private void mergeDissJobSecondaryState(DisseminationJob dissJob, String secondaryState)
	{
		dissJob.setSecondaryState(secondaryState);

		// Write back to database
		entityManager.merge(dissJob);
	}

	/**
	 * Get monitoring information from a dissemination harness via a wsdl call.
	 *      *
	 * @param requestIds a list of request ids for which the status shall be checked
	 * @return a list of DisseminationStatus objects if the call to the harness was successful, null otherwise
	 */
	private List<DisseminationStatus> monitorHarness(String actualURL, List<String> requestIds)
	{
		List<DisseminationStatus> statusList = null;

		try
		{
			URL url = new URL(actualURL);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Connecting to " + actualURL);
			}

			DisseminationImplService disseminationImplService = new DisseminationImplService(url);
			Dissemination disseminationHarness = disseminationImplService.getDisseminationImplPort();

			if (actualURL.endsWith("?wsdl")) {
				// Set endpoint
				((BindingProvider) disseminationHarness).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
						actualURL.replace("?wsdl", ""));
			}

			if (disseminationHarness != null)
			{
				statusList = disseminationHarness.monitorDissemination(requestIds);
			}
			else
			{
				LOG.error("DisseminationHarness not initialized: " + actualURL);
			}
		}
		catch(MalformedURLException e)
		{
			LOG.error("Can not initialize the wsdl: " + actualURL);
		}
		catch(WebServiceException wse)
		{
			LOG.error("WebServiceException: "  + wse.getMessage() + " URL: " + actualURL);
		}
		catch (Throwable t)
		{
         LOG.error(t.getMessage(), t);
		}

		return statusList;
	}

	/**
	 * Fill members out of the configuration file
	 */
   private void configure() {

	   // TODO: eventually use different properties for the timer related values (these are also used by the DisseminationManagerTimerService)
	   disseminationInitialDelay = ConfigServiceFacade.getInstance().getLong(DISSEMINATION_TIMER_INITIAL_DELAY_KEY);
	   disseminationPeriod = ConfigServiceFacade.getInstance().getLong(DISSEMINATION_TIMER_PERIOD_KEY);

	   disseminationHarnessPublicURL = ConfigServiceFacade.getInstance().getString(DISSEMINATION_HARNESS_PUBLIC_URL_KEY);
	   disseminationHarnessRMDCNURL = ConfigServiceFacade.getInstance().getString(DISSEMINATION_HARNESS_RMDCN_URL_KEY);

	   stagingPostDirectory = ConfigServiceFacade.getInstance().getString(STAGING_POST_DIRECTORY_KEY);
	   stagingPostPurgingTime = ConfigServiceFacade.getInstance().getLong(STAGING_POST_PURGE_TIME);

	   publicRetriesCounter = 0;
	   privateRetriesCounter = 0;
	   maxNumOfRetries = 5;
   }
}