package org.openwis.dataservice.extraction;

import javax.ejb.Remote;

import org.openwis.dataservice.ConfigurationInfo;

/**
 * EJB to manually insert a message into the RequestQueue, such triggering the Extraction process 
 * @author kulbatzki
 *
 */
@Remote
public interface ExtractionTrigger extends ConfigurationInfo {
	
	public void sendMessageToQueue(Long id, String productDateString);	
	
	public void createExampleCollectableFile(String filename);
	
	// for the testing of some CacheIndex functions
	public void getCacheContentTest();
	public void getCachedFileByIdTest(Long id);
	public void listFilesByMetadataUrnAndDateTest(String metadataUrn, String startDateString, String endDateString);
	public void listFilesByMetadataUrnAndTimeTest(String metadataUrn, String timePeriod);
}