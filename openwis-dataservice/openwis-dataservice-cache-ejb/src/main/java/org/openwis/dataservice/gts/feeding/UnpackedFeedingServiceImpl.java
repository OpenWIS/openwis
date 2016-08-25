package org.openwis.dataservice.gts.feeding;

import java.io.File;
import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;

import org.apache.tools.ant.util.FileUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.datasource.server.jaxb.serializer.incomingds.FeedingMessage;
import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.ManagedServiceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageDriven(messageListenerInterface=MessageListener.class, name = "UnpackedFeedingService", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/UnpackedFeedingQueue"),
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")})
@TransactionTimeout(18000)    
public class UnpackedFeedingServiceImpl implements ConfigurationInfo,UnpackedFeedingService {

	// Logging
	private final Logger LOG = LoggerFactory.getLogger(UnpackedFeedingServiceImpl.class);
	
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
	// -----------------------------
	
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
        	 copyFileToOutgoingDirectory(feedingMessage);
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
	
	private File getCachedSourceFile(String absoluteSourcePath){
		File sourceFile = new File(absoluteSourcePath);
		return sourceFile;
	}
	
	private File getOutgoingFile(String outgoingFilename){
		String outgoingDirectory = getOutgoingDirectory();
		File outgoingFile = new File(outgoingDirectory,outgoingFilename);
		return outgoingFile;
	}
	
	private void copyFileToOutgoingDirectory(FeedingMessage feedingMessage){
		File sourceFile = getCachedSourceFile(feedingMessage.getFullSourcePath());
		File outgoingFile = getOutgoingFile(feedingMessage.getTargetFilename());
		try {
			FileUtils.getFileUtils().copyFile(sourceFile, outgoingFile);
			LOG.debug("+++ Feeding file " + outgoingFile.getName() + " to harness");
		} 
		catch (IOException e) {
			LOG.error("--- Could not move file " + sourceFile.getAbsolutePath() + " to " + outgoingFile.getAbsolutePath());
		}
	}
	
	private String getOutgoingDirectory(){
		return ConfigServiceFacade.getInstance().getString(HARNESS_OUTGOING_DIRECTORY_KEY);
   }
}