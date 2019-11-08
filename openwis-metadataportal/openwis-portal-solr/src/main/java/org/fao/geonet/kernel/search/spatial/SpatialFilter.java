//=== Copyright (C) 2001-2007 Food and Agriculture Organization of the
//=== United Nations (FAO-UN), United Nations World Food Programme (WFP)
//=== and United Nations Environment Programme (UNEP)
//===
//=== This program is free software; you can redistribute it and/or modify
//=== it under the terms of the GNU General Public License as published by
//=== the Free Software Foundation; either version 2 of the License, or (at
//=== your option) any later version.
//===
//=== This program is distributed in the hope that it will be useful, but
//=== WITHOUT ANY WARRANTY; without even the implied warranty of
//=== MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//=== General Public License for more details.
//===
//=== You should have received a copy of the GNU General Public License
//=== along with this program; if not, write to the Free Software
//=== Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//=== Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//=== Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.kernel.search.spatial;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.SpatialOperator;
import org.openwis.metadataportal.search.solr.spatial.PostgisSpatial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.io.WKTReader;

public abstract class SpatialFilter extends Filter {
   private static final long serialVersionUID = -6221744013750827050L;

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(SpatialFilter.class);

   static {
      SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
      builder.add(PostgisSpatial.SPATIAL_INDEX_COLUMN_GEOMETRY, Geometry.class,
            DefaultGeographicCRS.WGS84);
      builder.setDefaultGeometry(PostgisSpatial.SPATIAL_INDEX_COLUMN_GEOMETRY);
      builder.setName(PostgisSpatial.SPATIAL_INDEX_TABLE);
   }

   protected final Geometry _geom;

   protected final FeatureSource _featureSource;

   protected final SpatialIndex _index;

   protected final FilterFactory2 _filterFactory;

   protected final Query _query;

   protected final FieldSelector _selector;

   private org.opengis.filter.Filter _spatialFilter;

   private Map<String, FeatureId> _unrefinedMatches;

   private boolean warned = false;

   protected SpatialFilter(Query query, Geometry geom, FeatureSource featureSource,
         SpatialIndex index) throws IOException {
      _query = query;
      _geom = geom;
      _featureSource = featureSource;
      _index = index;
      _filterFactory = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());

