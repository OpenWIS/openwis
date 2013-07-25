/*
 *
 */
package org.openwis.metadataportal.search.solr.spatial;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.fao.geonet.kernel.search.spatial.ContainsFilter;
import org.fao.geonet.kernel.search.spatial.CrossesFilter;
import org.fao.geonet.kernel.search.spatial.EqualsFilter;
import org.fao.geonet.kernel.search.spatial.IntersectionFilter;
import org.fao.geonet.kernel.search.spatial.IsFullyOutsideOfFilter;
import org.fao.geonet.kernel.search.spatial.OgcGenericFilters;
import org.fao.geonet.kernel.search.spatial.OverlapsFilter;
import org.fao.geonet.kernel.search.spatial.SpatialFilter;
import org.fao.geonet.kernel.search.spatial.SpatialIndexWriter;
import org.fao.geonet.kernel.search.spatial.TouchesFilter;
import org.fao.geonet.kernel.search.spatial.WithinFilter;
import org.fao.geonet.kernel.search.spatial.XmlUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.io.WKTReader;

/**
 * The Class OpenwisGeometryTool.
 */
public class OpenwisGeometryTool {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(OpenwisGeometryTool.class);

   /** The _lock. */
   private final Lock lock;

   /** The types. */
   private final Map<String, Constructor<? extends SpatialFilter>> types;

   /** The writer. */
   private SpatialIndexWriter writer;

   /** The data store. */
   private final DataStore datastore;

   /** The transaction. */
   private DefaultTransaction transaction;

   /** The gml parser. */
   private final Parser gmlParser;

   /** The Constant FILTER_1_0_0. */
   private static final Configuration FILTER_1_0_0 = new org.geotools.filter.v1_0.OGCConfiguration();

   /** The Constant FILTER_1_1_0. */
   private static final Configuration FILTER_1_1_0 = new org.geotools.filter.v1_1.OGCConfiguration();

   /** The instance. */
   private static OpenwisGeometryTool instance;

   /**
    * Gets the single instance of OpenwisGeometryTool.
    *
    * @return single instance of OpenwisGeometryTool
    */
   public static OpenwisGeometryTool getInstance() {
      return instance;
   }

   /**
    * Initialize.
    *
    * @param ds the ds
    * @return the openwis geometry tool
    * @throws NoSuchMethodException
    * @throws SecurityException
    */
   public static OpenwisGeometryTool initialize() throws SecurityException, NoSuchMethodException {
      DataStore ds = DataStoreFactory.createDataStore();
      instance = new OpenwisGeometryTool(ds);
      return instance;
   }

   /**
    * Instantiates a new test geometry filter.
    *
    * @param datastore the datastore
    * @throws SecurityException the security exception
    * @throws NoSuchMethodException the no such method exception
    */
   private OpenwisGeometryTool(DataStore datastore) throws SecurityException, NoSuchMethodException {
      super();
      lock = new ReentrantLock();

      this.datastore = datastore;
      transaction = new DefaultTransaction("SpatialIndexWriter");
      gmlParser = new Parser(new GMLConfiguration());

      // Define Spatial Filter constructor
      Map<String, Constructor<? extends SpatialFilter>> types = null;
      try {
         types = new HashMap<String, Constructor<? extends SpatialFilter>>();
         types.put("encloses", constructor(ContainsFilter.class));
         types.put("crosses", constructor(CrossesFilter.class));
         types.put("fullyOutsideOf", constructor(IsFullyOutsideOfFilter.class));
         types.put("equal", constructor(EqualsFilter.class));
         types.put("intersection", constructor(IntersectionFilter.class));
         types.put("overlaps", constructor(OverlapsFilter.class));
         types.put("touches", constructor(TouchesFilter.class));
         types.put("within", constructor(WithinFilter.class));
         // types.put(Geonet.SearchResult.Relation.CONTAINS,
         // constructor(BeyondFilter.class));
         // types.put(Geonet.SearchResult.Relation.CONTAINS,
         // constructor(DWithinFilter.class));
      } finally {
         this.types = Collections.unmodifiableMap(types);
      }
   }

   /**
    * Builds the filter.
    *
    * @param query the query
    * @param xml the xml
    * @return the filter
    * @throws Exception the exception
    */
   public Filter buildFilter(Query query, Element xml) throws Exception {
      Filter filter = null;
      Geometry geometry = getGeometry(xml);
      if (geometry != null) {
         filter = new CachingWrapperFilter(buildSpatialFilter(query, geometry, xml));
      }

      return filter;
   }

   /**
    * Clear the spatial index.
    *
    * @throws Exception the exception
    */
   public void reset() throws Exception {
      logger.info("\tClear spatial index");
      lock.lock();
      try {
         getSpatialIndexWriter().reset();
      } finally {
         lock.unlock();
      }
   }

   /**
    * Removes the metadata.
    *
    * @param uuid the uuid
    * @throws Exception the exception
    */
   public void removeMetadata(String uuid) throws Exception {
      logger.info("\tRemove spatial metadata: {}", uuid);
      lock.lock();
      try {
         getSpatialIndexWriter().delete(uuid);
      } finally {
         lock.unlock();
      }
   }

