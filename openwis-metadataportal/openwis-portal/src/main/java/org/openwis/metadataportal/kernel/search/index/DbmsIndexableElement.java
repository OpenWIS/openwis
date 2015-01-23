package org.openwis.metadataportal.kernel.search.index;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.Xml;
import jeeves.xlink.Processor;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.XmlSerializer;
import org.fao.geonet.kernel.search.IndexField;
import org.jdom.Attribute;
import org.jdom.Element;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.IProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.ProductMetadataManager;

/**
 * The Class DbmsIndexableElement. <P>
 * Explanation goes here. <P>
 */
public class DbmsIndexableElement implements IndexableElement {

   /** The jeeves dbms. */
   private final Dbms dbms;

   /** The schema. */
   private String schema;

   /** The metadata. */
   private Element metadata;

   /** The is template. */
   private String isTemplate;

   /** The create date. */
   private String createDate;

   /** The change date. */
   private String changeDate;

   /** The local import date **/
   private String localImportDate;

   /** The source. */
   private String source;

   /** The uuid. */
   private final String uuid;

   /** The id. */
   private String id;

   /** The is harvested. */
   private String isHarvested;

   /** The owner. */
   private String owner;

   /** The popularity. */
   private String popularity;

   /** The rating. */
   private String rating;

   /** The datapolicy. */
   private String datapolicy;

   /** The category id. */
   private String categoryId;

   /** The category name. */
   private String categoryName;

   /** The product metadata. */
   private ProductMetadata productMetadata;

   /** The stop gap. */
   private String stopGap;

   /** The extra fields. */
   private List<Element> extraFields = null;

   /** The display order. */
   private String displayOrder;

   /**
    * Instantiates a new dbms indexable element.
    *
    * @param dbms the dbms
    * @param id the id
    * @param productMetadata
    */
   public DbmsIndexableElement(Dbms dbms, String uuid, ProductMetadata productMetadata) {
      super();
      this.dbms = dbms;
      this.uuid = uuid;
      this.productMetadata = productMetadata;
   }

   /**
    * Instantiates a new dbms indexable element.
    *
    * @param dbms the dbms
    * @param uuid the uuid
    */
   public DbmsIndexableElement(Dbms dbms, String uuid) {
      super();
      this.dbms = dbms;
      this.uuid = uuid;
      productMetadata = null;
   }

   /**
    * Gets the type.
    *
    * @return the type
    */
   public String getSchema() {
      // Lazy loading
      if (schema == null) {
         fillElement();
      }
      return schema;
   }

   /**
    * Fill element.
    */
   private void fillElement() {
      // get metadata
      try {
         String query = "SELECT md.id, md.data, md.schemaId, md.createDate, md.changeDate, md.localimportdate, md.source, md.isTemplate, "
               + "md.isHarvested, md.owner, md.popularity, md.rating, md.datapolicy, md.category, Categories.name, md.displayorder "
               + "FROM Metadata md LEFT JOIN Categories ON md.category=Categories.id WHERE md.uuid = ?";

         Element rec = dbms.select(query, StringEscapeUtils.escapeSql(uuid)).getChild("record");

         String xmlData = rec.getChildText("data");
         metadata = Xml.loadString(xmlData, false);
         id = rec.getChildText("id");
         schema = rec.getChildText("schemaid");
         createDate = rec.getChildText("createdate");
         changeDate = rec.getChildText("changedate");
         localImportDate = rec.getChildText("localimportdate");
         source = rec.getChildText("source");
         isTemplate = rec.getChildText("istemplate");
         isHarvested = rec.getChildText("isharvested");
         owner = rec.getChildText("owner");
         popularity = rec.getChildText("popularity");
         rating = rec.getChildText("rating");
         // OpenWIS Data Policy
         datapolicy = rec.getChildText("datapolicy");
         // category
         categoryId = rec.getChildText("category");
         categoryName = rec.getChildText("name");
         displayOrder = rec.getChildText("displayorder");
         // FIXME StopGap
         if (CategoryManager.DRAFT_CATEGORY_NAME.equals(categoryName)) {
            stopGap = "true";
         } else {
            stopGap = "false";
         }

      } catch (Exception e) {
         Log.error(Geonet.INDEX_ENGINE, "Could not retrieve element for URN: " + uuid, e);
      }
   }

