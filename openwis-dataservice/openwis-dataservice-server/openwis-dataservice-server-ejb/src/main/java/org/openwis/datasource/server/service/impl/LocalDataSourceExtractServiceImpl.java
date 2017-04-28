/**
 *
 */
package org.openwis.datasource.server.service.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.common.domain.bean.MessageStatus;
import org.openwis.dataservice.common.domain.bean.Status;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.exception.OpenWisException;
import org.openwis.dataservice.common.service.LocalDataSourceExtractService;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.openwis.harness.localdatasource.LocalDataSource;
import org.openwis.harness.localdatasource.LocalDataSourceService;
import org.openwis.harness.localdatasource.MonitorStatus;
import org.openwis.management.config.PropertySource;
import org.openwis.management.service.AlertService;
import org.openwis.management.utils.DataServiceAlerts;
import org.openwis.management.utils.ManagementServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
@Local(LocalDataSourceExtractService.class)
@Stateless(name = "LocalDataSourceExtractService")
public class LocalDataSourceExtractServiceImpl implements LocalDataSourceExtractService {

   /** logger. */
   private static Logger logger = LoggerFactory.getLogger(LocalDataSourceExtractServiceImpl.class);

   /** The local data source service map. */
   private static Map<String, LocalDataSourceService> localDataSourceServiceMap;
   
   private PropertySource localDataSourcePropertySource;

   /**
    * Default constructor.
    * Builds a LocalDataSourceExtractServiceImpl.
    *
    * @throws Exception the exception
    */
   public LocalDataSourceExtractServiceImpl() throws Exception {
      super();
   }
   
   @PostConstruct
   public void initialized() {
      localDataSourcePropertySource = new PropertySource(PropertySource.LOCAL_DATA_SOURCE);
   }

