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

package org.fao.geonet.kernel.harvest.harvester.geonet20;

import java.util.Collection;
import java.util.List;

import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.XmlRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.UUIDMapper;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================

public class Aligner {

   private Dbms dbms;

   private Logger log;

   private XmlRequest req;

   private GeonetParams params;

   private DataManager dataMan;

   private ServiceContext context;

   private List<Category> localCateg;

   private UUIDMapper localUuids;

   private AlignerResult result;

   /**
    * Default constructor.
    * Builds a Aligner.
    * @param log
    * @param req
    * @param params
    * @param dm
    * @param dbms
    * @param sc
    */
   public Aligner(Logger log, XmlRequest req, GeonetParams params, DataManager dm, Dbms dbms,
         ServiceContext sc) {
      this.log = log;
      this.req = req;
      this.params = params;
      dataMan = dm;
      this.dbms = dbms;
      context = sc;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Alignment method
   //---
   //--------------------------------------------------------------------------

   public AlignerResult align(Element result, String siteId) throws Exception {
      log.info("Start of alignment for site-id=" + siteId);

      this.result = new AlignerResult();
      this.result.siteId = siteId;

      List mdList = result.getChildren("metadata");

      //-----------------------------------------------------------------------
      //--- retrieve local uuids for given site-id

      localUuids = new UUIDMapper(dbms, siteId);

      // retieve local categories
      CategoryManager cm = new CategoryManager(dbms);
      localCateg = cm.getAllCategories();

      //-----------------------------------------------------------------------
      //--- remove old metadata

      for (String uuid : localUuids.getUUIDs())
         if (!exists(mdList, uuid)) {
            String id = localUuids.getID(uuid);

            log.debug("  - Removing old metadata with id=" + id);
            dataMan.deleteMetadataById(dbms, id);
            dbms.commit();
            this.result.locallyRemoved++;
         }

      //-----------------------------------------------------------------------
      //--- insert/update new metadata

      dataMan.startIndexGroup();
      try {
         for (Object aMdList : mdList) {
            Element info = ((Element) aMdList).getChild("info", Edit.NAMESPACE);

            String remoteUuid = info.getChildText("uuid");
            String schema = info.getChildText("schema");
            String changeDate = info.getChildText("changeDate");

            this.result.totalMetadata++;

            log.debug("Obtained remote URN= " + remoteUuid + ", changeDate=" + changeDate);

            if (!dataMan.existsSchema(schema)) {
               log.debug("  - Skipping unsupported schema : " + schema);
               this.result.schemaSkipped++;
            } else {
               String id = dataMan.getMetadataId(dbms, remoteUuid);

               if (id == null) {
                  id = addMetadata(info);
               } else {
                  updateMetadata(siteId, info, id);
               }

               dbms.commit();

               //--- maybe the metadata was unretrievable

               if (id != null) {
                  dataMan.indexMetadataGroup(dbms, remoteUuid, null);
               }
            }
         }
      } finally {
         dataMan.endIndexGroup();
      }

      log.info("End of alignment for site-id=" + siteId);

      return this.result;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods : addMetadata
   //---
   //--------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   private String addMetadata(Element info) throws Exception {
      String remoteId = info.getChildText("id");
      String remoteUuid = info.getChildText("uuid");
      String schema = info.getChildText("schema");
      String createDate = info.getChildText("createDate");
      String changeDate = info.getChildText("changeDate");

      log.debug("  - Adding metadata with remote id=" + remoteId);

      Element md = getRemoteMetadata(req, remoteId);

      if (md == null) {
         log.warning("  - Cannot get metadata (possibly bad XML) with remote id=" + remoteId);
         return null;
      }

      String id = dataMan.insertMetadataExt(dbms, schema, md, params.uuid, createDate, changeDate,
            remoteUuid, null);

      int iId = Integer.parseInt(id);

      dataMan.setTemplate(dbms, remoteUuid, "n", null);
      dataMan.setHarvested(dbms, remoteUuid, params.uuid);

      result.addedMetadata++;

      Collection<String> catNames = CollectionUtils.collect(info.getChildren("category"),
            new Transformer() {

               @Override
               public Object transform(Object input) {
                  return ((Element) input).getText();
               }
            });

      Category category = getValidCategory(catNames);

      // FIXME OpenWIS if null, no category assigned??
      if (category != null) {
         MetadataManager mm = new MetadataManager(dbms);
         mm.updateCategory(remoteUuid, category.getId());
      }

      return id;
   }

   /**
    * Get valid category
    * @param children
    * @return
    */
   private Category getValidCategory(final Collection<String> categNames) {
      return (Category) CollectionUtils.find(localCateg, new Predicate() {

         @Override
         public boolean evaluate(Object object) {

            return categNames.contains(((Category) object).getName());
         }
      });
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods : updateMetadata
   //---
   //--------------------------------------------------------------------------

   private void updateMetadata(String siteId, Element info, String id) throws Exception {
      String remoteId = info.getChildText("id");
      String remoteUuid = info.getChildText("uuid");
      String changeDate = info.getChildText("changeDate");

      if (localUuids.getID(remoteUuid) == null) {
         log.error("  - Warning! The remote uuid '" + remoteUuid + "' does not belong to site '"
               + siteId + "'");
         log.error("     - The site id of this metadata has been changed.");
         log.error("     - The metadata update will be skipped.");

         result.uuidSkipped++;
      } else {
         updateMetadata(id, remoteId, remoteUuid, changeDate);
         updateCategories(id, info);
      }
   }

   //--------------------------------------------------------------------------

   private void updateMetadata(String id, String remoteId, String remoteUuid, String changeDate)
         throws Exception {
      String date = localUuids.getChangeDate(remoteUuid);

      if (!updateCondition(date, changeDate)) {
         log.debug("  - XML not changed to local metadata with id=" + id);
         result.unchangedMetadata++;
      } else {
         log.debug("  - Updating local metadata with id=" + id);

         Element md = getRemoteMetadata(req, remoteId);

         if (md == null)
            log.warning("  - Cannot get metadata (possibly bad XML) with remote id=" + remoteId);
         else {
            dataMan.updateMetadataExt(dbms, id, md, changeDate);
            result.updatedMetadata++;
         }
      }
   }

   //--------------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   private void updateCategories(String id, Element info) throws Exception {

      // Get harvested categories
      List<Element> catList = info.getChildren("category");

      // Get the category manager
      CategoryManager cm = new CategoryManager(dbms);
      Category oldCategory = cm.getCategoryByMetadataUrn(info.getChildText("uuid"));

      Collection<String> catNames = CollectionUtils.collect(catList, new Transformer() {

         @Override
         public Object transform(Object input) {
            return ((Element) input).getAttributeValue("name");
         }
      });

      if (!catNames.contains(oldCategory.getName())) {
         // FIXME OpenWIS raise an alarm.
         log.debug(" Category unchanged...");
      }
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods
   //---
   //--------------------------------------------------------------------------

   private Element getRemoteMetadata(XmlRequest req, String id) throws Exception {
      req.setAddress("/" + params.servlet + "/srv/en/" + Geonet.Service.XML_METADATA_GET);
      req.clearParams();
      req.addParam("id", id);

      try {
         Element md = req.execute();
         Element info = md.getChild("info", Edit.NAMESPACE);

         if (info != null)
            info.detach();

         return md;
      } catch (Exception e) {
         log.warning("Cannot retrieve remote metadata with id:" + id);
         log.warning(" (C) Error is : " + e.getMessage());

         return null;
      }
   }

   //--------------------------------------------------------------------------
   /** Return true if the sourceId is present in the remote site */

   private boolean exists(List mdList, String uuid) {
      for (Object aMdList : mdList) {
         Element elInfo = ((Element) aMdList).getChild("info", Edit.NAMESPACE);

         if (uuid.equals(elInfo.getChildText("uuid"))) {
            return true;
         }
      }

      return false;
   }

   //--------------------------------------------------------------------------

   private boolean updateCondition(String localDate, String remoteDate) {
      ISODate local = new ISODate(localDate);
      ISODate remote = new ISODate(remoteDate);

      //--- accept if remote date is greater than local date

      return (remote.sub(local) > 0);
   }

}

//=============================================================================

class AlignerResult {
   public String siteId;

   public int totalMetadata;

   public int addedMetadata;

   public int updatedMetadata;

   public int unchangedMetadata;

   public int locallyRemoved;

   public int schemaSkipped;

   public int uuidSkipped;
}

//=============================================================================

