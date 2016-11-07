/**
 *
 */
package org.openwis.management.utils;

import javax.naming.NamingException;

import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.monitoring.IngestedDataStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to the Remote management EJBs.
 */
public final class ManagementServiceProvider {
   
   private static final Logger log = LoggerFactory.getLogger(ManagementServiceProvider.class);
	
   private static ManagementServiceProvider instance;
   
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
   public org.openwis.management.service.AlertService getAlertService() {
      try {
         log.info("Getting AlertService");
         return ManagementServiceBeans.getInstance().getAlertService();
      } catch (NamingException e) {
         throw new RuntimeException("Cannot get AlertService", e);
      }
   }

   /**
    * Returns the shared instance of the {@code ControlService}.
    *
    * @return the {@code ControlService}.
    */
   public org.openwis.management.service.ControlService getControlService() {
      try {
         log.info("Getting ControlService");
         return ManagementServiceBeans.getInstance().getControlService();
      } catch (NamingException e) {
         throw new RuntimeException("Cannot get AlertService", e);
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
   public org.openwis.management.service.DisseminatedDataStatistics getDisseminatedDataStatistics() {
      try {
         log.info("Getting DisseminatedDataStatistics");
         return ManagementServiceBeans.getInstance().getDisseminatedDataStatistics();
      } catch (NamingException e) {
         throw new RuntimeException("Cannot get AlertService", e);
      }
   }

   /**
    * Returns the shared instance of the {@code ExchangedDataStatistics}.
    * 
    * @return the {@code ExchangedDataStatistics}.
    */
   public org.openwis.management.service.ExchangedDataStatistics getExchangedDataStatistics() {
      try {
         log.info("Getting ExchangedDataStatistics");
         return ManagementServiceBeans.getInstance().getExchangedDataStatistics();
      } catch (NamingException e) {
         throw new RuntimeException("Cannot get AlertService", e);
      }
   }

   /**
    * Returns the shared instance of the {@code ReplicatedDataStatistics}.
    * 
    * @return the {@code ReplicatedDataStatistics}.
    */
   public org.openwis.management.service.ReplicatedDataStatistics getReplicatedDataStatistics() {
      try {
         log.info("Getting ReplicatedDataStatistics");
         return ManagementServiceBeans.getInstance().getReplicatedDataStatistics();
      } catch (NamingException e) {
         throw new RuntimeException("Cannot get AlertService", e);
      }
   }

   /**
    * Returns the shared instance of the {@code IngestedDataStatistics}.
    * 
    * @return the {@code IngestedDataStatistics}.
    */
   public IngestedDataStatistics getIngestedDataStatistics() {
      log.info("Getting IngestedDataStatistics");
      return ManagementServiceProvider.getInstance().getIngestedDataStatistics();
   }

}
