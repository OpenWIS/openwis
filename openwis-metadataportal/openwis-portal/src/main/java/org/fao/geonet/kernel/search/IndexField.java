package org.fao.geonet.kernel.search;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.ObjectUtils;
import org.apache.solr.common.util.DateUtil;

/**
 * Names of fields in the index.
 */
public enum IndexField {

   /** The Constant ABSTRACT. */
   ABSTRACT("abstract"),

   /** The Constant ANY. */
   ANY("any"),

   /** The ANYTEXT. */
   ANYTEXT("anytext"),

   /** The AUTHORITY. */
   AUTHORITY("authority"),

   /** The Constant CATEGORY_ID. */
   CATEGORY_ID("_categoryId"),

   /** The Constant CATEGORY_NAME. */
   CATEGORY_NAME("_categoryName"),

   /** The Constant CHANGE_DATE. */
   CHANGE_DATE("changeDate", true, Date.class),

   /** The Constant CHANGE_DATE. */
   _CHANGE_DATE("_changeDate", true, Date.class),

   /** The constant _LOCAL_IMPORT_DATE */
   _LOCAL_IMPORT_DATE("_localImportDate", true, Date.class),

   /** The Constant DUMMY. */
   DUMMY("_dummy"),

   /** The Constant EAST. */
   EAST("eastBL"),

   /** The Constant INSPIRE_ANNEX. */
   INSPIRE_ANNEX("inspireannex"),

   /** The Constant INSPIRE_THEME. */
   INSPIRE_THEME("inspiretheme"),

   /** The Constant IS_TEMPLATE. */
   IS_TEMPLATE("_isTemplate"),

   /** The Constant INSPIRE_CAT. */
   INSPIRE_CAT("inspirecat"),

   /** The Constant KEYWORD. */
   KEYWORD("keyword"),

   /** The Constant METADATA_STANDARD_NAME. */
   METADATA_STANDARD_NAME("metadataStandardName"),

   /** The Constant NORTH. */
   NORTH("northBL"),

   /** The Constant OWNER. */
   OWNER("_owner"),

   /** The Constant PROTOCOL. */
   PROTOCOL("protocol"),

   /** The Constant SOURCE. */
   SOURCE("_source"),

   /** The Constant SOUTH. */
   SOUTH("southBL"),

   /** The SPATIAL. */
   SPATIAL("spatial"),

   /** The SPATIAL REPRENTATION. */
   SPATIAL_REPRESENTATION("spatialRepresenation"),

   /** The Constant TITLE. */
   TITLE("title"),

   /** The DEFAUL t_ title. */
   DEFAULT_TITLE("_defaultTitle"),

   /** The AL t_ title. */
   ALT_TITLE("altTitle"),

   /** The Constant _TITLE. (Use for sorting) */
   _TITLE("_title"),

   /** The Constant TOPIC_CATEGORY. */
   TOPIC_CATEGORY("topicCat"),

   /** The Constant TYPE. */
   TYPE("type"),

   /** The Constant UUID. Since UUIDs are case insensitive and because Solr does not process values stored for primary keys,
    *  the value of this field is converted to lowercase before being stored in the index.
    */
   UUID("_uuid"),
   
   UUID_TRUNCATED("uuid"),
   
   /** The original, unprocessed UUID.  This value is used in OAI-PMH results. */
   UUID_ORIGINAL("uuid_original"),

   /** The Constant WEST. */
   WEST("westBL"),

   /** The Constant PARENTUUID. */
   PARENTUUID("parentUuid"),

   /** The Constant OPERATESON. */
   OPERATESON("operatesOn"),

   /** The KEYWOR d_ type. */
   KEYWORD_TYPE("keywordType"),

   /** The GE o_ des c_ code. */
   GEO_DESC_CODE("geoDescCode"),

   /** The TOPI c_ cat. */
   TOPIC_CAT("topicCat"),

   /** The DATASE t_ lang. */
   DATASET_LANG("datasetLang"),

   /** The DENOMINATOR. */
   DENOMINATOR("denominator", true, Integer.class),

   /** The Constant SCHEMA. */
   SCHEMA("_schema"),

   /** The FORMAT. */
   FORMAT("format"),

   /** The FIEL d_ id. */
   FILE_ID("fileId"),

   /** The Constant TEMPORALEXTENT_BEGIN. */
   TEMPORALEXTENT_BEGIN("tempExtentBegin", true, Date.class),

   /** The Constant TEMPORALEXTENT_END. */
   TEMPORALEXTENT_END("tempExtentEnd", true, Date.class),

   /** The Constant PUBLICATION_DATE. */
   PUBLICATION_DATE("publicationDate", true, Date.class),