   /**
    * Gets the local data source service map.
    *
    * @return the local data source service map
    */
   private Map<String, LocalDataSourceService> getLocalDataSourceServiceMap() {
      if (localDataSourceServiceMap == null) {
         localDataSourceServiceMap = new HashMap<String, LocalDataSourceService>();
//         InitialContext ctx;
//         try {
//            ctx = new InitialContext();
//            Properties properties = (Properties) ctx
//                  .lookup(DataServiceConfiguration.LOCA_DATA_SOURCE_CONFIGURATION_LOCATION);
            initialize(localDataSourcePropertySource.getProperties());
//         } catch (NamingException e1) {
//            logger.error("Can not initialize the initial context JNDI.", e1);
//         }
      }
      return localDataSourceServiceMap;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.LocalDataSourceExtractService#getAllLocalDataSourceRef()
    */
   @Override
   public List<String> getAllLocalDataSourceRef() {
      List<String> result = new ArrayList<String>(getLocalDataSourceServiceMap().keySet());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.LocalDataSourceExtractService#getAvailability(java.lang.String, java.lang.String)
    */
   @Override
   public List<String> getAvailability(String ldsName, String timestampUTC) {
      logger.debug("getAvailability({}, {})", new Object[] {ldsName, timestampUTC});
      List<String> result;
      try {
         LocalDataSourceService lds = getLocalDataSourceServiceMap().get(ldsName);
         if (lds != null) {
            LocalDataSource port = lds.getLocalDataSourcePort();
            result = port.getAvailability(timestampUTC);
         } else {
            result = new ArrayList<String>();
         }

      } catch (Throwable t) {
         logger.error("Error in local datasource getAvailability", t);
         raiseAlarm(ldsName, t);
         result = new ArrayList<String>();
      }
      return result;
   }

   /**
    * Initialize.
    *
    * @param properties the properties
    */
   private void initialize(Properties properties) {
      Set<String> stringPropertyNames = properties.stringPropertyNames();
      URL url;
      String ldsLocation;
      for (String ldsName : stringPropertyNames) {
         if (!ldsName.endsWith(DataServiceConfiguration.LOCAL_DATA_SOURCE_POLLING_ENDS)) {
            ldsLocation = properties.getProperty(ldsName);
            try {
               url = new URL(ldsLocation);
               logger.info("Found LocalDataSource [{}]  at {}", new Object[] {ldsName, ldsLocation});
               getLocalDataSourceServiceMap().put(ldsName, new LocalDataSourceService(url));
            } catch (MalformedURLException e) {
               logger.error("Can not initialize the default wsdl from {}.", ldsLocation);
            }
         }
      }
   }

   /**
    * Extract.
    *
    * @param parameters the parameters
    * @param metadataURN the metadata urn
    * @param processedRequestId the processed request id
    * @param localDataSource the local data source
    * @param stagingPostUri the staging post uri
    * @param productId the product Id (may be null)
    * @return the message status
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ExtractService#extract(java.util.List, java.lang.String, java.lang.Long, java.lang.String)
    */
   @Override
   @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
   public MessageStatus extract(List<Parameter> parameters, String metadataURN,
         Long processedRequestId, String localDataSource, String stagingPostUri, String productId) {
      MessageStatus messageStatus;
      try {
         String localDS = localDataSource;
         if ((localDataSource == null) || (localDataSource.length() == 0)) {
            logger.warn(
                  "Local data source Id not present for metadataurn {}. Using the default one.",
                  metadataURN);
            Set<String> keys = getLocalDataSourceServiceMap().keySet();
            Iterator<String> it = keys.iterator();
            localDS = it.next();
         }

         logger.info("Extract LocalDataSourceService processed request id: {}", processedRequestId);
         LocalDataSourceService localDataSourceService = getLocalDataSourceServiceMap()
               .get(localDS);
         ensureLocalDataSourceServiceNotNullOrException(localDataSourceService);

         List<org.openwis.harness.localdatasource.Parameter> parametersToSend = new ArrayList<org.openwis.harness.localdatasource.Parameter>();
         for (Parameter parameter : parameters) {
            org.openwis.harness.localdatasource.Parameter param = new org.openwis.harness.localdatasource.Parameter();
            param.setCode(parameter.getCode());
            Set<Value> values = parameter.getValues();
            for (Value value : values) {
               param.getValues().add(value.getValue());
            }
            parametersToSend.add(param);
            logger.info("Parameter sent: {}, values: {}.", param.getCode(), param.getValues());
         }
         
         // Add product id if available
         if (productId != null) {
            org.openwis.harness.localdatasource.Parameter param = new org.openwis.harness.localdatasource.Parameter();
            param.setCode(ParameterCode.PRODUCT_ID);
            param.getValues().add(productId);
            parametersToSend.add(param);
            logger.info("Parameter sent: {}, values: {}.", param.getCode(), param.getValues());
         }

         logger.debug("Get the LocalDataSourcePort.");
         LocalDataSource localDataSourcePort = localDataSourceService.getLocalDataSourcePort();
         logger.debug("Call the extract service.");
         MonitorStatus status = localDataSourcePort.extract(metadataURN, parametersToSend,
               processedRequestId.longValue(), stagingPostUri);
         logger.info("Retrieve the message status {} for processed request id: {}",
               status.getStatus(), processedRequestId);
         messageStatus = transformMessageStatus(status);
      } catch (Throwable t) {
         logger.error("Error in local datasource extraction", t);
         messageStatus = new MessageStatus();
         messageStatus.setMessage(t.getMessage());
         messageStatus.setStatus(Status.ERROR);

         raiseAlarm(localDataSource, t);
      }
      return messageStatus;
   }

   private void raiseAlarm(String dataSourceName, Throwable t) {
      AlertService alertService = ManagementServiceProvider.getInstance().getAlertService();
      List<Object> args = new ArrayList<Object>();
      args.add(dataSourceName);
      args.add(t.toString());
      alertService.raiseEvent("Data Service", "LocalDatasource", null,
            DataServiceAlerts.UNREACHABLE_LOCAL_DATASOURCE.getKey(), args);
   }

   /**
    * Description goes here.
    *
    * @param localDataSourceService the local data source service
    */
   private void ensureLocalDataSourceServiceNotNullOrException(
         LocalDataSourceService localDataSourceService) {
      if (localDataSourceService == null) {
         throw new OpenWisException("The localDataSource doesn't exist into the JNDI definition.");
      }
      return;
   }

   /**
    * Monitor extraction.
    *
    * @param metadataURN the metadata urn
    * @param processedRequestId the processed request id
    * @param localDataSource the local data source
    * @return the message status
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.service.ExtractService#monitorExtraction(java.lang.String, java.lang.Long, java.lang.String)
    */
   @Override
   @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
   public MessageStatus monitorExtraction(String metadataURN, Long processedRequestId,
         String localDataSource) {
      MessageStatus messageStatus;
      try {
         String localDS = localDataSource;
         if ((localDataSource == null) || (localDataSource.length() == 0)) {
            logger.warn(
                  "Local data source Id not present for metadataUrn {}. Using the default one.",
                  metadataURN);
            Set<String> keys = getLocalDataSourceServiceMap().keySet();
            Iterator<String> it = keys.iterator();
            localDS = it.next();
         }

         logger.info("Monitor extraction LocalDataSourceService for processed request id: {}",
               processedRequestId);
         LocalDataSourceService localDataSourceService = getLocalDataSourceServiceMap()
               .get(localDS);
         ensureLocalDataSourceServiceNotNullOrException(localDataSourceService);

         LocalDataSource localDataSourcePort = localDataSourceService.getLocalDataSourcePort();
         messageStatus = transformMessageStatus(localDataSourcePort
               .monitorExtraction(processedRequestId.longValue()));
         logger.info("Retrieve the message status {} for processed request id: {}",
               messageStatus.getStatus(), processedRequestId);
      } catch (Throwable t) {
         logger.error("Error in local datasource extraction", t);
         messageStatus = new MessageStatus();
         messageStatus.setMessage(t.getMessage());
         messageStatus.setStatus(Status.ERROR);

         raiseAlarm(localDataSource, t);
      }
      return messageStatus;
   }

   /**
    * Description goes here.
    *
    * @param messageStatusReceive the message status receive
    * @return the message status
    */
   private MessageStatus transformMessageStatus(MonitorStatus messageStatusReceive) {
      MessageStatus messageStatus = new MessageStatus();
      messageStatus.setMessage(messageStatusReceive.getMessage());
      messageStatus.setStatus(Status.fromValue(messageStatusReceive.getStatus().value()));
      return messageStatus;
   }

}
