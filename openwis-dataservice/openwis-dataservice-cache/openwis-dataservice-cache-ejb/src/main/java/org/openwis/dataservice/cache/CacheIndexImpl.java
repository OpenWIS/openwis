package org.openwis.dataservice.cache;

import java.io.File;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.domain.entity.cache.CacheConfiguration;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.cache.MappedMetadata;
import org.openwis.dataservice.common.domain.entity.statistics.CachedFileInfo;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.dataservice.util.FileInfo;
import org.openwis.dataservice.util.FileNameInfoFilter;
import org.openwis.dataservice.util.FileNameParser;
import org.openwis.dataservice.util.ProductDateFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(name = "CacheIndexWebService")
@Stateless(name = "CacheIndex")
@TransactionTimeout(18000)
public class CacheIndexImpl implements CacheIndex, ConfigurationInfo {

   private final Logger LOG = LoggerFactory.getLogger(CacheIndexImpl.class);

   @PersistenceContext
   private EntityManager entityManager;

   private static final String INSERTION_DATE_JPQL_SORT_FIELD_NAME = "insertionDate";

   private static final String INSERTION_DATE_NATIVE_SORT_FIELD_NAME = "INSERTION_DATE";

   private static final String ORIGIN_JPQL_SORT_FIELD_NAME = "origin";

   private static final String ORIGIN_NATIVE_SORT_FIELD_NAME = "RECEIVED_FROM_GTS";

   private static final String URN_JPQL_SORT_FIELD_NAME = "metadataUrn";

   private static final String URN_NATIVE_SORT_FIELD_NAME = "URN";

   private static final String DEFAULT_SORT_ORDER = "DESC";

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.cache.CacheIndex#ping()
    */
   @Override
   public void ping() {
      // does nothing
      if (LOG.isTraceEnabled()) {
         LOG.trace("CacheIndex service instance reachable from {}", getClass().getName());
      }
   }

   private String getFilteredCacheCountQuery(String filenameFilter, String metadataFilter) {
      StringBuffer filteredCacheCountQuery = new StringBuffer();
      boolean hasFilenameFilter = filenameFilter != null && !"".equals(filenameFilter);
      boolean hasMetadataFilter = metadataFilter != null && !"".equals(metadataFilter);

      filteredCacheCountQuery.append("SELECT ");
      filteredCacheCountQuery.append("count(lcf.CACHED_FILE_ID) ");
      filteredCacheCountQuery.append("FROM ");
      filteredCacheCountQuery.append("(SELECT ");
      filteredCacheCountQuery.append("cf.filename, ");
      filteredCacheCountQuery.append("cf.CACHED_FILE_ID ");
      filteredCacheCountQuery.append("FROM openwis_cached_file cf ");
      filteredCacheCountQuery.append((hasFilenameFilter ? "WHERE " + filenameFilter.toLowerCase()
            + " " : ""));
      filteredCacheCountQuery.append(") lcf ");
      filteredCacheCountQuery.append("LEFT JOIN openwis_mapped_metadata USING (CACHED_FILE_ID) ");
      filteredCacheCountQuery
            .append("LEFT JOIN openwis_product_metadata pm USING (product_metadata_id) ");
      filteredCacheCountQuery.append((hasMetadataFilter ? "WHERE " + metadataFilter.toLowerCase()
            : ""));

      return filteredCacheCountQuery.toString();
   }

