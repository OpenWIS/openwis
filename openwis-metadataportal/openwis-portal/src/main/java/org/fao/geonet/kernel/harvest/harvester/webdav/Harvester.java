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
package org.fao.geonet.kernel.harvest.harvester.webdav;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.UriMapper;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================

class Harvester {

   private Logger log;

   private ServiceContext context;

   private Dbms dbms;

   private WebDavParams params;

   private DataManager dataMan;

   private List<Category> localCateg;

   private UriMapper localUris;

   private WebDavResult result;

   /**
    * Default constructor.
    * Builds a Harvester.
    * @param log
    * @param context
    * @param dbms
    * @param params
    */
   public Harvester(Logger log, ServiceContext context, Dbms dbms, WebDavParams params) {
      this.log = log;
      this.context = context;
      this.dbms = dbms;
      this.params = params;

      result = new WebDavResult();

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      dataMan = gc.getDataManager();
   }

   //---------------------------------------------------------------------------
   //---
   //--- API methods
   //---
   //---------------------------------------------------------------------------

   public WebDavResult harvest() throws Exception {
      log.debug("Retrieving remote metadata information for : " + params.name);
      RemoteRetriever rr = new WebDavRetriever();
      rr.init(log, context, params);
      List<RemoteFile> files = rr.retrieve();
      log.debug("Remote files found : " + files.size());
      align(files);
      rr.destroy();
      return result;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Private methods
   //---
   //---------------------------------------------------------------------------

   private void align(List<RemoteFile> files) throws Exception {
      log.info("Start of alignment for : " + params.name);
      //-----------------------------------------------------------------------
      //--- retrieve all local categories
      //--- retrieve harvested uuids for given harvesting node
      CategoryManager cm = new CategoryManager(dbms);
      localCateg = cm.getAllCategories();
      localUris = new UriMapper(dbms, params.uuid);
      dbms.commit();
      //-----------------------------------------------------------------------
      //--- remove old metadata
      for (String uri : localUris.getUris()) {
         if (!exists(files, uri)) {
            String id = localUris.getID(uri);

            log.debug("  - Removing old metadata with local id:" + id);
            dataMan.deleteMetadataById(dbms, id);
            dbms.commit();
            result.locallyRemoved++;
         }
      }
      //-----------------------------------------------------------------------
      //--- insert/update new metadata

      for (RemoteFile rf : files) {
         result.total++;
         String id = localUris.getID(rf.getPath());
         if (id == null) {
            addMetadata(rf);
         } else {
            updateMetadata(rf, id, params.uuid);
         }
      }
      log.info("End of alignment for : " + params.name);
   }

   //--------------------------------------------------------------------------
   /** Returns true if the uri is present in the remote folder */
   private boolean exists(List<RemoteFile> files, String uri) {
      for (RemoteFile rf : files) {
         if (uri.equals(rf.getPath())) {
            return true;
         }
      }
      return false;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods : addMetadata
   //---
   //--------------------------------------------------------------------------
   private void addMetadata(RemoteFile rf) throws Exception {
      Element md = retrieveMetadata(rf);
      if (md == null) {
         return;
      }
      //--- schema handled check already done
      String schema = dataMan.autodetectSchema(md);
      String uuid = UUID.randomUUID().toString();

      log.debug("  - Setting uuid for metadata with remote path : " + rf.getPath());

      //--- set uuid inside metadata and get new xml
      md = dataMan.setUUID(schema, uuid, md);

      log.debug("  - Adding metadata with remote path : " + rf.getPath());

      String id = dataMan.insertMetadataExt(dbms, schema, md, params.uuid, rf.getChangeDate(),
            rf.getChangeDate(), uuid, null);

      int iId = Integer.parseInt(id);

      dataMan.setTemplateExt(dbms, uuid, "n", null);
      dataMan.setHarvestedExt(dbms, uuid, params.uuid, rf.getPath());

      Category category = getValidCategory(params.getCategories());

      // FIXME OpenWIS if null, no category assigned??
      if (category != null) {
         MetadataManager mm = new MetadataManager(dbms);
         mm.updateCategory(uuid, category.getId());
      }

      dbms.commit();
      dataMan.indexMetadataGroup(dbms, uuid, null);
      result.added++;
   }

   //--------------------------------------------------------------------------

   private Element retrieveMetadata(RemoteFile rf) {
      try {
         log.debug("Getting remote file : " + rf.getPath());
         Element md = rf.getMetadata();
         log.debug("Record got:\n" + Xml.getString(md));

         String schema = dataMan.autodetectSchema(md);
         if (schema == null) {
            log.warning("Skipping metadata with unknown schema. Path is : " + rf.getPath());
            result.unknownSchema++;
         } else {
            if (!params.validate || validates(schema, md)) {
               return (Element) md.detach();
            }
            log.warning("Skipping metadata that does not validate. Path is : " + rf.getPath());
            result.doesNotValidate++;
         }
      } catch (JDOMException e) {
         log.warning("Skipping metadata with bad XML format. Path is : " + rf.getPath());
         result.badFormat++;
      } catch (Exception e) {
         log.warning("Raised exception while getting metadata file : " + e);
         result.unretrievable++;
      }
      //--- we don't raise any exception here. Just try to go on
      return null;
   }

   //--------------------------------------------------------------------------

   private boolean validates(String schema, Element md) {
      try {
         dataMan.validate(schema, md);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods : updateMetadata
   //---
   //--------------------------------------------------------------------------

   private void updateMetadata(RemoteFile rf, String id, String uuid) throws Exception {
      String date = localUris.getChangeDate(rf.getPath());
      if (!rf.isMoreRecentThan(date)) {
         log.debug("  - Metadata XML not changed for path : " + rf.getPath());
         result.unchanged++;
      } else {
         log.debug("  - Updating local metadata for path : " + rf.getPath());
         Element md = retrieveMetadata(rf);
         if (md == null) {
            return;
         }
         dataMan.updateMetadataExt(dbms, id, md, rf.getChangeDate());

         //--- the administrator could change categories using the
         //--- web interface so we have to re-set
         Category category = getValidCategory(params.getCategories());

         // FIXME OpenWIS if null, no category assigned??
         if (category != null) {
            MetadataManager mm = new MetadataManager(dbms);
            mm.updateCategory(uuid, category.getId());
         }
         dbms.commit();
         dataMan.indexMetadataGroup(dbms, uuid, null);
         result.updated++;
      }
   }

   /**
   * Get valid category
   * @param children
   * @return
   */
   private Category getValidCategory(final Collection<String> categIds) {
      return (Category) CollectionUtils.find(localCateg, new Predicate() {

         @Override
         public boolean evaluate(Object object) {

            return categIds.contains(((Category) object).getId());
         }
      });
   }

}

//=============================================================================

interface RemoteRetriever {
   public void init(Logger log, ServiceContext context, WebDavParams params);

   public List<RemoteFile> retrieve() throws Exception;

   public void destroy();
}

//=============================================================================

interface RemoteFile {
   public String getPath();

   public String getChangeDate();

   public Element getMetadata() throws Exception;

   public boolean isMoreRecentThan(String localDate);
}

//=============================================================================
