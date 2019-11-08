//=============================================================================
//===	Copyright (C) 2001-2009 Food and Agriculture Organization of the
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
package org.fao.geonet.kernel.harvest.harvester.arcsde;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jeeves.exceptions.BadInputEx;
import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.server.resources.ResourceManager;
import jeeves.utils.Xml;

import org.fao.geonet.arcgis.ArcSDEMetadataAdapter;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.harvest.harvester.AbstractHarvester;
import org.fao.geonet.kernel.harvest.harvester.AbstractParams;
import org.fao.geonet.kernel.harvest.harvester.GroupMapper;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.model.category.Category;

//import com.esri.sde.sdk.GeoToolsDummyAPI;
/**
 *
 * Harvester from ArcSDE. Requires the propietary ESRI libraries containing their API. Since those are not
 * committed to our svn, you'll need to replace the dummy library arcsde-dummy.jar with the real ones for this
 * to work.
 *
 * @author heikki doeleman
 *
 */
public class ArcSDEHarvester extends AbstractHarvester {

   private ArcSDEParams params;

   private ArcSDEResult result;

   private static final String ARC_TO_ISO19115_TRANSFORMER = "ArcCatalog8_to_ISO19115.xsl";

   private static final String ISO19115_TO_ISO19139_TRANSFORMER = "ISO19115-to-ISO19139.xsl";

   private static String ARC_TO_ISO19115_TRANSFORMER_LOCATION;

   private static String ISO19115_TO_ISO19139_TRANSFORMER_LOCATION;

   public static void init(ServiceContext context) throws Exception {
      ARC_TO_ISO19115_TRANSFORMER_LOCATION = context.getAppPath() + Geonet.Path.STYLESHEETS
            + "/conversion/import/" + ARC_TO_ISO19115_TRANSFORMER;
      ISO19115_TO_ISO19139_TRANSFORMER_LOCATION = context.getAppPath() + Geonet.Path.STYLESHEETS
            + "/conversion/import/" + ISO19115_TO_ISO19139_TRANSFORMER;
   }

   @Override
   protected void storeNodeExtra(Dbms dbms, AbstractParams params, String path, String siteId,
         String optionsId) throws SQLException {
      ArcSDEParams as = (ArcSDEParams) params;
      settingMan.add(dbms, "id:" + siteId, "icon", as.icon);
      settingMan.add(dbms, "id:" + siteId, "server", as.server);
      settingMan.add(dbms, "id:" + siteId, "port", as.port);
      settingMan.add(dbms, "id:" + siteId, "username", as.username);
      settingMan.add(dbms, "id:" + siteId, "password", as.password);
      settingMan.add(dbms, "id:" + siteId, "database", as.database);
   }

   @Override
   protected String doAdd(Dbms dbms, Element node) throws BadInputEx, SQLException {
      /*	try {
      		@SuppressWarnings("unused")
      		int test = GeoToolsDummyAPI.DUMMY_API_VERSION;
      		// if you get here, you're using the dummy API
      		System.out.println("ERROR: NO ARCSDE LIBRARIES INSTALLED");
      		System.out.println("Replace arcsde-dummy.jar with the real ArcSDE libraries from ESRI");
      		System.err.println("ERROR: NO ARCSDE LIBRARIES INSTALLED");
      		System.err.println("Replace arcsde-dummy.jar with the real ArcSDE libraries from ESRI");
      		return null;
      	}
      	catch(NoClassDefFoundError n) {
      */// using the real ESRI ArcSDE libraries : continue
      params = new ArcSDEParams(dataMan);

      //--- retrieve/initialize information
      params.create(node);

      //--- force the creation of a new uuid
      params.uuid = UUID.randomUUID().toString();

      String id = settingMan.add(dbms, "harvesting", "node", getType());
      storeNode(dbms, params, "id:" + id);

      Lib.sources.update(dbms, params.uuid, params.name, true);
      Lib.sources.copyLogo(context, "/images/harvesting/" + params.icon, params.uuid);

      return id;
      //	}
   }

   @Override
   protected void doAddInfo(Element node) {
      //--- if the harvesting is not started yet, we don't have any info

      if (result == null)
         return;

      //--- ok, add proper info

      Element info = node.getChild("info");
      Element res = new Element("result");
      add(res, "total", result.total);
      add(res, "added", result.added);
      add(res, "updated", result.updated);
      add(res, "unchanged", result.unchanged);
      add(res, "unknownSchema", result.unknownSchema);
      add(res, "removed", result.removed);
      add(res, "unretrievable", result.unretrievable);
      add(res, "badFormat", result.badFormat);
      add(res, "doesNotValidate", result.doesNotValidate);

      info.addContent(res);
   }

   @Override
   protected void doDestroy(Dbms dbms) throws SQLException {
      File icon = new File(context.getAppPath() + "images/logos", params.uuid + ".gif");
      icon.delete();
      Lib.sources.delete(dbms, params.uuid);
   }

   @Override
   protected void doHarvest(Logger l, ResourceManager rm) throws Exception {
      System.out.println("ArcSDE harvest starting");
      ArcSDEMetadataAdapter adapter = new ArcSDEMetadataAdapter(params.server, params.port,
            params.database, params.username, params.password);
      List<String> metadataList = adapter.retrieveMetadata();
      align(metadataList, rm);
      System.out.println("ArcSDE harvest finished");
   }