   private String getFilteredBrowseCacheQuery(String sortField, String sortOrder, int maxResults,
         int firstResult, String filenameFilter, String metadataFilter) {
      boolean sortByMetadataUrn = false;
      String resolvedSortField = sortField;

      boolean hasFilenameFilter = filenameFilter != null && !"".equals(filenameFilter);
      boolean hasMetadataFilter = metadataFilter != null && !"".equals(metadataFilter);

      if (INSERTION_DATE_JPQL_SORT_FIELD_NAME.equals(sortField)) {
         resolvedSortField = INSERTION_DATE_NATIVE_SORT_FIELD_NAME;
      } else if (ORIGIN_JPQL_SORT_FIELD_NAME.equals(sortField)) {
         resolvedSortField = ORIGIN_NATIVE_SORT_FIELD_NAME;
      } else if (URN_JPQL_SORT_FIELD_NAME.equals(sortField)) {
         resolvedSortField = URN_NATIVE_SORT_FIELD_NAME;
         sortByMetadataUrn = true;
      }

      StringBuffer filteredBrowseCacheQuery = new StringBuffer();
      filteredBrowseCacheQuery.append("SELECT ");
      filteredBrowseCacheQuery.append("lcf.filename, ");
      filteredBrowseCacheQuery.append("lcf.checksum, ");
      filteredBrowseCacheQuery.append("lcf.received_from_gts, ");
      filteredBrowseCacheQuery.append("cast(pm.urn as text), ");
      filteredBrowseCacheQuery.append("lcf.insertion_date ");
      filteredBrowseCacheQuery.append("FROM ");
      filteredBrowseCacheQuery.append("(SELECT ");
      filteredBrowseCacheQuery.append("cf.filename, ");
      filteredBrowseCacheQuery.append("cf.CACHED_FILE_ID, ");
      filteredBrowseCacheQuery.append("cf.checksum, ");
      filteredBrowseCacheQuery.append("cf.received_from_gts, ");
      filteredBrowseCacheQuery.append("cf.insertion_date ");
      filteredBrowseCacheQuery.append("FROM openwis_cached_file cf ");
      filteredBrowseCacheQuery.append((hasFilenameFilter ? "WHERE " + filenameFilter.toLowerCase()
            + " " : ""));
      filteredBrowseCacheQuery.append((sortByMetadataUrn ? "" : "ORDER BY " + resolvedSortField
            + " " + sortOrder + " "));
      filteredBrowseCacheQuery.append(") lcf ");
      filteredBrowseCacheQuery.append("LEFT JOIN openwis_mapped_metadata USING (CACHED_FILE_ID) ");
      filteredBrowseCacheQuery
            .append("LEFT JOIN openwis_product_metadata pm USING (product_metadata_id) ");
      filteredBrowseCacheQuery.append((hasMetadataFilter ? "WHERE " + metadataFilter.toLowerCase()
            + " " : ""));
      filteredBrowseCacheQuery.append((sortByMetadataUrn ? "ORDER BY " + resolvedSortField + " "
            + sortOrder + " " : ""));
      filteredBrowseCacheQuery.append("LIMIT " + maxResults + " ");
      filteredBrowseCacheQuery.append("OFFSET " + firstResult);

      return filteredBrowseCacheQuery.toString();
   }

   @Override
   @WebMethod(operationName = "getCacheContent")
   public List<CachedFileInfo> getCacheContent() {
      return getCacheContentSorted(INSERTION_DATE_NATIVE_SORT_FIELD_NAME, DEFAULT_SORT_ORDER, 0,
            Integer.MAX_VALUE);
   }

   @Override
   @WebMethod(operationName = "getCacheContentSorted")
   @SuppressWarnings("unchecked")
   public List<CachedFileInfo> getCacheContentSorted(
         @WebParam(name = "sortField") final String sortField,
         @WebParam(name = "sortOrder") final String sortOrder,
         @WebParam(name = "firstResult") final int firstResult,
         @WebParam(name = "maxResults") final int maxResults) {
      List<CachedFileInfo> cachedFileInfoList = new ArrayList<CachedFileInfo>();

      String query = getFilteredBrowseCacheQuery(sortField, sortOrder, maxResults, firstResult,
            null, null);

      if (LOG.isDebugEnabled()) {
         LOG.debug("Query = " + query);
      }

      Query cacheContentQuery = entityManager.createNativeQuery(query);

      List<Object[]> foundContent = cacheContentQuery.getResultList();
      LOG.debug("number of found entries: " + foundContent.size());

      for (Object[] cacheObject : foundContent) {
         String filename = (String) cacheObject[0];
         String checksum = (String) cacheObject[1];
         Boolean receivedFromGTS = (Boolean) cacheObject[2];
         String origin = (receivedFromGTS.booleanValue() ? "Collection" : "Replication");
         String metadataURN = (String) cacheObject[3];
         Date insertionDate = (Date) cacheObject[4];

         CachedFileInfo cachedFileInfo = new CachedFileInfo();
         cachedFileInfo.setName(filename);
         cachedFileInfo.setChecksum(checksum);
         cachedFileInfo.setOrigin(origin);
         cachedFileInfo.setMetadataUrn(metadataURN);
         cachedFileInfo.setInsertionDate(insertionDate);

         cachedFileInfoList.add(cachedFileInfo);
      }
      return cachedFileInfoList;
   }

