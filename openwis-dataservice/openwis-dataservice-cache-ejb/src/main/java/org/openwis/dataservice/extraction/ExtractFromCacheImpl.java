/**
 *
 */
package org.openwis.dataservice.extraction;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.tools.ant.util.FileUtils;
import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.cache.CacheIndex;
import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.entity.cache.CachedFile;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.service.BlacklistService;
import org.openwis.dataservice.common.util.ConfigServiceFacade;
import org.openwis.dataservice.common.util.DateTimeUtils;
import org.openwis.management.service.AlertService;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an implementation for the {@code ExtractFromCache} service.
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
@Stateless(name = "ExtractFromCache")
public class ExtractFromCacheImpl implements ExtractFromCache, ConfigurationInfo {

   // -------------------------------------------------------------------------
   // Instance Variables
   // -------------------------------------------------------------------------

   // cache directory
   private String cacheDirectory;

   private String stagingPostDirectory;

   /** The CacheIndex service. */
   @EJB
   private CacheIndex cacheIndexService;

   /** The StatisticsService service. */
   //   private Ext statisticsService;

   private static final String PERIOD_FORMAT_REGEX = "(\\d{2}:\\d{2}:\\d{2})Z?";

   private static final String TIME_FORMAT_REGEX = "(\\d{2}:\\d{2})Z?";

   private static final String DATE_FORMAT_REGEX = "(\\d{4}-\\d{2}-\\d{2})[T](\\d{2}\\:\\d{2}:\\d{2})Z?";

   // Logging tool
   private static final Logger LOG = LoggerFactory.getLogger(ExtractFromCacheImpl.class);

   @PersistenceContext
   private EntityManager entityManager;

   /** The blacklist service. */
   @EJB
   private BlacklistService blacklistService;

   // -------------------------------------------------------------------------
   // ExtractFromCache Impl.
   // -------------------------------------------------------------------------

   /**
    * This methods obtains the list of products matching the selection criteria
    * from the cache index and then proceeds with the extraction.
    * <p/>
    * Opens a new transaction for updating the processed request size.
    */
   @Override
   public MessageStatus extract(final String userId, final String metadataURN, final List<Parameter> parameters,
         final Long processedRequestId, final String stagingPostURI, Calendar lowerBoundInsertionDate) {

	   String cause = "";

      // return value
      MessageStatus status = new MessageStatus();
      try {
         // extract using CacheIndex...
         CachedFile[] matchingFiles = extractFromCache(metadataURN, parameters, processedRequestId,
               stagingPostURI, lowerBoundInsertionDate);

         if (matchingFiles == null) {
            status.setMessage("Invalid value returned by the CacheIndex. No file found matching filter criteria!");
            cause = "Invalid value returned by the CacheIndex. No file found matching filter criteria!";
            status.setStatus(Status.ERROR);
         } else if (matchingFiles.length == 0) {
            status.setMessage("No file found matching filter criteria!");
            cause = "No file found matching filter criteria!";
            status.setStatus(Status.NO_RESULT_FOUND);
         } else {
            // target directory
            String targetDirectory = String.format("%s%s%s", getStagingPostDirectory(),
                  File.separator, stagingPostURI);

            int count = copyFiles(targetDirectory, matchingFiles);

            status.setMessage("Number of file(s) found: " + count);
            if (count > 0) {
               // report success
               status.setStatus(Status.EXTRACTED);

               // update statistics
               updateExtractedDataStatistics(processedRequestId, userId, matchingFiles);

            } else {
               status.setStatus(Status.NO_RESULT_FOUND);
               cause = "No results found.";
            }
         }
      } catch (Exception e) {
         LOG.error("Exception from extraction", e);      // TEMP
         // report failure
         status.setMessage(e.getMessage());
         status.setStatus(Status.ERROR);
         cause = "Error.";
      }
      finally{
    	  if (status.getStatus() == Status.ERROR){
    		  String productInfo = metadataURN; // FIXME
    		  raiseExtractionFailsAlert(productInfo, cause);
    	  }
      }

      // feedback
      return status;
   }

