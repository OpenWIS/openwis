package org.openwis.dataservice.webapp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.cache.CacheManager;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.dataservice.dissemination.DisseminationStatusMonitor;
import org.openwis.dataservice.webapp.wrapper.DisseminationServiceWrapper;
import org.openwis.dataservice.webapp.wrapper.FeedingWrapper;
import org.openwis.dataservice.webapp.wrapper.GlobalDataCollectionWrapper;
import org.openwis.management.control.ControlService;
import org.openwis.management.control.ManagedServiceIdentifier;
import org.openwis.management.control.ManagedServiceStatus;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceServlet extends HttpServlet {

	private final Logger LOG = LoggerFactory.getLogger(ServiceServlet.class);

	private GlobalDataCollectionWrapper globalDataCollection;
	private FeedingWrapper feeding;

	private CacheManager cacheManager;

	private DisseminationServiceWrapper disseminationService;

	// ----------------------------------

	/**
	 * Starts Services like the Collection and the CleanUp/Housekeeping (located in the CacheManager)
	 */
	@Override
	public void init() throws ServletException {
		InitialContext context;
		try {
			context = new InitialContext();
			cacheManager = (CacheManager) context.lookup(JndiUtils.getString(ConfigurationInfo.CACHE_MANAGER_URL_KEY));
			cacheManager.stop();

			// this resets the flag if the server crashed while service was running
			if (cacheManager.isServiceAlreadyRunning(cacheManager.cleanupInUseKey)) cacheManager.setServiceRunning(cacheManager.cleanupInUseKey, false);
			if (cacheManager.isServiceAlreadyRunning(cacheManager.housekeepingInUseKey)) cacheManager.setServiceRunning(cacheManager.housekeepingInUseKey, false);
			if (cacheManager.isServiceAlreadyRunning(cacheManager.alertCleanerInUseKey)) cacheManager.setServiceRunning(cacheManager.alertCleanerInUseKey, false);

			cacheManager.start();
		}
		catch (NamingException e) {
         LOG.error(e.getMessage(), e);
		}

		// re-enable degraded services
		//resetStatusOfAllServices();

		globalDataCollection = new GlobalDataCollectionWrapper();
		globalDataCollection.start();

		feeding = new FeedingWrapper();
		feeding.start();

		try {
			context = new InitialContext();
			// XXX: Unify
			// DisseminationStatusMonitor disseminationStatusMonitor = (DisseminationStatusMonitor) context.lookup("openwis-dataservice/DisseminationStatusMonitor/local");
			DisseminationStatusMonitor disseminationStatusMonitor = (DisseminationStatusMonitor) context.lookup("ejb:openwis-dataservice/openwis-dataservice-cache-ejb/DisseminationStatusMonitor!org.openwis.dataservice.dissemination.DisseminationStatusMonitor");
			disseminationStatusMonitor.stop();
			if (disseminationStatusMonitor.isPurgeStagingPostAlreadyRunning()) disseminationStatusMonitor.setPurgeStagingPostRunning(false);
		} catch (NamingException e) {
         LOG.error(e.getMessage(), e);
		}

		disseminationService = new DisseminationServiceWrapper();
		disseminationService.start();
	}

	private void resetStatusOfAllServices() {
		ControlService controlService = ManagementServiceProvider.getControlService();
		if (controlService == null){
			LOG.error("Could not reset status of data service. Control service not found.");
			return;
		} else {
			String DEGRADED = ManagedServiceStatus.DEGRADED.name();
			String UNKNOWN = ManagedServiceStatus.UNKNOWN.name();

			for (ManagedServiceIdentifier service : ManagedServiceIdentifier.values()){
				String serviceStatusString = controlService.getServiceStatus(service);
				if (serviceStatusString.equals(DEGRADED) || serviceStatusString.equals(UNKNOWN)){
					LOG.info(service.name() + " will now be enabled.");
					controlService.setServiceStatus(service, ManagedServiceStatus.ENABLED);
				}
			}
		}
	}

	@Override
	public void destroy() {
		globalDataCollection.stop();
		feeding.stop();
		cacheManager.stop();
		disseminationService.stop();
	}
}