      _selector = new FieldSelector() {
         @Override
         public final FieldSelectorResult accept(String name) {
            if (name.equals(PostgisSpatial.SPATIAL_INDEX_UUID_FIELD))
               return FieldSelectorResult.LOAD_AND_BREAK;
            else
               return FieldSelectorResult.NO_LOAD;
         }
      };
   }

   protected SpatialFilter(Query query, Envelope bounds, FeatureSource featureSource,
         SpatialIndex index) throws IOException {
      this(query, JTS.toGeometry(bounds), featureSource, index);
   }

   @Override
   public BitSet bits(final IndexReader reader) throws IOException {
      final BitSet bits = new BitSet(reader.maxDoc());

      final Map<String, FeatureId> unrefinedSpatialMatches = unrefinedSpatialMatches();
      final Set<FeatureId> matches = new HashSet<FeatureId>();
      final Map<FeatureId, Integer> docIndexLookup = new HashMap<FeatureId, Integer>();

      new IndexSearcher(reader).search(_query, new Collector() {
         private int docBase;

         //ignore scorer
         @Override
         public void setScorer(Scorer scorer) {
         }

         // accept docs out of order (for a BitSet it doesn't matter)
         @Override
         public boolean acceptsDocsOutOfOrder() {
            return true;
         }

         @Override
         public final void collect(int doc) {
            doc = doc + docBase;
            try {
               Document document = reader.document(doc, _selector);
               String key = document.get(PostgisSpatial.SPATIAL_INDEX_UUID_FIELD);
               FeatureId featureId = unrefinedSpatialMatches.get(key);
               if (featureId != null) {
                  matches.add(featureId);
                  docIndexLookup.put(featureId, doc);
               }
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }

         @Override
         public void setNextReader(IndexReader reader, int docBase) {
            this.docBase = docBase;
         }
      });

      if (matches.isEmpty()) {
         return bits;
      } else {
         return applySpatialFilter(matches, docIndexLookup, bits);
      }
   }

   private BitSet applySpatialFilter(Set<FeatureId> matches,
         Map<FeatureId, Integer> docIndexLookup, BitSet bits) throws IOException {
      Id fidFilter = _filterFactory.id(matches);
      String ftn = _featureSource.getSchema().getName().getLocalPart();
      String[] geomAtt = {_featureSource.getSchema().getGeometryDescriptor().getLocalName()};
      FeatureCollection<SimpleFeatureType, SimpleFeature> features = _featureSource
            .getFeatures(new DefaultQuery(ftn, fidFilter, geomAtt));
      FeatureIterator<SimpleFeature> iterator = features.features();

      try {
         while (iterator.hasNext()) {
            SimpleFeature feature = iterator.next();
            if (evaluateFeature(feature)) {
               FeatureId featureId = feature.getIdentifier();
               bits.set(docIndexLookup.get(featureId));
            }
         }
      } finally {
         iterator.close();
      }
      return bits;
   }

   private boolean evaluateFeature(SimpleFeature feature) {
      try {
         return getFilter().evaluate(feature);
      } catch (TopologyException e) {
         if (!warned) {
            warned = true;
            logger.warn(e.getMessage() + " errors are occuring with filter: " + getFilter(), e);
         }
         logger.debug(
               MessageFormat.format("{0}: occurred during a search: {1} on feature: {2}",
                     e.getMessage(), getFilter(), feature.getDefaultGeometry()), e);
         return false;
      }
   }

   private synchronized org.opengis.filter.Filter getFilter() {
      if (_spatialFilter == null) {
         _spatialFilter = createFilter(_featureSource);
      }

      return _spatialFilter;
   }

   /**
    * Returns all the FeatureId and ID attributes based on the query against the spatial index
    *
    * @return all the FeatureId and ID attributes based on the query against the spatial index
    */
   protected synchronized Map<String, FeatureId> unrefinedSpatialMatches() {
      if (_unrefinedMatches == null) {
         Geometry geom = null;
         // _index.query returns geometries that intersect with provided envelope. To use later a spatial filter that
         // provides geometries that don't intersect with the query envelope (_geom) should be used a full extent
         // envelope in this method, instead of the query envelope (_geom)
         if (getFilter().getClass().getName().equals("org.geotools.filter.spatial.DisjointImpl")) {
            try {
               WKTReader reader = new WKTReader();

               String geomWKT = "POLYGON((-180 90, 180 90, 180 -90, -180 -90, -180 90))";
               geom = reader.read(geomWKT);
            } catch (Exception ex) {
               logger.error(ex.getMessage() + ": Error in unrefinedSpatialMatches.", ex);
               return _unrefinedMatches;
            }
         } else {
            geom = _geom;
         }

         List<Pair<FeatureId, String>> fids = _index.query(geom.getEnvelopeInternal());
         _unrefinedMatches = new HashMap<String, FeatureId>();
         for (Pair<FeatureId, String> match : fids) {
            _unrefinedMatches.put(match.two(), match.one());
         }
      }
      return _unrefinedMatches;
   }

   protected org.opengis.filter.Filter createFilter(FeatureSource source) {
      String geomAttName = source.getSchema().getGeometryDescriptor().getLocalName();
      PropertyName geomPropertyName = _filterFactory.property(geomAttName);

      Literal geomExpression = _filterFactory.literal(_geom);
      return createGeomFilter(_filterFactory, geomPropertyName, geomExpression);
   }

   protected SpatialOperator createGeomFilter(FilterFactory2 filterFactory,
         PropertyName geomPropertyName, Literal geomExpression) {
      throw new UnsupportedOperationException(
            "createGeomFilter must be overridden if createFilter is not overridden");
   }

}
