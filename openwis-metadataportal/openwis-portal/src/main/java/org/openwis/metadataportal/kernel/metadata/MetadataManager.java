/**
 *
 */
package org.openwis.metadataportal.kernel.metadata;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.AbstractMetadata;
import org.openwis.metadataportal.model.metadata.DeletedMetadata;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.Template;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 *
 */
public class MetadataManager implements IMetadataManager {
   public static final String SET_TITLE_XSL = "set-title.xsl";
   
   /** The title template for stop gap. */
   private static final String STOP_GAP_TITLE_TEMPLATE = "Draft Stop-Gap metadata generated for bulletin  {0}";
   
   private Dbms dbms;

   /** The string Index field mapping. */
   private final Map<String, IndexField> mmEnumMap;

   /**
    * The data manager.
    */
   private DataManager dataManager;

   private SettingManager settingManager;

   private String appPath;

   /**
    * Default constructor.
    * Builds a MetadataManager.
    */
   public MetadataManager() {
      super();
      mmEnumMap = new HashMap<String, IndexField>();
      mmEnumMap.put("urn", IndexField.UUID);
      mmEnumMap.put("title", IndexField._TITLE);
      mmEnumMap.put("category", IndexField.CATEGORY_NAME);
      mmEnumMap.put("originator", IndexField.ORIGINATOR);
      mmEnumMap.put("process", IndexField.PROCESS);
      mmEnumMap.put("gtsCategory", IndexField.GTS_CATEGORY);
      mmEnumMap.put("localDataSource", IndexField.LOCAL_DATA_SOURCE);
      mmEnumMap.put("ingested", IndexField.IS_INGESTED);
      mmEnumMap.put("fed", IndexField.IS_FED);
   }

   /**
    * Default constructor.
    * Builds a MetadataManager.
    * @param dbms
    */
   public MetadataManager(Dbms dbms) {
      this();
      this.dbms = dbms;
   }

   /**
    * Default constructor.
    * Builds a MetadataManager.
    * @param dbms
    */
   public MetadataManager(Dbms dbms, DataManager dataManager, SettingManager settingManager,
         String appPath) {
      this();
      this.dbms = dbms;
      this.dataManager = dataManager;
      this.settingManager = settingManager;
      this.appPath = appPath;
   }

