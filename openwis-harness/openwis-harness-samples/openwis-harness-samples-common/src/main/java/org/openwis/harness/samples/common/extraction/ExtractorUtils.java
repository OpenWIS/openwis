/**
 * 
 */
package org.openwis.harness.samples.common.extraction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExtractorUtils. <P>
 * Explanation goes here. <P>
 */
public class ExtractorUtils {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ExtractorUtils.class);

   /** The executor service. */
   private final ExecutorService executorService;

   /** The local datasource file utils. */
   private LocalDatasourceUtils localDatasourceFileUtils;

   /**
    * Instantiates a new extractor utils.
    *
    * @param localDatasourceFileUtils the local datasource file utils
    */
   public ExtractorUtils(LocalDatasourceUtils localDatasourceFileUtils) {
      super();
      executorService = Executors.newCachedThreadPool();
      this.localDatasourceFileUtils = localDatasourceFileUtils;
   }

   /**
    * Extract.
    *
    * @param extractionRunnable the extraction runnable
    * @return the monitor status
    */
   public MonitorStatus extract(ExtractionRunnable extractionRunnable) {
      MonitorStatus result = new MonitorStatus();
      result.setStatus(Status.ERROR);
      try {
         // launch extraction in background
         executorService.execute(extractionRunnable);
         result.setStatus(Status.ONGOING_EXTRACTION);
         result.setMessage("Processing :" + extractionRunnable);
      } catch (RejectedExecutionException e) {
         logger.error("Cannot execute extraction :'(", e);
         result.setMessage(e.toString());
         result.setStatus(Status.ERROR);
      }
      return result;
   }

   /**
    * Gets the status.
    *
    * @param id the id
    * @return the status
    */
   public MonitorStatus getStatus(long id) {
      return localDatasourceFileUtils.readStatus(id);
   }
}
