package org.openwis.metadataportal.kernel.search.index.solr;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import jeeves.server.ServiceConfig;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.util.DateUtil;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.IndexEvent;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.IndexListener;
import org.geotools.geometry.jts.JTS;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Parser;
import org.jdom.Element;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.metadata.product.IProductMetadataExtractor;
import org.openwis.metadataportal.kernel.search.index.DbmsIndexableElement;
import org.openwis.metadataportal.kernel.search.index.IIndexManager;
import org.openwis.metadataportal.kernel.search.index.IndexException;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * The Class SolrIndexManager. <P>
 * Explanation goes here. <P>
 */
public class SolrIndexManager implements IIndexManager {

   /** If auto commit. */
   private final boolean autoCommit = false;

   /** The schemas dir. */
   private final File schemasDir;

   /** The is inspire enabled. */
   private final boolean isInspireEnabled;

   /** The solr server. */
   private final String solrUrl;

   /** The parser. */
   //   private final static Parser parser = new Parser(new GMLConfiguration());

   /** The listeners. */
   private final Set<IndexListener> listeners = new CopyOnWriteArraySet<IndexListener>();

   /**
    * Instantiates a new index manager.
    *
    * @param config the config
    * @param appPath the application path
    * @param guiConfigXmlFile the gui config xml file
    * @throws IndexException the index exception
    */
   public SolrIndexManager(ServiceConfig config, String appPath) throws IndexException {
      super();

      // Check Inspire
      isInspireEnabled = checkInspireEnable(config);

      schemasDir = new File(appPath, SCHEMA_STYLESHEETS_DIR_PATH);

      solrUrl = OpenwisMetadataPortalConfig.getString(ConfigurationConstants.SOLR_URL);

      // Configuration of Date pattern for SolR
      DateUtil.DEFAULT_DATE_FORMATS.add("yyyy-MM-dd'Z'");
   }

   /**
    * Adds the index listener.
    *
    * @param listener the listener
    */
   @Override
   public void addIndexListener(IndexListener listener) {
      if (listener != null) {
         listeners.add(listener);
      }
   }

