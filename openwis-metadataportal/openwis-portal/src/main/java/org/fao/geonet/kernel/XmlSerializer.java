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

import java.io.Serializable;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jeeves.constants.Jeeves;
import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.SerialFactory;
import jeeves.utils.Util;
import jeeves.utils.Xml;
import jeeves.xlink.Processor;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.model.metadata.source.HarvestingSource;
import org.openwis.metadataportal.model.metadata.source.ProcessType;
import org.openwis.metadataportal.model.metadata.source.SiteSource;

import com.google.common.base.Joiner;

//=============================================================================

/**
 * This class is responsible of reading and writing xml on the database. It
 * works on tables like (id, data, lastChangeDate)<P>
 */
public class XmlSerializer {
   private static SettingManager sm;

   //--------------------------------------------------------------------------
   //---
   //--- PRIVATE METHODS
   //---
   //--------------------------------------------------------------------------

   /**
    * Retrieve the xml element which id matches the given one. The element is
    * read from 'table' and the string read is converted into xml.
    * @param dbms
    * @param table
    * @param id
    * @return
    * @throws Exception
    */
   private static Element internalSelect(Dbms dbms, String table, String id) throws Exception {
      String query = MessageFormat.format("SELECT data FROM {0} WHERE id = ?",
            StringEscapeUtils.escapeSql(table));

      Element rec = dbms.select(query, new Integer(id)).getChild(Jeeves.Elem.RECORD);

      if (rec == null) {
         return null;
      }

      String xmlData = rec.getChildText("data");

      rec = Xml.loadString(xmlData, false);

      return (Element) rec.detach();
   }

   //--------------------------------------------------------------------------
   //---
   //--- PUBLIC API
   //---
   //--------------------------------------------------------------------------

   public static void setSettingManager(SettingManager sMan) {
      sm = sMan;
   }

   //--------------------------------------------------------------------------

   public static boolean resolveXLinks() {
      if (sm == null) { // no initialization, no XLinks
         Log.error(Geonet.DATA_MANAGER,
               "No settingManager in XmlSerializer, XLink Resolver disabled.");
         return false;
      }

      String xlR = sm.getValue("system/xlinkResolver/enable");
      if (xlR != null) {
         boolean isEnabled = xlR.equals("true");
         if (isEnabled)
            Log.info(Geonet.DATA_MANAGER, "XLink Resolver enabled.");
         else
            Log.info(Geonet.DATA_MANAGER, "XLink Resolver disabled.");
         return isEnabled;
      } else {
         Log.error(Geonet.DATA_MANAGER,
               "XLink resolver setting does not exist! XLink Resolver disabled.");
         return false;
      }
   }

   //--------------------------------------------------------------------------

   /** Retrieve the xml element which id matches the given one. The element is
     * read from 'table' and the string read is converted into xml, XLinks are
   	* resolved when config'd on
     */

   public static Element select(Dbms dbms, String table, String id) throws Exception {
      Element rec = internalSelect(dbms, table, id);
      if (resolveXLinks())
         Processor.detachXLink(rec);
      return rec;
   }

   //--------------------------------------------------------------------------

   /** Retrieve the xml element which id matches the given one. The element is
     * read from 'table' and the string read is converted into xml, XLinks are
   	* NOT resolved even if they are config'd on - this is used when you want
   	* to do XLink processing yourself
     */

   public static Element selectNoXLinkResolver(Dbms dbms, String table, String id) throws Exception {
      return internalSelect(dbms, table, id);
   }

   //--------------------------------------------------------------------------

   /**
   * @deprecated
   */
   public static String insert(Dbms dbms, String schema, Element xml, int serial, String source,
         String uuid, String owner, int datapolicy, int category) throws SQLException {

      return insert(dbms, schema, xml, serial, source, uuid, null, null, "n", null, owner,
            datapolicy, category, "n", null, null);
   }

   //--------------------------------------------------------------------------

   /**
    * @deprecated
    */
   public static String insert(Dbms dbms, String schema, Element xml, int serial, String source,
         String uuid, String isTemplate, String title, String owner, int datapolicy,
         Integer category, String isHarvested, Integer harvestingTaskId, String localHarvestDate)
         throws SQLException {

      return insert(dbms, schema, xml, serial, source, uuid, null, null, isTemplate, title, owner,
            datapolicy, category, isHarvested, harvestingTaskId, localHarvestDate);
   }

   //--------------------------------------------------------------------------