   /**
    * Gets the metadata.
    *
    * @return the metadata
    */
   public Element getMetadata() {
      // Lazy
      if (metadata == null) {
         fillElement();
      }
      return metadata;
   }

   /**
    * Gets the more fields.
    *
    * @return the more fields
    */
   public List<Element> getMoreFields() {
      // Lazy
      if (extraFields == null) {
         List<Element> elts = new ArrayList<Element>();
         Element md = getMetadata(); // ensure fields are loaded

         // extracting and indexing any xlinks
         if (XmlSerializer.resolveXLinks()) {
            List<Attribute> xlinks = Processor.getXLinks(md);
            if (xlinks.size() > 0) {
               elts.add(makeField(IndexField.HAS_XLINK, "1", true, true, false));
               StringBuilder sb = new StringBuilder();
               for (Attribute xlink : xlinks) {
                  sb.append(xlink.getValue());
                  sb.append(" ");
               }
               elts.add(makeField(IndexField.XLINKS, sb.toString(), true, true, false));
               Processor.detachXLink(md);
            } else {
               elts.add(makeField(IndexField.HAS_XLINK, "0", true, true, false));
            }
         } else {
            elts.add(makeField(IndexField.HAS_XLINK, "0", true, true, false));
         }

         // get metadata table fields
         String root = md.getName();

         elts.add(makeField(IndexField.ROOT, root, true, true, false));
         elts.add(makeField(IndexField.SCHEMA, schema, true, true, false));
         elts.add(makeField(IndexField._CREATE_DATE, createDate, true, true, false));
         elts.add(makeField(IndexField._CHANGE_DATE, changeDate, true, true, false));
         elts.add(makeField(IndexField._LOCAL_IMPORT_DATE, localImportDate, true, true, false));
         elts.add(makeField(IndexField.SOURCE, source, true, true, false));
         elts.add(makeField(IndexField.IS_TEMPLATE, isTemplate, true, true, false));
         elts.add(makeField(IndexField.UUID, uuid.toLowerCase(), true, true, false));
         elts.add(makeField(IndexField.UUID_ORIGINAL, uuid, true, true, false));
         elts.add(makeField(IndexField.ID, id, true, true, false));
         elts.add(makeField(IndexField.IS_HARVESTED, isHarvested, true, true, false));
         elts.add(makeField(IndexField.OWNER, owner, true, true, false));
         elts.add(makeField(IndexField.DUMMY, "0", false, true, false));
         elts.add(makeField(IndexField.POPULARITY, popularity, true, true, false));
         elts.add(makeField(IndexField.RATING, rating, true, true, false));
         elts.add(makeField(IndexField.STOP_GAP, stopGap, true, true, false));

         // OpenWIS
         elts.add(makeField(IndexField.EFFECTIVE_DATAPOLICY, datapolicy, true, true, false));
         elts.add(makeField(IndexField.CATEGORY_ID, categoryId, true, true, false));
         elts.add(makeField(IndexField.CATEGORY_NAME, categoryName, true, true, false));
         elts.add(makeField(IndexField.DISPLAY_ORDER, displayOrder, true, false, false));

         if (productMetadata == null && isTemplate.equals("n")) {
            // query the data service for PM if md is not template...
            IProductMetadataManager pmm = new ProductMetadataManager();
            productMetadata = pmm.getProductMetadataByUrn(uuid);
         }

         // If not a metadata template.
         if (productMetadata != null) {
            elts.add(makeField(IndexField.IS_FED, productMetadata.isFed(), true, true, false));
            elts.add(makeField(IndexField.FILE_EXTENSION, productMetadata.getFileExtension(), true,
                  true, false));
            elts.add(makeField(IndexField.FNC_PATTERN, productMetadata.getFncPattern(), true, true,
                  false));
            elts.add(makeField(IndexField.GTS_CATEGORY, productMetadata.getGtsCategory(), true,
                  true, false));
            elts.add(makeField(IndexField.IS_INGESTED, productMetadata.isIngested(), true, true,
                  false));
            elts.add(makeField(IndexField.LOCAL_DATA_SOURCE, productMetadata.getLocalDataSource(),
                  true, true, false));
            elts.add(makeField(IndexField.ORIGINATOR, productMetadata.getOriginator(), true, true,
                  false));
            elts.add(makeField(IndexField.DATAPOLICY, productMetadata.getDataPolicy(), true, true,
                  false));
            elts.add(makeField(IndexField.OVERRIDDEN_DATAPOLICY,
                  productMetadata.getOverridenDataPolicy(), true, true, false));
            elts.add(makeField(IndexField.OVERRIDDEN_GTS_CATEGORY,
                  productMetadata.getOverridenGtsCategory(), true, true, false));
            elts.add(makeField(IndexField.OVERRIDDEN_FILE_EXTENSION,
                  productMetadata.getOverridenFileExtension(), true, true, false));
            elts.add(makeField(IndexField.OVERRIDDEN_FNC_PATTERN,
                  productMetadata.getOverridenFncPattern(), true, true, false));
            elts.add(makeField(IndexField.OVERRIDDEN_PRIORITY,
                  productMetadata.getOverridenPriority(), true, true, false));
            elts.add(makeField(IndexField.PRIORITY, productMetadata.getPriority(), true, true,
                  false));
            elts.add(makeField(IndexField.PROCESS, productMetadata.getProcess(), true, true, false));
         }

         extraFields = elts;
      }
      return extraFields;
   }