   /**
    * Removes the index listener.
    *
    * @param listener the listener
    */
   @Override
   public void removeIndexListener(IndexListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   /**
    * Fire index event.
    *
    * @param event the event
    */
   protected void fireIndexEvent(IndexEvent event) {
      if (event != null) {
         for (IndexListener listener : listeners) {
            try {
               listener.onIndexEvent(event);
            } catch (Exception e) {
               Log.warning(Geonet.INDEX_ENGINE, "error in event listener", e);
            }
         }
      }
   }

   /**
    * Check inspire enable.
    * @param config
    *
    * @param appPath the application path
    * @param guiConfigXmlFile the gui config xml file
    * @return true, if successful
    */
   private boolean checkInspireEnable(ServiceConfig config) {
      // FIXME Igor: retrieve the inspire config
      return false;
   }

   /**
    * Clear.
    *
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#clear()
    */
   @Override
   public void clear() throws IndexException {
      // delete everything!
      try {
         SolrServer server = SolRUtils.getSolRServer(solrUrl, this);
         if (server == null) {
            throw new IndexException("Unavailable SolR Server");
         }
         UpdateResponse response = server.deleteByQuery("*:*");

         if (Log.isDebug(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "Clear: " + response);
         }
         commit();
      } catch (SolrServerException e) {
         throw new IndexException(e);
      } catch (IOException e) {
         throw new IndexException(e);
      }
   }

   /**
    * Optimize.
    *
    * @return true, if successful
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#optimize()
    */
   @Override
   public boolean optimize() throws IndexException {
      boolean result = false;
      // Optimize!
      try {
         SolrServer server = SolRUtils.getSolRServer(solrUrl, this);
         if (server == null) {
            throw new IndexException("Unavailable SolR Server");
         }
         UpdateResponse response = server.optimize();
         if (Log.isDebug(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "Optimize: " + response);
         }
         result = (response.getStatus() == 0);
         if (result) {
            fireIndexEvent(IndexEvent.Factory.createOptimizedEvent());
         }
      } catch (SolrServerException e) {
         throw new IndexException(e);
      } catch (IOException e) {
         throw new IndexException(e);
      }
      return result;
   }

   /**
    * Commit.
    *
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#commit()
    */
   @Override
   public void commit() throws IndexException {
      UpdateResponse response;
      try {
         SolrServer server = SolRUtils.getSolRServer(solrUrl, this);
         if (server == null) {
            throw new IndexException("Unavailable SolR Server");
         }
         response = server.commit();
         if (Log.isDebug(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "Commit: " + response);
         }
         if (response.getStatus() == 0) {
            fireIndexEvent(IndexEvent.Factory.createCommittedEvent());
         }
      } catch (SolrServerException e) {
         throw new IndexException(e);
      } catch (IOException e) {
         throw new IndexException(e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#isAvailable()
    */
   @Override
   public boolean isAvailable() throws IndexException {
      boolean isAvailable = false;
      try {
         SolrServer server = SolRUtils.getSolRServer(solrUrl, this);
         isAvailable = (server != null);
      } catch (Exception e) {
         throw new IndexException(e);
      }
      return isAvailable;
   }

   /**
    * Adds the.
    *
    * @param element the element
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#add(org.openwis.metadataportal.kernel.search.index.IndexableElement)
    */
   @Override
   public void add(IndexableElement element) throws IndexException {
      if (element != null) {
         add(element, autoCommit);
      }
   }

   /**
    * Adds the.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#add(java.util.Collection)
    */
   @Override
   public void add(Collection<IndexableElement> elements) throws IndexException {
      if (elements != null && !elements.isEmpty()) {
         add(elements, autoCommit);
      }
   }

   /**
    * Adds the.
    *
    * @param element the element
    * @param commit the commit
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#add(org.openwis.metadataportal.kernel.search.index.IndexableElement, boolean)
    */
   @Override
   public void add(IndexableElement element, boolean commit) throws IndexException {
      if (element != null) {
         add(Collections.singleton(element), commit);
      }
   }

   /**
    * Adds the.
    *
    * @param elements the elements
    * @param commit the commit
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#add(java.util.Collection, boolean)
    */
   @Override
   public void add(Collection<IndexableElement> elements, boolean commit) throws IndexException {
      if (elements != null && !elements.isEmpty()) {
         Collection<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
         long before, after;
         try {
            SolrServer server = SolRUtils.getSolRServer(solrUrl, this);
            if (server == null) {
               throw new IndexException("Unavailable SolR Server");
            }
            Log.info(Geonet.INDEX_ENGINE, "Creating " + elements.size() + " indexable elements...");
            Set<String> ids = new HashSet<String>();
            int count = 0;
            for (IndexableElement elt : elements) {
               if (elt instanceof DbmsIndexableElement) {
                  before = System.currentTimeMillis();
                  DbmsIndexableElement element = (DbmsIndexableElement) elt;

                  // clone to avoid memory size growing while lazy filling
                  element = (DbmsIndexableElement) element.clone();

                  Element indexBuiltDocument = buildIndexDocument(element);
                  SolrInputDocument doc = buildSolrInput(indexBuiltDocument);
                  ids.add(element.getUniqueKey());
                  documents.add(doc);
                  after = System.currentTimeMillis();
                  if (Log.isStatEnabled()) {
                     Log.statTime("SolrIndexManager", "SolrIndexManager#add(elements, commit)",
                           "Create Solr doc.", after - before);
                  }
               }
               count++;
               if (count % 100 == 0) {
                  Log.info(Geonet.INDEX_ENGINE, " -> created " + count + " indexable elements");
                  addToServer(documents, server, ids);
                  documents = new ArrayList<SolrInputDocument>();
                  ids = new HashSet<String>();
                  // force commit
                  commit();
               }
            }
            if (documents.size() > 0) {
               addToServer(documents, server, ids);
            }

            if (commit) {
               before = System.currentTimeMillis();
               commit();
               after = System.currentTimeMillis();
               if (Log.isStatEnabled()) {
                  Log.statTime("SolrIndexManager", "SolrIndexManager#add(elements, commit)",
                        "Commit in SolR.", after - before);
               }
            }

         } catch (SolrServerException e) {
            throw new IndexException(e);
         } catch (IOException e) {
            throw new IndexException(e);
         }
      }
   }

   private void addToServer(Collection<SolrInputDocument> documents, SolrServer server,
         Set<String> ids) throws SolrServerException, IOException {
      long before = System.currentTimeMillis();
      if (Log.isInfo(Geonet.INDEX_ENGINE)) {
         Log.info(Geonet.INDEX_ENGINE, "Will index : " + ids);
      }
      UpdateResponse response = server.add(documents);
      long after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("SolrIndexManager", "SolrIndexManager#add(elements, commit)",
               "Add docs to SolR.", after - before);
      }
      if (Log.isDebug(Geonet.INDEX_ENGINE)) {
         Log.debug(Geonet.INDEX_ENGINE, "\tAdd: " + response);
      }
   }

   /**
    * Builds the solr input.
    *
    * @param xml the xml
    * @return the solr input document
    * @throws IndexException
    */
   private SolrInputDocument buildSolrInput(Element xml) throws IndexException {
      long before, after;
      before = System.currentTimeMillis();
      SolrInputDocument document = new SolrInputDocument();
      @SuppressWarnings("unchecked")
      List<Element> children = xml.getChildren("field");
      String text;
      IndexField indexField;
      SolrInputField field;
      for (Element fieldElt : children) {
         indexField = IndexField.getField(fieldElt.getAttributeValue("name"));
         if (indexField == null) {
            throw new IndexException("Unkown field: " + fieldElt.getAttributeValue("name"));
         }
         text = fieldElt.getText();
         field = document.getField(indexField.getField());
         if (StringUtils.isNotBlank(text) && (field == null || !text.equals(field.getFirstValue()))) {
            if (indexField.isDate()) {
               Date date = parseDate(text.toUpperCase());
               if (date != null) {
                  document.addField(indexField.getField(), date);
               }
            } else {
               document.addField(indexField.getField(), indexField.valueToString(text));
            }
         }
      }
      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("SolrIndexManager", "SolrIndexManager#buildSolrInput(Element)",
               "buildSolrInput.", after - before);
      }
      return document;
   }

   /**
    * Removes the.
    *
    * @param element the element
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#remove(org.openwis.metadataportal.kernel.search.index.IndexableElement)
    */
   @Override
   public void remove(IndexableElement element) throws IndexException {
      if (element != null) {
         remove(element, autoCommit);
      }
   }

   /**
    * Removes the.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#remove(java.util.Collection)
    */
   @Override
   public void remove(Collection<IndexableElement> elements) throws IndexException {
      if (elements != null && !elements.isEmpty()) {
         remove(elements, autoCommit);
      }
   }

   /**
    * Removes the.
    *
    * @param element the element
    * @param commit the commit
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#remove(org.openwis.metadataportal.kernel.search.index.IndexableElement, boolean)
    */
   @Override
   public void remove(IndexableElement element, boolean commit) throws IndexException {
      remove(Collections.singleton(element), commit);
   }

   /**
    * Removes the.
    *
    * @param elements the elements
    * @param commit the commit
    * @throws IndexException the index exception
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.search.index.IIndexManager#remove(java.util.Collection, boolean)
    */
   @Override
   public void remove(Collection<IndexableElement> elements, boolean commit) throws IndexException {
      if (elements != null && !elements.isEmpty()) {
         List<String> ids = new ArrayList<String>();
         DbmsIndexableElement elt2;
         for (IndexableElement elt : elements) {
            if (elt instanceof DbmsIndexableElement) {
               elt2 = (DbmsIndexableElement) elt;
               ids.add(elt2.getUniqueKey());
            }
         }
         try {
            SolrServer server = SolRUtils.getSolRServer(solrUrl, this);
            if (server == null) {
               throw new IndexException("Unavailable SolR Server");
            }

            if (Log.isInfo(Geonet.INDEX_ENGINE)) {
               Log.info(Geonet.INDEX_ENGINE, "Remove SolR: " + ids);
            }
            UpdateResponse response = server.deleteById(ids);
            if (Log.isDebug(Geonet.INDEX_ENGINE)) {
               Log.debug(Geonet.INDEX_ENGINE, "\tRemove: " + response);
            }
            if (commit) {
               commit();
            }
         } catch (SolrServerException e) {
            throw new IndexException(e);
         } catch (IOException e) {
            throw new IndexException(e);
         }
      }
   }

   /**
    * Builds the index document.
    *
    * @param elt the elt
    * @return the document
    * @throws IndexException the index exception
    */
   private Element buildIndexDocument(DbmsIndexableElement elt) throws IndexException {
      long before, after;
      String id = elt.getUniqueKey();
      Element metadata = elt.getMetadata();
      if (Log.isDebug(Geonet.INDEX_ENGINE)) {
         Log.debug(Geonet.INDEX_ENGINE, MessageFormat.format("Deleting {0} from index", id));
      }

      Element xmlDoc;
      // check for sub templates
      if ("s".equals(elt.getIsTemplate())) {
         // create empty document with only title and "any" fields
         xmlDoc = new Element("Document");

         StringBuffer sb = new StringBuffer();
         allText(metadata, sb);
         addField(xmlDoc, IndexField.ANY, sb.toString());
      } else {
         before = System.currentTimeMillis();
         if (Log.isDebug(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "Metadata to index:\n" + Xml.getString(metadata));
         }
         xmlDoc = getSolrIndexFields(elt.getSchema(), metadata);
         if (Log.isDebug(Geonet.INDEX_ENGINE)) {
            Log.debug(Geonet.INDEX_ENGINE, "Indexing fields:\n" + Xml.getString(xmlDoc));
         }
         after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("SolrIndexManager",
                  "SolrIndexManager#buildIndexDocument(DbmsIndexableElement)",
                  "Get SolR index fields.", after - before);
         }
      }

      // add more fields
      before = System.currentTimeMillis();
      for (Element moreField : elt.getMoreFields()) {
         addField(xmlDoc, IndexField.getField(moreField.getAttributeValue("name")),
               moreField.getAttributeValue("string"));
         // overridden gts category may influence the isGlobal field
         if (IndexField.getField(moreField.getAttributeValue("name")) == IndexField.OVERRIDDEN_GTS_CATEGORY) {
            updateIsGlobalField(xmlDoc, moreField.getAttributeValue("string"));
         }
      }
      after = System.currentTimeMillis();
      if (Log.isStatEnabled()) {
         Log.statTime("SolrIndexManager",
               "SolrIndexManager#buildIndexDocument(DbmsIndexableElement)", "Add more fields.",
               after - before);
      }

      // Spatial
      try {
         Geometry geometry = extractGeometry(elt);
         if (geometry != null) {
            WKTWriter wktWriter = new WKTWriter();
            String geo = wktWriter.write(geometry);
            addField(xmlDoc, IndexField.GEOMETRY, geo);
         }
      } catch (Exception e) {
         Log.error(Geonet.SPATIAL, "Could not build the index geometry", e);
      }

      if (Log.isDebug(Geonet.INDEX_ENGINE)) {
         Log.debug(Geonet.INDEX_ENGINE, "Solr document:\n" + Xml.getString(xmlDoc));
      }
      return xmlDoc;
   }

   /**
    * Update the isGlobal field if an overridden GTS category has been set.
    * 
    * @param xmlDoc the xml doc
    * @param overriddenGtsCategory the overridden GTS category
    */
   private void updateIsGlobalField(Element xmlDoc, String overriddenGtsCategory) {
      if (overriddenGtsCategory != null && overriddenGtsCategory.length() > 0) {
         boolean isGlobal = Pattern.matches(IProductMetadataExtractor.GTS_CATEGORY_ESSENTIAL, overriddenGtsCategory)
               || Pattern.matches(IProductMetadataExtractor.GTS_CATEGORY_ADDITIONAL, overriddenGtsCategory);
         // find isGlobal field
         @SuppressWarnings("unchecked")
         List<Element> children = xmlDoc.getChildren();
         boolean found = false;
         for (Element child : children) {
            if (IndexField.IS_GLOBAL.getField().equals(child.getAttributeValue("name"))) {
               child.setText(String.valueOf(isGlobal));
               found = true;
               break;
            }
         }
         // If was not global, the field was not there
         if (!found && isGlobal) {
            addField(xmlDoc, IndexField.IS_GLOBAL, String.valueOf(true));
         }
      }
   }

   /**
    * Creates a new field for the solr index.
    *
    * @param xmlDoc the xml doc
    * @param name the name
    * @param value the value
    */
   private void addField(Element xmlDoc, IndexField name, String value) {
      Element field = new Element("field");
      field.setAttribute("name", name.getField());
      field.addContent(value);
      xmlDoc.addContent(field);
   }

   /**
    * Extracts text from metadata record.
    *
    * @param metadata the metadata
    * @param sb the sb
    * @return all text in the metadata elements for indexing
    */
   private void allText(Element metadata, StringBuffer sb) {
      String text = metadata.getText().trim();
      if (text.length() > 0) {
         if (sb.length() > 0)
            sb.append(" ");
         sb.append(text);
      }
      @SuppressWarnings("unchecked")
      List<Element> children = metadata.getChildren();
      if (children.size() > 0) {
         for (Element child : children) {
            allText(child, sb);
         }
      }
   }

   /**
    * Gets the index fields.
    *
    * @param schema the schema
    * @param xml the xml
    * @return the index fields
    * @throws IndexException the index exception
    */
   private Element getSolrIndexFields(String schema, Element xml) throws IndexException {
      File schemaDir = new File(schemasDir, schema);
      if (Log.isDebug(Geonet.INDEX_ENGINE)) {
         Log.debug(Geonet.INDEX_ENGINE, "Indexing element:\n" + Xml.getString(xml));
      }
      try {
         String styleSheet = new File(schemaDir, "solr-fields.xsl").getAbsolutePath();
         Map<String, String> params = new HashMap<String, String>();
         params.put("inspire", Boolean.toString(isInspireEnabled));

         return Xml.transform(xml, styleSheet, params);
      } catch (Exception e) {
         Log.error(Geonet.INDEX_ENGINE, "Indexing stylesheet contains errors : " + e.getMessage());
         throw new IndexException(e);
      }
   }

   /**
    * Parses the date.
    *
    * @param sDate the s date
    * @return the date
    */
   @Override
   public Date parseDate(String sDate) {
      Date result = null;
      try {
         result = DateUtil.parseDate(sDate);
      } catch (ParseException e) {
         Log.error(Geonet.INDEX_ENGINE, "Could not parse date: " + sDate, e);
      }
      return result;
   }

   /**
    * Extract geometry.
    *
    * @param elt the element
    * @return the geometry
    * @throws Exception the exception
    */
   @SuppressWarnings("unchecked")
   private Geometry extractGeometry(DbmsIndexableElement elt) throws IndexException {
      File schemaDir = new File(schemasDir, elt.getSchema());
      String sSheet = new File(schemaDir, "extract-gml.xsl").getAbsolutePath();
      Element transform;
      try {
         transform = Xml.transform(elt.getMetadata(), sSheet);
      } catch (Exception e1) {
         throw new IndexException(e1);
      }
      if (transform.getChildren().size() == 0) {
         return null;
      }

      List<Polygon> allPolygons = new ArrayList<Polygon>();

      for (Element geom : (List<Element>) transform.getChildren()) {
         String srs = geom.getAttributeValue("srsName");
         CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84;
         String gml = Xml.getString(geom);

         try {
            if (srs != null && !(srs.equals("")))
               sourceCRS = CRS.decode(srs);
            Parser parser = new Parser(new GMLConfiguration());
            MultiPolygon jts = parseGml(parser, gml);

            // if we have an srs and its not WGS84 then transform to WGS84
            if (!CRS.equalsIgnoreMetadata(sourceCRS, DefaultGeographicCRS.WGS84)) {
               MathTransform tform = CRS.findMathTransform(sourceCRS, DefaultGeographicCRS.WGS84);
               jts = (MultiPolygon) JTS.transform(jts, tform);
            }

            for (int i = 0; i < jts.getNumGeometries(); i++) {
               allPolygons.add((Polygon) jts.getGeometryN(i));
            }
         } catch (Exception e) {
            Log.error(Geonet.INDEX_ENGINE, "Failed to convert gml to jts object: " + gml, e);
         }
      }

      if (allPolygons.isEmpty()) {
         return null;
      } else {
         try {
            Polygon[] array = new Polygon[allPolygons.size()];
            GeometryFactory geometryFactory = allPolygons.get(0).getFactory();
            return geometryFactory.createMultiPolygon(allPolygons.toArray(array));

         } catch (Exception e) {
            Log.error(Geonet.INDEX_ENGINE, "Failed to create a MultiPolygon from: " + allPolygons,
                  e);
            return null;
         }
      }
   }

   /**
    * Parses the GML.
    *
    * @param parser the parser
    * @param gml the GML
    * @return the multi polygon
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws SAXException the sAX exception
    * @throws ParserConfigurationException the parser configuration exception
    */
   @SuppressWarnings("rawtypes")
   public static MultiPolygon parseGml(Parser parser, String gml) throws IOException, SAXException,
         ParserConfigurationException {
      Object value = parser.parse(new StringReader(gml));
      if (value instanceof Map) {
         Map map = (Map) value;
         List<Polygon> geoms = new ArrayList<Polygon>();
         for (Object entry : map.values()) {
            addGeometryEntryToList(geoms, entry);
         }
         if (geoms.isEmpty()) {
            return null;
         } else if (geoms.size() > 1) {
            GeometryFactory factory = geoms.get(0).getFactory();
            return factory.createMultiPolygon(geoms.toArray(new Polygon[0]));
         } else {
            return toMultiPolygon(geoms.get(0));
         }

      } else if (value == null) {
         return null;
      } else {
         return toMultiPolygon((Geometry) value);
      }
   }

   /**
    * Adds the to list.
    *
    * @param geoms the geometries
    * @param entry the entry
    */
   @SuppressWarnings("rawtypes")
   public static void addGeometryEntryToList(List<Polygon> geoms, Object entry) {
      if (entry instanceof Polygon) {
         geoms.add((Polygon) entry);
      } else if (entry instanceof Collection) {
         Collection collection = (Collection) entry;
         for (Object object : collection) {
            geoms.add((Polygon) object);
         }
      }
   }

   /**
    * To multi polygon.
    *
    * @param geometry the geometry
    * @return the multi polygon
    */
   public static MultiPolygon toMultiPolygon(Geometry geometry) {
      if (geometry instanceof Polygon) {
         Polygon polygon = (Polygon) geometry;
         return geometry.getFactory().createMultiPolygon(new Polygon[] {polygon});
      } else if (geometry instanceof MultiPolygon) {
         return (MultiPolygon) geometry;
      }
      String message = geometry.getClass() + " cannot be converted to a polygon. Check Metadata";
      Log.error(Geonet.INDEX_ENGINE, message);
      throw new IllegalArgumentException(message);
   }

}
