package org.openwis.metadataportal.search.solr.spatial;

/**
 * The Interface PostgisSpatial.
 */
public interface PostgisSpatial {

   /** The Constant SPATIAL_INDEX_TABLE. */
   public static final String SPATIAL_INDEX_TABLE = "spatialindex";

   /** The Constant SPATIAL_INDEX_COLUMN_ID. */
   public static final String SPATIAL_INDEX_COLUMN_ID = "fid";

   /** The Constant SPATIAL_INDEX_COLUMN_UUID. */
   public static final String SPATIAL_INDEX_COLUMN_UUID = "_uuid";

   /** The Constant SPATIAL_INDEX_COLUMN_GEOMETRY. */
   public static final String SPATIAL_INDEX_COLUMN_GEOMETRY = "geo";

   /** The Constant SPATIAL_INDEX_LUCENCE_COLUMN. */
   public static final String SPATIAL_INDEX_GEOMETRY_FIELD = "_geometry";

   /** The Constant SPATIAL_INDEX_UUID_FIELD. */
   public static final String SPATIAL_INDEX_UUID_FIELD = "_uuid";

   /** The Constant REQUEST_GEOMETRY_ELEMENT. */
   public static final String REQUEST_GEOMETRY_ELEMENT = "geometry";

}
