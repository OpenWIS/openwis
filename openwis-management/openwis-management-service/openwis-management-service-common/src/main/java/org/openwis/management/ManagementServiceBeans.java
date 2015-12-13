package org.openwis.management;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.management.service.AlertService;
import org.openwis.management.service.ConfigService;
import org.openwis.management.service.ControlService;
import org.openwis.management.service.DisseminatedDataStatistics;
import org.openwis.management.service.IngestedDataStatistics;
import org.openwis.management.service.ReplicatedDataStatistics;

/**
 * Facade to the remote management service beans.
 */
public class ManagementServiceBeans {

   private static final String MANAGEMENT_SERVICE_JNDI_PREFIX = "ejb:openwis-management-service/openwis-management-service-ejb/";

   private final InitialContext initialContext;

   private ManagementServiceBeans() throws NamingException {
      this.initialContext = new InitialContext();
   }

   public static ManagementServiceBeans getInstance() {
      try {
         return new ManagementServiceBeans();
      } catch (NamingException e) {
         throw new RuntimeException("Failed to get initial context", e);
      }
   }

   /**
    * Returns the remote interface of the ControlService.
    * 
    * @throws NamingException
    */
   public ControlService getControlService() throws NamingException {
      return getRemoteBean("ControlService", ControlService.class);
   }
   
   /**
    * Returns the remote interface to the ConfigService.
    * 
    * @return
    *       The ConfigService
    * @throws NamingException
    */
   public ConfigService getConfigService() throws NamingException {
      return getRemoteBean("ConfigService", ConfigService.class);
   }

   /**
    * Returns the remote interface of the AlertService.
    * 
    * @throws NamingException
    */
   public AlertService getAlertService() throws NamingException {
      return getRemoteBean("AlertService", AlertService.class);
   }
   
   public ReplicatedDataStatistics getReplicatedDataStatistics() throws NamingException {
      return getRemoteBean("ReplicatedDataStatistics", ReplicatedDataStatistics.class);
   }
   
   public DisseminatedDataStatistics getDisseminatedDataStatistics() throws NamingException {
      return getRemoteBean("DisseminatedDataStatistics", DisseminatedDataStatistics.class);
   }
   
   /**
    * Returns the ingested data statistics bean.
    * 
    * @throws NamingException
    */
   public IngestedDataStatistics getIngestedDataStatistics()
         throws NamingException {
      return getRemoteBean("IngestedDataStatistics",
            IngestedDataStatistics.class);
   }

   /**
    * Provides access to a remote bean. This uses the standard remote EJB naming
    * protocol used by JBoss AS 7.1 See:
    * https://docs.jboss.org/author/display/AS71
    * /EJB+invocations+from+a+remote+client+using+JNDI
    * 
    * @param class1
    * @return
    * @throws NamingException
    */
   private <T> T getRemoteBean(String beanName, Class<T> remoteInterfaceClass)
         throws NamingException {
      String jndiName = String.format("%s/%s!%s",
            MANAGEMENT_SERVICE_JNDI_PREFIX, beanName,
            remoteInterfaceClass.getName());
      return remoteInterfaceClass.cast(initialContext.lookup(jndiName));
   }
}