   private void raiseExtractionFailsAlert(Object productInfo, Object cause){
	   AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
	   if (alertService == null){
		   LOG.error("Could not get hold of the AlertService. No alert was passed!");
		   return;
	   }

	   String source = "openwis-dataservice-cache-ejb-ExtractFromCacheImpl";
	   String location = "Extraction";
	   String severity = "WARN";
	   String eventId = DataServiceAlerts.EXTRACTION_FAILS.getKey();

	   List<Object> arguments = Arrays.asList(source,productInfo,cause);

	   alertService.raiseEvent(source, location, severity, eventId, arguments);
   }

   // -------------------------------------------------------------------------
   // Extract From Cache Index
   // -------------------------------------------------------------------------

   /**
    * Extract a cached product by exploring the file system.
    *
    * @param parameters sub selection parameter list
    * @param metadataURN metadata URN of the product
    * @param processedRequestId request technical identifier
    * @param stagingPostURI the suffix of the URI
    * @param lowerBoundInsertionDate lower bound for insertion date of the extracted file (may be null)
    * @return
    */
   protected CachedFile[] extractFromCache(final String metadataURN,
         final List<Parameter> parameters, final Long processedRequestId,
         final String stagingPostURI, Calendar lowerBoundInsertionDate) {

      // check: Extraction parameters
      checkExtractionParameters(metadataURN, parameters, processedRequestId, stagingPostURI);

      // check: CacheIndex service
      //CacheIndex cacheIndex = getCacheIndexService();
//      if (cacheIndex == null) {
//         throw new IllegalArgumentException("Unresolved CacheIndex service insatnce.");
//      }

      // return values
      Map<String, CachedFile> cachedFiles = new LinkedHashMap<String, CachedFile>();

      // Iterate over the list of parameters
      for (Parameter parameter : parameters) {
         // check parameter instance
         if (parameter == null) {
            LOG.warn("Invalid parameter specified for extraction, ignoring!");
            continue;
         }

         String parameterCode = parameter.getCode();

         // check parameter values
         Set<Value> values = parameter.getValues();
         if (values == null || values.isEmpty()) {
            LOG.warn("No values specified for extraction parameter {}, ignoring!", parameterCode);
            continue;
         }

         // parse parameter value
         for (Value value : parameter.getValues()) {
            if (value == null || value.getValue() == null || value.getValue().trim().isEmpty()) {
               // trace...
               if (LOG.isTraceEnabled()) {
                  LOG.warn("Null or empty value specified for parameter {}, ignoring!",
                        parameterCode);
               }
               continue;
            }

            // do the real work
            String parameterValue = value.getValue().trim();
            listFiles(cacheIndexService, cachedFiles, metadataURN, parameterCode, parameterValue, lowerBoundInsertionDate);
         }
      }

      // convert into array
      CachedFile[] matchingFiles = new CachedFile[cachedFiles.size()];
      matchingFiles = cachedFiles.values().toArray(matchingFiles);

      return matchingFiles;
   }

   // -------------------------------------------------------------------------
   // Extracted Data StatisticsService
   // -------------------------------------------------------------------------

   /**
    * Update the extracted data statistics
    */
   protected void updateExtractedDataStatistics(final Long processedRequestId, final String userId, final CachedFile[] extractedFiles) {
      // check arguments
      if (extractedFiles == null || extractedFiles.length == 0) {
         return;
      }

      long totalSize = 0;

      for (Object fileObj : extractedFiles) {
         Long size = getFileSize(fileObj);
         if (size != null) {
            totalSize += size.longValue();
         }
      }
      Date now = DateTimeUtils.getUTCCalendar().getTime();

      try {
         // Update process request size
         ProcessedRequest pr = entityManager.getReference(ProcessedRequest.class,
               processedRequestId);
         pr.setSize(totalSize);
         entityManager.merge(pr);
         entityManager.flush();
         // Update statistics
         blacklistService.checkAndUpdateDisseminatedData(userId, pr.getRequest().getEmail(),
               DateTimeUtils.formatUTC(now), extractedFiles.length, totalSize);
      } catch (Exception e) {
         LOG.warn("Could no update statistics", e);
      }
   }

   // -------------------------------------------------------------------------
   // Bound Properties
   // -------------------------------------------------------------------------

   /**
    * Sets the local CacheIndex service instance to use.
    */
   protected final void setCacheIndexService(final CacheIndex cacheIndex) {
      cacheIndexService = cacheIndex;
   }

