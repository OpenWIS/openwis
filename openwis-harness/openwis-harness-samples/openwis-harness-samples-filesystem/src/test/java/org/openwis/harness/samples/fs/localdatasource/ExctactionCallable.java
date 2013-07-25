package org.openwis.harness.samples.fs.localdatasource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.localdatasource.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ExctactionCallable. <P>
 */
public class ExctactionCallable implements Callable<File> {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(ExctactionCallable.class);

   /** The file. */
   private final File file;

   /** The urn. */
   private final String urn;

   /** The parameters. */
   private final List<Parameter> parameters;

   /** The nb expected result. */
   private final int nbExpectedResult;

   /** The uri. */
   private final String uri;

   /** The request id. */
   private final long requestId;

   /**
    * Default constructor.
    * Builds a FileSystemLocalDatasourceTestCase.ExctactionCallable.
    *
    * @param urn the urn
    * @param parameters the parameters
    * @param nbExpectedResult the nb expected result
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public ExctactionCallable(String urn, List<Parameter> parameters, int nbExpectedResult)
         throws IOException {
      super();
      this.urn = urn;
      this.parameters = new ArrayList<Parameter>(parameters);
      this.nbExpectedResult = nbExpectedResult;
      requestId = FileSystemLocalDatasourceTestCase.nextId();
      uri = FileSystemLocalDatasourceTestCase.URI_PREFIX + requestId;

      // clean previous extraction
      file = FileSystemLocalDatasourceTestCase.getLocalDataSource().getLocalDatasourceFileUtils()
            .getStagingPostFile(uri);
      FileUtils.deleteDirectory(file);
   }

   /**
    * Gets the file.
    *
    * @return the file
    */
   public File getFile() {
      return file;
   }

   /**
    * Gets the nbExpectedResult.
    * @return the nbExpectedResult.
    */
   public int getNbExpectedResult() {
      return nbExpectedResult;
   }

   /**
    * Gets the requestId.
    * @return the requestId.
    */
   public long getRequestId() {
      return requestId;
   }

   /**
    * {@inheritDoc}
    * @see java.util.concurrent.Callable#call()
    */
   @Override
   public File call() throws Exception {
      MonitorStatus status;
      // extract
      logger.debug("Extraction {}", requestId);
      status = FileSystemLocalDatasourceTestCase.getLocalDataSource().extract(urn, parameters,
            requestId, uri);

      // Wait result
      while (Status.ONGOING_EXTRACTION.equals(status.getStatus())) {
         status = FileSystemLocalDatasourceTestCase.getLocalDataSource().monitorExtraction(
               requestId);
         logger.debug("Extraction {} : {} - {}", new Object[] {requestId, status.getStatus(),
               status.getMessage()});
         if (!Status.ONGOING_EXTRACTION.equals(status.getStatus())) {
            break;
         }
         logger.debug("Extraction {} - Not yet ended, try in {}ms", requestId,
               FileSystemLocalDatasourceTestCase.WAITING_TIMEOUT);
         FileSystemLocalDatasourceTestCase
               .waitFor(FileSystemLocalDatasourceTestCase.WAITING_TIMEOUT);
      }
      return file;
   }
}
