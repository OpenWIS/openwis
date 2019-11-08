package org.openwis.management;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.management.service.AlertService;
import org.openwis.management.service.ConfigService;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.DisseminatedDataStatistics;
import org.openwis.management.service.ExchangedDataStatistics;
import org.openwis.management.service.IngestedDataStatistics;
import org.openwis.management.service.ReplicatedDataStatistics;

/**
 * Facade to the remote management service beans.
 */
public abstract class ManagementServiceBeans {
   
   private static ManagementServiceBeans instance;

   public static ManagementServiceBeans getInstance() {
      if (instance == null) {
         synchronized(ManagementServiceBeans.class) {
            if (instance == null) {
               try {
                  instance = JndiManagementServiceBeans.createInstance();
               } catch (NamingException e) {
                  throw new RuntimeException("Failed to get initial context", e);
               }
            }
         }
      }
      
      return instance;
   }
   
   /**
    * Install the given instance as the singleton value.  Only used if it is necessary to customize
    * the process of looking up management service beans.
    * 
    * !!HACK!! This is a hack and will eventually be replaced with something a little more robust.
    * 
    * @param newInstance
    */
//   public static void setInstance(ManagementServiceBeans newInstance) {
//      synchronized (ManagementServiceBeans.class) {
//         instance = newInstance;
//      }
//   }

   /**
    * Returns the remote interface of the ControlService.
    * 
    * @throws NamingException
    */
   public abstract ControlService getControlService() throws NamingException;
   
   /**
    * Returns the remote interface to the ConfigService.
    * 
    * @return
    *       The ConfigService
    * @throws NamingException
    */
   public abstract ConfigService getConfigService() throws NamingException;

   /**
    * Returns the remote interface of the AlertService.
    * 
    * @throws NamingException
    */
   public abstract AlertService getAlertService() throws NamingException;
   
   public abstract ReplicatedDataStatistics getReplicatedDataStatistics() throws NamingException;
   
   public abstract DisseminatedDataStatistics getDisseminatedDataStatistics() throws NamingException;
   
   public abstract ExchangedDataStatistics getExchangedDataStatistics() throws NamingException;
   
   /**
    * Returns the ingested data statistics bean.
    * 
    * @throws NamingException
    */
   public abstract IngestedDataStatistics getIngestedDataStatistics() throws NamingException;
}
