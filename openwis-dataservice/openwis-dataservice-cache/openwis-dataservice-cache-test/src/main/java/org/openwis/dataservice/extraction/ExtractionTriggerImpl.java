package org.openwis.dataservice.extraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.statistics.CachedFileInfo;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.datasource.server.jaxb.serializer.Serializer;
import org.openwis.datasource.server.jaxb.serializer.incomingds.ProcessedRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(name = "SendExtraction")
public class ExtractionTriggerImpl implements ExtractionTrigger {
	
	private Queue requestQueue;
	
	private final Logger LOG = LoggerFactory.getLogger(ExtractionTriggerImpl.class);
	
	public void sendMessageToQueue(Long id, String productDateString){
		try {
			InitialContext context = new InitialContext();
			QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("ConnectionFactory");
			QueueConnection connection = factory.createQueueConnection();
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(getRequestQueue());

			ProcessedRequestMessage processedRequestMessage = new ProcessedRequestMessage();
			processedRequestMessage.setId(id);
			processedRequestMessage.setProductDate(productDateString);
									
			// XML transformation
			StringWriter writer = new StringWriter();
			Serializer.serialize(processedRequestMessage, writer);
			String body = writer.toString();

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
	
	private Queue getRequestQueue(){
		if (requestQueue == null){
			try {
				InitialContext context = new InitialContext();			
				
				requestQueue = (Queue) context.lookup(REQUEST_QUEUE_NAME);
			}
			catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return requestQueue;
	}

	@Override
	public void createExampleCollectableFile(String filename) {
		String incomingDirectory = JndiUtils.getString(HARNESS_INCOMING_DIRECTORY_KEY);		
		File tempExampleFile = new File(incomingDirectory,filename + ".tmp");
		File exampleFile = new File(incomingDirectory,filename);
		try {
			tempExampleFile.createNewFile();
			FileOutputStream os = new FileOutputStream(tempExampleFile);
			os.write('1');
			byte[] data = {'a','b','c','d'};
			os.write(data);
			os.flush();
			os.close();
			tempExampleFile.renameTo(exampleFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private CacheIndex getCacheIndex(){
		CacheIndex cacheIndex = null;
		try {
			InitialContext context = new InitialContext();
			cacheIndex = (CacheIndex) context.lookup(JndiUtils.getString(CACHE_INDEX_URL_KEY));
		}
		catch (NamingException e) {
			e.printStackTrace();
		}		
		return cacheIndex;
	}
	
	@Override
	public void getCacheContentTest(){
		List<CachedFileInfo> cachedFileInfoList = getCacheIndex().getCacheContent();
		for(CachedFileInfo info : cachedFileInfoList){
			String filename = info.getName();
			String checksum = info.getChecksum();
			LOG.info("+++ getCachedContentTest +++");
			LOG.info(filename + " : " + checksum);
		}
	}
	
	@Override
	public void getCachedFileByIdTest(Long id) {
		CachedFile cachedFile = getCacheIndex().getCachedFileById(id);		
		LOG.info("+++ getCachedFileByIdTest, id=" + id + " +++");
		if (cachedFile != null)LOG.info(cachedFile.getPath() + " --- " + cachedFile.getInternalFilename());
	}

	@Override
	public void listFilesByMetadataUrnAndDateTest(String metadataUrn, String startDateString, String endDateString) {
		List<CachedFile> cachedFileList = getCacheIndex().listFilesByMetadataUrn(metadataUrn,startDateString,endDateString);
		LOG.info("+++ listFilesByMetadataUrnAndDateTest +++");		
		for (CachedFile cachedFile : cachedFileList){
			LOG.info(cachedFile.getPath() + " --- " + cachedFile.getInternalFilename());
		}
	}

	@Override
	public void listFilesByMetadataUrnAndTimeTest(String metadataUrn, String timePeriod) {
		List<CachedFile> cachedFileList = getCacheIndex().listFilesByMetadataUrn(metadataUrn,timePeriod);
		LOG.info("+++ listFilesByMetadataUrnAndTimeTest +++");		
		for (CachedFile cachedFile : cachedFileList){
			LOG.info(cachedFile.getPath() + " --- " + cachedFile.getInternalFilename());
		}
	}	
}