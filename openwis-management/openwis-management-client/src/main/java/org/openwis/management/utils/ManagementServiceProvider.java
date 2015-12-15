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
	
   // Management services
   private static AlertService alertService;
   private static ControlService controlService;

   // Monitoring Services
   private static IngestedDataStatistics ingestedDataStatistics;
   private static ExchangedDataStatistics exchangedDataStatistics;
   private static ReplicatedDataStatistics replicatedDataStatistics;
   private static DisseminatedDataStatistics disseminatedDataStatistics;
   
   private static ManagementServiceProvider instance;
   
   private final ManagementServiceUrls managementServiceUrls = new JndiManagementServiceUrls();
   
   private ManagementServiceProvider() {
      
   }
   
   public static ManagementServiceProvider getInstance() {
      if (instance == null) {
         synchronized(ManagementServiceProvider.class) {
            if (instance == null) {
               instance = new ManagementServiceProvider();
            }
         }
      }
      
      return instance;
   }
   

   /**
    * Returns the shared instance of the {@code AlertService}.
    *
    * @return the {@code AlertService}.
    */
   public AlertService getAlertService() {
      try {
         if (alertService == null) {
            String wsdl = managementServiceUrls.getAlertServiceWsdl();
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
   public ControlService getControlService() {
      try {
         if (controlService == null) {
            String wsdl = managementServiceUrls.getControlServiceWsdl();
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
   public DisseminatedDataStatistics getDisseminatedDataStatistics() {
      try {
         if (disseminatedDataStatistics == null) {
            String wsdl = managementServiceUrls.getDisseminatedDataStatisticsWsdl();
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
   public ExchangedDataStatistics getExchangedDataStatistics() {
      try {
         if (exchangedDataStatistics == null) {
            String wsdl = managementServiceUrls.getExchangedDataStatisticsWsdl();
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
   public ReplicatedDataStatistics getReplicatedDataStatistics() {
      try {
         if (replicatedDataStatistics == null) {
            String wsdl = managementServiceUrls.getReplicatedDataStatisticsWsdl();
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
   public IngestedDataStatistics getIngestedDataStatistics() {
      try {
         if (ingestedDataStatistics == null) {
            String wsdl = managementServiceUrls.getIgestedDataStatisticsWsdl();
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
