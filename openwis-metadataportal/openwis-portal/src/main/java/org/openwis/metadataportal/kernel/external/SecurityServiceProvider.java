/**
 * 
 */
package org.openwis.metadataportal.kernel.external;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.securityservice.DisseminationParametersService;
import org.openwis.securityservice.DisseminationParametersService_Service;
import org.openwis.securityservice.GroupManagementService;
import org.openwis.securityservice.GroupManagementService_Service;
import org.openwis.securityservice.MonitoringService;
import org.openwis.securityservice.MonitoringService_Service;
import org.openwis.securityservice.UserManagementService;
import org.openwis.securityservice.UserManagementService_Service;

/**
 * The security service provider. <P>
 * This class is a helper to retrieve EJB interfaces for Security Service. <P>
 */
public final class SecurityServiceProvider {

   /**
    * The user Management Service
    */
   private static UserManagementService userManagementService;

   /**
    * The group management service.
    */
   private static GroupManagementService groupManagementService;

   /**
    * The dissemination parameter service.
    */
   private static DisseminationParametersService disseminationParametersService;

   /**
    * The monitoring service.
    */
   private static MonitoringService monitoringService;

   /**
    * Default constructor.
    * Builds a SecurityServiceProvider.
    */
   private SecurityServiceProvider() {
      super();
   }

   /**
    * Gets the User Management Service.
    * @return the User Management Service.
    */
   public static UserManagementService getUserManagementService() {
      try {
         if (userManagementService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.SECURITYSERVICE_USERMANAGEMENT_WSDL);
            UserManagementService_Service service = new UserManagementService_Service(new URL(wsdl));
            userManagementService = service.getUserManagementServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) userManagementService,
                  wsdl);
         }
         return userManagementService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Gets the Group Management Service.
    * @return the Group Management Service.
    */
   public static GroupManagementService getGroupManagementService() {
      try {
         if (groupManagementService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.SECURITYSERVICE_GROUPMANAGEMENT_WSDL);
            GroupManagementService_Service service = new GroupManagementService_Service(new URL(
                  wsdl));
            groupManagementService = service.getGroupManagementServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) groupManagementService,
                  wsdl);
         }
         return groupManagementService;
      } catch (MalformedURLException e) {
         return null;
      }

   }

   /**
    * Gets the Dissemination Parameters Management Service.
    * @return the Dissemination Parameters Management Service.
    */
   public static DisseminationParametersService getDisseminationParametersManagementService() {
      try {
         if (disseminationParametersService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.SECURITYSERVICE_DISS_PARAM_MANAGEMENT_WSDL);
            DisseminationParametersService_Service service = new DisseminationParametersService_Service(
                  new URL(wsdl));
            disseminationParametersService = service.getDisseminationParametersServicePort();
            ServiceProviderUtil.enforceServiceEndpoint(
                  (BindingProvider) disseminationParametersService, wsdl);
         }
         return disseminationParametersService;
      } catch (MalformedURLException e) {
         return null;
      }
   }
   
   /**
    * Gets the Dissemination Parameters Management Service.
    * @return the Dissemination Parameters Management Service.
    */
   public static MonitoringService getMonitoringService() {
      try {
         if (monitoringService == null) {
            String wsdl = OpenwisMetadataPortalConfig
                  .getString(ConfigurationConstants.SECURITYSERVICE_MONITORING_SERVICE_WSDL);
            MonitoringService_Service service = new MonitoringService_Service(
                  new URL(wsdl));
            monitoringService = service.getMonitoringServicePort();
            ServiceProviderUtil.enforceServiceEndpoint(
                  (BindingProvider) monitoringService, wsdl);
         }
         return monitoringService;
      } catch (MalformedURLException e) {
         return null;
      }

   }

   // --------------------------------------------------------------------------------
}
