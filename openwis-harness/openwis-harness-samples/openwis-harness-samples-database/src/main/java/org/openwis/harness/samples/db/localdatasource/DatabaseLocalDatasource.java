package org.openwis.harness.samples.db.localdatasource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DatabaseLocalDatasource. <P>
 * Explanation goes here. <P>
 */
@WebService(targetNamespace = "http://localdatasource.harness.openwis.org/", name = "LocalDataSourceService", portName = "LocalDataSourcePort", serviceName = "LocalDataSourceService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class DatabaseLocalDatasource implements LocalDataSource {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(DatabaseLocalDatasource.class);

   /** The local datasource file utils. */
   private LocalDatasourceDbUtils localDatasourceUtils;

   /** The extractor utils. */
   private final ExtractorUtils extractorUtils;

   /**
    * Default constructor.
    * Builds a FileSystemLocalDatasource.
    */
   public DatabaseLocalDatasource() {
      super();
      logger.trace("Create DatabaseLocalDatasource");
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
         in = getClass().getResourceAsStream("/DatabaseLocalDatasource.properties");
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
      localDatasourceUtils = new LocalDatasourceDbUtils(props);
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

         // Query Database
         Connection c = null;
         PreparedStatement ps = null;
         ResultSet resultSet = null;
         try {
            c = getLocalDatasourceDbUtils().getConnection();
            if (timestamp != null) {
               Timestamp ts = new Timestamp(DateTimeUtils.parseDateTime(timestamp)
                     .getTimeInMillis());
               ps = c.prepareStatement(SqlRequest.AVAILABILITY_FROM);
               ps.setTimestamp(1, ts);
            } else {
               ps = c.prepareStatement(SqlRequest.AVAILABILITY);
            }

            String mdUrn;
            resultSet = ps.executeQuery();
            // Handle result
            while (resultSet.next()) {
               mdUrn = resultSet.getString(1);
               result.add(mdUrn);
            }
         } catch (SQLException e) {
            logger.error("Could not query the database", e);
            localDatasourceUtils.close(c, ps, resultSet);
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
      DbExtractionRunnable extractionRunnable;
      try {
         extractionRunnable = new DbExtractionRunnable(getLocalDatasourceDbUtils(), urn,
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
   public LocalDatasourceDbUtils getLocalDatasourceDbUtils() {
      return localDatasourceUtils;
   }

}
