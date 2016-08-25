package org.openwis.datasource.server.jndi;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;

import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.statistics.CachedFileInfo;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.dataservice.util.FileInfo;
import org.openwis.dataservice.util.FileNameInfoFilter;
import org.openwis.dataservice.util.FileNameParser;
import org.openwis.dataservice.util.ProductDateFilter;

/**
 * The Class MockCacheIndex. <P>
 * Explanation goes here. <P>
 */
//@Stateless(name="MockCacheIndex")
public class MockCacheIndex implements CacheIndex {

   /** The Constant INSTANCE. */
   private static final MockCacheIndex INSTANCE = new MockCacheIndex();

   /**
    * Gets the cache index.
    *
    * @return the cache index
    */
   public static final CacheIndex getCacheIndex() {
      return INSTANCE;
   }

   /** The cached. */
   private final Map<String, List<CachedFile>> cached;

   /** The count. */
   private long count;

   /**
    * Instantiates a new mock cache index.
    */
   private MockCacheIndex() {
      super();
      count = 0L;
      cached = new HashMap<String, List<CachedFile>>();
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#ping()
    */
   @Override
   public void ping() {
      // does nothing
   }

   /**
    * Adds the cache index entry.
    *
    * @param file the file
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#addCacheIndexEntry(org.openwis.dataservice.util.FileInfo)
    */
   @Override
   public CachedFile addCacheIndexEntry(final FileInfo file) {
      CachedFile cachedFile = createCacheFile(file);
      List<CachedFile> list;
      for (String urn : file.getMetadataURNList()) {
         list = cached.get(urn);
         if (list == null) {
            list = new ArrayList<CachedFile>();
            cached.put(urn, list);
         }
         list.add(cachedFile);
      }
      return cachedFile;
   }

   /**
    * Creates the cache file.
    *
    * @param fileInfo the file
    * @return the cached file
    */
   private CachedFile createCacheFile(final FileInfo fileInfo) {
      CachedFile cachedFile = new CachedFile();
      cachedFile.setId(count++);

      File file = new File(fileInfo.getFileURL());
      cachedFile.setInternalFilename(file.getName());
      cachedFile.setFilename(fileInfo.getProductFilename());

      String fileURL = fileInfo.getFileURL().replace('\\', '/');

      int index = fileURL.lastIndexOf('/');
      if (index > 0) {
         cachedFile.setPath(fileURL.substring(0, index));
      }
      cachedFile.setChecksum(fileInfo.getChecksum());
      cachedFile.setNumberOfChecksumBytes(fileInfo.getNumberOfChecksumBytes());
      cachedFile.setPriority(fileInfo.getPriority());
      cachedFile.setInsertionDate(fileInfo.getInsertionDate());
      cachedFile.setReceivedFromGTS(true);
      cachedFile.setFilesize(fileInfo.getSize());
      return cachedFile;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#listFilesByMetadataUrn(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<CachedFile> listFilesByMetadataUrn(final String metadataUrn,
         final String startDateString, final String endDateString) {
      List<CachedFile> result = new ArrayList<CachedFile>();

      List<CachedFile> cachedFiles = cached.get(metadataUrn);

      if (cachedFiles != null && !cachedFiles.isEmpty()) {
         for (CachedFile file : cachedFiles) {
            String filename = file.getFilename();

            Date productDate = null;
            Calendar startCalendar = null;
            Calendar endCalendar = null;
            Calendar productCalendar = null;
            try {
               productDate = FileNameParser.parseFileName(filename).getProductDate();
               if (productDate == null) {
                  throw new NullPointerException(
                        "The product date could not be derived from the cached file's name!");
               }
               productCalendar = DateTimeUtils.getUTCCalendar();
               productCalendar.setTime(productDate);

               startCalendar = DateTimeUtils.getUTCCalendar(startDateString);
               endCalendar = DateTimeUtils.getUTCCalendar(endDateString);
            } catch (ParseException e) {
               e.printStackTrace();
            } catch (Exception e) {
               e.printStackTrace();
            }
            if ((productCalendar.compareTo(startCalendar) == 0)
                  || (productCalendar.compareTo(endCalendar) == 0)
                  || (productCalendar.after(startCalendar) && productCalendar.before(endCalendar))) {
               result.add(file);
            }
         }
      }

      return result;
   }

   /**
    * List all cached files.
    *
    * @return the list
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#listAllCachedFiles()
    */
   @Override
   public List<CachedFile> listAllCachedFiles() {
      List<CachedFile> result = new ArrayList<CachedFile>();
      for (List<CachedFile> files : cached.values()) {
         result.addAll(files);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#getCacheContent()
    */
   @Override
   public List<CachedFileInfo> getCacheContent() {
      List<CachedFileInfo> result = new ArrayList<CachedFileInfo>();
      CachedFileInfo info;

      for (Entry<String, List<CachedFile>> entry : cached.entrySet()) {
         for (CachedFile cachedFile : entry.getValue()) {
            info = new CachedFileInfo();
            info.setChecksum(cachedFile.getChecksum());
            info.setInsertionDate(cachedFile.getInsertionDate());
            info.setMetadataUrn(entry.getKey());
            info.setName(cachedFile.getFilename());
            result.add(info);
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#getCachedFileById(java.lang.Long)
    */
   @Override
   public CachedFile getCachedFileById(final Long id) {
      CachedFile result = null;
      if (id != null) {
         for (CachedFile cachedFile : listAllCachedFiles()) {
            if (id.equals(cachedFile.getId())) {
               result = cachedFile;
               break;
            }
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#listFilesByMetadataUrn(java.lang.String, java.lang.String)
    */
   @Override
   public List<CachedFile> listFilesByMetadataUrn(final String metadataUrn, final String timePeriod) {
      List<CachedFile> result = new ArrayList<CachedFile>();

      FileNameInfoFilter filter = FileNameInfoFilter.createProductDateFilter(timePeriod);
      ProductDateFilter dateFilter = (ProductDateFilter) filter;

      List<CachedFile> cachedFiles = cached.get(metadataUrn);
      if (cachedFiles != null && !cachedFiles.isEmpty()) {

         for (CachedFile file : cachedFiles) {
            String filename = file.getFilename();
            if (dateFilter.accept(filename)) {
               result.add(file);
            }
         }
      }
      return result;
   }

   @Override
   public List<CachedFile> listCachedFiles(int offset, int limit, Date lastVuzeDownloadCreationDate) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<String> getAllMetadataUrnsForCachedFile(String filename, String checksum) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<CachedFileInfo> getCacheContentSorted(String sortField, String sorOrder,
         int firstResult, int maxResults) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public CachedFile getCachedFile(String filename, String checksum) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setLastCollectDate(long lastCollectDate) {
      // TODO Auto-generated method stub

   }

   @Override
   public long getBackupLastCollectDate() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public void backupLastCollectDate() {
      // TODO Auto-generated method stub

   }

   @Override
   public List<CachedFileInfo> getCacheContentFilteredSorted(String filenameFilterExpression,
         String metadataFilterExpression, String sortField, String sortOrder, int firstResult,
         int maxResults) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public long getCacheContentCount(String filenameFilterExpression, String metadataFilterExpression) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public List<CachedFile> listCachedFilesBetweenDates(int offset, int limit, Date date1, Date date2) {
      // TODO Auto-generated method stub
      return null;
   }
}