/**
 *
 */
package eu.akka.openwis.dataservice.common.domain;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For information to use a hsqldb physical :<p>
 *
 * Create a server.properties file into jboss AS:
 * <code>$JBOSS_HOME/server/default/data/hypersonic</code></p>
 *
 * Add into the server.properties file this elements:
 * <ul>
 *  <li><code>server.database.0 file:./ jpatest</code></li>
 *  <li><code>server.dbname.0 jpatest</code></li>
 *  <li><code>server.database.1 file:./localDB</code></li>
 *  <li><code>server.dbname.1 localDB</code></li>
 *  <li><code>server.database.2 file:./default</code></li>
 *  <li><code>server.dbname.2 default</code></li>
 * </ul>
 *
 * This file allow hsqldb to create 3 database:
 * <ul>
 *  <li><code>jpatest</code>: our test database</li>
 *  <li><code>localDB</code>: database used by JBoss for web app </li>
 *  <li><code>default</code>: the default database which may be accessed by TCPIP</li>
 * </ul>
 *
 * Run HsqlDbServer:
 * <code>cd $JBOSS_HOME\jboss-as\server\default\data\hypersonic java -cp ..\..\..\..\common\lib\hsqldb.jar org.hsqldb.Server</code>
 *
 * To stop hsqldb server : <code>CTRL-C</code>
 *
 * @author n.guerrier
 *
 */
public abstract class AbstractTestCase extends DatabaseTestCase {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(AbstractTestCase.class);

   /** The em. */
   protected static EntityManager em;

   /** The emf. */
   protected static EntityManagerFactory emf;

   // XXX hacking classloader to load the testing persistence.xml file
   // @see http://stackoverflow.com/questions/385532/how-to-configure-jpa-for-testing-in-maven
   // @see http://www.touilleur-express.fr/2010/06/17/jpa-et-maven-gerer-2-persistence-xml-distincts/
   static {
      final Thread currentThread = Thread.currentThread();
      final ClassLoader saveClassLoader = currentThread.getContextClassLoader();
      ClassLoaderProxy clProxy = new ClassLoaderProxy(saveClassLoader);
      currentThread.setContextClassLoader(clProxy);
   }

   /**
    * Gets the connection.
    *
    * @return the connection
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.dbunit.DatabaseTestCase#getConnection()
    */
   @Override
   protected IDatabaseConnection getConnection() throws Exception {
      try {
         emf = Persistence.createEntityManagerFactory("sample");
         em = emf.createEntityManager();
         Class<?> driverClass = Class.forName("org.hsqldb.jdbcDriver");
         driverClass.getClass();
         Connection jdbcConnection = DriverManager.getConnection("jdbc:hsqldb:mem", "sa", "");
         return new DatabaseConnection(jdbcConnection);
      } catch (Exception e) {
         logger.error(e.getMessage(), e.getCause());
         throw e;
      }
   }

   /**
    * Gets the data set.
    *
    * @return the data set
    * @throws Exception the exception
    * {@inheritDoc}
    * @see org.dbunit.DatabaseTestCase#getDataSet()
    */
   @Override
   protected IDataSet getDataSet() throws Exception {
      return new FlatXmlDataSet(getClass().getResourceAsStream(getRelativeDataSet()));
   }

   /**
    * Description goes here.
    *
    * @return the relative data set
    */
   public abstract String getRelativeDataSet();

   /**
    * {@inheritDoc}
    * @see org.dbunit.DatabaseTestCase#tearDown()
    */
   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      em.clear();
   }

}
