package org.openwis.dataservice.cache;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.statistics.CachedFileInfo;
import org.openwis.dataservice.util.FileInfo;

@Local
public interface CacheIndex {

   /**
    * Used to test the reachability of the service instance.
    */
   void ping();

   CachedFile addCacheIndexEntry(FileInfo file);

   List<CachedFile> listFilesByMetadataUrn(String metadataUrn, String startDateString, String endDateString);

   List<CachedFile> listAllCachedFiles();

   /**
    * Returns a List of all CachedFile objects in the DB (inserted since {@code lastVuzeDownloadCreationDate} with an index offset of {@code offset}.
    * Returns not more than {@code limit} elements.
    * @param offset
    * @param limit
    * @param lastVuzeDownloadCreationDate
    * @return
    */
   List<CachedFile> listCachedFiles(int offset, int limit, Date lastVuzeDownloadCreationDate);

   List<CachedFile> listCachedFilesBetweenDates(int offset, int limit, Date date1, Date date2);

   List<String> getAllMetadataUrnsForCachedFile(String filename, String checksum);


   List<CachedFileInfo> getCacheContent();

   List<CachedFileInfo> getCacheContentSorted(String sortField, String sorOrder, int firstResult, int maxResults);

   List<CachedFileInfo> getCacheContentFilteredSorted(String filenameFilterExpression, String metadataFilterExpression, String sortField, String sortOrder, int firstResult, int maxResults);

   long getCacheContentCount(String filenameFilterExpression, String metadataFilterExpression);

   CachedFile getCachedFileById(Long id);

   List<CachedFile> listFilesByMetadataUrn(String metadataUrn, String timePeriod);

   CachedFile getCachedFile(String filename, String checksum);

   void setLastCollectDate(long lastCollectDate);

   long getBackupLastCollectDate();

   void backupLastCollectDate();
}