package org.openwis.harness.samples.ws.localdatasource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.io.IOUtils;
import org.openwis.harness.localdatasource.LocalDataSource;
import org.openwis.harness.localdatasource.LocalDataSourceService;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.harness.localdatasource.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FileSystemLocalDatasource. <P>
 * Explanation goes here. <P>
 */
@WebService(targetNamespace = "http://localdatasource.harness.openwis.org/", name = "LocalDataSourceService", portName = "LocalDataSourcePort", serviceName = "LocalDataSourceService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class WebServiceLocalDatasource implements LocalDataSource {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(WebServiceLocalDatasource.class);

   /** The wsdl. */
   private String wsdl;

   /** The delegate. */
   private LocalDataSource delegate;

   /**
    * Default constructor.
    * Builds a FileSystemLocalDatasource.
    */
   public WebServiceLocalDatasource() {
      super();
      logger.trace("Create WebServiceLocalDatasource");
      initialize();
   }

   /**
    * Initialize.
    */
   private void initialize() {
      Properties props = new Properties();
      InputStream in = null;
      try {
         in = getClass().getResourceAsStream("/WebServiceLocalDatasource.properties");
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
      wsdl = props.getProperty("localDataSource.delegate.wsdl");
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
      return getDelegateLocalDataSource().getAvailability(timestamp);
   }

   /**
    * Gets the delegate local data source.
    *
    * @return the delegate local data source
    */
   private synchronized LocalDataSource getDelegateLocalDataSource() {
      if (delegate == null) {
         URL url;
         try {
            url = new URL(wsdl);
            LocalDataSourceService service = new LocalDataSourceService(url);
            delegate = service.getLocalDataSourcePort();
         } catch (MalformedURLException e) {
            logger.error("Fail to locate delegate localDatasource: " + wsdl);
         }
      }
      return delegate;
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
      return getDelegateLocalDataSource().extract(urn, parameters, id, stagingPostURI);
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
      return getDelegateLocalDataSource().monitorExtraction(id);
   }
}
