//==============================================================================
//===
//=== DataManager
//===
//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.kernel;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jeeves.constants.Jeeves;
import jeeves.exceptions.JeevesException;
import jeeves.exceptions.OperationNotAllowedEx;
import jeeves.resources.dbms.Dbms;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.SerialFactory;
import jeeves.utils.Xml;
import jeeves.utils.Xml.ErrorHandler;
import jeeves.xlink.Processor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.kernel.schema.MetadataSchema;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.IndexEvent;
import org.fao.geonet.kernel.search.IndexField;
import org.fao.geonet.kernel.search.IndexListener;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.util.ISODate;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.openwis.dataservice.CannotDeleteAllProductMetadataException_Exception;
import org.openwis.dataservice.ProductMetadata;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.kernel.metadata.DeletedMetadataManager;
import org.openwis.metadataportal.kernel.metadata.IDeletedMetadataManager;
import org.openwis.metadataportal.kernel.metadata.IProductMetadataManager;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.kernel.metadata.ProductMetadataManager;
import org.openwis.metadataportal.kernel.search.index.DbmsIndexableElement;
import org.openwis.metadataportal.kernel.search.index.IndexableElement;
import org.openwis.metadataportal.kernel.search.query.SearchException;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.datapolicy.Operation;
import org.openwis.metadataportal.model.datapolicy.OperationEnum;
import org.openwis.metadataportal.model.group.Group;
import org.openwis.metadataportal.model.metadata.AbstractMetadata;
import org.openwis.metadataportal.model.metadata.DeletedMetadata;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.Template;

//=============================================================================

/** Handles all operations on metadata (select,insert,update,delete etc...)
  */

public class DataManager implements IndexListener {

   private final String baseURL;

   private final EditLib editLib = new EditLib();

   private final ISearchManager searchMan;

   private final SettingManager settingMan;

   private final String dataDir;

   private final String appPath;

   /** static executor service to be able to shutdown it when application stops */
   private static final ExecutorService executor = Executors.newSingleThreadExecutor();

   /** The context. */
   private final ServiceContext context;

   //--------------------------------------------------------------------------
   //---
   //--- Constructor
   //---
   //--------------------------------------------------------------------------

   /** initializes the search manager and index not-indexed metadata
     */

   public DataManager(ServiceContext context, ISearchManager sm, Dbms dbms, SettingManager ss,
         String baseURL, String htmlCacheDir, String dataDir, String appPath) throws Exception {
      searchMan = sm;
      settingMan = ss;

      this.baseURL = baseURL;
      this.dataDir = dataDir;
      this.appPath = appPath;

      XmlSerializer.setSettingManager(ss);

      searchMan.addIndexListener(this);
      this.context = context;
   }

   /**
    * Gets the search man.
    *
    * @return the search man
    */
   public ISearchManager getSearchMan() {
      return searchMan;
   }

   /**
    * Gets the data dir.
    *
    * @return the data dir
    */
   public String getDataDir() {
      return dataDir;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.search.IndexListener#onIndexEvent(org.fao.geonet.kernel.search.IndexEvent)
    */
   @Override
   public void onIndexEvent(IndexEvent event) {
      Log.info(Geonet.INDEX_ENGINE, "Receive event: " + event);
      switch (event.getType()) {
      case AVAILABLE:
         executor.execute(new Runnable() {
            @Override
            public void run() {
               try {
                  searchMan.synchronizeDocs(context);
               } catch (SearchException e) {
                  Log.error(Geonet.DATA_MANAGER, "Could not sync index", e);
               }
            }
         });
         break;
      case UNAVAILABLE:
         // Nothing to do
      case COMMITTED:
         // Nothing to do
      case OPTIMIZED:
         // Nothing to do
      default:
         break;
      }
   }

   //--------------------------------------------------------------------------

   public void indexMetadata(Dbms dbms, String uuid, ProductMetadata productMetadata)
         throws Exception {
      if (Log.isDebug(Geonet.DATA_MANAGER)) {
         Log.debug(Geonet.DATA_MANAGER, "Indexing record (" + uuid + ")");
      }

      indexMetadata(dbms, uuid, searchMan, productMetadata, false);
   }

   //--------------------------------------------------------------------------

   public void startIndexGroup() throws Exception {
      searchMan.startIndexGroup();
   }

   //--------------------------------------------------------------------------

   public void endIndexGroup() throws Exception {
      searchMan.endIndexGroup();
   }

   //--------------------------------------------------------------------------

   public void indexMetadataGroup(Dbms dbms, String uuid, ProductMetadata productMetadata)
         throws Exception {
      if (Log.isDebug(Geonet.DATA_MANAGER)) {
         Log.debug(Geonet.DATA_MANAGER, "Indexing record (" + uuid + ")");
      }
      indexMetadata(dbms, uuid, searchMan, productMetadata, true);
   }

   //--------------------------------------------------------------------------

   public static void indexMetadata(Dbms dbms, String uuid, ISearchManager sm,
         ProductMetadata productMetadata, boolean indexGroup) throws Exception {
      try {
         indexMetadataI(dbms, uuid, sm, productMetadata, indexGroup);
      } catch (Exception e) {
         Log.error(Geonet.DATA_MANAGER, "The metadata document index with urn=" + uuid
               + " is corrupt/invalid - ignoring it. Error: " + e.getMessage());
         e.printStackTrace();
      }
   }

   //--------------------------------------------------------------------------

   private static void indexMetadataI(Dbms dbms, String uuid, ISearchManager sm,
         ProductMetadata productMetadata, boolean indexGroup) throws Exception {
      IndexableElement element = new DbmsIndexableElement(dbms, uuid, productMetadata);
      sm.index(element, !indexGroup);
   }

   //--------------------------------------------------------------------------

   public void rescheduleOptimizer(Calendar beginAt, int interval) throws Exception {
      searchMan.rescheduleOptimizer(beginAt, interval);
   }

   //--------------------------------------------------------------------------

   public void disableOptimizer() throws Exception {
      searchMan.disableOptimizer();
   }

   //--------------------------------------------------------------------------

   public void addSchema(String id, String xmlSchemaFile, String xmlSuggestFile,
         String xmlSubstitutesFile) throws Exception {
      editLib.addSchema(id, xmlSchemaFile, xmlSuggestFile, xmlSubstitutesFile);
   }

   //--------------------------------------------------------------------------

   public MetadataSchema getSchema(String name) {
      return editLib.getSchema(name);
   }

   //--------------------------------------------------------------------------

   public Set<String> getSchemas() {
      return editLib.getSchemas();
   }

   //--------------------------------------------------------------------------

   public boolean existsSchema(String name) {
      return editLib.existsSchema(name);
   }

   //--------------------------------------------------------------------------

   public String getSchemaDir(String name) {
      return editLib.getSchemaDir(name);
   }

   //--------------------------------------------------------------------------

   public void validate(String schema, Element md) throws Exception {
      Xml.validate(editLib.getSchemaDir(schema) + Geonet.File.SCHEMA, md);
   }

   //--------------------------------------------------------------------------

   public String getMetadataSchema(Dbms dbms, String id) throws Exception {
      List list = dbms.select("SELECT schemaId FROM Metadata WHERE id = " + id).getChildren();

      if (list.size() == 0)
         throw new IllegalArgumentException("Metadata not found for id : " + id);
      else {
         // get metadata
         Element record = (Element) list.get(0);
         return record.getChildText("schemaid");
      }
   }

   //--------------------------------------------------------------------------
   /**
    * Create XML schematron report.
    */
   public Element doSchemaTronForEditor(String schema, Element md, String lang) throws Exception {

      // enumerate the metadata xml so that we can report any problems found
      // by the schematron_xml script to the geonetwork editor
      editLib.enumerateTree(md);

      // get an xml version of the schematron errors and return for error display
      Element schemaTronXmlReport = getSchemaTronXmlReport(schema, md, lang);

      // remove editing info added by enumerateTree
      editLib.removeEditingInfo(md);

      return schemaTronXmlReport;
   }

   //--------------------------------------------------------------------------
   /**
    * Create XML schematron report for each set of rules defined
    * in schema directory.
    */
   public Element getSchemaTronXmlReport(String schema, Element md, String lang) throws Exception {
      // NOTE: this method assumes that you've run enumerateTree on the
      // metadata

      MetadataSchema metadataSchema = getSchema(schema);
      String[] rules = metadataSchema.getSchematronRules();

      // Schematron report is composed of one or more report(s)
      // for each set of rules.
      Element schemaTronXmlOut = new Element("schematronerrors", Edit.NAMESPACE);

      for (String rule : rules) {
         // -- create a report for current rules.
         // Identified by a rule attribute set to shematron file name
         Log.debug(Geonet.DATA_MANAGER, " - rule:" + rule);
         Element report = new Element("report", Edit.NAMESPACE);
         report.setAttribute("rule", rule.substring(0, rule.indexOf(".xsl")), Edit.NAMESPACE);

         String schemaTronXmlXslt = metadataSchema.getSchemaDir() + File.separator + rule;
         try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("lang", lang);
            params.put("rule", rule);
            params.put("dataDir", dataDir);
            Element xmlReport = Xml.transform(md, schemaTronXmlXslt, params);
            if (xmlReport != null)
               report.addContent(xmlReport);
         } catch (Exception e) {
            Log.error(Geonet.DATA_MANAGER, "WARNING: schematron xslt " + schemaTronXmlXslt
                  + " failed");
            e.printStackTrace();
         }

         // -- append report to main XML report.
         schemaTronXmlOut.addContent(report);
      }

      return schemaTronXmlOut;
   }

