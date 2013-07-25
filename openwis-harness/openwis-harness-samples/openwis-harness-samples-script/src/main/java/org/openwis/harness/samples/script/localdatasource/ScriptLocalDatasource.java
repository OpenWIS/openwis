package org.openwis.harness.samples.script.localdatasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openwis.harness.localdatasource.LocalDataSource;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Parameter;
import org.openwis.harness.localdatasource.Status;
import org.openwis.harness.samples.common.extraction.ExtractorUtils;
import org.openwis.harness.samples.common.parameters.ParameterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(targetNamespace = "http://localdatasource.harness.openwis.org/", name = "LocalDataSourceService", portName = "LocalDataSourcePort", serviceName = "LocalDataSourceService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class ScriptLocalDatasource implements LocalDataSource {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ScriptLocalDatasource.class);

   /** The local datasource file utils. */
   private LocalDatasourceScriptUtils localDatasourceUtils;

   /** The extractor utils. */
   private final ExtractorUtils extractorUtils;

   /** The availability script. */
   private String availabilityScript;

   /** The extract script. */
   private String extractScript;

   /**
    * Default constructor.
    * Builds a FileSystemLocalDatasource.
    */
   public ScriptLocalDatasource() {
      super();
      logger.trace("Create ScriptLocalDatasource");
      initialize();
      extractorUtils = new ExtractorUtils(localDatasourceUtils);
   }

   /**
    * Initialize.
    */
   private void initialize() {
      Properties props = new Properties();
      InputStream in = null;
      try {
         in = getClass().getResourceAsStream("/ScriptLocalDatasource.properties");
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
      localDatasourceUtils = new LocalDatasourceScriptUtils(props);
      availabilityScript = props.getProperty("availability.script");
      extractScript = props.getProperty("extract.script");
   }

   /**
    * Gets the availability.
    *
    * @param timestamp the timestamp
    * @return the availability
    * {@inheritDoc}
    * @see org.openwis.harness.localdatasource.LocalDataSource#getAvailability(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   @Override
   @WebMethod()
   public @WebResult(name = "metadataURNs")
   List<String> getAvailability(@WebParam(name = "since") String timestamp) {
      Set<String> result = new HashSet<String>();
      logger.trace(">>> getAvailability({})", timestamp);
      File outputFile;
      try {
         File rootFolder = localDatasourceUtils.getTmpRootFolder();
         rootFolder.mkdirs();
         outputFile = File.createTempFile("lds", null, rootFolder);
         int res = localDatasourceUtils.runAndWaitResult("/bin/sh", availabilityScript, timestamp,
               outputFile.getCanonicalPath());
         if (res == 0) {
            result.addAll(FileUtils.readLines(outputFile));
            outputFile.delete();
         } else {
            logger.warn("Could not retrieve available urns");
         }
      } catch (IOException e) {
         logger.error("Could not retrieve available urns", e);
      }
      logger.trace("<<< getAvailability({}): {}", timestamp, result);
      return new ArrayList<String>(result);
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
      try {
         StringBuffer params = new StringBuffer();
         boolean isFirst = true;
         boolean isFirstParam;
         // Handle parameter
         for (Parameter p : parameters) {
            if (isFirst) {
               isFirst = false;
            } else {
               params.append('&');
            }
            params.append(p.getCode());
            params.append('=');
            // handle value
            isFirstParam = true;
            for (String val : p.getValues()) {
               if (isFirstParam) {
                  isFirstParam = false;
               } else {
                  params.append(',');
               }
               params.append(val);
            }
         }
         // Call script
         File outputFile = localDatasourceUtils.getStagingPostFile(stagingPostURI);
         localDatasourceUtils.runInBackground("/bin/sh", extractScript, String.valueOf(id), urn,
               params.toString(), outputFile.getCanonicalPath());

         result = new MonitorStatus();
         result.setStatus(Status.ONGOING_EXTRACTION);
      } catch (Exception e) {
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
   public LocalDatasourceScriptUtils getLocalDatasourceScriptUtils() {
      return localDatasourceUtils;
   }

}