   /**
    * Adds the metadata.
    *
    * @param uuid the metadata uuid
    * @param wktGeometry the wkt geometry
    * @throws Exception the exception
    */
   public void addMetadata(String uuid, String wktGeometry) throws Exception {
      logger.info("\tAdd spatial metadata: {}", uuid);
      if (uuid != null && wktGeometry != null)
         lock.lock();
      try {
         // Extract Geometry
         WKTReader reader = new WKTReader();
         Geometry geometry = reader.read(wktGeometry);
         getSpatialIndexWriter().index(uuid, geometry);
      } finally {
         lock.unlock();
      }
   }

   /**
    * Commit.
    *
    * @throws Exception the exception
    */
   public void commit() throws Exception {
      logger.info("\tCommit Spatial index");
      lock.lock();
      try {
         getSpatialIndexWriter().commit();
      } finally {
         lock.unlock();
      }
   }

   /**
    * Close.
    *
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws Exception the exception
    */
   public void close() throws IOException, Exception {
      lock.lock();
      try {
         getSpatialIndexWriter().close();
      } finally {
         lock.unlock();
      }
   }

   /**
    * Gets the spatial filter.
    *
    * @param query the query
    * @param geometry the geometry
    * @param xml the xml
    * @return the spatial filter
    * @throws Exception the exception
    */
   @SuppressWarnings("rawtypes")
   private SpatialFilter buildSpatialFilter(Query query, Geometry geometry, Element xml)
         throws Exception {
      lock.lock();
      try {
         String relation = getParam(xml, "relation", "intersection");
         SpatialIndex index = getSpatialIndexWriter().getIndex();
         FeatureSource featureSource = getSpatialIndexWriter().getFeatureSource();
         Constructor<? extends SpatialFilter> constructor = types.get(relation);
         return constructor.newInstance(query, xml, geometry, featureSource, index);
      } finally {
         lock.unlock();
      }
   }

   /**
    * Builds the filter.
    *
    * @param query the query
    * @param filterExpr the filter expr
    * @param filterVersion the filter version
    * @return the filter
    */
   @SuppressWarnings("rawtypes")
   public Filter buildFilter(Query query, Element filterExpr, String filterVersion) {
      Filter result = null;
      lock.lock();
      try {
         SpatialIndex index = getSpatialIndexWriter().getIndex();
         Parser filterParser = getFilterParser(filterVersion);
         FeatureSource featureSource = getSpatialIndexWriter().getFeatureSource();
         result = OgcGenericFilters.create(query, filterExpr, featureSource, index, filterParser);
      } catch (Exception e) {
         logger.error(MessageFormat.format(
               "Error when parsing spatial filter (version: {0}):{1}. Error is: {2}",
               filterVersion, XmlUtils.getString(filterExpr), e.toString()));
      } finally {
         lock.unlock();
      }
      return result;
   }

   /**
    * Gets the filter parser.
    *
    * @param filterVersion the filter version
    * @return the filter parser
    */
   private Parser getFilterParser(String filterVersion) {
      Configuration config;
      config = "1.0.0".equals(filterVersion) ? FILTER_1_0_0 : FILTER_1_1_0;
      return new Parser(config);
   }

   /**
    * Gets the geometry.
    *
    * @param request the request
    * @return the geometry
    * @throws Exception the exception
    */
   private Geometry getGeometry(Element request) throws Exception {
      String geomWKT = getParam(request, PostgisSpatial.REQUEST_GEOMETRY_ELEMENT, null);
      if (geomWKT != null) {
         WKTReader reader = new WKTReader();
         return reader.read(geomWKT);
      }
      return null;
   }

   /**
    * Gets the param.
    *
    * @param el the el
    * @param name the name
    * @param defValue the def value
    * @return the param
    */
   private static String getParam(Element el, String name, String defValue) {
      if (el == null)
         return defValue;

      Element param = el.getChild(name);

      if (param == null)
         return defValue;

      String value = param.getTextTrim();

      if (value.length() == 0)
         return defValue;

      return value;
   }

   /**
    * Constructor.
    *
    * @param clazz the class
    * @return the constructor<? extends spatial filter>
    * @throws SecurityException the security exception
    * @throws NoSuchMethodException the no such method exception
    */
   private static Constructor<? extends SpatialFilter> constructor(
         Class<? extends SpatialFilter> clazz) throws SecurityException, NoSuchMethodException {
      return clazz.getConstructor(Query.class, Element.class, Geometry.class, FeatureSource.class,
            SpatialIndex.class);
   }

   /**
    * Writer no locking.
    *
    * @return the spatial index writer
    * @throws Exception the exception
    */
   private synchronized SpatialIndexWriter getSpatialIndexWriter() throws Exception {
      if (writer == null) {
         writer = new SpatialIndexWriter(datastore, gmlParser, transaction, lock);
      }
      return writer;
   }

   /**
    * Clean Data Store in case IO Error occurred.
    */
   public void cleanDataStore() {
      logger.info("Cleaning current Data Store");
      try {
         if (writer != null) {
            writer.close();
         }
      } catch (Exception e) {
         logger.warn("Unable to close writer", e);
      }
      writer = null;
      transaction = new DefaultTransaction("SpatialIndexWriter");
   }

}