   /**
    * Returns an array containing the directory settings for the directory scanner. <br>
    *
    * @return the configured value for the cache directory
    */
   protected final String getCacheDirectory() {

      if (cacheDirectory != null) {
         return cacheDirectory;
      }

      // dataservice-cache module configuration.

      // cache directory
      String directory = ConfigServiceFacade.getInstance().getString(CACHE_DIRECTORY_KEY);
      if (directory == null || "".equals(directory.trim())) {
         // trace...
         LOG.warn("No cache directory defined, ignoring");
         return null;
      }

      return directory;
   }

   /**
    * Sets the cacheDirectory.
    *
    * @param cacheDirectory the cacheDirectory to set.
    */
   protected final void setCacheDirectory(final String cacheDirectory) {
      this.cacheDirectory = cacheDirectory;
   }

   /**
    * @return the staging post directory
    */
   protected final String getStagingPostDirectory() {

      if (stagingPostDirectory == null) {
         stagingPostDirectory = ConfigServiceFacade.getInstance().getString(STAGING_POST_DIRECTORY_KEY); //JndiUtils.getString(STAGING_POST_DIRECTORY_KEY);
      }
      return stagingPostDirectory;
   }

   /**
    * @return the staging post directory
    */
   protected final void setStagingPostDirectory(final String pathname) {
      stagingPostDirectory = pathname;
   }

   // -------------------------------------------------------------------------
   // Utilities
   // -------------------------------------------------------------------------

   /**
    * Validate the arguments for the extract functions
    */
   private void checkExtractionParameters(final String metadataURN,
         final List<Parameter> parameters, final Long processedRequestId,
         final String stagingPostURI) {
      // check: metadataURN
      if (metadataURN == null || "".equals(metadataURN.trim())) {
         throw new IllegalArgumentException("metadata URN is null or empty!");
      }
      // check: parameters
      if (parameters == null || parameters.isEmpty()) {
         throw new IllegalArgumentException("parameters constraints is null or empty!");
      }
      // check: stagingPostURI
      if (stagingPostURI == null || "".equals(stagingPostURI.trim())) {
         throw new IllegalArgumentException("staging post URI is null or empty!");
      }

      // target directory
      String targetDirectory = getStagingPostDirectory();
      if (targetDirectory == null || "".equals(targetDirectory.trim())) {
         throw new IllegalArgumentException("Unresolved staging post directory location: "
               + targetDirectory);
      }
   }

   /**
    * Description goes here.
    *
    * @param matchingFiles
    * @param parameterCode
    * @param metadataUrn
    * @param parameterValue
    * @param lowerBoundInsertionDate lower bound for insertion date of the extracted file (may be null)
    */
   private void listFiles(final CacheIndex cacheIndex, final Map<String, CachedFile> matchingFiles,
         final String metadataUrn, final String parameterCode, final String parameterValue, Calendar lowerBoundInsertionDate) {

      // Resolve parameter code
      String code = resolveParameterCode(parameterCode, parameterValue);
      if (code == null) {
         // parsing errors were logged already
         return;
      }

      List<CachedFile> cachedFiles = null;

      // filter on time interval
      if (ParameterCode.TIME_INTERVAL.equalsIgnoreCase(code)) {
         String timeInterval = parseTimeInterval(parameterValue, true);

         if (timeInterval != null) {
            cachedFiles = cacheIndex.listFilesByMetadataUrn(metadataUrn, timeInterval);
         }
      }
      // filter on date interval
      else if (ParameterCode.DATE_TIME_INTERVAL.equalsIgnoreCase(code)) {
         String[] dateTimeInterval = parseDateTimeInterval(parameterValue, true);

         if (dateTimeInterval != null && dateTimeInterval.length > 1) {
            cachedFiles = cacheIndex.listFilesByMetadataUrn(metadataUrn, dateTimeInterval[0],
                  dateTimeInterval[1]);
         }
      }
      // filter on product id
      else if (ParameterCode.PRODUCT_ID.equalsIgnoreCase(code)) {
         Long productId = parseProductID(parameterValue, true);

         if (productId != null) {
            CachedFile file = cacheIndex.getCachedFileById(productId);
            if (file != null) {
               cachedFiles = Arrays.asList(file);
            }
         }
      }

      // Update result set
      if (cachedFiles != null && !cachedFiles.isEmpty()) {
         for (CachedFile fileIt : cachedFiles) {

            // check insertion date against lower bound value (not in case of product id)
            if (fileIt.getInsertionDate() != null &&
                  lowerBoundInsertionDate != null &&
                  !ParameterCode.PRODUCT_ID.equalsIgnoreCase(code)) {
               Calendar insertionDate = Calendar.getInstance();
               insertionDate.setTime(fileIt.getInsertionDate());
               if (!lowerBoundInsertionDate.before(insertionDate)) {
                  // filter out this cached file
                  continue;
               }
            }

            String pathname = String
                  .format("%s/%s", fileIt.getPath(), fileIt.getInternalFilename());
            matchingFiles.put(pathname, fileIt);
         }
      }
   }