   @Override
   @WebMethod(operationName = "getCacheContentFilteredSorted")
   @SuppressWarnings("unchecked")
   public List<CachedFileInfo> getCacheContentFilteredSorted(
         @WebParam(name = "filenameFilterExpression") final String filenameFilterExpression,
         @WebParam(name = "metadataFilterExpression") final String metadataFilterExpression,
         @WebParam(name = "sortField") final String sortField,
         @WebParam(name = "sortOrder") final String sortOrder,
         @WebParam(name = "firstResult") int firstResult,
         @WebParam(name = "maxResults") int maxResults) {
      List<CachedFileInfo> cachedFileInfoList = new ArrayList<CachedFileInfo>();

      String query = getFilteredBrowseCacheQuery(sortField, sortOrder, maxResults, firstResult,
            filenameFilterExpression, metadataFilterExpression);

      if (LOG.isDebugEnabled()) {
         LOG.debug("Query = " + query);
      }

      Query cacheContentQuery = entityManager.createNativeQuery(query);

      List<Object[]> foundContent = cacheContentQuery.getResultList();
      LOG.debug("number of found entries: " + foundContent.size());

      for (Object[] cacheObject : foundContent) {
         String filename = (String) cacheObject[0];
         String checksum = (String) cacheObject[1];
         Boolean receivedFromGTS = (Boolean) cacheObject[2];
         String origin = (receivedFromGTS.booleanValue() ? "Collection" : "Replication");
         String metadataURN = (String) cacheObject[3];
         Date insertionDate = (Date) cacheObject[4];

         CachedFileInfo cachedFileInfo = new CachedFileInfo();
         cachedFileInfo.setName(filename);
         cachedFileInfo.setChecksum(checksum);
         cachedFileInfo.setOrigin(origin);
         cachedFileInfo.setMetadataUrn(metadataURN);
         cachedFileInfo.setInsertionDate(insertionDate);

         cachedFileInfoList.add(cachedFileInfo);
      }
      return cachedFileInfoList;
   }

   @Override
   @WebMethod(operationName = "getCacheContentCount")
   @SuppressWarnings("unchecked")
   public long getCacheContentCount(
         @WebParam(name = "filenameFilterExpression") final String filenameFilterExpression,
         @WebParam(name = "metadataFilterExpression") final String metadataFilterExpression) {
      long count = 0;

      String query = getFilteredCacheCountQuery(filenameFilterExpression, metadataFilterExpression);

      Query cacheCountQuery = entityManager.createNativeQuery(query);
      List<BigInteger> resultList = cacheCountQuery.getResultList();
      if (resultList != null && !resultList.isEmpty()) {
         count = resultList.get(0).longValue();
      }
      if (LOG.isDebugEnabled()) {
         LOG.debug("The query delivers " + count + " valid entries.");
      }
      return count;
   }

   @Override
   public CachedFile getCachedFileById(final Long id) {
      Query cachedFileQuery = entityManager.createQuery("SELECT cf FROM CachedFile cf WHERE id = '"
            + id + "'");
      CachedFile cachedFile = null;
      try {
         cachedFile = (CachedFile) cachedFileQuery.getSingleResult();
      } catch (NoResultException e) {
         LOG.warn("+++ There is no cached file with ID {}. Returning null.", id);
      } catch (NonUniqueResultException e) {
         LOG.warn("+++ There is no unique result with ID {}. Ignoring results and returning null.",
               id);
      }
      return cachedFile;
   }