   /**
    * Make field.
    *
    * @param name the name
    * @param value the value
    * @param store the store
    * @param index the index
    * @param token the token
    * @return the element
    */
   private static Element makeField(IndexField name, Object value, boolean store, boolean index,
         boolean token) {
      Element field = new Element("Field");

      field.setAttribute("name", name.getField());
      field.setAttribute("string", ObjectUtils.toString(value));
      field.setAttribute("store", store + "");
      field.setAttribute("index", index + "");
      field.setAttribute("token", token + "");

      return field;
   }

   /**
    * Gets the checks if is template.
    *
    * @return the checks if is template
    */
   public String getIsTemplate() {
      if (isTemplate == null) {
         fillElement();
      }
      return isTemplate;
   }

   /**
    * Gets the categoryId.
    * @return the categoryId.
    */
   public String getCategoryId() {
      if (categoryId == null) {
         fillElement();
      }
      return categoryId;
   }

   /**
    * Gets the uuid.
    *
    * @return the uuid
    */
   public String getUuid() {
      if (uuid == null) {
         fillElement();
      }
      return uuid;
   }
   
   /**
    * Returns the unique key of this DbmsIndexableElement.
    * 
    * @return
    */
   public String getUniqueKey() {
      return getUuid().toLowerCase();
   }

   /**
    * Gets the categoryName.
    * @return the categoryName.
    */
   public String getCategoryName() {
      if (categoryName == null) {
         fillElement();
      }
      return categoryName;
   }

   /**
    * Gets the dbms.
    *
    * @return the dbms
    */
   public Dbms getDbms() {
      return dbms;
   }

   /**
    * To string.
    *
    * @return the string
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("IndexableElt: {0}", uuid);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof DbmsIndexableElement)) {
         return false;
      }
      DbmsIndexableElement other = (DbmsIndexableElement) obj;
      if (uuid == null) {
         if (other.uuid != null) {
            return false;
         }
      } else if (!uuid.equals(other.uuid)) {
         return false;
      }
      return true;
   }

   @Override
   public Object clone() {
      return new DbmsIndexableElement(dbms, uuid, productMetadata);
   }
}
