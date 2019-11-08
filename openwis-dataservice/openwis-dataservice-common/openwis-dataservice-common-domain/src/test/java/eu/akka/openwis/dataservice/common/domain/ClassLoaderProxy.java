package eu.akka.openwis.dataservice.common.domain;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ClassLoaderProxy. <P>
 * This proxy class loader is used by test to load the valid persitence.xml. <P>
 */
public class ClassLoaderProxy extends ClassLoader {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(ClassLoaderProxy.class);

   /**
    * Instantiates a new class loader proxy.
    *
    * @param parent the parent
    */
   public ClassLoaderProxy(final ClassLoader parent) {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.lang.ClassLoader#getResources(java.lang.String)
    */
   @Override
   public Enumeration<URL> getResources(final String name) throws IOException {
      Enumeration<URL> result;
      logger.info("lookup for: {}", name);
      if (!"META-INF/persistence.xml".equals(name)) {
         result = super.getResources(name);
      } else {
         logger.info("Redirecting persistence.xml to test-persistence.xml");
         result = super.getResources("META-INF/test-persistence.xml");
      }
      return result;
   }
}