   @Override
   @WebMethod(operationName = "listFilesByMetadataUrnAndTime")
   @SuppressWarnings("unchecked")
   public List<CachedFile> listFilesByMetadataUrn(final String metadataUrn, final String timePeriod) {
      List<CachedFile> cachedFiles = new ArrayList<CachedFile>();

      Query metadataIdQuery = entityManager
            .createQuery("SELECT pm.id FROM ProductMetadata pm WHERE urn = '" + metadataUrn + "'");
      List<Long> metadataIdList = metadataIdQuery.getResultList();

      for (Long metadataId : metadataIdList) {
         Query cachedFileQuery = entityManager
               .createQuery("SELECT DISTINCT cf FROM CachedFile cf, MappedMetadata mm WHERE mm.productMetadataId = '"
                     + metadataId + "' AND mm.id = cf.id");
         cachedFiles.addAll(cachedFileQuery.getResultList());
      }

      List<CachedFile> filteredCachedFiles = new ArrayList<CachedFile>();
      LOG.debug("+++ number of found files (before filtering) : " + cachedFiles.size());

      FileNameInfoFilter filter = FileNameInfoFilter.createProductDateFilter(timePeriod);
      ProductDateFilter dateFilter = (ProductDateFilter) filter;

      for (CachedFile file : cachedFiles) {
         String filename = file.getFilename();
         if (dateFilter.accept(filename)) {
            filteredCachedFiles.add(file);
         }
      }
      return filteredCachedFiles;
   }

