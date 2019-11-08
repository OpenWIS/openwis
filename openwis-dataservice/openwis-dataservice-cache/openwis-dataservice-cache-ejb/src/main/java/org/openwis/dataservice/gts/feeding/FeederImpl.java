package org.openwis.dataservice.gts.feeding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.util.FileInfo;
import org.openwis.datasource.server.jaxb.serializer.incomingds.FeedingMessage;
import org.openwis.management.service.AlertService;
import org.openwis.management.service.ControlService;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(name = "Feeder")
@TransactionTimeout(18000)
public class FeederImpl implements Feeder, ConfigurationInfo {		
	
	// Logging
	private final Logger LOG = LoggerFactory.getLogger(FeederImpl.class);
	
   /**
    * injection ConnectionFactory
    */
   @Resource(mappedName = "java:/JmsXA")
   private ConnectionFactory cf;

	//------------------------------------------------------------------------------------						
	
	public boolean add(FileInfo fileInfo) {
		if (!isValidForFeeding(fileInfo)) return false;

		String queueName = null;
		if (shouldFileBePacked(fileInfo.getProductFilename())){
			queueName = PACKED_FEEDING_QUEUE_NAME;
		} else {
			queueName = UNPACKED_FEEDING_QUEUE_NAME;
		}
		sendFileToQueue(fileInfo, queueName);
		return true;
	}		
	
	private boolean isValidForFeeding(FileInfo fileInfo){
		boolean isValidForFeeding = false;
		List<String> metadataURNList = fileInfo.getMetadataURNList();
		
		List<Pattern> feedingFilters = getFeedingFilters();
		if (feedingFilters == null){
			setServiceDegradedAndRaiseError("Could not find the feeding filters.");
			return false;
		}
		
		for (Pattern feedingFilter : feedingFilters){
			for (String metadataURN : metadataURNList){
				if (feedingFilter.matcher(metadataURN).matches()){
					isValidForFeeding = true;					
					break;
				}
			}
			if (isValidForFeeding) break;
		}
		return isValidForFeeding;
	}
	
	private boolean shouldFileBePacked(String filename){
		boolean shouldBePacked = false;
		char pflag = filename.charAt(0);
		if ('A' == pflag){
			shouldBePacked = true; 
		}
		return shouldBePacked;
	}

	private void sendFileToQueue(FileInfo fileInfo, String queueName){
	      try {
	         // create message object
	         FeedingMessage message = new FeedingMessage();
	         message.setFullSourcePath(fileInfo.getFileURL());
	         message.setTargetFilename(fileInfo.getProductFilename());
	         message.setPriority(fileInfo.getPriority());

	         postTextMessage(queueName, message);
	      }
	      catch (Exception e) {
	         LOG.error("Failed to create feeding queue message for: {} due to {}", new File(fileInfo.getFileURL()), e);
	         setServiceDegradedAndRaiseError("Could not send message to queue " + queueName);
	      }
	}
	
   private void postTextMessage(final String queueName, final FeedingMessage feedingMessage) {
      Connection connection = null;
      try {
         Context initCtx = new InitialContext();
         Queue queue = (Queue) initCtx.lookup(queueName);

         // Get JMS Session
         connection = cf.createConnection();
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageProducer sender = session.createProducer(queue);

         Message message = session.createObjectMessage(feedingMessage);

         sender.send(message);
      } catch (Exception e) {
         LOG.error("Failed to post feeding queue message due to {}", e);
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (Exception ignored) {
            }
         }
      }
   }

	private List<Pattern> getFeedingFilters(){	
		ControlService controlService = ManagementServiceProvider.getInstance().getControlService();
		if (controlService == null){
			LOG.error("Could not find the ControlService.");
			return null;
		}
		List<org.openwis.management.entity.FeedingFilter> feedingFilters = controlService.getFeedingFilters();
		List<Pattern> feedingFiltersPatterns = new ArrayList<Pattern>();
		for (org.openwis.management.entity.FeedingFilter filter : feedingFilters){
			feedingFiltersPatterns.add(Pattern.compile(filter.getRegex()));
		}
		return feedingFiltersPatterns;
	}

	private void setServiceDegradedAndRaiseError(String cause){
		ControlService controlService = ManagementServiceProvider.getInstance().getControlService();
		if (controlService == null){
			LOG.error("Could not find ControlSerivice.");
		} else {
			controlService.setServiceStatus(org.openwis.management.service.ManagedServiceIdentifier.FEEDING_SERVICE, org.openwis.management.service.ManagedServiceStatus.DEGRADED);
			LOG.error("Set Feeding status to degraded. Cause: " + cause);
			
			AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
			if (alertService == null){
				LOG.error("Could not find the AlertService.");
			} else {
				String source = "openwis-dataservice-cache-ejb-FeederImpl";
				String location = "Feeding";
				String severity = "ERROR";
				String eventId = DataServiceAlerts.SERVICE_DEGRADED.getKey();
				
				List<Object> arguments = new ArrayList<Object>();
				arguments.add(source);
				arguments.add(cause);
				
				alertService.raiseEvent(source, location, severity, eventId, arguments);
			}
		}
	}
}