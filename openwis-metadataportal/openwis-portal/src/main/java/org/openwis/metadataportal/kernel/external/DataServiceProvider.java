package org.openwis.metadataportal.kernel.external;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import org.openwis.dataservice.BlacklistService;
import org.openwis.dataservice.BlacklistService_Service;
import org.openwis.dataservice.ProcessedRequestService;
import org.openwis.dataservice.ProcessedRequestService_Service;
import org.openwis.dataservice.ProductMetadataService;
import org.openwis.dataservice.ProductMetadataService_Service;
import org.openwis.dataservice.RequestService;
import org.openwis.dataservice.RequestService_Service;
import org.openwis.dataservice.SubscriptionService;
import org.openwis.dataservice.SubscriptionService_Service;
import org.openwis.dataservice.UserAlarmManagerWebService;
import org.openwis.dataservice.UserAlarmManagerWebService_Service;
import org.openwis.dataservice.cache.CacheIndexImplService;
import org.openwis.dataservice.cache.CacheIndexWebService;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

/**
 * The data service provider. <P>
 * This class is a helper to retrieve EJB interfaces for Data Service. <P>
 */
public final class DataServiceProvider {

   private static RequestService requestService;

   private static ProcessedRequestService processedRequestService;

   private static SubscriptionService subscriptionService;

   private static ProductMetadataService productMetadataService;

   private static CacheIndexWebService cacheIndexService;

   private static UserAlarmManagerWebService userAlarmService;

   private static BlacklistService blacklistService;

   /**
    * Default constructor.
    * Builds a DataServiceProvider.
    */
   private DataServiceProvider() {
      super();
   }

   /**
    * Gets the request service.
    * @return the request service.
    */
   public static RequestService getRequestService() {
      try {
         if (requestService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.DATASERVICE_REQUESTSERVICE_WSDL);
            RequestService_Service service = new RequestService_Service(new URL(wsdl));
            requestService = service.getRequestServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) requestService, wsdl);
         }
         return requestService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Gets the subscription service.
    * @return the subscription service.
    */
   public static SubscriptionService getSubscriptionService() {
      try {
         if (subscriptionService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.DATASERVICE_SUBSCRIPTIONSERVICE_WSDL);
            SubscriptionService_Service service = new SubscriptionService_Service(new URL(wsdl));
            subscriptionService = service.getSubscriptionServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) subscriptionService, wsdl);
         }
         return subscriptionService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Gets the product metadata service.
    * @return the product metadata service.
    */
   public static ProductMetadataService getProductMetadataService() {
      try {
         if (productMetadataService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.DATASERVICE_PRODUCTMETADATASERVICE_WSDL);
            ProductMetadataService_Service service = new ProductMetadataService_Service(new URL(
                  wsdl));
            productMetadataService = service.getProductMetadataServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) productMetadataService,
                  wsdl);
         }
         return productMetadataService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Gets the processed request service.
    * @return the processed service.
    */
   public static ProcessedRequestService getProcessedRequestService() {
      try {
         if (processedRequestService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.DATASERVICE_PROCESSEDREQUESTSERVICE_WSDL);
            ProcessedRequestService_Service service = new ProcessedRequestService_Service(new URL(
                  wsdl));
            processedRequestService = service.getProcessedRequestServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) processedRequestService,
                  wsdl);
         }
         return processedRequestService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Gets the cache index service.
    * @return the cache index service.
    */
   public static CacheIndexWebService getCacheIndexService() {
      try {
         if (cacheIndexService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.DATASERVICE_CACHEINDEXSERVICE_WSDL);
            CacheIndexImplService service = new CacheIndexImplService(new URL(wsdl));
            cacheIndexService = service.getCacheIndexWebServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) cacheIndexService, wsdl);
         }
         return cacheIndexService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
     * Gets the blacklist service.
     * @return the blacklist service.
     */
   public static BlacklistService getBlacklistService() {
      try {
         if (blacklistService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.DATASERVICE_BLACKLISTSERVICE_WSDL);
            BlacklistService_Service service = new BlacklistService_Service(new URL(wsdl));
            blacklistService = service.getBlacklistServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) blacklistService, wsdl);
         }
         return blacklistService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   public static UserAlarmManagerWebService getUserAlarmManagerService() {
      try {
         if (userAlarmService == null) {
            String wsdl = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.USER_ALARM_SERVICE_WSDL);
            UserAlarmManagerWebService_Service service = new UserAlarmManagerWebService_Service(new URL(wsdl));
            userAlarmService = service.getUserAlarmManagerWebServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) userAlarmService, wsdl);
         }
         return userAlarmService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   // --------------------------------------------------------------------------------

}
