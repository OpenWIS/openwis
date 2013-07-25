/**
 *
 */
package org.openwis.management.utils;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.ws.BindingProvider;

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

/**
 * Provides access to the remote WS providing support for the management of the OpenWIS system.
 * <p>
 * Explanation goes here.
 */
public final class ManagementServiceProvider {
	
   // -------------------------------------------------------------------------
   // Management Client Settings
   // -------------------------------------------------------------------------
   private static final String MANAGEMENT_ALERTSERVICE_WSDL = "openwis.management.alertservice.wsdl";
   private static final String MANAGEMENT_CONTROLSERVICE_WSDL = "openwis.management.controlservice.wsdl";
   private static final String MANAGEMENT_DISSEMINATEDDATA_STATISTICS_WSDL = "openwis.management.disseminateddatastatistics.wsdl";
   private static final String MANAGEMENT_EXCHANGEDDATA_STATISTICS_WSDL = "openwis.management.exchangeddatastatistics.wsdl";
   private static final String MANAGEMENT_REPLICATEDDATA_STATISTICS_WSDL = "openwis.management.replicateddatastatistics.wsdl";
   private static final String MANAGEMENT_INGESTEDDATA_STATISTICS_WSDL = "openwis.management.ingesteddatastatistics.wsdl";

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
            String wsdl = JndiUtils.getString(MANAGEMENT_ALERTSERVICE_WSDL);
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
            String wsdl = JndiUtils.getString(MANAGEMENT_CONTROLSERVICE_WSDL);
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
            String wsdl = JndiUtils.getString(MANAGEMENT_DISSEMINATEDDATA_STATISTICS_WSDL);
            DisseminatedDataStatistics_Service service = new DisseminatedDataStatistics_Service(
                  new URL(wsdl));
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
            String wsdl = JndiUtils.getString(MANAGEMENT_EXCHANGEDDATA_STATISTICS_WSDL);
            ExchangedDataStatistics_Service service = new ExchangedDataStatistics_Service(new URL(
                  wsdl));
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
            String wsdl = JndiUtils.getString(MANAGEMENT_REPLICATEDDATA_STATISTICS_WSDL);
            ReplicatedDataStatistics_Service service = new ReplicatedDataStatistics_Service(
                  new URL(wsdl));
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
            String wsdl = JndiUtils.getString(MANAGEMENT_INGESTEDDATA_STATISTICS_WSDL);
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

}
