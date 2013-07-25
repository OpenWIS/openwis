package org.openwis.harness.samples.fs.localdatasource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.io.IOUtils;
import org.openwis.harness.localdatasource.LocalDataSource;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.localdatasource.Status;
import org.openwis.harness.samples.common.extraction.ExtractorUtils;
import org.openwis.harness.samples.common.parameters.ParameterUtils;
import org.openwis.harness.samples.common.time.DateTimeUtils;
import org.openwis.harness.samples.fs.filter.AfterDateFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileSystemLocalDatasource. <P>
 * Explanation goes here. <P>
 */
@WebService(targetNamespace = "http://localdatasource.harness.openwis.org/", name = "LocalDataSourceService", portName = "LocalDataSourcePort", serviceName = "LocalDataSourceService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class FileSystemLocalDatasource implements LocalDataSource {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(FileSystemLocalDatasource.class);

   /** The local datasource file utils. */
   private FsLocalDatasourceFileUtils localDatasourceFileUtils;

   /** The root folder. */
   private File rootFolder;

   /** The extractor utils. */
   private final ExtractorUtils extractorUtils;

   /**
    * Default constructor.
    * Builds a FileSystemLocalDatasource.
    */
   public FileSystemLocalDatasource() {
      super();
      logger.trace("Create FileSystemLocalDatasource");
      initialize();
      extractorUtils = new ExtractorUtils(localDatasourceFileUtils);
   }

   /**
    * Initialize.
    */
   private void initialize() {
      Properties props = new Properties();
      InputStream in = null;
      try {
         in = getClass().getResourceAsStream("/FileSystemLocalDatasource.properties");
         props.load(in);
         initialize(props);
      } catch (IOException ioe) {
         logger.error("Could not read propertie file!", ioe);
      } finally {
         IOUtils.closeQuietly(in);
      }
   }

   /**
    * Initialize.
    *
    * @param props the properties
    */
   private void initialize(Properties props) {
      // local datasource File Utils
      localDatasourceFileUtils = new FsLocalDatasourceFileUtils(props);
      rootFolder = localDatasourceFileUtils.getRootFolder();
   }

   /**
    * Gets the availability.
    *
    * @param timestamp the timestamp
    * @return the availability
    * {@inheritDoc}
    * @see org.openwis.harness.localdatasource.LocalDataSource#getAvailability(java.lang.String)
    */
   @Override
   @WebMethod()
   public @WebResult(name = "metadataURNs")
   List<String> getAvailability(@WebParam(name = "since") String timestamp) {
      List<String> result;
      logger.trace(">>> getAvailability({})", timestamp);
      try {
         result = new ArrayList<String>();
         FileFilter filter;
         if (timestamp != null) {
            Calendar date = DateTimeUtils.parseDateTime(timestamp);
            filter = new AfterDateFilter(date);
         } else {
            // Accept all filter
            filter = new FileFilter() {
               @Override
               public boolean accept(File pathname) {
                  return !pathname.getName().startsWith(".");
               }
            };
         }
         // Extract files
         for (File file : rootFolder.listFiles(filter)) {
            result.add(localDatasourceFileUtils.getMetadataURN(file));
         }
      } catch (ParseException e) {
         logger.error("Could not parse the date: " + timestamp, e);
         throw new IllegalArgumentException(e);
      }
      logger.trace("<<< getAvailability({}): {}", timestamp, result);
      return result;
   }

   /**
    * Extract.
    *
    * @param urn the urn
    * @param parameters the parameters
    * @param id the id
    * @param stagingPostURI the staging post uri
    * @return the monitor status
    * {@inheritDoc}
    * @see eu.akka.meteo.openwis.generated.LocalDataSource#extract(java.lang.String, java.util.List)
    */
   @Override
   @WebMethod()
   public @WebResult(name = "extractionStatus")
   MonitorStatus extract(@WebParam(name = "metadataURN") String urn,
         @WebParam(name = "parametersList") List<Parameter> parameters,
         @WebParam(name = "requestId") long id,
         @WebParam(name = "stagingPostURI") String stagingPostURI) {
      logger.trace(">>> extract({},{},{},{})",
            new Object[] {urn, ParameterUtils.parametersToString(parameters), id, stagingPostURI});

      MonitorStatus result;
      FsExtractionRunnable extractionRunnable;
      try {
         extractionRunnable = new FsExtractionRunnable(getLocalDatasourceFileUtils(), urn,
               parameters, id, stagingPostURI);
         result = extractorUtils.extract(extractionRunnable);
      } catch (IOException e) {
         logger.error("Error when create runnable", e);
         result = new MonitorStatus();
         result.setStatus(Status.ERROR);
         result.setMessage(e.getMessage());
      }

      logger.trace("<<< extract({},{},{},{}) : {}",
            new Object[] {urn, ParameterUtils.parametersToString(parameters), id, stagingPostURI,
                  result});
      return result;
   }

   /**
    * Monitor extraction.
    *
    * @param id the id
    * @return the monitor status
    * {@inheritDoc}
    * @see org.openwis.harness.localdatasource.LocalDataSource#monitorExtraction(long)
    */
   @Override
   @WebMethod()
   public @WebResult(name = "extractionStatus")
   MonitorStatus monitorExtraction(@WebParam(name = "requestId") long id) {
      logger.trace(">>> monitorExtraction({})", id);
      MonitorStatus result = extractorUtils.getStatus(id);
      logger.trace("<<< monitorExtraction({}): {}", id, result);
      return result;
   }

   /**
    * Gets the localDatasourceFileUtils.
    * @return the localDatasourceFileUtils.
    */
   public FsLocalDatasourceFileUtils getLocalDatasourceFileUtils() {
      return localDatasourceFileUtils;
   }
}
