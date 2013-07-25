package org.openwis.dataservice.cache;

import java.io.File;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Timer;

import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.util.FileInfo;
import org.openwis.dataservice.util.WMOFNC;

@Local
public interface CacheManager {

	final String cleanupInUseKey = "cleanupInUse";
	final String housekeepingInUseKey = "housekeepingInUse";
	final String alertCleanerInUseKey = "alertCleanerInUse";


	public void start();

	public void stop();

	public void timeout(Timer timer);

	public boolean isServiceAlreadyRunning(String service);

	public void setServiceRunning(String service, boolean status);

	public void copyFileToHarness(File file);

	public boolean isDuplicate(String filename, String checksum, int numberOfChecksumBytes);

	public CachedFile moveFileIntoCache(FileInfo file);

	public void removeFileFromCache(String filename, String checksum);

	public void createNewIncomingDataMessage(WMOFNC fileNameInfo, FileInfo file, Long cachedFileId);

	public void archiveFileToTemporaryDirectory(File file, boolean keepOriginal);

	public boolean isValidForReplication(List<String> metadataUrnList);
}