   /**
    * Parses the passed argument as a number.
    *
    * @param value the value to parse
    * @return the resulting number or <code>null</code> is the conversion fails.
    */
   private static Long parseProductID(final String value, final boolean handleException) {
      try {
         return Long.parseLong(value.trim());
      } catch (Exception e) {
         // report exception
         if (handleException) {
            LOG.error("Unable to parse parameter value as a number: {}", value);
         }
         return null;
      }
   }

   /**
    * Parses the passed argument as time interval with the following format
    * <tt>HH:mm:ss'Z'/HH:mm:ss'Z'</tt>.
    *
    * @param value the value to parse
    * @return the resulting interval or <code>null</code> is the conversion fails.
    */
   private static String parseTimeInterval(final String value, final boolean handleException) {
      try {
         // range separator
         String period = value.trim();
         int separator = period.indexOf('/');
         if (separator > 0) {

            // start time
            String startTime = period.substring(0, separator).trim();
            if (!startTime.matches(TIME_FORMAT_REGEX) && !startTime.matches(PERIOD_FORMAT_REGEX)) {
               if (handleException) {
                  LOG.error("Invalid start time interval expression: " + value);
               }
               return null;
            }

            // end time
            String endTime = period.substring(separator + 1).trim();
            if (!endTime.matches(TIME_FORMAT_REGEX) && !startTime.matches(PERIOD_FORMAT_REGEX)) {
               if (handleException) {
                  LOG.error("Invalid end time interval expression: " + value);
               }
               return null;
            }

            return period;
         } else {
            if (handleException) {
               LOG.error("Invalid time interval expression: " + value);
            }
            return null;
         }
      } catch (Exception e) {
         // report exception
         if (handleException) {
            LOG.error("Unable to parse parameter value as a time interval: {}", value);
         }
         return null;
      }
   }

   /**
    * Parses the passed argument as date-time interval with the following format
    * <tt>yyyy-MM-dd'T'HH:mm:ss'Z'/yyyy-MM-dd'T'HH:mm:ss'Z'</tt>..
    *
    * @param value the value to parse
    * @return the resulting interval bounds or <code>null</code> is the conversion fails.
    */
   private static String[] parseDateTimeInterval(final String value, final boolean handleException) {
      try {
         // range separator
         String period = value.trim();
         int separator = period.indexOf('/');
         if (separator > 0) {

            // start date
            String startDate = period.substring(0, separator).trim();
            if (!startDate.matches(DATE_FORMAT_REGEX)) {
               if (handleException) {
                  LOG.error("Invalid start date interval expression: " + value);
               }
               return null;
            }

            // end date
            String endDate = period.substring(separator + 1).trim();
            if (!endDate.matches(TIME_FORMAT_REGEX)) {
               if (handleException) {
                  LOG.error("Invalid end date interval expression: " + value);
               }
               return null;
            }

            return new String[] {startDate, endDate};
         } else {
            if (handleException) {
               LOG.error("Invalid date-time interval expression: " + value);
            }
            return null;
         }
      } catch (Exception e) {
         // report exception
         if (handleException) {
            LOG.error("Unable to parse parameter value as a date-time interval: {}", value);
         }
         return null;
      }
   }