   @Override
   @WebMethod(operationName = "listFilesByMetadataUrnAndDate")
   @SuppressWarnings("unchecked")
   public List<CachedFile> listFilesByMetadataUrn(final String metadataUrn,
         final String startDateString, final String endDateString) {
      List<CachedFile> cachedFiles = new ArrayList<CachedFile>();

      Query metadataIdQuery = entityManager
            .createQuery("SELECT pm.id FROM ProductMetadata pm WHERE urn = '" + metadataUrn + "'");
      List<Long> metadataIdList = metadataIdQuery.getResultList();

      for (Long metadataId : metadataIdList) {
         Query cachedFileQuery = entityManager
               .createQuery("SELECT DISTINCT cf FROM CachedFile cf, MappedMetadata mm WHERE mm.productMetadataId = '"
                     + metadataId + "' AND mm.id = cf.id");
         cachedFiles.addAll(cachedFileQuery.getResultList());
      }

      List<CachedFile> filteredCachedFiles = new ArrayList<CachedFile>();
      LOG.debug("+++ number of found files (before filtering) : " + cachedFiles.size());
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
            LOG.error(e.getMessage(), e);
         } catch (Exception e) {
            LOG.error(e.getMessage(), e);
         }
         if ((productCalendar.compareTo(startCalendar) == 0)
               || (productCalendar.compareTo(endCalendar) == 0)
               || (productCalendar.after(startCalendar) && productCalendar.before(endCalendar))) {
            filteredCachedFiles.add(file);
         }
      }
      return filteredCachedFiles;
   }

   @Override
   public CachedFile addCacheIndexEntry(final FileInfo fileInfo) {
      CachedFile cachedFile = new CachedFile();

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
      cachedFile.setReceivedFromGTS(fileInfo.isReceivedFromGTS());
      cachedFile.setFilesize(fileInfo.getSize());

      try {
         entityManager.persist(cachedFile);
         // force duplicate exception
         entityManager.flush();

         for (Long metadataId : fileInfo.getMetadataIDList()) {
            MappedMetadata mappedMetadata = new MappedMetadata();
            mappedMetadata.setCachedFileId(cachedFile.getId());
            mappedMetadata.setProductMetadataId(metadataId);
            entityManager.persist(mappedMetadata);
         }
      } catch (PersistenceException e) {
         LOG.error("Could not persist file {}, {}", cachedFile.getFilename(), e);
         return null;
      }

      return cachedFile;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<CachedFile> listAllCachedFiles() {
      List<CachedFile> cachedFiles = new ArrayList<CachedFile>();
      Query query = entityManager.createQuery("SELECT cf FROM CachedFile cf");
      cachedFiles.addAll(query.getResultList());
      return cachedFiles;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<CachedFile> listCachedFiles(int offset, int limit, Date lastVuzeDownloadCreationDate) {
      List<CachedFile> cachedFiles = new ArrayList<CachedFile>();
      Query query = entityManager.createNativeQuery(
            "SELECT DISTINCT * FROM openwis_cached_file WHERE INSERTION_DATE > '"
                  + lastVuzeDownloadCreationDate + "' LIMIT " + String.valueOf(limit) + " OFFSET "
                  + String.valueOf(offset), CachedFile.class);
      cachedFiles.addAll(query.getResultList());
      return cachedFiles;
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<String> getAllMetadataUrnsForCachedFile(String filename, String checksum) {
      List<String> metadataUrnList = new ArrayList<String>();

      String query = "SELECT DISTINCT pm.urn FROM ProductMetadata pm WHERE pm.id IN "
            + "(SELECT mm.productMetadataId FROM MappedMetadata mm WHERE mm.id IN "
            + "(SELECT cf.id FROM CachedFile cf WHERE cf.filename = '" + filename
            + "' AND cf.checksum = '" + checksum + "'))";
      Query metadataUrnQuery = entityManager.createQuery(query);

      metadataUrnList.addAll(metadataUrnQuery.getResultList());
      return metadataUrnList;
   }

   @Override
   public CachedFile getCachedFile(String filename, String checksum) {
      Query getCachedFileQuery = entityManager
            .createQuery("SELECT DISTINCT cf FROM CachedFile cf WHERE cf.filename = '" + filename
                  + "' AND cf.checksum = '" + checksum + "'");
      CachedFile resultCachedFile = null;
      try {
         resultCachedFile = (CachedFile) getCachedFileQuery.getSingleResult();
      } catch (NoResultException e) {
      } catch (NonUniqueResultException e) {
      }
      return resultCachedFile;
   }

   @Override
   public void setLastCollectDate(long lastCollectDate) {
      CacheConfiguration cc = null;

      try {
         Query query = entityManager
               .createQuery("SELECT cc FROM CacheConfiguration cc WHERE cc.key = 'LAST_COLLECT_DATE'");
         cc = (CacheConfiguration) query.getSingleResult();
         cc.setValue(lastCollectDate);
         entityManager.merge(cc);
      } catch (Exception e) {
         if (cc == null) {
            cc = new CacheConfiguration();
            cc.setKey("LAST_COLLECT_DATE");
            cc.setValue(lastCollectDate);
            entityManager.persist(cc);
         }
      }
   }

   private long getLastCollectDate() {
      Long date = null;
      long newTime = 0;

      try {
         Query query = entityManager
               .createQuery("SELECT cc.value FROM CacheConfiguration cc WHERE cc.key = 'LAST_COLLECT_DATE'");
         date = (Long) query.getSingleResult();
         newTime = date;
      } catch (Exception e) {
         if (date == null) {
            CacheConfiguration cc = new CacheConfiguration();
            cc.setKey("LAST_COLLECT_DATE");
            cc.setValue(newTime);
            entityManager.persist(cc);
         }
      }
      return newTime;
   }

   @Override
   public long getBackupLastCollectDate() {
      Long date = null;
      long newTime = 0;

      try {
         Query query = entityManager
               .createQuery("SELECT cc.value FROM CacheConfiguration cc WHERE cc.key = 'BACKUP_COLLECT_DATE'");
         date = (Long) query.getSingleResult();
         newTime = date;
      } catch (Exception e) {
         if (date == null) {
            CacheConfiguration cc = new CacheConfiguration();
            cc.setKey("BACKUP_COLLECT_DATE");
            cc.setValue(newTime);
            entityManager.persist(cc);
         }
      }
      return newTime;
   }

   @Override
   public void backupLastCollectDate() {
      long lastCollectDate = getLastCollectDate();
      CacheConfiguration cc = null;
      try {
         Query query = entityManager
               .createQuery("SELECT cc FROM CacheConfiguration cc WHERE cc.key = 'BACKUP_COLLECT_DATE'");
         cc = (CacheConfiguration) query.getSingleResult();
         cc.setValue(lastCollectDate);
         entityManager.merge(cc);
      } catch (Exception e) {
         if (cc == null) {
            cc = new CacheConfiguration();
            cc.setKey("BACKUP_COLLECT_DATE");
            cc.setValue(lastCollectDate);
            entityManager.persist(cc);
         }
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<CachedFile> listCachedFilesBetweenDates(int offset, int limit, Date date1, Date date2) {
      List<CachedFile> cachedFiles = new ArrayList<CachedFile>();
      Query query = entityManager.createNativeQuery(
            "SELECT DISTINCT * FROM openwis_cached_file WHERE insertion_date > '" + date1
                  + "' AND insertion_date < '" + date2 + "' ORDER BY insertion_date ASC LIMIT "
                  + String.valueOf(limit) + " OFFSET " + String.valueOf(offset), CachedFile.class);
      cachedFiles.addAll(query.getResultList());
      return cachedFiles;
   }
}