   private void align(List<String> metadataList, ResourceManager rm) throws Exception {
      System.out.println("Start of alignment for : " + params.name);
      ArcSDEResult result = new ArcSDEResult();
      Dbms dbms = (Dbms) rm.open(Geonet.Res.MAIN_DB);
      //----------------------------------------------------------------
      //--- retrieve all local categories and groups
      //--- retrieve harvested uuids for given harvesting node
      CategoryManager cm = new CategoryManager(dbms);
      List<Category> localCateg = cm.getAllCategories();

      GroupMapper localGroups = new GroupMapper(dbms);
      dbms.commit();
      List<String> idsForHarvestingResult = new ArrayList<String>();
      //-----------------------------------------------------------------------
      //--- insert/update metadata
      for (String metadata : metadataList) {
         result.total++;
         // create JDOM element from String-XML
         Element metadataElement = Xml.loadString(metadata, false);
         // transform ESRI output to ISO19115
         Element iso19115 = Xml.transform(metadataElement, ARC_TO_ISO19115_TRANSFORMER_LOCATION);
         // transform ISO19115 to ISO19139
         Element iso19139 = Xml.transform(iso19115, ISO19115_TO_ISO19139_TRANSFORMER_LOCATION);

         String schema = dataMan.autodetectSchema(iso19139);
         if (schema == null) {
            result.unknownSchema++;
         }
         // the xml is recognizable iso19139 format
         else {
            String uuid = dataMan.extractUUID(schema, iso19139);
            if (uuid == null || uuid.equals("")) {
               System.out
                     .println("Skipping metadata due to failure extracting uuid (uuid null or empty).");
               result.badFormat++;
            } else {
               //
               // add / update the metadata from this harvesting result
               //
               String id = dataMan.getMetadataId(dbms, uuid);
               if (id == null) {
                  System.out.println("adding new metadata");
                  id = addMetadata(iso19139, uuid, dbms, schema, localGroups, localCateg);
                  result.added++;
               } else {
                  System.out.println("updating existing metadata, id is: " + id);
                  updateMetadata(iso19139, id, uuid, dbms, localGroups, localCateg);
                  result.updated++;
               }
               idsForHarvestingResult.add(id);
            }
         }
      }
      //
      // delete locally existing metadata from the same source if they were
      // not in this harvesting result
      //
      List<Element> existingMetadata = dataMan.getMetadataByHarvestingSource(dbms, params.uuid);
      for (Element existingId : existingMetadata) {
         String ex$ = existingId.getChildText("id");
         if (!idsForHarvestingResult.contains(ex$)) {
            dataMan.deleteMetadataById(dbms, ex$);
            result.removed++;
         }
      }
   }

   private void updateMetadata(Element xml, String id, String uuid, Dbms dbms,
         GroupMapper localGroups, List<Category> localCateg) throws Exception {
      System.out.println("  - Updating metadata with id: " + id);

      dataMan.updateMetadataExt(dbms, id, xml, new ISODate().toString());

      Category category = getValidCategory(params.getCategories(), localCateg);

      // FIXME OpenWIS if null, no category assigned??
      if (category != null) {
         MetadataManager mm = new MetadataManager(dbms);
         mm.updateCategory(uuid, category.getId());
      }

      dbms.commit();
      dataMan.indexMetadataGroup(dbms, uuid, null);
   }

   /**
    * Inserts a metadata into the database. The index is updated after insertion.
    * @param xml
    * @param uuid
    * @param dbms
    * @param schema
    * @param localGroups
    * @param localCateg
    * @throws Exception
    */
   private String addMetadata(Element xml, String uuid, Dbms dbms, String schema,
         GroupMapper localGroups, List<Category> localCateg) throws Exception {
      System.out.println("  - Adding metadata with remote uuid: " + uuid);

      String source = params.uuid;
      String createDate = new ISODate().toString();
      String id = dataMan.insertMetadataExt(dbms, schema, xml, source, createDate, createDate,
            uuid, null);

      int iId = Integer.parseInt(id);
      dataMan.setTemplateExt(dbms, uuid, "n", null);
      dataMan.setHarvestedExt(dbms, uuid, source);

      Category category = getValidCategory(params.getCategories(), localCateg);

      // FIXME OpenWIS if null, no category assigned??
      if (category != null) {
         MetadataManager mm = new MetadataManager(dbms);
         mm.updateCategory(uuid, category.getId());
      }

      dbms.commit();
      dataMan.indexMetadataGroup(dbms, uuid, null);
      return id;
   }

   @Override
   protected void doInit(Element entry) throws BadInputEx {
      params = new ArcSDEParams(dataMan);
      params.create(entry);
   }

   @Override
   protected void doUpdate(Dbms dbms, String id, Element node) throws BadInputEx, SQLException {
      ArcSDEParams copy = params.copy();

      //--- update variables
      copy.update(node);

      String path = "harvesting/id:" + id;

      settingMan.removeChildren(dbms, path);

      //--- update database
      storeNode(dbms, copy, path);

      //--- we update a copy first because if there is an exception ArcSDEParams
      //--- could be half updated and so it could be in an inconsistent state

      Lib.sources.update(dbms, copy.uuid, copy.name, true);
      Lib.sources.copyLogo(context, "/images/harvesting/" + copy.icon, copy.uuid);

      params = copy;
   }

   @Override
   public AbstractParams getParams() {
      return params;
   }

   @Override
   public String getType() {
      return "arcsde";
   }

   class ArcSDEResult {
      public int total;

      public int added;

      public int updated;

      public int unchanged;

      public int removed;

      public int unknownSchema;

      public int unretrievable;

      public int badFormat;

      public int doesNotValidate;
   }
}
