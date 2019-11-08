package org.openwis.metadataportal.search.solr.spatial;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.geotools.data.DataStore;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.data.shapefile.indexed.IndexType;
import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openwis.metadataportal.search.solr.config.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.MultiPolygon;

/**
 * Factory of GeoTool Datastore.
 */
public class DataStoreFactory {
   
   // Load properties
   public static ResourceBundle bundle = ResourceBundle.getBundle("openwis");

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(DataStoreFactory.class);
   
   /** flag to keep track of the first initialization test postgis/shapefile */
   private static Boolean usePostgis = null;


   /**
    * Creates the data store.
    *
    * @return the data store
    */
   public static DataStore createDataStore() {
      DataStore ds = null;
      try {
         if (usePostgis == null) {
            ds = createPostgisDataStore(bundle);
            if (ds == null) {
               logger.warn("Could not create the Postgis Datastore, Create shape file DS");
               File index = new File(bundle.getString("openwis.solr.spatial"));
               ds = createShapeFileDataStore(index);
               usePostgis = false;
            } else {
               usePostgis = true;
            }
         } else if (usePostgis) {
            logger.warn("Re-initializing PostGIS Datastore");
            ds = createPostgisDataStore(bundle);
         } else {
            logger.warn("Re-initializing ShapeFile Datastore");;
            File index = new File(bundle.getString("openwis.solr.spatial"));
            ds = createShapeFileDataStore(index);
         }
      } catch (Exception e) {
         logger.error("Could not create the Datastore", e);
      }
      return ds;
   }

   /**
    * Creates a new OpenwisGeometryFilter object.
    *
    * @param indexDir the index dir
    * @return the data store
    * @throws Exception
    * @throws MalformedURLException
    */
   private static DataStore createShapeFileDataStore(File indexDir) throws Exception {
      File file = new File(indexDir, PostgisSpatial.SPATIAL_INDEX_TABLE + ".shp");
      file.getParentFile().mkdirs();
      if (!file.exists()) {
         logger.info("Creating shapefile {}", file.getAbsolutePath());
      } else {
         logger.info("Using shapefile {}", file.getAbsolutePath());
      }

      IndexedShapefileDataStore ds = null;
      ds = new IndexedShapefileDataStore(file.toURI().toURL(), new URI("http://geonetwork.org"),
            true, true, IndexType.QIX, Charset.defaultCharset());
      CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

      if (crs != null) {
         ds.forceSchemaCRS(crs);
      }

      if (!file.exists()) {
         SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
         AttributeDescriptor geomDescriptor = new AttributeTypeBuilder()
               .crs(DefaultGeographicCRS.WGS84).binding(MultiPolygon.class)
               .buildDescriptor(PostgisSpatial.SPATIAL_INDEX_COLUMN_GEOMETRY);
         builder.setName(PostgisSpatial.SPATIAL_INDEX_COLUMN_UUID);
         builder.add(geomDescriptor);
         builder.add(PostgisSpatial.SPATIAL_INDEX_COLUMN_UUID, String.class);
         ds.createSchema(builder.buildFeatureType());
      }

      logger.info("NOTE: Using shapefile for spatial index, this can be slow for larger catalogs");
      return ds;

   }

   /**
    * Creates a new OpenwisGeometryFilter object.
    *
    * @param bundle the bundle
    * @return the data store
    * @throws SQLException the sQL exception
    */
   private static DataStore createPostgisDataStore(ResourceBundle bundle) throws SQLException {
      DataStore ds = null;

      Connection connection = null;
      try {

         String jdbcDriver = bundle.getString("jdbc.driver");
         String jdbcUrl = bundle.getString("jdbc.url");
         String user = bundle.getString("jdbc.user");
         String password = bundle.getString("jdbc.password");

         Class.forName(jdbcDriver);
         connection = DriverManager.getConnection(jdbcUrl, user, password);

         if (checkPostgisSpatialIndex(connection)) {
            // Create PostgisDataStore
            String[] values = jdbcUrl.split("/");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(PostgisDataStoreFactory.DBTYPE.key, PostgisDataStoreFactory.DBTYPE.sample);
            params.put(PostgisDataStoreFactory.DATABASE.key, values[3]);
            params.put(PostgisDataStoreFactory.USER.key, user);
            params.put(PostgisDataStoreFactory.PASSWD.key, password);
            params.put(PostgisDataStoreFactory.HOST.key, values[2].split(":")[0]);
            params.put(PostgisDataStoreFactory.PORT.key, values[2].split(":")[1]);
            //logger.info("Connecting using "+params); - don't show unless we need it

            PostgisDataStoreFactory factory = new PostgisDataStoreFactory();
            ds = factory.createDataStore(params);
            logger.info("NOTE: Using POSTGIS for spatial index");
         }
      } catch (Exception e) {
         logger.error("Could not initialize PostgisDataStore!", e);
         ds = null;
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
      return ds;
   }

   /**
    * Check postgis spatial index.
    *
    * @param connection the connection
    * @return true, if successful
    * @throws SQLException
    */
   private static boolean checkPostgisSpatialIndex(Connection connection) throws SQLException {
      boolean result = false;
      ResultSet rs;
      Statement s = null;
      try {
         s = connection.createStatement();
         try {
            // Check PostGIS presence
            rs = s.executeQuery("SELECT postgis_version()");
            if (rs.next()) {
               // Check spatial index
               result = checkSpatialIndex(s);
            }
         } catch (SQLException e) {
            logger.warn("The database does not have PostGIS installed");
            result = false;
         }
      } finally {
         if (s != null) {
            s.close();
         }
      }
      return result;
   }

   /**
    * Check spatial index.
    *
    * @param s the s
    * @return true, if successful
    * @throws SQLException the sQL exception
    */
   private static boolean checkSpatialIndex(Statement s) throws SQLException {
      boolean result = false;
      String sql = MessageFormat.format("SELECT {0},{1}, {2} FROM {3}",
            PostgisSpatial.SPATIAL_INDEX_COLUMN_ID, PostgisSpatial.SPATIAL_INDEX_COLUMN_UUID,
            PostgisSpatial.SPATIAL_INDEX_COLUMN_GEOMETRY, PostgisSpatial.SPATIAL_INDEX_TABLE);
      try {
         s.executeQuery(sql);
         result = true;
      } catch (SQLException e) {
         logger.warn("The spatial index table seems to be invalid!", e);
         //  try to create the table
         ScriptRunner runner = new ScriptRunner(s.getConnection(), true, true);

         InputStream input = DataStoreFactory.class.getClassLoader()
               .getResourceAsStream("/sql/create-postgis-spatialindex.sql");
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         try {
            runner.runScript(reader);

            s.executeQuery(sql);
            result = true;
         } catch (Exception e1) {
            logger.error("Could not initialize spatial index table!", e1);
            result = false;
         }
      }

      return result;
   }

   
}