   /**
    * Description goes here.
    * @param parameterCode
    * @param parameterValue
    * @return
    */
   private static String resolveParameterCode(final String parameterCode,
         final String parameterValue) {

      // known parameter codes
      if (parameterCode != null && !"".equals(parameterCode.trim())) {
         if (ParameterCode.TIME_INTERVAL.equalsIgnoreCase(parameterCode)) {
            return ParameterCode.TIME_INTERVAL;
         }
         if (ParameterCode.DATE_TIME_INTERVAL.equalsIgnoreCase(parameterCode)) {
            return ParameterCode.DATE_TIME_INTERVAL;
         }
         if (ParameterCode.PRODUCT_ID.equalsIgnoreCase(parameterCode)) {
            return ParameterCode.PRODUCT_ID;
         }
      }

      // guess parameter code from its value

      // product ID
      Object productID = parseProductID(parameterValue, false);
      if (productID != null) {
         return ParameterCode.PRODUCT_ID;
      }

      // time period
      Object period = parseTimeInterval(parameterValue, false);
      if (period != null) {
         return ParameterCode.TIME_INTERVAL;
      }

      // date time interval
      Object interval = parseDateTimeInterval(parameterValue, false);
      if (interval != null) {
         return ParameterCode.DATE_TIME_INTERVAL;
      }

      // unresolved
      return null;
   }

   /**
    * Description goes here.
    * @param stagingPostURI
    * @param matchingFiles
    */
   private static int copyFiles(final String targetDir, final Object[] sourceFiles) {
      // fail fast
      if (sourceFiles == null) {
         return -1;
      }
      if (sourceFiles.length == 0) {
         return 0;
      }

      // File move/copy utilities
      int modCount = 0;
      FileUtils utils = FileUtils.getFileUtils();

      // print the matching files
      for (Object source : sourceFiles) {

         // check file attributes
         if (source == null) {
            continue;
         }
         File targetFile = null;
         File sourceFile = new File(getAbsolutePath(source));

         if (source instanceof CachedFile) {
        	 // the copied file also contains the checksum so that files with the same name but different checksums can be extracted for the same request
        	 String filename = ((CachedFile) source).getFilename();
        	 int extensionIndex = filename.lastIndexOf('.');
        	 String filenameWithoutExtension = filename.substring(0, extensionIndex);
        	 String extension = filename.substring(extensionIndex);
        	 String checksum = ((CachedFile) source).getChecksum();
        	 String newFileName = filenameWithoutExtension + "_" + checksum + extension;
            targetFile = new File(targetDir, newFileName);
         } else {
            targetFile = new File(targetDir, sourceFile.getName());
         }

         // trace
         if (LOG.isTraceEnabled()) {
            LOG.trace("Processing cached file: {}", sourceFile.getName());
         }

         // check whether this file corresponds to a WMO-FTP or WMO-FNC file

         try {
            // move source file to target directory
            utils.copyFile(sourceFile, targetFile);

            // check transaction
            if (targetFile.isFile()) {
               modCount++;
            }
         } catch (IOException ioe) {
            // trace
            LOG.error("Failed to copy file: {} to {} due to {}", new Object[] {sourceFile,
                  targetDir, ioe.getMessage()});
         }
      }

      // summary of moved files
      return modCount;
   }

   /**
    * Returns the length of the file denoted by File object or a CachedFile object.
    * @param fileObj
    * @param The length of the file
    */
   private static Long getFileSize(final Object fileObj) {
      // default value
      Long size = null;

      if (fileObj instanceof File) {
         File file = (File) fileObj;
         size = Long.valueOf(file.length());

      } else if (fileObj instanceof CachedFile) {
         CachedFile file = (CachedFile) fileObj;
         size = file.getFilesize();
      }

      // feedback
      return size;
   }

   /**
    * Resolves the path name form either a File object or a CachedFile object.
    * @param fileObj
    * @param The absolute pathname string
    */
   private static String getAbsolutePath(final Object fileObj) {
      // default value
      String absolutePath = null;

      if (fileObj instanceof File) {
         File file = (File) fileObj;
         absolutePath = file.getAbsolutePath();

      } else if (fileObj instanceof CachedFile) {
         CachedFile file = (CachedFile) fileObj;
         absolutePath = String.format("%s/%s", file.getPath(), file.getInternalFilename());
      }

      // feedback
      return absolutePath;
   }

}