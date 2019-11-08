package org.openwis.dataservice.gts.feeding;

import java.io.File;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

import org.jboss.ejb3.annotation.Depends;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.util.FilePacker;
import org.openwis.dataservice.util.Priority;
import org.openwis.datasource.server.jaxb.serializer.incomingds.FeedingMessage;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.ManagedServiceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageDriven(messageListenerInterface=MessageListener.class, name = "PackedFeedingService", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/PackedFeedingQueue"),
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")})
@Stateless(name = "PackedFeedingTimerService")
@Depends({"jboss.ha:service=HASingletonDeployer,type=Barrier"})
@TransactionTimeout(18000)
public class PackedFeedingTimerServiceImpl implements ConfigurationInfo, PackedFeedingTimerService {
	
	// Logging
	private final Logger LOG = LoggerFactory.getLogger(PackedFeedingTimerServiceImpl.class);
	
	// Timer Service
	@Resource
	private TimerService timerService;
	
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
	// --------------------------------
	
	@Override
	public void onMessage(Message message){
		if (!isFeedingEnabled()){
         // Right now the message is consumed without treatment. 
         // Insert here code if the message should be stored somewhere : Feeding retro-activity
			return;
		}
		
		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
    		FeedingMessage feedingMessage = (FeedingMessage) objectMessage.getObject();
    		addFileToPackage(feedingMessage);
        } 
        catch (JMSException e) {
           LOG.error("Unable to read the message from the queue", e);
        }
        catch (Throwable t) {
           LOG.error("Unexpected error !!", t);
        }
	}
		
	private boolean isFeedingEnabled(){
		return getControlService().isServiceEnabled(ManagedServiceIdentifier.FEEDING_SERVICE);
	}

	@Timeout
	public void timeout(Timer timer) {
		if (!isFeedingEnabled()) return;
		
		FilePacker filePacker = getFilePacker();		

		if (filePacker.getNumberOfIncludedBulletins() > 0){
			String packageFilename = filePacker.getPackedFile().getName();
			filePacker.flush();			
			LOG.info("+++ Finishing feeding package " + packageFilename + " due to timeout.");
		}
	}

	private void addFileToPackage(FeedingMessage feedingMessage){
		FilePacker filePacker = getFilePacker();
		
		File sourceBulletin = getCachedSourceFile(feedingMessage.getFullSourcePath());
		String originalFilename = feedingMessage.getTargetFilename();
		
		filePacker.appendBulletinToPackedFile(sourceBulletin, originalFilename);
		
		if (isOfHighPriority(feedingMessage.getPriority())){
			String packageFilename = filePacker.getPackedFile().getName();
			filePacker.flush();
			LOG.info("+++ Finishing feeding package " + packageFilename + " due to arrival of high priority product " + originalFilename);
		}
	}
	
	private boolean isOfHighPriority(Integer priorityInteger){
		boolean isOfHighPriority = false;
		int priority = priorityInteger.intValue();
		if (Priority.HIGH == Priority.getPriorityFromValue(priority)) isOfHighPriority = true;
		return isOfHighPriority;
	}
	
	private FilePacker getFilePacker(){
		return FilePacker.getFeedingFilePacker(getOutgoingDirectory());
	}

	private File getCachedSourceFile(String absoluteSourcePath){
		File sourceFile = new File(absoluteSourcePath);
		return sourceFile;
	}
	
	private String getOutgoingDirectory(){
		return ConfigServiceFacade.getInstance().getString(HARNESS_OUTGOING_DIRECTORY_KEY);
	}

	private int getFeedingTimerInitialDelay(){
		return ConfigServiceFacade.getInstance().getInt(FEEDING_TIMER_INITIAL_DELAY_KEY);
	}

	private int getFeedingTimerPeriod(){
		return ConfigServiceFacade.getInstance().getInt(FEEDING_TIMER_PERIOD_KEY);
	}

	// --------------------------------
	
	public void start() {
		int feedingTimerInitialDelay = getFeedingTimerInitialDelay();
		int feedingTimerPeriod = getFeedingTimerPeriod();
		timerService.createTimer(feedingTimerInitialDelay,feedingTimerPeriod,"PackedFeedingTimer");
		LOG.info("PackedFeedingTimer was successfully started, with " + feedingTimerPeriod + " ms delay!");
	}

	public void stop() {
		@SuppressWarnings("unchecked")
      Iterator<Timer> it = timerService.getTimers().iterator();
		while (it.hasNext()){
			Timer timer = it.next();
			if (timer != null && "PackedFeedingTimer".equals(timer.getInfo())) timer.cancel();
		}
	}
	
}