   /** The Constant CREATE_DATE. */
   CREATE_DATE("createDate", true, Date.class),

   /** The CREATION_DATE. */
   CREATION_DATE("creationDate", true, Date.class),

   /** The Constant CREATE_DATE. */
   _CREATE_DATE("_createDate", true, Date.class),

   /** The Constant REVISION_DATE. */
   REVISION_DATE("revisionDate", true, Date.class),

   /** The DISTANC e_ val. */
   DISTANCE_VALUE("distanceVal", true, Double.class),

   /** The DISTANC e_ value. */
   DISTANCE_UOM("distanceUom"),

   /** The RELATION. */
   RELATION("relation"),

   /** The CRS. */
   CRS("crs"),

   /** The CRS code. */
   CRS_CODE("crsCode"),

   /** The CR s_ version. */
   CRS_VERSION("crsVersion"),

   /** The Constant DOWNLOAD. */
   DOWNLOAD("download"),

   /** The OR g_ name. */
   ORG_NAME("orgName"),

   /** The SE c_ constr. */
   SEC_CONSTR("secConstr"),

   /** The LANGUAGE. */
   LANGUAGE("language"),

   /** The level name. */
   LEVEL_NAME("levelName"),

   /** The IDENTIFIER. */
   IDENTIFIER("identifier"),

   /** The Constant DIGITAL. */
   DIGITAL("digital"),

   /** The Constant PAPER. */
   PAPER("paper"),

   /** The Constant SERVICE_TYPE. */
   SERVICE_TYPE("serviceType"),

   /** The SERVIC e_ typ e_ version. */
   SERVICE_TYPE_VERSION("serviceTypeVersion"),

   /** The OPERATION. */
   OPERATION("operation"),

   /** The OPERATE s_ on. */
   OPERATES_ON("operatesOn"),

   /** The OPERATE s_ o n_ identifier. */
   OPERATES_ON_IDENTIFIER("operatesOnIdentifier"),

   /** The OPERATE s_ o n_ name. */
   OPERATES_ON_NAME("operatesOnName"),

   /** The METADAT a_ poc. */
   METADATA_POC("metadataPOC"),

   /** The MIMETYPE. */
   MIMETYPE("mimetype"),

   /** The LINEAGE. */
   LINEAGE("lineage"),

   /** The SPECIFICATIO n_ date. */
   SPECIFICATION_DATE("specificationDate", true, Date.class),

   /** The SPECIFICATIO n_ dat e_ type. */
   SPECIFICATION_DATE_TYPE("specificationDateType"),

   /** The ACCES s_ constr. */
   ACCESS_CONSTR("accessConstr"),

   /** The OTHE r_ constr. */
   OTHER_CONSTR("otherConstr"),

   /** The CLASSIF. */
   CLASSIF("classif"),

   /** The CONDITIO n_ applyin g_ t o_ acces s_ an d_ use. */
   CONDITION_APPLYING_TO_ACCESS_AND_USE("conditionApplyingToAccessAndUse"),

   /** The COUPLIN g_ type. */
   COUPLING_TYPE("couplingType"),

   /** The DEGREE. */
   DEGREE("degree"),

   /** The SPECIFICATIO n_ title. */
   SPECIFICATION_TITLE("specificationTitle"),

   /** The Constant ID. */
   ID("_id"),

   /** The Constant ROOT. */
   ROOT("_root"),

   /** The Constant HAS_XLINK. */
   HAS_XLINK("_hasxlinks"),

   /** The Constant XLINKS. */
   XLINKS("_xlinks"),

   /** The Constant IS_HARVESTED. */
   IS_HARVESTED("_isHarvested"),

   /** The Constant POPULARITY. */
   POPULARITY("_popularity"),

   /** The Constant RATING. */
   RATING("_rating"),

   /** The Constant IS_GLOBAL. */
   IS_GLOBAL("_isGlobal"),

   /** The Constant REQUEST_URL. */
   REQUEST_URL("_linkOpenwisRequestUrl"),

   /** The Constant SUBSCRIBE_URL. */
   SUBSCRIBE_URL("_linkOpenwisSubscribeUrl"),

   /** The other actions URL **/
   OTHER_ACTIONS_URL("_linkOtherActions"),

   /** The Constant SCORE. */
   SCORE("score"),

   /** The Constant GEOMETRY. */
   GEOMETRY("_geometry"),

   /** The Constant LOGO_URL. */
   LOGO_URL("logoUrl"),

   /** The Constant EFFECTIVE_DATAPOLICY in Portal. */
   EFFECTIVE_DATAPOLICY("_effectiveDatapolicy"),

