package org.openwis.management;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
 * Implementation of ManagementServiceBeans which lookup the bean using the JNDI name.
 */
public class JndiManagementServiceBeans extends ManagementServiceBeans {

   /**
    * JNDI name prefix for referencing beans deployed in the same module.  Used mainly for Arquillian tests.
    */
   public static final String LOCAL_JNDI_PREFIX = "java:module/";

   /**
    * JNDI name prefix for referencing beans from openwis-management-service.ear artifact.
    */
   public static final String REMOTE_JNDI_PREFIX = "ejb:openwis-management-service/openwis-management-service-ejb/";

   private final String jndiNamePrefix;
   private final InitialContext initialContext;

   private JndiManagementServiceBeans(String jndiNamePrefix) throws NamingException {
      this.initialContext = new InitialContext();
      this.jndiNamePrefix = jndiNamePrefix;
   }

   /**
    * Returns the remote interface of the ControlService.
    * 
    * @throws NamingException
    */
   @Override
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
   @Override
   public ConfigService getConfigService() throws NamingException {
      return getRemoteBean("ConfigService", ConfigService.class);
   }

   /**
    * Returns the remote interface of the AlertService.
    * 
    * @throws NamingException
    */
   @Override
   public AlertService getAlertService() throws NamingException {
      return getRemoteBean("AlertService", AlertService.class);
   }
   
   @Override
   public ReplicatedDataStatistics getReplicatedDataStatistics() throws NamingException {
      return getRemoteBean("ReplicatedDataStatistics", ReplicatedDataStatistics.class);
   }
   
   @Override
   public DisseminatedDataStatistics getDisseminatedDataStatistics() throws NamingException {
      return getRemoteBean("DisseminatedDataStatistics", DisseminatedDataStatistics.class);
   }
   
   @Override
   public ExchangedDataStatistics getExchangedDataStatistics() throws NamingException {
      return getRemoteBean("ExchangedDataStatistics", ExchangedDataStatistics.class);
   }
   
   /**
    * Returns the ingested data statistics bean.
    * 
    * @throws NamingException
    */
   @Override
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
            jndiNamePrefix, beanName,
            remoteInterfaceClass.getName());
      return remoteInterfaceClass.cast(initialContext.lookup(jndiName));
   }
   
   /**
    * Create an instance of {@link JndiManagementServiceBeans}.  This will look for a resource with the same name
    * as the class to determine whether to use the local client or the remote client.
    * <p>
    * @return
    */
   public static JndiManagementServiceBeans createInstance() throws NamingException {
      InputStream classConfig = JndiManagementServiceBeans.class.getResourceAsStream("JndiManagementServiceBeans.properties");
      
      if (classConfig != null) {
         try {
            try {
               Properties props = new Properties();
               props.load(classConfig);
               
               if (props.getProperty("prefix", "remote").equals("local")) {
                  return new JndiManagementServiceBeans(LOCAL_JNDI_PREFIX);               
               }
            } finally {
               classConfig.close();
            }
         } catch (IOException e) {
            // Cannot load the properties file.  Simply default to Remote
         }
      }
      
      return new JndiManagementServiceBeans(REMOTE_JNDI_PREFIX);
   }
}
