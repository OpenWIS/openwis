
/**
 *
 */
package org.openwis.datasource.server.jndi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.openejb.client.LocalInitialContext;
import org.apache.openejb.client.LocalInitialContextFactory;
import org.openwis.dataservice.common.util.JndiUtils;
import org.openwis.datasource.server.utils.DataServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A factory for creating TestInitialContext objects.
 */
public class TestInitialContextFactory extends LocalInitialContextFactory {

   /**
    * The Class LocalInitialContextHack. <P>
    * Explanation goes here. <P>
    */
   private final class LocalInitialContextHack extends LocalInitialContext {

      /** The ws local datasource props. */
      private final Properties wsLocalDatasourceProps;

      /** The conf cache props. */
      private final Properties confCacheProps;

      /**
       * Instantiates a new local initial context hack.
       *
       * @param env the env
       * @param factory the factory
       * @param wsLocalDatasourceProps the ws local datasource props
       * @param confCacheProps the conf cache props
       * @throws NamingException the naming exception
       */
      private LocalInitialContextHack(Hashtable env, LocalInitialContextFactory factory,
            Properties wsLocalDatasourceProps, Properties confCacheProps) throws NamingException {
         super(env, factory);
         this.wsLocalDatasourceProps = wsLocalDatasourceProps;
         this.confCacheProps = confCacheProps;
      }

      /**
       * Lookup.
       *
       * @param name the name
       * @return the object
       * @throws NamingException the naming exception
       * {@inheritDoc}
       * @see org.apache.openejb.core.ivm.naming.ContextWrapper#lookup(java.lang.String)
       */
      @Override
      public Object lookup(String name) throws NamingException {
         Object result;
         if (DataServiceConfiguration.LOCA_DATA_SOURCE_CONFIGURATION_LOCATION.equals(name)) {
            result = wsLocalDatasourceProps;
            logger.info("Retrieve  {}, value: {}",
                  DataServiceConfiguration.LOCA_DATA_SOURCE_CONFIGURATION_LOCATION, result);
         } else if (JndiUtils.JNDI_CONFIGURATION_LOCATION.equals(name)) {

            result = confCacheProps;
            logger.info("Retrieve {}, value: {}", JndiUtils.JNDI_CONFIGURATION_LOCATION, result);
         } else {
            result = super.lookup(name);
         }
         return result;
      }
   }

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(TestInitialContextFactory.class);

   /**
    * Gets the initial context.
    *
    * @param env the env
    * @return the initial context
    * @throws NamingException the naming exception
    * {@inheritDoc}
    * @see org.apache.openejb.client.LocalInitialContextFactory#getInitialContext(java.util.Hashtable)
    */
   @Override
   public Context getInitialContext(Hashtable env) throws NamingException {
      // load WS local datasource properties
      final Properties wsLocalDatasourceProps = loadProperties("/ws-localdatasources.properties");

      // Load dataservice properties
      final Properties confCacheProps = loadProperties("/conf-dataservice.properties");

      // FIXME Ugly code
      Context ctx = new LocalInitialContextHack(env, this, wsLocalDatasourceProps, confCacheProps);

      return ctx;
   }

   /**
    * Load properties.
    *
    * @param resource the resource
    * @return the properties
    */
   private Properties loadProperties(String resource) {
      Properties props = new Properties();
      InputStream in = getClass().getResourceAsStream(resource);
      try {
         props.load(in);
      } catch (IOException e) {
         logger.error("Fail to load properties :'(", e);
      }
      return props;
   }

}
