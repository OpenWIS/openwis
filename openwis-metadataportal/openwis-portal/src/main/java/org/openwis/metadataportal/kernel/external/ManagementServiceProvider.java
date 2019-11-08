/**
 *
 */
package org.openwis.metadataportal.kernel.external;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

import jeeves.utils.Log;

import org.openwis.management.alert.AlertService;
import org.openwis.management.alert.AlertService_Service;
import org.openwis.management.control.ControlService;
import org.openwis.management.control.ControlService_Service;
import org.openwis.management.monitoring.DisseminatedDataStatistics;
import org.openwis.management.monitoring.DisseminatedDataStatistics_Service;
import org.openwis.management.monitoring.ExchangedDataStatistics;
import org.openwis.management.monitoring.ExchangedDataStatistics_Service;
import org.openwis.management.monitoring.IngestedDataStatistics;
import org.openwis.management.monitoring.IngestedDataStatistics_Service;
import org.openwis.management.monitoring.ReplicatedDataStatistics;
import org.openwis.management.monitoring.ReplicatedDataStatistics_Service;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

/**
 * Provides access to the remote WS providing support for the management of the OpenWIS system.
 * <p>
 * Explanation goes here.
 */
public class ManagementServiceProvider {

   private static String LOGMODULE = "openwis.service.provider";
   
   // Management services
   private static AlertService alertService;
   private static ControlService controlService;

   // Monitoring Services
   private static IngestedDataStatistics ingestedDataStatistics;
   private static ExchangedDataStatistics exchangedDataStatistics;
   private static ReplicatedDataStatistics replicatedDataStatistics;
   private static DisseminatedDataStatistics disseminatedDataStatistics;

   /**
    * Default constructor.
    * Builds a ManagementServiceProvider.
    */
   private ManagementServiceProvider() {
   }

   /**
    * Returns the shared instance of the {@code AlertService}.
    *
    * @return the {@code AlertService}.
    */
   public static AlertService getAlertService() {
      try {
         if (alertService == null) {
            String wsdl = getWsdlResource(ConfigurationConstants.MANAGEMENT_ALERTSERVICE_WSDL);
            AlertService_Service service = new AlertService_Service(new URL(wsdl));
            alertService = service.getAlertServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) alertService, wsdl);
         }
         return alertService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Returns the shared instance of the {@code ControlService}.
    *
    * @return the {@code ControlService}.
    */
   public static ControlService getControlService() {
      try {
         if (controlService == null) {
            // String wsdl = "http://172.17.143.31:8080/openwis-management-service-openwis-management-service-ejb-1.0-SNAPSHOT/ControlService?wsdl";
            String wsdl = getWsdlResource(ConfigurationConstants.MANAGEMENT_CONTROLSERVICE_WSDL);
            ControlService_Service service = new ControlService_Service(new URL(wsdl));
            controlService = service.getControlServicePort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) controlService, wsdl);
         }
         return controlService;
      } catch (MalformedURLException e) {
         return null;
      }
   }

   // -------------------------------------------------------------------------
   // Monitoring Services
   // -------------------------------------------------------------------------

   /**
    * Returns the shared instance of the {@code DisseminatedDataStatistics}.
    * 
    * @return the {@code DisseminatedDataStatistics}.
    */
   public static DisseminatedDataStatistics getDisseminatedDataStatistics() {
      try {
         if (disseminatedDataStatistics == null) {
            String wsdl = getWsdlResource(ConfigurationConstants.MANAGEMENT_DISSEMINATEDDATA_STATISTICS_WSDL);
            DisseminatedDataStatistics_Service service = new DisseminatedDataStatistics_Service(new URL(wsdl));
            disseminatedDataStatistics = service.getDisseminatedDataStatisticsPort();
            ServiceProviderUtil.enforceServiceEndpoint(
                  (BindingProvider) disseminatedDataStatistics, wsdl);
         }
         return disseminatedDataStatistics;
      }
      catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Returns the shared instance of the {@code ExchangedDataStatistics}.
    * 
    * @return the {@code ExchangedDataStatistics}.
    */
   public static ExchangedDataStatistics getExchangedDataStatistics() {
      try {
         if (exchangedDataStatistics == null) {
            String wsdl = getWsdlResource(ConfigurationConstants.MANAGEMENT_EXCHANGEDDATA_STATISTICS_WSDL);
            ExchangedDataStatistics_Service service = new ExchangedDataStatistics_Service(new URL(wsdl));
            exchangedDataStatistics = service.getExchangedDataStatisticsPort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) exchangedDataStatistics,
                  wsdl);
         }
         return exchangedDataStatistics;
      }
      catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Returns the shared instance of the {@code ReplicatedDataStatistics}.
    * 
    * @return the {@code ReplicatedDataStatistics}.
    */
   public static ReplicatedDataStatistics getReplicatedDataStatistics() {
      try {
         if (replicatedDataStatistics == null) {
            String wsdl = getWsdlResource(ConfigurationConstants.MANAGEMENT_REPLICATEDDATA_STATISTICS_WSDL);
            ReplicatedDataStatistics_Service service = new ReplicatedDataStatistics_Service(new URL(wsdl));
            replicatedDataStatistics = service.getReplicatedDataStatisticsPort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) replicatedDataStatistics,
                  wsdl);
         }
         return replicatedDataStatistics;
      }
      catch (MalformedURLException e) {
         return null;
      }
   }

   /**
    * Returns the shared instance of the {@code IngestedDataStatistics}.
    * 
    * @return the {@code IngestedDataStatistics}.
    */
   public static IngestedDataStatistics getIngestedDataStatistics() {
      try {
         if (ingestedDataStatistics == null) {
            String wsdl = getWsdlResource(ConfigurationConstants.MANAGEMENT_INGESTEDDATA_STATISTICS_WSDL);
            IngestedDataStatistics_Service service = new IngestedDataStatistics_Service(new URL(
                  wsdl));
            ingestedDataStatistics = service.getIngestedDataStatisticsPort();
            ServiceProviderUtil.enforceServiceEndpoint((BindingProvider) ingestedDataStatistics,
                  wsdl);
         }
         return ingestedDataStatistics;
      } catch (MalformedURLException e) {
         return null;
      }
   }
   
   private static String getWsdlResource(final String resourceID) {
      String wsdl = OpenwisMetadataPortalConfig.getString(resourceID);
      Log.info(LOGMODULE, "WSDL for " + resourceID + " is: " + wsdl);
      return wsdl;
   }

}