   //--------------------------------------------------------------------------
   /**
    * Valid the metadata record against its schema.
    * For each error found, an xsderror attribute is added to
    * the corresponding element trying to find the element
    * based on the xpath return by the ErrorHandler.
    *
    */
   private synchronized Element getXSDXmlReport(String schema, Element md) throws Exception {

      // NOTE: this method assumes that enumerateTree has NOT been run on the
      // metadata
      String schemaDir = getSchemaDir(schema);
      ErrorHandler errorHandler = new ErrorHandler();
      errorHandler.setNs(Edit.NAMESPACE);
      Element xsdErrors;

      try {
         xsdErrors = Xml.validateInfo(schemaDir + Geonet.File.SCHEMA, md, errorHandler);
      } catch (Exception e) {
         xsdErrors = JeevesException.toElement(e);
         return xsdErrors;
      }

      if (xsdErrors != null) {
         MetadataSchema mds = getSchema(schema);
         List<Namespace> schemaNamespaces = mds.getSchemaNS();

         //-- now get each xpath and evaluate it
         //-- xsderrors/xsderror/{message,xpath}
         List list = xsdErrors.getChildren();
         for (Object o : list) {
            Element elError = (Element) o;
            String xpath = elError.getChildText("xpath", Edit.NAMESPACE);
            String message = elError.getChildText("message", Edit.NAMESPACE);
            message = "\\n" + message;

            //-- get the element from the xpath and add the error message to it
            Element elem = Xml.selectElement(md, xpath, schemaNamespaces);
            if (elem != null) {
               String existing = elem.getAttributeValue("xsderror", Edit.NAMESPACE);
               if (existing != null)
                  message = existing + message;
               elem.setAttribute("xsderror", message, Edit.NAMESPACE);
            } else {
               Log.warning(Geonet.DATA_MANAGER, "WARNING: evaluating XPath " + xpath
                     + " against metadata failed - XSD validation message: " + message
                     + " will NOT be shown by the editor");
            }
         }
      }
      return xsdErrors;
   }

   //--------------------------------------------------------------------------
   //---
   //--- General purpose API
   //---
   //--------------------------------------------------------------------------

   /**
    * @deprecated To be removed....
    */
   @Deprecated
   public String extractUUID(String schema, Element md) throws Exception {
      String styleSheet = editLib.getSchemaDir(schema) + Geonet.File.EXTRACT_UUID;
      String uuid = Xml.transform(md, styleSheet).getText().trim();

      Log.debug(Geonet.DATA_MANAGER, "Extracted UUID '" + uuid + "' for schema '" + schema + "'");

      //--- needed to detach md from the document
      md.detach();

      return uuid;
   }

   //--------------------------------------------------------------------------

   public Element setUUID(String schema, String uuid, Element md) throws Exception {
      //--- setup environment

      Element env = new Element("env");
      env.addContent(new Element("uuid").setText(uuid));

      //--- setup root element

      Element root = new Element("root");
      root.addContent(md.detach());
      root.addContent(env.detach());

      //--- do an XSL  transformation

      String styleSheet = editLib.getSchemaDir(schema) + Geonet.File.SET_UUID;

      return Xml.transform(root, styleSheet);
   }

   @SuppressWarnings("unchecked")
   public List<Element> getMetadataByHarvestingSource(Dbms dbms, String harvestingSource)
         throws Exception {
      String query = "SELECT id FROM Metadata WHERE harvestUuid=?";
      return dbms.select(query, harvestingSource).getChildren();
   }

   //--------------------------------------------------------------------------

   public String getMetadataId(Dbms dbms, String uuid) throws Exception {
      String query = "SELECT id FROM Metadata WHERE uuid=?";

      List list = dbms.select(query, uuid).getChildren();

      if (list.size() == 0)
         return null;

      Element record = (Element) list.get(0);

      return record.getChildText("id");
   }

   //--------------------------------------------------------------------------

   public String getMetadataId(ServiceContext srvContext, String uuid) throws Exception {
      Dbms dbms = (Dbms) srvContext.getResourceManager().open(Geonet.Res.MAIN_DB);
      String query = "SELECT id FROM Metadata WHERE uuid=?";
      List list = dbms.select(query, uuid).getChildren();
      if (list.size() == 0)
         return null;
      Element record = (Element) list.get(0);
      return record.getChildText("id");
   }

   //--------------------------------------------------------------------------

   public String getMetadataUuid(Dbms dbms, String id) throws Exception {
      String query = "SELECT uuid FROM Metadata WHERE id=?";

      List list = dbms.select(query, new Integer(id)).getChildren();

      if (list.size() == 0)
         return null;

      Element record = (Element) list.get(0);

      return record.getChildText("uuid");
   }

   //--------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   public MdInfo getMetadataInfo(Dbms dbms, String id) throws Exception {
      String query = "SELECT id, uuid, schemaId, isTemplate, isHarvested, createDate, "
            + "       changeDate, source, title, root, owner, displayOrder, datapolicy "
            + "FROM   Metadata " + "WHERE id=?";

      List<Element> list = dbms.select(query, new Integer(id)).getChildren();

      if (list.size() == 0)
         return null;

      Element record = list.get(0);

      MdInfo info = new MdInfo();

      info.id = id;
      info.uuid = record.getChildText("uuid");
      info.schemaId = record.getChildText("schemaid");
      info.isHarvested = "y".equals(record.getChildText("isharvested"));
      info.createDate = record.getChildText("createdate");
      info.changeDate = record.getChildText("changedate");
      info.source = record.getChildText("source");
      info.title = record.getChildText("title");
      info.root = record.getChildText("root");
      info.owner = record.getChildText("owner");
      info.displayOrder = record.getChildText("displayOrder");
      info.datapolicy = record.getChildText("datapolicy");

      String temp = record.getChildText("istemplate");

      if ("y".equals(temp))
         info.template = MdInfo.Template.TEMPLATE;

      else if ("s".equals(temp))
         info.template = MdInfo.Template.SUBTEMPLATE;

      else
         info.template = MdInfo.Template.METADATA;