   /**
    * @deprecated
    */
   public static String insert(Dbms dbms, String schema, Element xml, int serial, String source,
         String uuid, String createDate, String changeDate, String isTemplate, String title,
         String owner, int datapolicy, Integer category, String isHarvested,
         Integer harvestingTaskId, String localHarvestDate) throws SQLException {

      if (resolveXLinks()) {
         Processor.removeXLink(xml);
      }

      String date = new ISODate().toString();

      if (createDate == null) {
         createDate = date;
      }

      if (changeDate == null) {
         changeDate = date;
      }

      fixCR(xml);

      StringBuffer fields = new StringBuffer(
            "id, schemaId, data, createDate, changeDate, source, uuid, isTemplate, isHarvested, root, owner, datapolicy");
      StringBuffer values = new StringBuffer("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");

      List<Serializable> args = new ArrayList<Serializable>();
      args.add(serial);
      args.add(schema);
      args.add(Xml.getString(xml));
      args.add(createDate);
      args.add(changeDate);
      args.add(source);
      args.add(uuid);
      args.add(isTemplate);
      args.add(isHarvested);
      args.add(xml.getQualifiedName());
      args.add(owner);
      args.add(datapolicy);

      if (title != null) {
         fields.append(", title");
         values.append(", ?");
         args.add(title);
      }

      if (harvestingTaskId != null) {
         fields.append(", harvestingTask");
         values.append(", ?");
         args.add(harvestingTaskId);
      }

      if (localHarvestDate != null) {
         fields.append(", localImportDate");
         values.append(", ?");
         args.add(localHarvestDate);
      }

      if (category != null) {
         fields.append(", category");
         values.append(", ?");
         args.add(category);
      }

      String query = "INSERT INTO Metadata (" + fields + ") VALUES(" + values + ")";
      dbms.execute(query, args.toArray());

      return Integer.toString(serial);
   }

   //--------------------------------------------------------------------------

   /**
    * @deprecated
    */
   public static void update(Dbms dbms, String id, Element xml) throws SQLException {
      update(dbms, Integer.parseInt(id), xml, null, null);
   }

   /**
    * @deprecated
    */
   public static void update(Dbms dbms, String id, Element xml, String changeDate)
         throws SQLException {
      update(dbms, Integer.parseInt(id), xml, changeDate, null);
   }

   /**
    * Updates an xml element into the database. The new data replaces the old one
    * 
    * @deprecated
    * 
    * @param dbms
    * @param id
    * @param xml
    * @param changeDate
    * @param locaHarvestDate
    * @throws SQLException
    */
   public static void update(Dbms dbms, Integer id, Element xml, String changeDate,
         String locaHarvestDate) throws SQLException {
      if (resolveXLinks()) {
         Processor.removeXLink(xml);
      }

      String query = "UPDATE Metadata SET data=?, changeDate=?, root=?, localImportDate=? WHERE id=?";

      List<Serializable> args = new ArrayList<Serializable>();

      fixCR(xml);
      args.add(Xml.getString(xml));

      if (changeDate == null) {
         args.add(new ISODate().toString());
      } else {
         args.add(changeDate);
      }

      args.add(xml.getQualifiedName());
      args.add(locaHarvestDate);
      args.add(id);

      dbms.execute(query, args.toArray());
   }

   /**
    * Deletes an xml element given its id.
    * 
    * @param dbms
    * @param table
    * @param id
    * @throws SQLException
    */
   public static void delete(Dbms dbms, String table, String urn) throws SQLException {
      // TODO: Ultimately we want to remove any xlinks in this document
      // that aren't already in use from the xlink cache. For now we
      // rely on the admin clearing cache and reindexing regularly
      String query = "DELETE FROM " + table + " WHERE uuid=?";

      dbms.execute(query, urn);
   }
   
   /**
    * Deletes an xml element given its id.
    * 
    * @param dbms
    * @param table
    * @param id
    * @throws SQLException
    */
   public static void deleteMetadataRelations(Dbms dbms, Integer id) throws SQLException {
      String query = "DELETE FROM Relations WHERE id=? OR relatedid=?";
      dbms.execute(query, id, id);
   }

