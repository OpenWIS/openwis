/**
 * 
 */
package org.openwis.harness.samples.db.localdatasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.openwis.harness.samples.common.extraction.LocalDatasourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LocalDatasourceDbUtils. <P>
 * Explanation goes here. <P>
 */
public class LocalDatasourceDbUtils extends LocalDatasourceUtils {

   /**
    * The logger.
    */
   private static Logger logger = LoggerFactory.getLogger(LocalDatasourceDbUtils.class);

   /** The Constant JDBC_PASSWORD. */
   private static final String JDBC_PASSWORD = "jdbc.password";

   /** The Constant JDBC_USER. */
   private static final String JDBC_USER = "jdbc.user";

   /** The Constant JDBC_URL. */
   private static final String JDBC_URL = "jdbc.url";

   /** The Constant JDBC_DRIVER. */
   private static final String JDBC_DRIVER = "jdbc.driver";

   /** The driver. */
   private String driver;

   /** The url. */
   private String url;

   /** The user. */
   private String user;

   /** The password. */
   private String password;

   /** The data source. */
   private BasicDataSource dataSource;

   /**
    * Instantiates a new local datasource db utils.
    *
    * @param props the props
    */
   public LocalDatasourceDbUtils(Properties props) {
      super(props);
      initialize(props);
   }

   /**
    * Initialize.
    *
    * @param props the props
    */
   private void initialize(Properties props) {
      driver = props.getProperty(JDBC_DRIVER);
      url = props.getProperty(JDBC_URL);
      user = props.getProperty(JDBC_USER);
      password = props.getProperty(JDBC_PASSWORD);
      // Create Datasource
      dataSource = new BasicDataSource();
      dataSource.setDriverClassName(driver);
      dataSource.setUrl(url);
      dataSource.setUsername(user);
      dataSource.setPassword(password);

      dataSource.setMaxActive(10);
   }

   /**
    * Shutdown.
    */
   public void shutdown() {
      if (dataSource != null && dataSource instanceof BasicDataSource) {
         try {
            dataSource.close();
         } catch (SQLException e) {
            logger.error(e.getMessage(), e);
         }
      }
   }

   /**
    * Gets the connection.
    *
    * @return the connection
    * @throws SQLException the SQL exception
    */
   public Connection getConnection() throws SQLException {
      Connection result = dataSource.getConnection();
      logger.info("Connections [{}/{}] Active/Idle", new Object[] {dataSource.getNumActive(),
            dataSource.getNumIdle()});
      return result;
   }

   /**
    * Close.
    *
    * @param c the connection
    * @param s the statement
    * @param rs the rs
    */
   public void close(Connection c, Statement s, ResultSet rs) {
      // close ResultSet
      if (rs != null) {
         try {
            rs.close();
         } catch (SQLException e) {
            logger.error("Fail to close result set", e);
         }
      }
      // Close statement
      if (s != null) {
         try {
            s.close();
         } catch (SQLException e) {
            logger.error("Fail to close statement", e);
         }
      }
      // Close connection
      if (c != null) {
         try {
            c.close();
         } catch (SQLException e) {
            logger.error("Fail to close connection", e);
         }
      }
   }

}
