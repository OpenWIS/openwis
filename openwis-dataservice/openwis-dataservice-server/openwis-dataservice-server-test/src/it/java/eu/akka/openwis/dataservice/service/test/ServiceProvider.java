/**
 * 
 */
package eu.akka.openwis.dataservice.service.test;

/**
 * Pattern ServiceLocator, peut être optimisé avec
 * les génériques. <P>
 * Explanation goes here. <P>
 * 
 */

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openwis.dataservice.common.service.CacheExtraService;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.service.RequestService;
import org.openwis.dataservice.common.service.SubscriptionService;

public class ServiceProvider {

   /**
    * prefix pour lookup
    */
   private static final String PREFIX = "openwis-dataservice/";

   /** context. */
   static Context context;

   /** Constante REMOTE. */
   static final String REMOTE = "/remote";

   static Hashtable<Object, Object> env = new Hashtable<Object, Object>();

   private static boolean isPrefixed = true;

   private static String getPrefix() {
      if (isPrefixed) {
         return PREFIX;
      }
      return "";
   }

   public static void setEnablePrefix(boolean hasPrefix) {
      isPrefixed = hasPrefix;
   }

   private static Context getInitialContext() throws NamingException {
      setInitialContext();
      return context;
   }

   public static Context getDefaultContext() throws NamingException {
      //init parametres
      Properties prop = new Properties();
      prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
      prop.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces.NamingContextFactory");
      prop.put(Context.PROVIDER_URL, "jnp://localhost:1199");
      initEnv(prop);
      return getInitialContext();
   }

   /**
    * 
    * Description goes here.
    * @return 
    */
   public static RequestService getRequestSrv() {
      try {
         return (RequestService) getInitialContext().lookup(
               ServiceProvider.getPrefix() + "RequestService" + REMOTE);
      } catch (NamingException exception) {
         exception.printStackTrace();
         throw new RuntimeException("Impossible to retrieve service: " + RequestService.class);
      }
   }

   public static ProductMetadataService getMetadataSrv() {
      try {
         return (ProductMetadataService) getInitialContext().lookup(
               ServiceProvider.getPrefix() + "ProductMetadataService" + REMOTE);
      } catch (NamingException exception) {
         exception.printStackTrace();
         throw new RuntimeException("Impossible to retrieve service: "
               + ProductMetadataService.class);
      }
   }

   /**
    * 
    * Description goes here.
    * @return
    */
   public static SubscriptionService getSubscriptionSrv() {
      try {
         return (SubscriptionService) getInitialContext().lookup(
               ServiceProvider.getPrefix() + "SubscriptionService" + REMOTE);
      } catch (NamingException exception) {
         exception.printStackTrace();
         throw new RuntimeException("Impossible to retrieve service: " + SubscriptionService.class);
      }
   }

   /**
    * 
    * Description goes here.
    * @return
    */
   public static CacheExtraService getCacheSrv() {
      try {
         return (CacheExtraService) getInitialContext().lookup(
               "openwis-dataservice-cache/ExtractFromCache/remote");
      } catch (NamingException exception) {
         exception.printStackTrace();
         throw new RuntimeException("Impossible to retrieve service: " + SubscriptionService.class);
      }
   }

   public static void setInitialContext() {
      try {
         context = new InitialContext(env);
      } catch (NamingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void initEnv(Hashtable<Object, Object> envcontext) {
      env = envcontext;
   }

   public static String getREMOTE() {
      return REMOTE;
   }

}
