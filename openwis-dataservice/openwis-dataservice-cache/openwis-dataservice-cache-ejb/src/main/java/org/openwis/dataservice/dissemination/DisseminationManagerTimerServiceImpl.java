package org.openwis.dataservice.dissemination;

import java.util.Iterator;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.ManagedServiceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides an implementation for the {@code DisseminationManagerTimerService} service.
 *
 * @author <a href="mailto:christoph.bortlisz@vcs.de">Christoph Bortlisz</a>
 */
@Stateless(name = "DisseminationManagerTimerService")
@TransactionTimeout(18000)
public class DisseminationManagerTimerServiceImpl implements DisseminationManagerTimerService, ConfigurationInfo {

	// -------------------------------------------------------------------------
	// Instance Variables
	// -------------------------------------------------------------------------

	// Logging tool
	private final Logger LOG = LoggerFactory.getLogger(DisseminationManagerTimerServiceImpl.class);

	// Entity manager.
	@PersistenceContext
	protected EntityManager entityManager;

	// Timer service
	@Resource
	private TimerService timerService;
	
	// Timer parameter
	private long disseminationPeriod;
	private long disseminationInitialDelay;
	
	@EJB
	private DisseminationDelegate disseminationDelegate;
	
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
    * Default constructor.
    * Builds a DisseminationManagerTimerServiceImpl.
    */
	public DisseminationManagerTimerServiceImpl()
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
	public void start(){
		stop();
		//configure();
		
		timerService.createTimer(disseminationInitialDelay,disseminationPeriod,"DisseminationManagerTimerService");		
		if (LOG.isInfoEnabled()) {
            LOG.info("Timer was successfully started, with " + disseminationPeriod + " ms delay!");
        }		
	}
	
   @SuppressWarnings("unchecked")
   public void stop() {
      Iterator<Timer> it = timerService.getTimers().iterator();
		while (it.hasNext()){
			Timer timer = it.next();
			String name = (String) timer.getInfo();
			if (name.equals("DisseminationManagerTimerService")){
				timer.cancel();
				LOG.warn("Canceled Timer " + timer.toString());
			}
		}

	}
	
	@Timeout
	public void timeout(final Timer timer)
	{
		if (!isDisseminationEnabled()) return;
		
        try 
        {  
            disseminationDelegate.processJobs();
        }
        catch (Throwable t)
        {
        	LOG.error("Error while processing jobs", t);
        }	
	}

	// -------------------------------------------------------------------------
	// Service functions
	// -------------------------------------------------------------------------
	private boolean isDisseminationEnabled(){
		return getControlService().isServiceEnabled(ManagedServiceIdentifier.DISSEMINATION_SERVICE);
	}

	// -------------------------------------------------------------------------
	// Configuration
	// -------------------------------------------------------------------------	
   /**
    * Fill members out of the configuration file
    */
   private void configure() {
	         
	   disseminationInitialDelay = ConfigServiceFacade.getInstance().getLong(DISSEMINATION_TIMER_INITIAL_DELAY_KEY);      
	   disseminationPeriod = ConfigServiceFacade.getInstance().getLong(DISSEMINATION_TIMER_PERIOD_KEY);
   }
}