   /**
    * Update metadata category.
    *
    * @param mdId the metadata ID
    * @param categoryId the category ID
    * @throws SQLException
    */
   @Override
   public void updateCategory(String urn, Integer categoryId) throws Exception {
      // Create a deleted metadata manager to track category changes
      IDeletedMetadataManager dmm = new DeletedMetadataManager(dbms);
      DeletedMetadata deletedMetadata = dmm.createDeletedMetadataFromMetadataUrn(urn);

      String updateQuery = "UPDATE Metadata SET category=?,localImportDate=? WHERE uuid=?";
      dbms.execute(updateQuery, categoryId, new ISODate().toString(), urn);
      
      // clean if necessary deletedmetadata for the new category
      dmm.clean(urn, categoryId);

      // Will consider the metadata as deleted for the current category
      if (deletedMetadata != null && deletedMetadata.getCategory() != categoryId) {
         dmm.insertDeletedMetadata(deletedMetadata);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataManager#getAllMetadataByCategory(java.lang.Integer)
    */
   @Override
   public List<Metadata> getAllMetadataByCategory(Integer categoryId) throws Exception {
      List<Metadata> result = new ArrayList<Metadata>();

      String query = "SELECT Metadata.id as mdId, uuid, changeDate, localImportDate, Categories.id as categId, Categories.name, schemaId, isTemplate "
            + "FROM Metadata, Categories WHERE Metadata.category=Categories.id AND Categories.id=?";

      // Check record already into the DB and get information.
      @SuppressWarnings("unchecked")
      List<Element> records = dbms.select(query, categoryId).getChildren();

      if (!records.isEmpty()) {
         for (Element elt : records) {
            result.add(buildMetadataFromElement(elt));
         }
      }
      return result;

   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataManager#getMetadataInfoByUrn(java.lang.String)
    */
   @Override
   @SuppressWarnings("unchecked")
   public Metadata getMetadataInfoByUrn(String uuid) throws Exception {
      String query = "SELECT Metadata.id as mdId, uuid, changeDate, localImportDate, Categories.id as categId, Categories.name, schemaId, isTemplate "
            + "FROM Metadata, Categories WHERE Metadata.category=Categories.id AND uuid=?";

      // Check record already into the DB and get information.
      List<Element> records = dbms.select(query, uuid).getChildren();

      if (records.isEmpty()) {
         return null;
      }
      return buildMetadataFromElement(records.get(0));
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataManager#getMetadataInfoById(java.lang.Integer)
    */
   @SuppressWarnings("unchecked")
   @Override
   public AbstractMetadata getAbstractMetadataInfoById(Integer id) throws Exception {
      String query = "SELECT Metadata.id as mdId, uuid, changeDate, localImportDate, Categories.id as categId, Categories.name, "
            + "schemaId, isTemplate FROM Metadata LEFT JOIN Categories ON Metadata.category=Categories.id WHERE Metadata.id=?";
      // Check record already into the DB and get information.
      List<Element> records = dbms.select(query, id).getChildren();

      if (records.isEmpty()) {
         return null;
      }
      return buildAbstractMetadataFromElement(records.get(0));
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataManager#createMetadataFromTemplate(org.openwis.metadataportal.model.metadata.Metadata, org.openwis.metadataportal.model.metadata.Template)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Metadata createMetadataFromTemplate(Metadata metadata, Template template)
         throws Exception {

      //Get data and schema from template.
      StringBuffer query = new StringBuffer("SELECT schemaId, data FROM Metadata ");
      List<Element> listTempl = null;
      if (template.getId() != null) {
         query.append("WHERE id=?");
         listTempl = dbms.select(query.toString(), template.getId()).getChildren();
      } else {
         query.append("WHERE uuid=?");
         listTempl = dbms.select(query.toString(), template.getUrn()).getChildren();
      }
      if (listTempl.size() == 0) {
         throw new IllegalArgumentException(MessageFormat.format(
               "Template ID ({0}) or URN ({1}) not found.", template.getId(), template.getUrn()));
      }
      Element el = listTempl.get(0);
      String schema = el.getChildText("schemaid");
      String data = el.getChildText("data");

      //--- generate a new metadata id
      Element env = new Element("env");

      // FIXME Try to not use ID for XSL Transfo.
      //      env.addContent(new Element("id").setText(id));
      env.addContent(new Element("uuid").setText(metadata.getUrn()));
      //      FIXME Parent UUID? env.addContent(new Element("parentUuid").setText(parentUuid));
      env.addContent(new Element("updateDateStamp").setText("yes"));
      //      env.addContent(new Element("datadir").setText(Lib.resource.getDir(dataDir,
      //            Params.Access.PRIVATE, id)));
      //      env.addContent(new Element("dataPolicy").setText(metadata.getDataPolicy().getName()));

      env.addContent(new Element("changeDate").setText(metadata.getChangeDate()));
      env.addContent(new Element("siteURL").setText(dataManager.getSiteURL()));
      Element system = settingManager.get("system", -1);
      env.addContent(Xml.transform(system, appPath + Geonet.Path.STYLESHEETS + "/xml/config.xsl"));

      //--- setup root element

      Element root = new Element("root");
      root.addContent(Xml.loadString(data, false));
      root.addContent(env);

      //--- do the XSL transformation using update-fixed-info.xsl

      String styleSheet = dataManager.getSchemaDir(schema) + Geonet.File.UPDATE_FIXED_INFO;

      Element xml = Xml.transform(root, styleSheet);
      
      if (metadata.isStopGap()) {
         xml = setTitleForStopGap(xml, schema, metadata);
      }

      metadata.setData(xml);
      metadata.setSchema(schema);

      return metadata;
   }
   
   /**
    * Set the specific title for stop-gap metadata
    * 
    * @param xml the metadata xml to update with title element
    * @param schema the schema (should be iso19139)
    * @param metadata the metadata
    * @return the new updated xml element
    * @throws Exception if an error occurs
    */
   private Element setTitleForStopGap(Element xml, String schema, Metadata metadata)
         throws Exception {
      // Set specific title for stop gap
      int indexSep = metadata.getUrn().lastIndexOf("::");
      if (indexSep < 0) {
         return xml;
      }
      String ttaaii = metadata.getUrn().substring(indexSep + 2);
      String title = MessageFormat.format(STOP_GAP_TITLE_TEMPLATE, ttaaii);

      String setTitleStyleSheet = dataManager.getSchemaDir(schema) + SET_TITLE_XSL;
      HashMap<String, String> params = new HashMap<String, String>();
      params.put("title", title);

      return Xml.transform(xml, setTitleStyleSheet, params);
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataManager#getIndexFieldColumn(java.lang.String)
    */
   @Override
   public IndexField getIndexFieldColumn(String columnName) {
      return mmEnumMap.get(columnName);
   }


   /**
    * {@inheritDoc}
    * @see org.openwis.metadataportal.kernel.metadata.IMetadataManager#getAllMetadata()
    */
   @SuppressWarnings("unchecked")
   @Override
   public Integer getAllMetadata() throws Exception {
      List<Element> recs = dbms.select("select count(*) as count from metadata").getChildren();
      int nbMetadata = 0;
      for (Element e : recs) {
         nbMetadata = new Integer(e.getChildText("count"));
      }
      return nbMetadata;
   }

   /**
    * Description goes here.
    * @param element
    * @return
    */
   private Metadata buildMetadataFromElement(Element record) {
      Metadata md = new Metadata();
      md.setId(Integer.parseInt(record.getChildText("mdid")));
      md.setUrn(record.getChildText("uuid"));
      md.setChangeDate(record.getChildText("changedate"));
      md.setLocalImportDate(record.getChildText("localimportdate"));
      Category category = new Category();
      category.setId(Integer.parseInt(record.getChildText("categid")));
      category.setName(record.getChildText("name"));
      md.setCategory(category);
      md.setSchema(record.getChildText("schemaid"));
      
      // stop gap state given by the category name
      md.setStopGap(CategoryManager.DRAFT_CATEGORY_NAME.equals(category.getName()));
      
      return md;
   }

   /**
    * Description goes here.
    * @param element
    * @return
    */
   private AbstractMetadata buildAbstractMetadataFromElement(Element record) {
      if ("n".equals(record.getChildText("istemplate"))) {
         return buildMetadataFromElement(record);
      }

      Template template = new Template();
      template.setId(Integer.parseInt(record.getChildText("mdid")));
      template.setUrn(record.getChildText("uuid"));
      template.setChangeDate(record.getChildText("changedate"));
      template.setSchema(record.getChildText("schemaid"));

      return template;
   }

   /**
    * Update Metadata.
    * @param dataPolicyId The data policy Identifier
    * @param metadataId The metadata identifier
    * @throws SQLException if an error occurs.
    */
   public void updateMetadata(int dataPolicyId, int metadataId) throws SQLException {
      //Get data and schema from template.
      String query = "UPDATE Metadata SET dataPolicy = ? WHERE id = ?";
      dbms.execute(query, dataPolicyId, metadataId);

   }

}