      return info;
   }

   //--------------------------------------------------------------------------

   public String getVersion(String id) {
      return editLib.getVersion(id);
   }

   //--------------------------------------------------------------------------

   public String getNewVersion(String id) {
      return editLib.getNewVersion(id);
   }

   /**
    * @deprecated
    *
    * Description goes here.
    * @param dbms
    * @param id
    * @param isTemplate
    * @param title
    * @throws Exception
    *
    */
   @Deprecated
   public void setTemplate(Dbms dbms, String uuid, String isTemplate, String title)
         throws Exception {
      setTemplateExt(dbms, uuid, isTemplate, title);
      
      if ("y".equals(isTemplate)) {
         try {
            Log.info(Geonet.DATA_MANAGER,
                  "Transforming a metadata to a template -> Removing ProductMetadata with urn: "
                        + uuid);
            new ProductMetadataManager().delete(uuid);
         } catch (Exception e) {
            Log.warning(Geonet.DATA_MANAGER, "Unable to remove ProductMetadata with urn: " + uuid, e);
         }
      }
      
      indexMetadata(dbms, uuid, null);
   }

   /**
    * @deprecated
    */
   @Deprecated
   public void setTemplateExt(Dbms dbms, String uuid, String isTemplate, String title)
         throws Exception {
      if (title == null)
         dbms.execute("UPDATE Metadata SET isTemplate=? WHERE uuid=?", isTemplate, uuid);
      else
         dbms.execute("UPDATE Metadata SET isTemplate=?, title=? WHERE uuid=?", isTemplate, title,
               uuid);
   }

   /**
    * Description goes here.
    *
    * @deprecated
    *
    * @param dbms
    * @param id
    * @param harvestUuid
    * @throws Exception
    */
   @Deprecated
   public void setHarvested(Dbms dbms, String uuid, String harvestUuid) throws Exception {
      setHarvestedExt(dbms, uuid, harvestUuid);
      indexMetadata(dbms, harvestUuid, null);
   }

   /**
    * @deprecated
    */
   @Deprecated
   public void setHarvestedExt(Dbms dbms, String uuid, String harvestUuid) throws Exception {
      String value = (harvestUuid != null) ? "y" : "n";
      if (harvestUuid == null) {
         dbms.execute("UPDATE Metadata SET isHarvested=? WHERE uuid=?", value, uuid);
      } else {
         dbms.execute("UPDATE Metadata SET isHarvested=?, harvestUuid=? WHERE uuid=?", value,
               harvestUuid, uuid);
      }
   }

   /**
    * @deprecated
    */
   @Deprecated
   public void setHarvestedExt(Dbms dbms, String uuid, String harvestUuid, String harvestUri)
         throws Exception {
      String value = (harvestUuid != null) ? "y" : "n";
      String query = "UPDATE Metadata SET isHarvested=?, harvestUuid=?, harvestUri=? WHERE uuid=?";

      dbms.execute(query, value, harvestUuid, harvestUri, uuid);
   }

   //---------------------------------------------------------------------------

   public String getSiteURL() {
      String host = settingMan.getValue("system/server/host");
      String port = settingMan.getValue("system/server/port");
      String locServ = baseURL + "/" + Jeeves.Prefix.SERVICE + "/en";

      return "http://" + host + (port.equals("80") ? "" : ":" + port) + locServ;
   }

   //--------------------------------------------------------------------------

   public String autodetectSchema(Element md) {
      Namespace nons = Namespace.NO_NAMESPACE;

      Namespace metadatadRootElemenNSUri = md.getNamespace();

      List<Namespace> metadataAdditionalNS = md.getAdditionalNamespaces();

      Log.debug(Geonet.DATA_MANAGER, "Autodetect schema for metadata with :\n * root element:'"
            + md.getQualifiedName() + "'\n * with namespace:'" + md.getNamespace()
            + "\n * with additional namespaces:" + metadataAdditionalNS.toString());

      if (md.getName().equals("Record") && md.getNamespace().equals(Csw.NAMESPACE_CSW)) {
         return "csw-record";
      } else if (md.getNamespace().equals(nons)) {
         if (md.getName().equals("Metadata")) {
            return "iso19115";
         }

         /* there are some other suggested container names,
          * like <dc>, <dublinCore>, <resource>, <record> and <metadata>
          * We may need to also check for those on import and export
          */
         if (md.getName().equals("simpledc")) {
            return "dublin-core";
         }
         if (md.getName().equals("metadata")) {
            return "fgdc-std";
         }
      } else if (metadataAdditionalNS.contains(Csw.NAMESPACE_GMD)
            || metadatadRootElemenNSUri.equals(Csw.NAMESPACE_GMD)) {
         // Here we have an iso19139 or an ISO profil

         // the root element will have different namespace (element name
         // and additionnal namespace).
         // this is important for profiles which usually need to override the top level
         // element for proper definition
         // eg. mcp:MD_Metadata versus wmo:MD_Metadata
         //
         // But profil for france does not override top level element. But only sub
         // elements.
         //
         // we suppose that the root element declare the prime namespace of the profil
         // declared as targetNamespace of schema.xsd.
         // eg. <gmd:MD_Metadata  xmlns:gmd="http://www.isotc211.org/2005/gmd
         //	 xmlns:fra="http://www.cnig.gouv.fr/2005/fra" ...

         // FIXME : Issue if :
         // eg. <gmd:MD_Metadata xmlns:gmd="http://www.isotc211.org/2005/gmd ..;
         //	 <fra:FRA_DataIdentification xmlns:fra="http://www.cnig.gouv.fr/2005/fra">
         // if profil specific namespace only declared on sub elements and not on root.

         for (String schema : getSchemas()) {
            MetadataSchema mds = getSchema(schema);
            String primeNs = mds.getPrimeNS();

            // Check if gmd is not the root element namespace
            // and root element as a namespace which is
            // defined in one schema, we have an ISO profil
            // and current schema is ok.
            if (metadatadRootElemenNSUri.getURI().equals(primeNs)
                  && !metadatadRootElemenNSUri.equals(Csw.NAMESPACE_GMD)) {
               return schema;
            }

            // Check if a prime namespace exists in all
            // additional namespaces of the root element
            for (Namespace ns : metadataAdditionalNS) {
               if (ns.equals(Csw.NAMESPACE_CSW) || ns.equals(Csw.NAMESPACE_GFC))
                  continue;

               if (ns.getURI().equals(primeNs)
                     && metadatadRootElemenNSUri.equals(Csw.NAMESPACE_GMD)) {
                  return schema;
               }
            }
         }

         // Default schema name is
         return "iso19139";
      }
      return null;
   }

   //--------------------------------------------------------------------------
   public void updateDisplayOrder(Dbms dbms, String id, String displayOrder) throws Exception {
      String query = "UPDATE Metadata SET displayOrder = ? WHERE id = ?";
      dbms.execute(query, new Integer(displayOrder), new Integer(id));
   }

   //--------------------------------------------------------------------------

   public void increasePopularity(Dbms dbms, String uuid) throws Exception {
      String query = "UPDATE Metadata SET popularity = popularity +1 WHERE " + "uuid = ?";

      dbms.execute(query, uuid);
      indexMetadata(dbms, uuid, null);
   }

   //--------------------------------------------------------------------------
   //---
   //--- Metadata Insert API
   //---
   //--------------------------------------------------------------------------

   /**
    * Adds a metadata in xml form (the xml should be validated). This method is
    * used to add a metadata got from a remote site via a mef and the data has
    * NOT been included. Note that neither permissions nor indexes are updated..
    * @param dbms
    * @param schema
    * @param md
    * @param source
    * @param createDate
    * @param changeDate
    * @param uuid
    * @param owner
    * @return
    * @throws Exception
    *
    * @deprecated Should use insertHarvestedMetadata method
    */
   @Deprecated
   public String insertMetadataExt(Dbms dbms, String schema, Element md, String source,
         String createDate, String changeDate, String uuid, String owner) throws Exception {
      //--- generate a new metadata id
      int id = SerialFactory.getSerial(dbms, "Metadata");

      return insertMetadataExt(dbms, schema, md, id, source, createDate, changeDate, uuid, owner,
            "n", "n", null, null, null);
   }

   /**
    * Insert harvested metadata. This method sets the localHarvestDate to the current time.
    * @deprecated
    */
   @Deprecated
   public String insertHarvestedMetadata(Dbms dbms, String schema, Element md, String source,
         String createDate, String changeDate, String uuid, String owner, Integer harvestingTaskId,
         Integer category) throws Exception {

      //--- generate a new metadata id
      int id = SerialFactory.getSerial(dbms, "Metadata");
      return insertMetadataExt(dbms, schema, md, id, source, createDate, changeDate, uuid, owner,
            "n", "y", harvestingTaskId, new ISODate().toString(), category);
   }

   /**
    * Description goes here.
    * @deprecated
    */
   @Deprecated
   public String insertMetadataExt(Dbms dbms, String schema, Element md, int id, String source,
         String createDate, String changeDate, String uuid, String owner, String isTemplate,
         String isHarvested, Integer harvestingTaskId, String localHarvestDate, Integer category)
         throws Exception {

      if (source == null)
         source = getSiteID();

      //--- force namespace prefix for iso19139 metadata
      setNamespacePrefixUsingSchemas(md, schema);

      DataPolicy dp = null;
      DataPolicyManager dpm = new DataPolicyManager(dbms);

      // Handle product metadata only for ISO 19139 or profiles.
      if (schema.contains("iso19139")) {
         //-- Extract Metadata Product
         //FIXME JB Cleaning up these methods
         //         new ProductMetadataManager().saveOrUpdate(null);

         // FIXME MCT Set data policy chosen from create metadata panel (only DP with edit rights)
         // Get extracted data policy from Product Metadata
         //         dp = dpm.getDataPolicyByName(pm.getDataPolicy(), false, false);

         // Ensure deleted metadata table is clean
         IDeletedMetadataManager dmm = new DeletedMetadataManager(dbms);
         dmm.clean(uuid, category);

      }

      if (dp == null) {
         dp = dpm.getDataPolicyByName(dpm.getDefaultDataPolicyName(), false, false);
      }

      //--- Note: we cannot index metadata here. Indexing is done in the harvesting part

      return XmlSerializer.insert(dbms, schema, md, id, source, uuid, createDate, changeDate,
            isTemplate, null, owner, dp.getId(), category, isHarvested, harvestingTaskId,
            localHarvestDate);
   }

   //--------------------------------------------------------------------------
   //---
   //--- Metadata Get API
   //---
   //--------------------------------------------------------------------------

   /** Retrieves a metadata (in xml) given its id with no geonet:info
    */
   public Element getMetadataNoInfo(ServiceContext srvContext, String id) throws Exception {
      Element md = getMetadata(srvContext, id, false, false);
      md.removeChild(Edit.RootChild.INFO, Edit.NAMESPACE);
      md.removeNamespaceDeclaration(Edit.NAMESPACE);
      return md;
   }

   /** Retrieves a metadata (in xml) given its id; adds editing information
    *  if requested and does NOT include validation errors
    */

   public Element getMetadata(ServiceContext srvContext, String id, boolean forEditing)
         throws Exception {
      return getMetadata(srvContext, id, forEditing, false);
   }

   /** Retrieves a metadata (in xml) given its id; adds editing information
    *  if requested and validation errors if requested
    */
   public Element getMetadata(ServiceContext srvContext, String id, boolean forEditing,
         boolean withEditorValidationErrors) throws Exception {

      Dbms dbms = (Dbms) srvContext.getResourceManager().open(Geonet.Res.MAIN_DB);

      boolean doXLinks = XmlSerializer.resolveXLinks();

      Element md = XmlSerializer.selectNoXLinkResolver(dbms, "Metadata", id);
      if (md == null)
         return null;

      String version = null;

      if (forEditing) { // copy in xlink'd fragments but leave xlink atts to editor
         if (doXLinks)
            Processor.processXLink(md);
         String schema = getMetadataSchema(dbms, id);

         if (withEditorValidationErrors) {
            //-- get an XSD validation report and add results to the metadata
            //-- as geonet:xsderror attributes on the affected elements
            getXSDXmlReport(schema, md);
         }

         //-- now expand the elements and add the geonet: elements
         editLib.expandElements(schema, md);
         version = editLib.getVersionForEditing(schema, id, md);

         if (withEditorValidationErrors) {
            //-- get a schematron error report if no xsd errors and add results
            //-- to the metadata as a geonet:schematronerrors element with
            //-- links to the ref id of the affected element
            Element condChecks = getSchemaTronXmlReport(schema, md, srvContext.getLanguage());
            if (condChecks != null)
               md.addContent(condChecks);
         }
      } else {
         if (doXLinks)
            Processor.detachXLink(md);
      }

      md.addNamespaceDeclaration(Edit.NAMESPACE);
      Element info = buildInfoElem(srvContext, id, version);
      md.addContent(info);

      md.detach();
      return md;
   }

   //--------------------------------------------------------------------------
   /** Retrieves a metadata element given it's ref
    */

   public Element getElementByRef(Element md, String ref) {
      return editLib.findElement(md, ref);
   }

   //--------------------------------------------------------------------------
   /** Returns true if the metadata exists in the database
     */
   public boolean existsMetadata(Dbms dbms, int id) throws Exception {
      //FIXME : should use index

      List list = dbms.select("SELECT id FROM Metadata WHERE id=" + id).getChildren();
      return list.size() != 0;
   }

   /** Returns true if the metadata uuid exists in the database
     */

   public boolean existsMetadataUuid(Dbms dbms, String uuid) throws Exception {
      //FIXME : should use index

      List list = dbms.select("SELECT uuid FROM Metadata WHERE uuid='" + uuid + "'").getChildren();
      return list.size() != 0;
   }

   //--------------------------------------------------------------------------
   /** Returns all the keywords in the system
     */

   public Element getKeywords() throws Exception {
      Collection<String> keywords = searchMan.getTerm(IndexField.KEYWORD);

      Element el = new Element("keywords");

      for (Object keyword : keywords) {
         el.addContent(new Element("keyword").setText((String) keyword));
      }

      return el;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Embedded Metadata Update API for AJAX Editor support
   //---
   //--------------------------------------------------------------------------

   private Element getMetadataFromSession(UserSession session, String id) {
      Log.debug(Geonet.DATA_MANAGER, "Retrieving metadata from session " + session.getUserId());
      Element md = (Element) session.getProperty(Geonet.Session.METADATA_EDITING + id);
      md.detach();
      return md;
   }

   private void setMetadataIntoSession(UserSession session, Element md, String id) {
      Log.debug(Geonet.DATA_MANAGER, "Storing metadata in session " + session.getUserId());
      session.setProperty(Geonet.Session.METADATA_EDITING + id, md);
   }

   //--------------------------------------------------------------------------
   /** For Ajax Editing : removes metadata from session
     */
   public void removeMetadataEmbedded(UserSession session, String id) {
      Log.debug(Geonet.DATA_MANAGER, "Removing metadata from session " + session.getUserId());
      session.removeProperty(Geonet.Session.METADATA_EDITING + id);
   }

   //--------------------------------------------------------------------------
   /** For Ajax Editing : gets Metadata from database and places it in session
     */
   public Element getMetadataEmbedded(ServiceContext srvContext, String id, boolean forEditing,
         boolean withValidationErrors) throws Exception {
      Element md = getMetadata(srvContext, id, forEditing, withValidationErrors);

      UserSession session = srvContext.getUserSession();
      setMetadataIntoSession(session, md, id);
      return md;
   }

   //--------------------------------------------------------------------------
   /**
    * For Ajax Editing : adds an element or an attribute to a metadata element ([add] link)
    */
   public synchronized Element addElementEmbedded(Dbms dbms, UserSession session, String id,
         String ref, String name, String childName) throws Exception {

      String schema = getMetadataSchema(dbms, id);

      //--- get metadata from session
      Element md = getMetadataFromSession(session, id);

      //--- ref is parent element so find it
      Element el = editLib.findElement(md, ref);
      if (el == null)
         throw new IllegalStateException("Element not found at ref = " + ref);

      //--- locate the geonet:element and geonet:info elements and clone for
      //--- later re-use
      Element refEl = (Element) (el.getChild(Edit.RootChild.ELEMENT, Edit.NAMESPACE)).clone();
      Element info = (Element) (md.getChild(Edit.RootChild.INFO, Edit.NAMESPACE)).clone();
      md.removeChild(Edit.RootChild.INFO, Edit.NAMESPACE);

      Element child = null;
      MetadataSchema mds = editLib.getSchema(schema);
      if (childName != null) {
         if (childName.equals("geonet:attribute")) {
            String defaultValue = "";
            List attributeDefs = el.getChildren(Edit.RootChild.ATTRIBUTE, Edit.NAMESPACE);
            for (Object a : attributeDefs) {
               Element attributeDef = (Element) a;
               if (attributeDef != null
                     && attributeDef.getAttributeValue(Edit.Attribute.Attr.NAME).equals(name)) {
                  Element defaultChild = attributeDef.getChild(Edit.Attribute.Child.DEFAULT,
                        Edit.NAMESPACE);
                  if (defaultChild != null) {
                     defaultValue = defaultChild.getAttributeValue(Edit.Attribute.Attr.VALUE);
                  }
               }
            }
            //--- Add new attribute with default value
            el.setAttribute(new Attribute(name, defaultValue));
            child = el;
         } else {
            //--- normal element
            child = editLib.addElement(schema, el, name);
            if (!childName.equals("")) {
               //--- or element
               String uChildName = editLib.getUnqualifiedName(childName);
               String prefix = editLib.getPrefix(childName);
               String ns = editLib.getNamespace(childName, md, mds);
               if (prefix.equals("")) {
                  prefix = editLib.getPrefix(el.getName());
                  ns = editLib.getNamespace(el.getName(), md, mds);
               }
               Element orChild = new Element(uChildName, prefix, ns);
               child.addContent(orChild);

               //--- add mandatory sub-tags
               editLib.fillElement(schema, child, orChild);
            }
         }
      } else {
         child = editLib.addElement(schema, el, name);
      }

      //--- now add the geonet:element back again to keep ref number
      el.addContent(refEl);

      //--- now enumerate the new child
      int iRef = editLib.findMaximumRef(md);
      editLib.expandElements(schema, child);
      editLib.enumerateTreeStartingAt(child, iRef + 1, Integer.parseInt(ref));

      //--- add editing info to everything from the parent down
      editLib.expandTree(mds, el);

      //--- attach the info element to the child (and the metadata root)
      child.addContent(info);
      md.addContent((Element) info.clone());

      //--- store the metadata in the session again
      setMetadataIntoSession(session, (Element) md.clone(), id);

      // Return element added
      return child;

   }

   //--------------------------------------------------------------------------
   /** For Ajax Editing : removes an element from a metadata ([del] link)
     */

   public synchronized Element deleteElementEmbedded(Dbms dbms, UserSession session, String id,
         String ref, String parentRef) throws Exception {

      String schema = getMetadataSchema(dbms, id);

      //--- get metadata from session
      Element md = getMetadataFromSession(session, id);

      //--- locate the geonet:info element and clone for later re-use
      Element info = (Element) (md.getChild(Edit.RootChild.INFO, Edit.NAMESPACE)).clone();
      md.removeChild(Edit.RootChild.INFO, Edit.NAMESPACE);

      //--- get element to remove
      Element el = editLib.findElement(md, ref);

      if (el == null)
         throw new IllegalStateException("Element not found at ref = " + ref);

      String uName = el.getName();
      Namespace ns = el.getNamespace();
      Element parent = el.getParentElement();
      Element result = null;
      if (parent != null) {
         int me = parent.indexOf(el);

         //--- check and see whether the element to be deleted is the last one
         Filter elFilter = new ElementFilter(uName, ns);
         if (parent.getContent(elFilter).size() == 1) {

            //--- get geonet child element with attribute name = unqualified name
            Filter chFilter = new ElementFilter(Edit.RootChild.CHILD, Edit.NAMESPACE);
            List children = parent.getContent(chFilter);

            for (int i = 0; i < children.size(); i++) {
               Element ch = (Element) children.get(i);
               String name = ch.getAttributeValue("name");
               if (name != null && name.equals(uName)) {
                  result = (Element) ch.clone();
                  break;
               }
            }

            // -- now delete the element as requested
            parent.removeContent(me);

            //--- existing geonet child element not present so create it
            if (result == null) {
               result = editLib.createElement(schema, el, parent);
               parent.setContent(me, result);
            }
            result.setAttribute(Edit.ChildElem.Attr.PARENT, parentRef);
            result.addContent(info);
         }
         //--- if not the last one then just delete it
         else {
            parent.removeContent(me);
         }
      } else {
         throw new IllegalStateException("Element at ref = " + ref + " doesn't have a parent");
      }

      // if we don't need a child then create a geonet:null element
      if (result == null) {
         result = new Element(Edit.RootChild.NULL, Edit.NAMESPACE);
      }

      //--- reattach the info element to the metadata
      md.addContent((Element) info.clone());

      //--- store the metadata in the session again
      setMetadataIntoSession(session, (Element) md.clone(), id);

      return result;
   }

   /**
    * Remove attribute in embedded mode
    *
    * @param dbms
    * @param session
    * @param id
    * @param ref   Attribute identifier (eg. _169_uom).
    * @return
    * @throws Exception
    */
   public synchronized Element deleteAttributeEmbedded(Dbms dbms, UserSession session, String id,
         String ref) throws Exception {
      String[] token = ref.split("_");
      String elementId = token[1];
      String attributeName = token[2];
      Element result = new Element(Edit.RootChild.NULL, Edit.NAMESPACE);

      //--- get metadata from session
      Element md = getMetadataFromSession(session, id);

      //--- get element to remove
      Element el = editLib.findElement(md, elementId);
      if (el != null) {
         el.removeAttribute(attributeName);
      }

      //--- store the metadata in the session again
      setMetadataIntoSession(session, (Element) md.clone(), id);

      return result;
   }

   //--------------------------------------------------------------------------
   /** For Ajax Editing : swap element with sibling ([up] and [down] links)
     */

   public synchronized void swapElementEmbedded(Dbms dbms, UserSession session, String id,
         String ref, boolean down) throws Exception {
      getMetadataSchema(dbms, id);

      //--- get metadata from session
      Element md = getMetadataFromSession(session, id);

      //--- get element to swap
      Element elSwap = editLib.findElement(md, ref);

      if (elSwap == null)
         throw new IllegalStateException("Element not found at ref = " + ref);

      //--- swap the elements
      int iSwapIndex = -1;

      List list = ((Element) elSwap.getParent()).getChildren(elSwap.getName(),
            elSwap.getNamespace());

      for (int i = 0; i < list.size(); i++)
         if (list.get(i) == elSwap) {
            iSwapIndex = i;
            break;
         }

      if (iSwapIndex == -1)
         throw new IllegalStateException("Index not found for element --> " + elSwap);

      if (down)
         swapElements(elSwap, (Element) list.get(iSwapIndex + 1));
      else
         swapElements(elSwap, (Element) list.get(iSwapIndex - 1));

      //--- store the metadata in the session again
      setMetadataIntoSession(session, (Element) md.clone(), id);

   }

   //--------------------------------------------------------------------------
   /** For Ajax Editing : updates all leaves with new values
     */

   public synchronized boolean updateMetadataEmbedded(UserSession session, Dbms dbms, String id,
         String currVersion, Hashtable changes, String lang) throws Exception {
      String schema = getMetadataSchema(dbms, id);

      // --- check if the metadata has been modified from last time
      if (currVersion != null && !editLib.getVersion(id).equals(currVersion)) {
         Log.error(Geonet.DATA_MANAGER, "Version mismatch: had " + currVersion + " but expected "
               + editLib.getVersion(id));
         return false;
      }

      // --- get metadata from session
      Element md = getMetadataFromSession(session, id);

      // Store XML fragments to be handled after other elements update
      HashMap<String, String> xmlInputs = new HashMap<String, String>();

      // --- update elements
      for (Enumeration e = changes.keys(); e.hasMoreElements();) {
         String ref = ((String) e.nextElement()).trim();
         String val = ((String) changes.get(ref)).trim();
         String attr = null;

         // Catch element starting with a X to replace XML fragments
         if (ref.startsWith("X")) {
            ref = ref.substring(1);
            xmlInputs.put(ref, val);
            continue;
         }

         if (ref.equals(""))
            continue;

         if (updatedLocalizedTextElement(md, ref, val)) {
            continue;
         }

         int at = ref.indexOf('_');
         if (at != -1) {
            attr = ref.substring(at + 1);
            ref = ref.substring(0, at);
         }

         Element el = editLib.findElement(md, ref);
         if (el == null)
            Log.error(Geonet.DATA_MANAGER, "Element not found at ref = " + ref);

         if (attr != null) {
            Integer indexColon = attr.indexOf("COLON");
            if (indexColon != -1) {
               String prefix = attr.substring(0, indexColon);
               String localname = attr.substring(indexColon + 5);
               String namespace = editLib.getNamespace(prefix + ":" + localname, md,
                     getSchema(schema));
               Namespace attrNS = Namespace.getNamespace(prefix, namespace);
               if (el.getAttribute(localname, attrNS) != null) {
                  el.setAttribute(new Attribute(localname, val, attrNS));
               }
            } else {
               if (el.getAttribute(attr) != null)
                  el.setAttribute(new Attribute(attr, val));
            }
         } else {
            List content = el.getContent();

            for (int i = 0; i < content.size(); i++) {
               if (content.get(i) instanceof Text) {
                  el.removeContent((Text) content.get(i));
                  i--;
               }
            }
            el.addContent(val);
         }
      }

      // Deals with XML fragments to insert or update
      if (!xmlInputs.isEmpty()) {

         // Loop over each XML fragments to insert or replace
         for (String ref : xmlInputs.keySet()) {
            String value = xmlInputs.get(ref);

            String name = null;
            int addIndex = ref.indexOf('_');
            if (addIndex != -1) {
               name = ref.substring(addIndex + 1);
               ref = ref.substring(0, addIndex);
            }

            // Get element to fill
            Element el = editLib.findElement(md, ref);

            if (el == null) {
               throw new IllegalStateException("Element not found at ref = " + ref);
            }

            if (value != null && !value.equals("")) {
               String[] fragments = value.split("&&&");
               for (String fragment : fragments) {
                  if (name != null) {
                     name = name.replace("COLON", ":");
                     editLib.addFragment(schema, el, name, fragment);
                  } else {
                     // clean before update
                     el.removeContent();

                     fragment = addNamespaceToFragment(fragment);

                     // Add content
                     el.addContent(Xml.loadString(fragment, false));
                  }
               }
               Log.debug(Geonet.DATA_MANAGER, "replacing XML content");
            }
         }
      }

      // --- remove editing info
      editLib.removeEditingInfo(md);

      md.detach();
      return updateMetadata(session, dbms, id, md, false, currVersion, lang);

   }

   /**
    * Add a localised character string to an element.
    *
    * @param md metadata record
    * @param ref current ref of element. All _lang_AB_123 element will be processed.
    * @param val
    * @return
    */
   private boolean updatedLocalizedTextElement(Element md, String ref, String val) {
      if (ref.startsWith("lang")) {
         if (val.length() > 0) {
            String[] ids = ref.split("_");
            // --- search element in current metadata record
            Element parent = editLib.findElement(md, ids[2]);

            // --- add required attribute
            parent.setAttribute("type", "gmd:PT_FreeText_PropertyType",
                  Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));

            // --- add new translation
            Namespace gmd = Namespace.getNamespace("gmd", "http://www.isotc211.org/2005/gmd");
            Element langElem = new Element("LocalisedCharacterString", gmd);
            langElem.setAttribute("locale", "#" + ids[1]);
            langElem.setText(val);

            Element freeText = getOrAdd(parent, "PT_FreeText", gmd);

            Element textGroup = new Element("textGroup", gmd);
            freeText.addContent(textGroup);
            textGroup.addContent(langElem);
            Element refElem = new Element(Edit.RootChild.ELEMENT, Edit.NAMESPACE);
            refElem.setAttribute(Edit.Element.Attr.REF, "");
            textGroup.addContent(refElem);
            langElem.addContent((Element) refElem.clone());
         }
         return true;
      }
      return false;
   }

   /**
    * If no PT_FreeText element exist create a geonet:element with
    * an empty ref.
    *
    * @param parent
    * @param name
    * @param ns
    * @return
    */
   private Element getOrAdd(Element parent, String name, Namespace ns) {
      Element child = parent.getChild(name, ns);
      if (child == null) {
         child = new Element(name, ns);
         Element refElem = new Element(Edit.RootChild.ELEMENT, Edit.NAMESPACE);
         refElem.setAttribute(Edit.Element.Attr.REF, "");
         child.addContent(refElem);
         parent.addContent(child);
      }
      return child;
   }

   //--------------------------------------------------------------------------
   /** For Ajax Editing : retrieves metadata from session and validates it
     */

   public Element validateMetadataEmbedded(UserSession session, Dbms dbms, String id, String lang)
         throws Exception {
      String schema = getMetadataSchema(dbms, id);

      //--- get metadata from session and clone it for validation
      Element realMd = getMetadataFromSession(session, id);
      Element md = (Element) realMd.clone();

      //--- remove editing info
      editLib.removeEditingInfo(md);
      editLib.contractElements(md);
      md = updateFixedInfo(schema, id, md, dbms);

      //--- do the validation on the metadata
      return doValidate(session, schema, id, md, lang);

   }

   //--------------------------------------------------------------------------
   /** For Editing : adds an attribute from a metadata ([add] link)
     * FIXME: Modify and use within Ajax controls
     */

   public synchronized boolean addAttribute(Dbms dbms, String id, String uuid, String ref,
         String name, String currVersion) throws Exception {
      Element md = XmlSerializer.select(dbms, "Metadata", id);

      //--- check if the metadata has been deleted
      if (md == null)
         return false;

      String schema = getMetadataSchema(dbms, id);
      editLib.expandElements(schema, md);
      editLib.enumerateTree(md);

      //--- check if the metadata has been modified from last time
      if (currVersion != null && !editLib.getVersion(id).equals(currVersion))
         return false;

      //--- get element to add
      Element el = editLib.findElement(md, ref);

      if (el == null)
         Log.error(Geonet.DATA_MANAGER, "Element not found at ref = " + ref);
      //throw new IllegalStateException("Element not found at ref = " + ref);

      //--- remove editing info added by previous call
      editLib.removeEditingInfo(md);

      if (el != null) {
         el.setAttribute(new Attribute(name, ""));
      }

      editLib.contractElements(md);
      md = updateFixedInfo(schema, id, md, dbms);
      XmlSerializer.update(dbms, id, md);

      //--- update search criteria
      indexMetadata(dbms, uuid, null);

      return true;
   }

   //--------------------------------------------------------------------------
   /** For Editing : removes an attribute from a metadata ([del] link)
     * FIXME: Modify and use within Ajax controls
     */

   public synchronized boolean deleteAttribute(Dbms dbms, String id, String uuid, String ref,
         String name, String currVersion) throws Exception {
      Element md = XmlSerializer.select(dbms, "Metadata", id);

      //--- check if the metadata has been deleted
      if (md == null)
         return false;

      String schema = getMetadataSchema(dbms, id);
      editLib.expandElements(schema, md);
      editLib.enumerateTree(md);

      //--- check if the metadata has been modified from last time
      if (currVersion != null && !editLib.getVersion(id).equals(currVersion))
         return false;

      //--- get element to remove
      Element el = editLib.findElement(md, ref);

      if (el == null)
         throw new IllegalStateException("Element not found at ref = " + ref);

      //--- remove editing info added by previous call
      editLib.removeEditingInfo(md);

      el.removeAttribute(name);

      editLib.contractElements(md);
      md = updateFixedInfo(schema, id, md, dbms);
      XmlSerializer.update(dbms, id, md);

      //--- update search criteria
      indexMetadata(dbms, uuid, null);

      return true;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Metadata Update API
   //---
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   /** For update of owner info
     */

   public synchronized void updateMetadataOwner(Dbms dbms, String id, String owner)
         throws Exception {
      updateMetadataOwner(dbms, new Integer(id), owner);
   }

   public synchronized void updateMetadataOwner(Dbms dbms, int id, String owner) throws Exception {
      dbms.execute("UPDATE Metadata SET owner=? WHERE id=?", owner, id);
   }

   //--------------------------------------------------------------------------
   /** For Editing : updates all leaves with new values
     */

   public synchronized boolean updateMetadata(UserSession session, Dbms dbms, String id,
         String currVersion, Hashtable changes, boolean validate, String lang) throws Exception {
      Element md = XmlSerializer.select(dbms, "Metadata", id);

      //--- check if the metadata has been deleted
      if (md == null)
         return false;
      String schema = getMetadataSchema(dbms, id);
      editLib.expandElements(schema, md);
      editLib.enumerateTree(md);

      //--- check if the metadata has been modified from last time
      if (currVersion != null && !editLib.getVersion(id).equals(currVersion))
         return false;

      //--------------------------------------------------------------------
      //--- update elements

      for (Enumeration e = changes.keys(); e.hasMoreElements();) {
         String ref = ((String) e.nextElement()).trim();
         String val = ((String) changes.get(ref)).trim();
         String attr = null;

         if (updatedLocalizedTextElement(md, ref, val)) {
            continue;
         }

         int at = ref.indexOf('_');
         if (at != -1) {
            attr = ref.substring(at + 1);
            ref = ref.substring(0, at);
         }
         boolean xmlContent = false;
         if (ref.startsWith("X")) {
            ref = ref.substring(1);
            xmlContent = true;
         }
         Element el = editLib.findElement(md, ref);
         if (el == null)
            throw new IllegalStateException("Element not found at ref = " + ref);

         if (attr != null) {
            // The following work-around decodes any attribute name that has a COLON in it
            // The : is replaced by the word COLON in the xslt so that it can be processed
            // by the XML Serializer when an update is submitted - a better solution is
            // to modify the argument handler in Jeeves to store arguments with their name
            // as a value rather than as the element itself
            Integer indexColon = attr.indexOf("COLON");
            if (indexColon != -1) {
               String prefix = attr.substring(0, indexColon);
               String localname = attr.substring(indexColon + 5);
               String namespace = editLib.getNamespace(prefix + ":" + localname, md,
                     getSchema(schema));
               Namespace attrNS = Namespace.getNamespace(prefix, namespace);
               if (el.getAttribute(localname, attrNS) != null) {
                  el.setAttribute(new Attribute(localname, val, attrNS));
               }
               // End of work-around
            } else {
               if (el.getAttribute(attr) != null)
                  el.setAttribute(new Attribute(attr, val));
            }
         } else if (xmlContent) {
            Log.debug(Geonet.DATA_MANAGER, "replacing XML content");
            el.removeContent();

            val = addNamespaceToFragment(val);

            el.addContent(Xml.loadString(val, false));
         } else {
            List content = el.getContent();

            for (int i = 0; i < content.size(); i++) {
               if (content.get(i) instanceof Text) {
                  el.removeContent((Text) content.get(i));
                  i--;
               }
            }
            el.addContent(val);
         }
      }
      //--- remove editing info added by previous call
      editLib.removeEditingInfo(md);

      return updateMetadata(session, dbms, id, md, validate, currVersion, lang);
   }

   //--------------------------------------------------------------------------
   /**
    * Update a metadata record.
    * Clean current validation report in session. If user ask for validation
    * the validation report will be (re-)created then.
    */
   public synchronized boolean updateMetadata(UserSession session, Dbms dbms, String id,
         Element md, boolean validate, String version, String lang) throws Exception {
      Collection<Integer> operationsAllowed = getOperationEnum(session, dbms, id);
      session.removeProperty(Geonet.Session.VALIDATION_REPORT);

      MdInfo mdInfo = getMetadataInfo(dbms, id);
      boolean owner = session.getUsername().equals(mdInfo.owner);
      
      // Operator and so Administrator are allowed to modify metadata
      boolean operator = session.getProfile().equals(Geonet.Profile.ADMINISTRATOR)
            || session.getProfile().equals(Geonet.Profile.OPERATOR);
      if (!operator && !owner
            && (operationsAllowed == null || !operationsAllowed.contains(OperationEnum.EDITING
                  .getId()))) {
         Log.error(Geonet.DATA_MANAGER, "Operation not allowed");
         throw new OperationNotAllowedEx();
      }

      //--- check if the metadata has been modified from last time
      if (version != null && !editLib.getVersion(id).equals(version)) {
         Log.error(Geonet.DATA_MANAGER, "Version mismatch: had " + version + " but expected "
               + editLib.getVersion(id));
         return false;
      }

      editLib.contractElements(md);

      // Get template/metadata information
      MetadataManager mm = new MetadataManager(dbms);
      AbstractMetadata abstractMdInfo = mm.getAbstractMetadataInfoById(new Integer(id));

      md = updateFixedInfo(abstractMdInfo.getSchema(), id, md, dbms);
      String urn = abstractMdInfo.getUrn();
      ProductMetadata pm = null;

      if (abstractMdInfo instanceof Metadata) {
         // Create metadata to update
         Metadata metadata = new Metadata(abstractMdInfo.getId(), urn);
         metadata.setData(md);
         metadata.setChangeDate(new ISODate().toString());
         metadata.setSchema(abstractMdInfo.getSchema());

         Log.info(Geonet.DATA_MANAGER, "Updating local metadata with URN : " + urn);

         //-- Extract Metadata Product
         long before = System.currentTimeMillis();
         IProductMetadataManager pmm = new ProductMetadataManager();
         pm = pmm.extract(metadata, true);
         
         // Get extracted information from PM.
         IDataPolicyManager dpm = new DataPolicyManager(dbms);

         // Manage Data Policy
         MetadataAligner.enforceDataPolicy(pm, metadata, dpm);
         MetadataAligner.enforceGlobalExchange(pmm, metadata, pm);
         
         new ProductMetadataManager().saveOrUpdate(pm);
         long after = System.currentTimeMillis();
         if (Log.isStatEnabled()) {
            Log.statTime("DataManager", "DataManager#updateMetadata",
                  "Updating PM in DataService.", after - before);
         }

         metadata.setTitle(pm.getTitle());

         // Update metadata
         XmlSerializer.updateMetadata(dbms, metadata);

      } else {

         Template template = new Template();
         template.setId(abstractMdInfo.getId());
         template.setUrn(abstractMdInfo.getUrn());
         template.setData(md);
         template.setChangeDate(new ISODate().toString());

         Log.info(Geonet.DATA_MANAGER, "Updating local template with URN : " + urn);

         // Update template
         XmlSerializer.updateTemplate(dbms, template);

      }

      // Update the index
      indexMetadata(dbms, urn, pm);

      // TODO handle Openwis validation
      // do the validation last - it throws exceptions
      if (validate) {
         doValidate(session, abstractMdInfo.getSchema(), id, md, lang);
      }

      return true;
   }

   /**
    * Used by the validate embedded service. The validation report
    * is stored in the session.
    *
    * FIXME : if a record A is in edit mode, only validation report
    * could be retrieve for record A. If asked for record B,
    * validateMetadataEmbedded will use A (stored in session) instead of B ?
    *
    * @param session
    * @param schema
    * @param id
    * @param md
    * @param lang
    * @return
    * @throws Exception
    */
   public Element doValidate(UserSession session, String schema, String id, Element md, String lang)
         throws Exception {
      Log.debug(Geonet.DATA_MANAGER, "Creating validation report for record #" + id + ".");

      Element sessionReport = (Element) session.getProperty(Geonet.Session.VALIDATION_REPORT);
      if (sessionReport != null) {
         Log.debug(Geonet.DATA_MANAGER, "  Validation report available in session.");
         sessionReport.detach();
         return sessionReport;
      }
      Element errorReport = new Element("report", Edit.NAMESPACE);
      errorReport.setAttribute("id", id, Edit.NAMESPACE);

      // XSD first...
      Element xsdErrors = getXSDXmlReport(schema, md);
      if (xsdErrors != null && xsdErrors.getContent().size() > 0) {
         errorReport.addContent(xsdErrors);
      }

      // ...then schematrons
      Element schemaTronXml = doSchemaTronForEditor(schema, md, lang);
      if (schemaTronXml != null && schemaTronXml.getContent().size() > 0) {
         Element schematron = new Element("schematronerrors", Edit.NAMESPACE);
         Element idElem = new Element("id", Edit.NAMESPACE);
         idElem.setText(id);
         schematron.addContent(idElem);
         errorReport.addContent(schemaTronXml);
         //throw new SchematronValidationErrorEx("Schematron errors detected - see schemaTron report for "+id+" in htmlCache for more details",schematron);
      }

      session.setProperty(Geonet.Session.VALIDATION_REPORT, errorReport);

      return errorReport;
   }

   /**
    * Update metadata. Used by old harvesting procedure
    * @param dbms
    * @param id
    * @param md
    * @param changeDate
    * @throws Exception
    */
   public void updateMetadataExt(Dbms dbms, String id, Element md, String changeDate)
         throws Exception {
      updateMetadataHarvested(dbms, Integer.parseInt(id), md, changeDate, null);
   }

   /**
    * Update metadata. Used by the harvesting procedure
    *
    * @deprecated
    *
    * @param dbms
    * @param id
    * @param metadata
    * @param changeDate
    * @param localHarvestDate
    * @throws SQLException
    * @throws JDOMException
    */
   public void updateMetadataHarvested(Dbms dbms, Integer id, Element metadata, String changeDate,
         String localHarvestDate) throws SQLException, JDOMException {
      new UnsupportedOperationException();
   }

   //--------------------------------------------------------------------------
   //---
   //--- Metadata Delete API
   //---
   //--------------------------------------------------------------------------

   /**
    * Deletes a set of metadata records based on the URN.
    *
    * @param dbms
    * @param urns
    * @param flagAsDeleted
    * @return
    *       <code>true</code> if ALL metadata records from the collection were deleted, <code>false</code> is some or all metadata
    *       records could not be deleted.
    * @throws Exception
    */
   public synchronized boolean deleteMetadataCollection(Dbms dbms, List<String> urns, boolean flagAsDeleted)
        throws Exception {

//      Log.info(Geonet.ADMIN, String.format("Deleting %d metadata records", urns.size()));

      boolean allMetadataRecordsDeleted = true;
      long startTime = System.currentTimeMillis();

      // Delete Metadata Product
      try
      {
         new ProductMetadataManager().delete(urns);
      }
      catch (CannotDeleteAllProductMetadataException_Exception e)
      {
         // If any metadata cannot be deleted, remove them from the list of metadata records to delete below.
         Log.error(Geonet.ADMIN, "The following metadata records could not be deleted from the data services.  They will not be deleted from the DB or index");
         Log.error(Geonet.ADMIN, "The URNs are: " + StringUtils.join(e.getFaultInfo().getUrns().iterator(), "; "));

         urns = new ArrayList<String>(urns);
         urns.removeAll(e.getFaultInfo().getUrns());
         allMetadataRecordsDeleted = false;
      }


      long split1 = System.currentTimeMillis();

      List<IndexableElement> itemsToDeleteFromIndex = new ArrayList<IndexableElement>();

      for (String urn : urns) {
         // Create a deleted metadata manager
         IDeletedMetadataManager dmm = new DeletedMetadataManager(dbms);
         DeletedMetadata deletedMetadata = null;
         if (flagAsDeleted) {
            deletedMetadata = dmm.createDeletedMetadataFromMetadataUrn(urn);
         }

         // fill the deletedMetadata table if deleted metadata not null
         if (flagAsDeleted && deletedMetadata != null) {
            dmm.insertDeletedMetadata(deletedMetadata);
         }

         //-- Delete metadata relations.
         Integer id = Integer.parseInt(getMetadataId(dbms, urn));
         XmlSerializer.deleteMetadataRelations(dbms, id);

         //--- remove metadata
         XmlSerializer.delete(dbms, "Metadata", urn);

         itemsToDeleteFromIndex.add(new DbmsIndexableElement(dbms, urn, null));
      }
      long split4 = System.currentTimeMillis();

      //--- update search criteria / search index...
      searchMan.delete(itemsToDeleteFromIndex);
      long split5 = System.currentTimeMillis();

      Log.info(Geonet.ADMIN, String.format("Deleted metadata collection: size = %d, (product, db, index) = %d, %d, %d",
            urns.size(),
            split1 - startTime,
            split4 - split1,
            split5 - split4));

      return allMetadataRecordsDeleted;
   }
   /**
    * Removes a metadata.
    * @param dbms
    * @param id the metadata ID
    * @param urn the metadata urn/uuid
    * @throws Exception
    */
   public synchronized void deleteMetadata(Dbms dbms, String urn, boolean flagAsDeleted)
         throws Exception {

      long startTime = System.currentTimeMillis();

      // Delete Metadata Product
      new ProductMetadataManager().delete(urn);
      long split1 = System.currentTimeMillis();

      // Create a deleted metadata manager
      IDeletedMetadataManager dmm = new DeletedMetadataManager(dbms);
      DeletedMetadata deletedMetadata = null;
      if (flagAsDeleted) {
         deletedMetadata = dmm.createDeletedMetadataFromMetadataUrn(urn);
      }

      // fill the deletedMetadata table if deleted metadata not null
      if (flagAsDeleted && deletedMetadata != null) {
         dmm.insertDeletedMetadata(deletedMetadata);
      }
      long split2 = System.currentTimeMillis();

      //-- Delete metadata relations.
      Integer id = Integer.parseInt(getMetadataId(dbms, urn));
      XmlSerializer.deleteMetadataRelations(dbms, id);
      long split3 = System.currentTimeMillis();

      //--- remove metadata
      XmlSerializer.delete(dbms, "Metadata", urn);
      long split4 = System.currentTimeMillis();

      //--- update search criteria / search index...
      searchMan.delete(new DbmsIndexableElement(dbms, urn, null));
      long split5 = System.currentTimeMillis();

      Log.info(Geonet.ADMIN, String.format("Deleted metadata times: (product, deletedMetadata, relations, db, index) = %d, %d, %d, %d, %d",
            split1 - startTime,
            split2 - split1,
            split3 - split2,
            split4 - split3,
            split5 - split4));
   }

   /**
    * Removes a metadata.
    * @param dbms
    * @param id the metadata ID
    * @param urn the metadata urn/uuid
    * @throws Exception
    */
   public synchronized void deleteTemplate(Dbms dbms, String urn) throws Exception {
      //--- remove metadata
      XmlSerializer.delete(dbms, "Metadata", urn);

      //--- update search criteria / search index...
      searchMan.delete(new DbmsIndexableElement(dbms, urn, null));
   }

   /**
    * Removes a metadata by ID.
    *
    *@deprecated use {@link #deleteMetadataByURN(Dbms, String)}
    *
    * @param dbms
    * @param id the metadata ID
    * @throws Exception
    */
   public void deleteMetadataById(Dbms dbms, String id) throws Exception {
      String urn = getMetadataUuid(dbms, id);
      deleteMetadata(dbms, urn, true);
   }

   /**
    * Removes a metadata by ID.
    *
    * @deprecated use {@link #deleteMetadataByURN(Dbms, String)}
    *
    * @param dbms
    * @param id the metadata ID
    * @throws Exception
    */
   public void deleteMetadataById(Dbms dbms, String id, boolean flagAsDeleted) throws Exception {
      String urn = getMetadataUuid(dbms, id);
      deleteMetadata(dbms, urn, flagAsDeleted);
   }

   /**
    * Removes a metadata by URN.
    * @param dbms
    * @param urn the metadata URN
    * @throws Exception
    */
   public void deleteMetadataByURN(Dbms dbms, String urn) throws Exception {
      deleteMetadata(dbms, urn, true);
   }

   //--------------------------------------------------------------------------
   //---
   //--- Metadata thumbnail API
   //---
   //--------------------------------------------------------------------------

   public Element getThumbnails(Dbms dbms, String id) throws Exception {
      Element md = XmlSerializer.select(dbms, "Metadata", id);

      if (md == null)
         return null;

      md.detach();

      String schema = getMetadataSchema(dbms, id);

      //--- do an XSL  transformation

      String styleSheet = editLib.getSchemaDir(schema) + Geonet.File.EXTRACT_THUMBNAILS;

      Element result = Xml.transform(md, styleSheet);
      result.addContent(new Element("id").setText(id));

      return result;
   }

   //--------------------------------------------------------------------------

   public void setThumbnail(Dbms dbms, String id, String uuid, boolean small, String file)
         throws Exception {
      int pos = file.lastIndexOf('.');
      String ext = (pos == -1) ? "???" : file.substring(pos + 1);

      Element env = new Element("env");
      env.addContent(new Element("file").setText(file));
      env.addContent(new Element("ext").setText(ext));

      manageThumbnail(dbms, id, uuid, small, env, Geonet.File.SET_THUMBNAIL);
   }

   //--------------------------------------------------------------------------

   public void unsetThumbnail(Dbms dbms, String id, String uuid, boolean small) throws Exception {
      Element env = new Element("env");

      manageThumbnail(dbms, id, uuid, small, env, Geonet.File.UNSET_THUMBNAIL);
   }

   //--------------------------------------------------------------------------

   private void manageThumbnail(Dbms dbms, String id, String uuid, boolean small, Element env,
         String styleSheet) throws Exception {
      Element md = XmlSerializer.select(dbms, "Metadata", id);

      if (md == null)
         return;

      md.detach();

      String schema = getMetadataSchema(dbms, id);

      //-----------------------------------------------------------------------
      //--- remove thumbnail from metadata

      //--- setup environment

      String type = small ? "thumbnail" : "large_thumbnail";

      env.addContent(new Element("type").setText(type));

      transformMd(dbms, id, uuid, md, env, schema, styleSheet);
   }

   //--------------------------------------------------------------------------

   void transformMd(Dbms dbms, String id, String uuid, Element md, Element env, String schema,
         String styleSheet) throws Exception {

      //--- setup root element

      Element root = new Element("root");
      root.addContent(md);
      root.addContent(env);

      //--- do an XSL  transformation

      styleSheet = getSchemaDir(schema) + styleSheet;

      md = Xml.transform(root, styleSheet);
      XmlSerializer.update(dbms, id, md);

      //--- update search criteria
      indexMetadata(dbms, uuid, null);
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods
   //---
   //--------------------------------------------------------------------------

   /** Used for editing : swaps 2 elements
     */

   private void swapElements(Element el1, Element el2) throws Exception {

      Element parent = el1.getParentElement();
      if (parent == null) {
         throw new IllegalArgumentException("No parent element for swapping");
      }

      int index1 = parent.indexOf(el1);
      if (index1 == -1) {
         throw new IllegalArgumentException("Element 1 not found for swapping");
      }
      int index2 = parent.indexOf(el2);
      if (index2 == -1) {
         throw new IllegalArgumentException("Element 2 not found for swapping");
      }

      Element el1Spare = (Element) el1.clone();

      parent.setContent(index1, (Element) el2.clone());
      parent.setContent(index2, el1Spare);
   }

   //--------------------------------------------------------------------------

   private Element updateFixedInfo(String schema, String id, Element md, Dbms dbms)
         throws Exception {
      String query = "SELECT uuid, source, isTemplate FROM Metadata WHERE id = " + id;

      Element rec = dbms.select(query).getChild("record");
      String isTemplate = rec.getChildText("istemplate");

      // don't process templates
      if (isTemplate.equals("n")) {
         String uuid = rec.getChildText("uuid");
         return updateFixedInfoExisting(schema, id, md, uuid);
      } else
         return md;
   }

   //--------------------------------------------------------------------------

   public Element updateFixedInfoExisting(String schema, String id, Element md, String uuid)
         throws Exception {
      //--- setup environment - for new records

      Element env = new Element("env");

      env.addContent(new Element("id").setText(id));
      env.addContent(new Element("uuid").setText(uuid));
      env.addContent(new Element("updateDateStamp").setText("no"));
      env.addContent(new Element("datadir").setText(Lib.resource.getDir(dataDir,
            Params.Access.PRIVATE, id)));
      return updateFixedInfo(schema, md, env);
   }

   //--------------------------------------------------------------------------

   public Element updateFixedInfoNew(String schema, String id, Element md, String uuid,
         String parentUuid, String dataPolicyName) throws Exception {
      //--- setup environment - for new records

      Element env = new Element("env");

      env.addContent(new Element("id").setText(id));
      env.addContent(new Element("uuid").setText(uuid));
      env.addContent(new Element("parentUuid").setText(parentUuid));
      env.addContent(new Element("updateDateStamp").setText("yes"));
      env.addContent(new Element("datadir").setText(Lib.resource.getDir(dataDir,
            Params.Access.PRIVATE, id)));
      env.addContent(new Element("dataPolicy").setText(dataPolicyName));
      return updateFixedInfo(schema, md, env);
   }

   //--------------------------------------------------------------------------

   private Element updateFixedInfo(String schema, Element md, Element env) throws Exception {

      //--- environment common to both existing and new records goes here

      env.addContent(new Element("changeDate").setText(new ISODate().toString()));
      env.addContent(new Element("siteURL").setText(getSiteURL()));
      Element system = settingMan.get("system", -1);
      env.addContent(Xml.transform(system, appPath + Geonet.Path.STYLESHEETS + "/xml/config.xsl"));

      //--- setup root element

      Element root = new Element("root");
      root.addContent(md);
      root.addContent(env);

      //--- do the XSL transformation using update-fixed-info.xsl

      String styleSheet = editLib.getSchemaDir(schema) + Geonet.File.UPDATE_FIXED_INFO;

      return Xml.transform(root, styleSheet);
   }

   //--------------------------------------------------------------------------

   /**
    * Update all children of the selected parent. Some elements are protected
    * in the children according to the stylesheet used in
    * xml/schemas/[SCHEMA]/update-child-from-parent-info.xsl.
    *
    * Children MUST be editable and also in the same schema of the parent.
    * If not, child is not updated.
    *
    * @param srvContext
    *            service context
    * @param parentUuid
    *            parent uuid
    * @param params
    *            parameters
    * @param children
    *            children
    * @return
    * @throws Exception
    */
   public Set<String> updateChildren(ServiceContext srvContext, String parentUuid,
         String[] children, Map<String, String> params) throws Exception {
      Dbms dbms = (Dbms) srvContext.getResourceManager().open(Geonet.Res.MAIN_DB);

      String parentId = params.get(Params.ID);
      String parentSchema = params.get(Params.SCHEMA);

      // --- get parent metadata in read/only mode
      Element parent = getMetadata(srvContext, parentId, false);

      Element env = new Element("update");
      env.addContent(new Element("parentUuid").setText(parentUuid));
      env.addContent(new Element("siteURL").setText(getSiteURL()));
      env.addContent(new Element("parent").addContent(parent));

      // Set of untreated children (out of privileges, different schemas)
      Set<String> untreatedChildSet = new HashSet<String>();

      AccessManager am = new AccessManager(dbms);

      // only get iso19139 records
      for (String childId : children) {

         // Check privileges
         if (!am.canEdit(srvContext, childId)) {
            untreatedChildSet.add(childId);
            Log.debug(Geonet.DATA_MANAGER, "Could not update child (" + childId
                  + ") because of privileges.");
            continue;
         }

         Element child = getMetadata(srvContext, childId, false);
         String childSchema = child.getChild(Edit.RootChild.INFO, Edit.NAMESPACE).getChildText(
               Edit.Info.Elem.SCHEMA);

         // Check schema matching. CHECKME : this suppose that parent and
         // child are in the same schema (even not profil different)
         if (!childSchema.equals(parentSchema)) {
            untreatedChildSet.add(childId);
            Log.debug(Geonet.DATA_MANAGER, "Could not update child (" + childId
                  + ") because schema (" + childSchema + ") is different from the parent one ("
                  + parentSchema + ").");
            continue;
         }

         Log.debug(Geonet.DATA_MANAGER, "Updating child (" + childId + ") ...");

         // --- setup xml element to be processed by XSLT

         Element rootEl = new Element("root");
         Element childEl = new Element("child").addContent(child.detach());
         rootEl.addContent(childEl);
         rootEl.addContent(env.detach());

         // --- do an XSL transformation

         String styleSheet = editLib.getSchemaDir(parentSchema)
               + Geonet.File.UPDATE_CHILD_FROM_PARENT_INFO;
         Element childForUpdate = new Element("root");
         childForUpdate = Xml.transform(rootEl, styleSheet, params);

         XmlSerializer.update(dbms, childId, childForUpdate, new ISODate().toString());

         // FIXME Handle product metadata if needed!

         rootEl = null;
      }

      return untreatedChildSet;
   }

   //--------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   private Collection<Integer> getOperationEnum(UserSession session, Dbms dbms, String id) throws Exception
   {
      GroupManager gm = new GroupManager(dbms);
      Collection<Integer> operationsEnum = null;
      List<Group> groups = null;
      if (session.isAuthenticated()) {
         if (session.getProfile().equals(Geonet.Profile.ADMINISTRATOR)) {
            groups = gm.getAllGroups();
         } else {
            groups = gm.getAllUserGroups(session.getUserId());
         }
         IDataPolicyManager dpm = new DataPolicyManager(dbms);
         Collection<Operation> operations = dpm.getAllOperationAllowedByMetadataId(id, groups);

         operationsEnum = CollectionUtils.collect(operations, new Transformer() {

            @Override
            public Object transform(Object arg0) {
               return ((Operation) arg0).getId();
            }
         });
      } else {
         // Always grant VIEW privileges for non authenticated users.
         operationsEnum = Arrays.asList(OperationEnum.VIEW.getId());
      }
      return operationsEnum;
   }

   private Element buildInfoElem(ServiceContext context, String id, String version)
         throws Exception {
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      String query = "SELECT schemaId, createDate, changeDate, source, isTemplate, title, "
            + "uuid, isHarvested, harvestingtask, popularity, rating, owner, displayOrder FROM Metadata WHERE id = "
            + id;

      // add Metadata table infos: schemaId, createDate, changeDate, source,
      Element rec = dbms.select(query).getChild("record");

      String schema = rec.getChildText("schemaid");
      String createDate = rec.getChildText("createdate");
      String changeDate = rec.getChildText("changedate");
      String source = rec.getChildText("source");
      String isTemplate = rec.getChildText("istemplate");
      String title = rec.getChildText("title");
      String uuid = rec.getChildText("uuid");
      String isHarvested = rec.getChildText("isharvested");
      String harvestingTaskId = rec.getChildText("harvestingtask");
      String popularity = rec.getChildText("popularity");
      String rating = rec.getChildText("rating");
      String owner = rec.getChildText("owner");
      String displayOrder = rec.getChildText("displayorder");

      Element info = new Element(Edit.RootChild.INFO, Edit.NAMESPACE);

      addElement(info, Edit.Info.Elem.ID, id);
      addElement(info, Edit.Info.Elem.SCHEMA, schema);
      addElement(info, Edit.Info.Elem.CREATE_DATE, createDate);
      addElement(info, Edit.Info.Elem.CHANGE_DATE, changeDate);
      addElement(info, Edit.Info.Elem.IS_TEMPLATE, isTemplate);
      addElement(info, Edit.Info.Elem.TITLE, title);
      addElement(info, Edit.Info.Elem.SOURCE, source);
      addElement(info, Edit.Info.Elem.UUID, uuid);
      addElement(info, Edit.Info.Elem.IS_HARVESTED, isHarvested);
      addElement(info, Edit.Info.Elem.POPULARITY, popularity);
      addElement(info, Edit.Info.Elem.RATING, rating);
      addElement(info, Edit.Info.Elem.DISPLAY_ORDER, displayOrder);

      if (isHarvested.equals("y")) {
         //FIXME Create a harvesting element type
         harvestingTaskId += "";
         Element harvestInfo = new Element(Edit.Info.Elem.HARVEST_INFO);
         harvestInfo.addContent(new Element("type").setText("oaipmh"));
         info.addContent(harvestInfo);
      }

      if (version != null)
         addElement(info, Edit.Info.Elem.VERSION, version);

      // add operations
      Collection<Integer> operationsEnum = getOperationEnum(context.getUserSession(), dbms, id);

      addElement(info, Edit.Info.Elem.VIEW,
            String.valueOf(operationsEnum.contains(OperationEnum.VIEW.getId())));
      addElement(info, Edit.Info.Elem.DOWNLOAD,
            String.valueOf(operationsEnum.contains(OperationEnum.DOWNLOAD.getId())));
      //      addElement(info, Edit.Info.Elem.FEATURED,
      //            String.valueOf(operationsEnum.contains(OperationEnum.FEATURED.getId())));

      AccessManager am = new AccessManager(dbms);
      if (am.canEdit(context, id))
         addElement(info, Edit.Info.Elem.EDIT, "true");

      if (am.isOwner(context, id)) {
         addElement(info, Edit.Info.Elem.OWNER, "true");
      }

      // add owner name
      addElement(info, Edit.Info.Elem.OWNERNAME, owner);

      // add categories
      CategoryManager cm = new CategoryManager(dbms);
      Category category = cm.getCategoryByMetadataUrn(uuid);
      if (category != null) {
         addElement(info, Edit.Info.Elem.CATEGORY, category.getName());
      }

      // add baseUrl of this site (from settings)
      String host = settingMan.getValue("system/server/host");
      String port = settingMan.getValue("system/server/port");
      addElement(info, Edit.Info.Elem.BASEURL, "http://" + host + (port == "80" ? "" : ":" + port)
            + baseURL);

      return info;
   }

   //--------------------------------------------------------------------------

   private static void addElement(Element root, String name, String value) {
      root.addContent(new Element(name).setText(value));
   }

   //--------------------------------------------------------------------------

   public String getSiteID() {
      return settingMan.getValue("system/site/siteId");
   }

   //---------------------------------------------------------------------------
   //---
   //--- Static methods - GAST is the only thing that should use these
   //---
   //---------------------------------------------------------------------------

   public static void setNamespacePrefix(Element md) {
      //--- if the metadata has no namespace or already has a namespace then
      //--- we must skip this phase

      Namespace ns = md.getNamespace();
      if (ns == Namespace.NO_NAMESPACE || (!md.getNamespacePrefix().equals("")))
         return;

      //--- set prefix for iso19139 metadata

      ns = Namespace.getNamespace("gmd", md.getNamespace().getURI());
      setNamespacePrefix(md, ns);
   }

   //---------------------------------------------------------------------------

   private static void setNamespacePrefix(Element md, Namespace ns) {
      if (md.getNamespaceURI().equals(ns.getURI()))
         md.setNamespace(ns);

      for (Object o : md.getChildren())
         setNamespacePrefix((Element) o, ns);
   }

   //---------------------------------------------------------------------------

   /**
    * Add missing namespace (ie. GML) to XML inputs. It should be done by the client side
    * but add a check in here.
    *
    * @param fragment 		The fragment to be checked and processed.
    *
    * @return 				The updated fragment.
    */
   private String addNamespaceToFragment(String fragment) {
      //add the gml namespace if its missing
      if (fragment.contains("<gml:") && !fragment.contains("xmlns:gml=\"")) {
         Log.debug(Geonet.DATA_MANAGER, "  Add missing GML namespace.");
         fragment = fragment.replaceFirst("<gml:([^ >]+)",
               "<gml:$1 xmlns:gml=\"http://www.opengis.net/gml\"");
      }
      return fragment;
   }

   //--------------------------------------------------------------------------

   public void setNamespacePrefixUsingSchemas(Element md, String schema) throws Exception {
      //--- if the metadata has no namespace or already has a namespace prefix
      //--- then we must skip this phase

      Namespace ns = md.getNamespace();
      if (ns == Namespace.NO_NAMESPACE)
         return;

      MetadataSchema mds = getSchema(schema);
      //--- get the namespaces and add prefixes to any that are
      //--- default ie. prefix is ''

      ArrayList nsList = new ArrayList();
      nsList.add(ns);
      nsList.addAll(md.getAdditionalNamespaces());
      for (Object aNsList : nsList) {
         Namespace aNs = (Namespace) aNsList;
         if (aNs.getPrefix().equals("")) { // found default namespace
            String prefix = mds.getPrefix(aNs.getURI());
            if (prefix == null) {
            	Log.warning(Geonet.DATA_MANAGER,
                     "No prefix - cannot find a namespace to set for element "
                           + md.getQualifiedName() + " - namespace URI " + aNs.getURI());
            } else {
            	ns = Namespace.getNamespace(prefix, aNs.getURI());
	            setNamespacePrefix(md, ns);
	            if (!md.getNamespace().equals(ns)) {
	               md.removeNamespaceDeclaration(aNs);
	               md.addNamespaceDeclaration(ns);
	            }
            }
         }
      }
   }

   //--------------------------------------------------------------------------

   private MetadataSchema findSchema(Element md, Namespace ns) throws Exception {
      String nsUri = ns.getURI();
      for (String schema : getSchemas()) {
         MetadataSchema mds = getSchema(schema);
         String nsSchema = mds.getPrimeNS();
         if (nsSchema != null && nsUri.equals(nsSchema)) {
            Log.debug(Geonet.DATA_MANAGER, "Found schema " + schema + " with NSURI " + nsSchema);
            return mds;
         }
      }

      throw new IllegalArgumentException("Cannot find a namespace to set for element "
            + md.getQualifiedName() + " with namespace URI " + nsUri);
   }

   
   /**
    * Shutdown executor service.
    */
   public static void shutdownExecutor() {
      executor.shutdownNow();
   }
}
