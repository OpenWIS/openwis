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

package org.fao.geonet.kernel.harvest.harvester.csw;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import jeeves.exceptions.OperationAbortedEx;
import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.CswOperation;
import org.fao.geonet.csw.common.CswServer;
import org.fao.geonet.csw.common.ElementSetName;
import org.fao.geonet.csw.common.requests.CatalogRequest;
import org.fao.geonet.csw.common.requests.GetRecordByIdRequest;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.RecordInfo;
import org.fao.geonet.kernel.harvest.harvester.UUIDMapper;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================

public class Aligner {

   private Logger log;

   private ServiceContext context;

   private Dbms dbms;

   private CswParams params;

   private DataManager dataMan;

   private List<Category> localCateg;

   private UUIDMapper localUuids;

   private CswResult result;

   private GetRecordByIdRequest request;

   /**
    * Default constructor.
    * Builds a Aligner.
    * @param log
    * @param sc
    * @param dbms
    * @param server
    * @param params
    * @throws OperationAbortedEx
    */
   public Aligner(Logger log, ServiceContext sc, Dbms dbms, CswServer server, CswParams params)
         throws OperationAbortedEx {
      this.log = log;
      context = sc;
      this.dbms = dbms;
      this.params = params;

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      dataMan = gc.getDataManager();
      result = new CswResult();

      //--- setup get-record-by-id request

      request = new GetRecordByIdRequest(sc);
      request.setElementSetName(ElementSetName.FULL);

      CswOperation oper = server.getOperation(CswServer.GET_RECORD_BY_ID);

      // Use the preferred HTTP method and check one exist.
      if (oper.getUrl != null && Harvester.PREFERRED_HTTP_METHOD.equals("GET")) {
         request.setUrl(oper.getUrl);
         request.setMethod(CatalogRequest.Method.GET);
      } else if (oper.postUrl != null && Harvester.PREFERRED_HTTP_METHOD.equals("POST")) {
         request.setUrl(oper.postUrl);
         request.setMethod(CatalogRequest.Method.POST);
      } else {
         if (oper.getUrl != null) {
            request.setUrl(oper.getUrl);
            request.setMethod(CatalogRequest.Method.GET);
         } else if (oper.getUrl != null) {
            request.setUrl(oper.postUrl);
            request.setMethod(CatalogRequest.Method.POST);
         } else {
            throw new OperationAbortedEx("No GET or POST DCP available in this service.");
         }
      }

      if (oper.preferredOutputSchema != null) {
         request.setOutputSchema(oper.preferredOutputSchema);
      }

      if (oper.preferredServerVersion != null) {
         request.setServerVersion(oper.preferredServerVersion);
      }

      if (params.useAccount) {
         request.setCredentials(params.username, params.password);
      }

   }

   //--------------------------------------------------------------------------
   //---
   //--- Alignment method
   //---
   //--------------------------------------------------------------------------