   /** The Constant DATAPOLICY. Extracted in PM */
   DATAPOLICY("_datapolicy"),

   /** The Constant OVERRIDDEN_DATAPOLICY. Overriden in PM */
   OVERRIDDEN_DATAPOLICY("_overriddenDatapolicy"),

   /** The Constant IS_FED. */
   IS_FED("_isFed"),

   /** The SUBJECT. */
   SUBJECT("subject"),

   /** The Constant IS_INGESTED. */
   IS_INGESTED("_isIngested"),

   /** The Constant FILE_EXTENSION. */
   FILE_EXTENSION("_fileExtension"),

   /** The Constant OVERRIDDEN_FILE_EXTENSION. */
   OVERRIDDEN_FILE_EXTENSION("_overriddenFileExtension"),

   /** The Constant FNC_PATTERN. */
   FNC_PATTERN("_fncPattern"),

   /** The Constant OVERRIDDEN_FNC_PATTERN. */
   OVERRIDDEN_FNC_PATTERN("_overriddenFncPattern"),

   /** The Constant GTS_CATEGORY. */
   GTS_CATEGORY("_gtsCategory"),

   /** The Constant OVERRIDDEN_GTS_CATEGORY. */
   OVERRIDDEN_GTS_CATEGORY("_overriddenGtsCategory"),

   /** The Constant LOCAL_DATA_SOURCE. */
   LOCAL_DATA_SOURCE("_localDataSource"),

   /** The Constant ORIGINATOR. */
   ORIGINATOR("_originator"),

   /** The Constant PRIORITY. */
   PRIORITY("_priority"),

   /** The Constant OVERRIDDEN_PRIORITY. */
   OVERRIDDEN_PRIORITY("_overriddenPriority"),

   /** The Constant PROCESS. */
   PROCESS("_process"),

   /** The DESCRIPTION. */
   DESCRIPTION("description"),

   /** The display order. */
   DISPLAY_ORDER("_displayOrder"),

   /** The TERM. */
   TERM("term"),

   /** The TER m_ count. */
   TERM_COUNT("count"),

   /** The stop gap. */
   STOP_GAP("_stopGap");

   /** The field. */
   private final String field;

   /** The range. */
   private final boolean range;

   /** The clazz. */
   @SuppressWarnings("rawtypes")
   private final Class clazz;

   /**
    * Instantiates a new index field.
    *
    * @param field the field
    * @param range the range
    * @param clazz the clazz
    */
   @SuppressWarnings("rawtypes")
   private IndexField(String field, boolean range, Class clazz) {
      this.field = field;
      this.range = range;
      this.clazz = clazz;
   }

   /**
    * Instantiates a new index field.
    *
    * @param field the field
    */
   private IndexField(String field) {
      this.field = field;
      range = false;
      clazz = String.class;
   }

   /**
    * Value from string.
    *
    * @param value the value
    * @return the object
    * @throws ParseException the parse exception
    */
   public Object valueFromString(String value) throws ParseException {
      Object result;
      if (String.class.equals(clazz)) {
         result = value;
      } else if (Date.class.equals(clazz)) {
         result = DateUtil.parseDate(value);
      } else if (Double.class.equals(clazz)) {
         result = Double.valueOf(value);
      } else if (Integer.class.equals(clazz)) {
         result = Integer.valueOf(value);
      } else {
         // By default return the object
         result = value;
      }
      return result;
   }

   /**
    * Value to string.
    *
    * @param value the value
    * @return the string
    */
   public String valueToString(Object value) {
      String result;
      if (String.class.equals(clazz)) {
         result = (String) value;
      } else if (Date.class.equals(clazz)) {
         result = DateUtil.getThreadLocalDateFormat().format((Date) value);
      } else if (Double.class.equals(clazz)) {
         result = String.valueOf(value);
      } else {
         // By default return the object
         result = ObjectUtils.toString(value);
      }
      return result;

   }

   /**
    * Gets the field.
    *
    * @param fieldName the field name
    * @return the field
    */
   public static IndexField getField(String fieldName) {
      IndexField result = null;
      for (IndexField field : values()) {
         if (field.getField().equals(fieldName)) {
            result = field;
            break;
         }
      }
      return result;
   }

   /**
    * Checks if is range.
    *
    * @return true, if is range
    */
   public boolean isRange() {
      return range;
   }

   /**
    * Gets the field.
    *
    * @return the field
    */
   public String getField() {
      return field;
   }

   /**
    * Checks if is date.
    *
    * @return true, if is date
    */
   public boolean isDate() {
      return Date.class.equals(clazz);
   }

}