   /**
    * Insert Metadata into the database.
    * 
    * @param dbms a dbms
    * @param metadata the metadata to insert
    * @return the metadata ID
    * @throws SQLException
    */
   public static Integer insertMetadata(Dbms dbms, Metadata metadata) throws SQLException {

      if (resolveXLinks()) {
         Processor.removeXLink(metadata.getData());
      }

      fixCR(metadata.getData());

      String date = new ISODate().toString();

      if (metadata.getCreateDate() == null) {
         metadata.setCreateDate(date);
      }

      if (metadata.getChangeDate() == null) {
         metadata.setChangeDate(date);
      }

      metadata.setLocalImportDate(date);

      metadata.setId(SerialFactory.getSerial(dbms, "Metadata"));

      List<String> fields = new ArrayList<String>();
      fields.addAll(Arrays.asList("id", "uuid", "schemaId", "isTemplate", "isHarvested",
            "createDate", "changeDate", "data", "title", "root", "datapolicy", "category",
            "localImportDate", "importProcess"));

      Boolean isHarvested = !metadata.getSource().getProcessType().equals(ProcessType.LOCAL);

      List<Serializable> args = new ArrayList<Serializable>();
      args.add(metadata.getId());
      args.add(metadata.getUrn());
      args.add(metadata.getSchema());
      args.add("n");
      args.add(BooleanUtils.toString(isHarvested, "y", "n"));
      args.add(metadata.getCreateDate());
      args.add(metadata.getChangeDate());
      args.add(Xml.getString(metadata.getData()));
      args.add(metadata.getTitle());
      args.add(metadata.getData().getQualifiedName());
      args.add(metadata.getDataPolicy().getId());
      args.add(metadata.getCategory().getId());
      args.add(metadata.getLocalImportDate());
      args.add(metadata.getSource().getProcessType().toString());

      String source = null;
      if (metadata.getSource() instanceof SiteSource) {
         // Handle owner
         String owner = ((SiteSource) metadata.getSource()).getUserName();
         fields.add("owner");
         args.add(owner);

         source = ((SiteSource) metadata.getSource()).getSourceId();

      } else {
         HarvestingSource harvestingSource = (HarvestingSource) metadata.getSource();
         // Add the harvesting task ID.
         fields.add("harvestingTask");
         args.add(harvestingSource.getHarvestingTask().getId());

         // If not null, add the harvesting URI for some specific harvesters.
         if (StringUtils.isNotBlank(harvestingSource.getHarvestingURI())) {
            fields.add("harvestUri");
            args.add(harvestingSource.getHarvestingURI());
         }

         source = harvestingSource.getHarvestingTask().getUuid();
      }

      // Finally add the source
      fields.add("source");
      args.add(source);

      // Dynamically builds values for the query.
      String[] fieldsArray = new String[fields.size()];
      Arrays.fill(fieldsArray, "?");

      // Build the query using Joiner.
      String query = "INSERT INTO Metadata (" + Joiner.on(",").join(fields) + ") VALUES("
            + Joiner.on(",").join(fieldsArray) + ")";

      // Execute query
      dbms.execute(query, args.toArray());

      return metadata.getId();
   }

   /**
    * Description goes here.
    * @param template
    */
   public static Integer insertTemplate(Dbms dbms, Template template) throws Exception {
      if (resolveXLinks()) {
         Processor.removeXLink(template.getData());
      }

      fixCR(template.getData());

      String date = new ISODate().toString();

      if (template.getCreateDate() == null) {
         template.setCreateDate(date);
      }

      if (template.getChangeDate() == null) {
         template.setChangeDate(date);
      }

      template.setId(SerialFactory.getSerial(dbms, "Metadata"));

      List<String> fields = new ArrayList<String>();
      fields.addAll(Arrays.asList("id", "uuid", "schemaId", "isTemplate", "isHarvested",
            "createDate", "changeDate", "data", "title", "root", "displayorder", "importProcess", "datapolicy"));
      
      Boolean isHarvested = !template.getSource().getProcessType().equals(ProcessType.LOCAL);

      List<Serializable> args = new ArrayList<Serializable>();
      args.add(template.getId());
      args.add(template.getUrn());
      args.add(template.getSchema());
      args.add(BooleanUtils.toString(template.isSubTemplate(), "s", "y"));
      args.add(BooleanUtils.toString(isHarvested, "y", "n"));
      args.add(template.getCreateDate());
      args.add(template.getChangeDate());
      args.add(Xml.getString(template.getData()));
      args.add(template.getTitle());
      args.add(template.getData().getQualifiedName());
      args.add(template.getDisplayOrder());
      args.add(template.getSource().getProcessType().toString());
      args.add(template.getDataPolicy().getId());

      String source = null;
      if (template.getSource() instanceof SiteSource) {
         // Handle owner
         String owner = ((SiteSource) template.getSource()).getUserName();
         fields.add("owner");
         args.add(owner);

         source = ((SiteSource) template.getSource()).getSourceId();

      } else {
         HarvestingSource harvestingSource = (HarvestingSource) template.getSource();
         // Add the harvesting task ID.
         fields.add("harvestingTask");
         args.add(harvestingSource.getHarvestingTask().getId());

         // If not null, add the harvesting URI for some specific harvesters.
         if (StringUtils.isNotBlank(harvestingSource.getHarvestingURI())) {
            fields.add("harvestUri");
            args.add(harvestingSource.getHarvestingURI());
         }

         source = harvestingSource.getHarvestingTask().getUuid();
      }

      // Finally add the source
      fields.add("source");
      args.add(source);

      // Dynamically builds values for the query.
      String[] fieldsArray = new String[fields.size()];
      Arrays.fill(fieldsArray, "?");

      // Build the query using Joiner.
      String query = "INSERT INTO Metadata (" + Joiner.on(",").join(fields) + ") VALUES("
            + Joiner.on(",").join(fieldsArray) + ")";

      // Execute query
      dbms.execute(query, args.toArray());

      return template.getId();
   }