   public CswResult align(Set<RecordInfo> records) throws Exception {
      log.info("Start of alignment for : " + params.name);

      //-----------------------------------------------------------------------
      //--- retrieve all local categories
      //--- retrieve harvested uuids for given harvesting node

      CategoryManager cm = new CategoryManager(dbms);
      localCateg = cm.getAllCategories();

      localUuids = new UUIDMapper(dbms, params.uuid);
      dbms.commit();

      //-----------------------------------------------------------------------
      //--- remove old metadata

      for (String uuid : localUuids.getUUIDs())
         if (!exists(records, uuid)) {
            String id = localUuids.getID(uuid);

            log.debug("  - Removing old metadata with local id:" + id);
            dataMan.deleteMetadataById(dbms, id);
            dbms.commit();
            result.locallyRemoved++;
         }

      //-----------------------------------------------------------------------
      //--- insert/update new metadata

      for (RecordInfo ri : records) {
         result.totalMetadata++;

         String id = dataMan.getMetadataId(dbms, ri.uuid);

         if (id == null)
            addMetadata(ri);
         else
            updateMetadata(ri, id);
      }

      log.info("End of alignment for : " + params.name);

      return result;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods : addMetadata
   //---
   //--------------------------------------------------------------------------

   private void addMetadata(RecordInfo ri) throws Exception {
      Element md = retrieveMetadata(ri.uuid);

      if (md == null)
         return;

      String schema = dataMan.autodetectSchema(md);

      if (schema == null) {
         log.debug("  - Metadata skipped due to unknown schema. uuid:" + ri.uuid);
         result.unknownSchema++;

         return;
      }

      log.debug("  - Adding metadata with remote uuid:" + ri.uuid + " schema:" + schema);

      String id = dataMan.insertMetadataExt(dbms, schema, md, params.uuid, ri.changeDate,
            ri.changeDate, ri.uuid, null);
      // FIXME use the insert harvested md method??

      dataMan.setTemplateExt(dbms, ri.uuid, "n", null);
      dataMan.setHarvestedExt(dbms, ri.uuid, params.uuid);

      Category category = getValidCategory(params.getCategories());

      // FIXME OpenWIS if null, no category assigned??
      if (category != null) {
         MetadataManager mm = new MetadataManager(dbms);
         mm.updateCategory(ri.uuid, category.getId());
      }

      dbms.commit();
      dataMan.indexMetadataGroup(dbms, ri.uuid, null);
      result.addedMetadata++;
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

   /**
    * Update metadata.
    * @param ri
    * @param id
    * @throws Exception
    */
   private void updateMetadata(RecordInfo ri, String id) throws Exception {
      String date = localUuids.getChangeDate(ri.uuid);

      if (date == null)
         log.debug("  - Skipped metadata managed by another harvesting node. uuid:" + ri.uuid
               + ", name:" + params.name);
      else {
         if (!ri.isMoreRecentThan(date)) {
            log.debug("  - Metadata XML not changed for uuid:" + ri.uuid);
            result.unchangedMetadata++;
         } else {
            log.debug("  - Updating local metadata for uuid:" + ri.uuid);

            Element md = retrieveMetadata(ri.uuid);

            if (md == null)
               return;

            dataMan.updateMetadataExt(dbms, id, md, ri.changeDate);

            Category category = getValidCategory(params.getCategories());

            // FIXME OpenWIS if null, no category assigned??
            if (category != null) {
               MetadataManager mm = new MetadataManager(dbms);
               mm.updateCategory(ri.uuid, category.getId());
            }

            dbms.commit();
            dataMan.indexMetadataGroup(dbms, ri.uuid, null);
            result.updatedMetadata++;
         }
      }
   }

   /**
    * Return true if the uuid is present in the remote node.
    * @param records
    * @param uuid
    * @return
    */
   private boolean exists(Set<RecordInfo> records, String uuid) {
      for (RecordInfo ri : records)
         if (uuid.equals(ri.uuid))
            return true;

      return false;
   }

   //--------------------------------------------------------------------------

   /**
    * Does CSW GetRecordById request.
    * @param uuid
    * @return
    */
   @SuppressWarnings("unchecked")
   private Element retrieveMetadata(String uuid) {
      request.clearIds();
      request.addId(uuid);

      try {
         log.debug("Getting record from : " + request.getHost() + " (uuid:" + uuid + ")");
         Element response = request.execute();
         log.debug("Record got:\n" + Xml.getString(response));

         List<Element> list = response.getChildren();

         //--- maybe the metadata has been removed

         if (list.size() == 0)
            return null;

         response = list.get(0);

         return (Element) response.detach();
      } catch (Exception e) {
         log.warning("Raised exception while getting record : " + e);
         e.printStackTrace();
         result.unretrievable++;

         //--- we don't raise any exception here. Just try to go on
         return null;
      }
   }
}

//=============================================================================

