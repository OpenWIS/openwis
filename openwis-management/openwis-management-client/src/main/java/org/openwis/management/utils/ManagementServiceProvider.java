/**
 *
 */
package org.openwis.management.utils;

import javax.naming.NamingException;

import org.openwis.management.ManagementServiceBeans;
import org.openwis.management.monitoring.IngestedDataStatistics;

/**
 * Provides access to the Remote management EJBs.
 */
public final class ManagementServiceProvider {
	
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
      return ManagementServiceProvider.getInstance().getIngestedDataStatistics();
   }

}