   /**
    * Update Metadata into the database.
    * 
    * @param dbms a dbms
    * @param metadata the metadata to update
    * @return the metadata ID
    * @throws SQLException
    */
   public static Integer updateMetadata(Dbms dbms, Metadata metadata) throws SQLException {

      if (resolveXLinks()) {
         Processor.removeXLink(metadata.getData());
      }

      fixCR(metadata.getData());

      List<String> fields = new ArrayList<String>();
      fields.addAll(Arrays.asList("uuid=?", "changeDate=?", "data=?", "root=?", "localImportDate=?"));

      List<Serializable> args = new ArrayList<Serializable>();
      args.add(metadata.getUrn());
      args.add(metadata.getChangeDate());
      args.add(Xml.getString(metadata.getData()));
      args.add(metadata.getData().getQualifiedName());
      args.add(new ISODate().toString());

      // Update the data policy if needed.
      if (metadata.getDataPolicy() != null) {
         fields.add("datapolicy=?");
         args.add(metadata.getDataPolicy().getId());
      }

      // Update the category if needed.      
      if (metadata.getCategory() != null) {
         fields.add("category=?");
         args.add(metadata.getCategory().getId());
      }

      // Update the title if needed.      
      if (metadata.getTitle() != null) {
         fields.add("title=?");
         args.add(metadata.getTitle());
      }

      if (metadata.getSource() != null) {
         String source = null;
         fields.add("importProcess=?");
         args.add(metadata.getSource().getProcessType().toString());
         
         if (metadata.getSource() instanceof SiteSource) {
            // Handle owner
            String owner = ((SiteSource) metadata.getSource()).getUserName();
            fields.add("owner=?");
            args.add(owner);
            // Reset other fields.
            fields.add("harvestingTask=?");
            args.add(null);
            fields.add("harvestUri=?");
            args.add(null);
            fields.add("isHarvested=?");
            args.add("n");
            source = ((SiteSource) metadata.getSource()).getSourceId();
         } else {
            HarvestingSource harvestingSource = (HarvestingSource) metadata.getSource();
            // Add the harvesting task ID.
            fields.add("harvestingTask=?");
            args.add(harvestingSource.getHarvestingTask().getId());
            fields.add("owner=?");
            args.add(null);
            fields.add("isHarvested=?");
            args.add("y");
            // If not null, add the harvesting URI for some specific harvesters.
            if (StringUtils.isNotBlank(harvestingSource.getHarvestingURI())) {
               fields.add("harvestUri=?");
               args.add(harvestingSource.getHarvestingURI());
            }
            source = harvestingSource.getHarvestingTask().getUuid();
         }
         
         // Finally add the source
         fields.add("source=?");
         args.add(source);
      }
      
      // Build query
      String query = "UPDATE Metadata SET " + Joiner.on(",").join(fields) + " WHERE uuid=?";
      
      // Add the query condition (URN) 
      args.add(metadata.getUrn());

      // Execute query
      dbms.execute(query, args.toArray());

      return metadata.getId();
   }

   /**
    * Update Template into the database.
    * 
    * @param dbms a dbms
    * @param template the template to update
    * @return 
    * @throws SQLException 
    */
   public static Integer updateTemplate(Dbms dbms, Template template) throws SQLException {
      
      // FIXME should resimve xlinks?
      
      fixCR(template.getData());

      List<String> fields = new ArrayList<String>();
      fields.addAll(Arrays.asList("changeDate=?", "data=?", "root=?"));

      List<Serializable> args = new ArrayList<Serializable>();
      args.add(template.getChangeDate());
      args.add(Xml.getString(template.getData()));
      args.add(template.getData().getQualifiedName());

      // Build query
      String query = "UPDATE Metadata SET " + Joiner.on(",").join(fields) + " WHERE uuid=?";
      
      // Add the query condition (URN) 
      args.add(template.getUrn());

      // Execute query
      dbms.execute(query, args.toArray());

      return template.getId();
      
   }

   /**
    * Fix the CR.
    * @param xml the XML
    */
   @SuppressWarnings("unchecked")
   private static void fixCR(Element xml) {
      List<Element> list = xml.getChildren();

      if (list.isEmpty()) {
         String text = xml.getText();
         xml.setText(Util.replaceString(text, "\r\n", "\n"));
      } else {
         for (Element el : list) {
            fixCR(el);
         }
      }
   }
}

//=============================================================================

