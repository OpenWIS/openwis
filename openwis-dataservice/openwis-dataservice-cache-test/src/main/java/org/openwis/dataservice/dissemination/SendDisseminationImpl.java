package org.openwis.dataservice.dissemination;

import java.io.StringWriter;

import javax.ejb.Stateless;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import javax.xml.bind.JAXBException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.Configuration;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.DisseminationMessage;

import org.openwis.harness.dissemination.Dissemination;
import org.openwis.harness.dissemination.DisseminationImplService;
import org.openwis.harness.dissemination.DisseminationStatus;
import org.openwis.harness.dissemination.DisseminationInfo;
import org.openwis.harness.dissemination.RequestStatus;
import org.openwis.harness.dissemination.FTPDiffusion;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(name = "SendDissemination")
public class SendDisseminationImpl implements SendDissemination, ConfigurationInfo {		
	
	// Logging	
	private final Logger LOG = LoggerFactory.getLogger(SendDissemination.class);
	
	/**
	 * The entity manager.
	 */
	@PersistenceContext
	protected EntityManager entityManager;
	
	// Queue	
	private Queue disseminationQueue;
	
	// Configuration	
	Configuration config;
	InitialContext context;
		
	//------------------------------------------------------------------------------------
	
	@Override
	public boolean testDisseminate(String ipAddress) {
		boolean returnVal = true;
		
		try 
		{
		   String actualURL = "http://" + ipAddress + ":8080/openwis-harness-samples-dissemination-1.0-SNAPSHOT/diss?wsdl";
		   URL url = new URL(actualURL);
			
		   LOG.info("Connecting to " + actualURL);
			
		   DisseminationImplService disseminationImplService = new DisseminationImplService(url);
		   Dissemination disseminationHarness = disseminationImplService.getDisseminationImplPort();
		   
		   if (disseminationHarness != null)
			{
			   String requestID = "myRequestId";
			   String fileURI = "file:///var/tmp/test/data";

			   
			   DisseminationInfo disseminationInfo = new DisseminationInfo();
				
			   disseminationInfo.setPriority(1);
			   disseminationInfo.setSLA(3);				
			   disseminationInfo.setDataPolicy("dataPolicy");
			   
			   FTPDiffusion ftpDiffusion = new FTPDiffusion();
			   ftpDiffusion.setFileName("fileName");
			   ftpDiffusion.setHost("host");
			   ftpDiffusion.setPort("port");
			   ftpDiffusion.setUser("user");
			   ftpDiffusion.setPassword("password");
			   ftpDiffusion.setPassive(true);
			   ftpDiffusion.setRemotePath("remotePath");

			   disseminationInfo.setDiffusion(ftpDiffusion);
			   			   
			   DisseminationStatus status = disseminationHarness.disseminate(requestID, fileURI, disseminationInfo);

			   LOG.info("Returned dissemination status: " + status.getRequestStatus().value() + " " + status.getRequestId() + " " + status.getMessage());
				
			   if (status.getRequestStatus() == RequestStatus.FAILED)
			   {
				   returnVal = false;
				   LOG.error("Start of dissemination via harness failed: " + actualURL);
			   }
			   else
			   {
				   LOG.info("Start of dissemination via harness succeeded: " + actualURL);
			   }
			}
			else
			{
				returnVal = false;
				LOG.error("DisseminationHarness not initialized: " + actualURL);
			}				
	   }
	   catch (Exception ex) 
	   {
		   returnVal = false;
		   LOG.error("Caught exception: " + ex.getMessage());
	   }

	   return returnVal;
	}
	
	@Override
	public boolean add(final Long id) {
		configure();
		
		Queue queue = disseminationQueue;			

		sendFileToQueue(id, queue);
		
		return true;
	}
	
	private void sendFileToQueue(Long id, Queue queue){
		LOG.info("Now try to send message with id: " + id);

		try {
			QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("ConnectionFactory");
			QueueConnection connection = factory.createQueueConnection();
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(queue);

			DisseminationMessage dissRequestMessage = new DisseminationMessage();
			dissRequestMessage.setId(id);
									
			// XML transformation
			StringWriter writer = new StringWriter();
			Serializer.serialize(dissRequestMessage, writer);
			String body = writer.toString();

			LOG.info("Now sending message: " + body);

			TextMessage message = session.createTextMessage(body);
							         
			sender.send(message);	         
		}
		catch (NamingException e) {
			e.printStackTrace();
		}
		catch (JAXBException e) 
		{
			e.printStackTrace();
	    }
		catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void configure(){
		setupQueues();
	}
	
	private void setupQueues(){		
		try {
			context = new InitialContext();			
			
			disseminationQueue = (Queue) context.lookup("queue/DisseminationQueue");
		}
		catch (NamingException e) {
			e.printStackTrace();
		}			
	}
}