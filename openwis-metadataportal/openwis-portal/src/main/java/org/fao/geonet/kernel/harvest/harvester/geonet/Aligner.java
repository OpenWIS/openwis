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

package org.fao.geonet.kernel.harvest.harvester.geonet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import jeeves.utils.XmlRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.RecordInfo;
import org.fao.geonet.kernel.harvest.harvester.UUIDMapper;
import org.fao.geonet.kernel.mef.IMEFVisitor;
import org.fao.geonet.kernel.mef.MEFLib;
import org.fao.geonet.kernel.mef.MEFVisitor;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================

public class Aligner {

   private Logger log;

   private ServiceContext context;

   private Dbms dbms;

   private XmlRequest request;

   private GeonetParams params;

   private DataManager dataMan;

   private GeonetResult result;

   private List<Category> localCateg;

   private UUIDMapper localUuids;

   private HashMap<String, HashMap<String, String>> hmRemoteGroups = new HashMap<String, HashMap<String, String>>();

   /**
    * Default constructor.
    * Builds a Aligner.
    * @param log
    * @param context
    * @param dbms
    * @param req
    * @param params
    * @param remoteInfo
    */
   public Aligner(Logger log, ServiceContext context, Dbms dbms, XmlRequest req,
         GeonetParams params, Element remoteInfo) {
      this.log = log;
      this.context = context;
      this.dbms = dbms;
      request = req;
      this.params = params;

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      dataMan = gc.getDataManager();
      result = new GeonetResult();

      //--- save remote categories and groups into hashmaps for a fast access

      List list = remoteInfo.getChild("groups").getChildren("group");
      setupLocEntity(list, hmRemoteGroups);
   }

   //--------------------------------------------------------------------------

   private void setupLocEntity(List list, HashMap<String, HashMap<String, String>> hmEntity) {

      for (Object aList : list) {
         Element entity = (Element) aList;
         String name = entity.getChildText("name");

         HashMap<String, String> hm = new HashMap<String, String>();
         hmEntity.put(name, hm);

         List labels = entity.getChild("label").getChildren();

         for (Object label : labels) {
            Element el = (Element) label;
            hm.put(el.getName(), el.getText());
         }
      }
   }

   //--------------------------------------------------------------------------
   //---
   //--- Alignment method
   //---
   //--------------------------------------------------------------------------

   public GeonetResult align(Set<RecordInfo> records) throws Exception {
      log.info("Start of alignment for : " + params.name);

      //-----------------------------------------------------------------------
      //--- retrieve all local categories and groups
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

            log.debug("  - Removing old metadata with id:" + id);
            dataMan.deleteMetadataById(dbms, id);
            dbms.commit();
            result.locallyRemoved++;
         }

      //-----------------------------------------------------------------------
      //--- insert/update new metadata

      for (RecordInfo ri : records) {
         result.totalMetadata++;

         if (!dataMan.existsSchema(ri.schema)) {
            log.debug("  - Metadata skipped due to unknown schema. uuid:" + ri.uuid + ", schema:"
                  + ri.schema);
            result.unknownSchema++;
         } else {
            String id = dataMan.getMetadataId(dbms, ri.uuid);

            // look up value of localrating/enabled
            GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
            SettingManager settingManager = gc.getSettingManager();
            boolean localRating = settingManager
                  .getValueAsBool("system/localrating/enabled", false);

            if (id == null) {
               addMetadata(ri, localRating);
            } else {
               updateMetadata(ri, id, localRating);
            }
         }
      }

      log.info("End of alignment for : " + params.name);

      return result;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods : addMetadata
   //---
   //--------------------------------------------------------------------------

   private void addMetadata(final RecordInfo ri, final boolean localRating) throws Exception {
      final String id[] = {null};
      final Element md[] = {null};

      //--- import metadata from MEF file

      File mefFile = retrieveMEF(ri.uuid);

      try {
         MEFLib.visit(mefFile, new MEFVisitor(), new IMEFVisitor() {
            @Override
            public void handleMetadata(Element mdata, int index) throws Exception {
               md[index] = mdata;
            }

            //--------------------------------------------------------------------

            @Override
            public void handleMetadataFiles(File[] files, int index) throws Exception {
            }

            //--------------------------------------------------------------------

            @Override
            public void handleInfo(Element info, int index) throws Exception {
               id[index] = addMetadata(ri, md[index], info, localRating);
            }

            //--------------------------------------------------------------------

            @Override
            public void handlePublicFile(String file, String changeDate, InputStream is, int index)
                  throws IOException {
               log.debug("    - Adding remote public file with name:" + file);
               String pubDir = Lib.resource.getDir(context, "public", id[index]);

               File outFile = new File(pubDir, file);
               FileOutputStream os = new FileOutputStream(outFile);
               BinaryFile.copy(is, os, false, true);
               outFile.setLastModified(new ISODate(changeDate).getSeconds() * 1000);
            }

            //--------------------------------------------------------------------

            public void handlePrivateFile() {
            }

            @Override
            public void handleFeatureCat(Element md, int index) throws Exception {
               // Feature Catalog not managed for harvesting
            }

            @Override
            public void handlePrivateFile(String file, String changeDate, InputStream is, int index)
                  throws IOException {
            }
         });
      } catch (Exception e) {
         //--- we ignore the exception here. Maybe the metadata has been removed just now
         log.debug("  - Skipped unretrievable metadata (maybe has been removed) with uuid:"
               + ri.uuid);
         result.unretrievable++;
         e.printStackTrace();
      } finally {
         mefFile.delete();
      }
   }

   //--------------------------------------------------------------------------

   private String addMetadata(RecordInfo ri, Element md, Element info, boolean localRating)
         throws Exception {
      Element general = info.getChild("general");

      String createDate = general.getChildText("createDate");
      String changeDate = general.getChildText("changeDate");
      String isTemplate = general.getChildText("isTemplate");
      String siteId = general.getChildText("siteId");
      String popularity = general.getChildText("popularity");

      if ("true".equals(isTemplate))
         isTemplate = "y";
      else
         isTemplate = "n";

      log.debug("  - Adding metadata with remote uuid:" + ri.uuid);

      String id = dataMan.insertMetadataExt(dbms, ri.schema, md, siteId, createDate, changeDate,
            ri.uuid, null);

      int iId = Integer.parseInt(id);

      dataMan.setTemplateExt(dbms, ri.uuid, isTemplate, null);
      dataMan.setHarvestedExt(dbms, ri.uuid, params.uuid);

      if (!localRating) {
         String rating = general.getChildText("rating");
         if (rating != null)
            dbms.execute("UPDATE Metadata SET rating=? WHERE id=?", new Integer(rating), iId);
      }

      if (popularity != null)
         dbms.execute("UPDATE Metadata SET popularity=? WHERE id=?", new Integer(popularity), iId);

      String pubDir = Lib.resource.getDir(context, "public", id);
      String priDir = Lib.resource.getDir(context, "private", id);

      new File(pubDir).mkdirs();
      new File(priDir).mkdirs();

      Category category = getValidCategory(params.getCategories());

      // FIXME OpenWIS if null, no category assigned??
      if (category != null) {
         MetadataManager mm = new MetadataManager(dbms);
         mm.updateCategory(ri.uuid, category.getId());
      }

      dbms.commit();
      dataMan.indexMetadataGroup(dbms, ri.uuid, null);
      result.addedMetadata++;

      return id;
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
    * Update Metadata.
    * @param ri
    * @param id
    * @param localRating
    * @throws Exception
    */
   private void updateMetadata(final RecordInfo ri, final String id, final boolean localRating)
         throws Exception {
      final Element md[] = {null};
      final Element publicFiles[] = {null};

      if (localUuids.getID(ri.uuid) == null)
         log.debug("  - Skipped metadata managed by another harvesting node. uuid:" + ri.uuid
               + ", name:" + params.name);
      else {
         File mefFile = retrieveMEF(ri.uuid);

         try {
            MEFLib.visit(mefFile, new MEFVisitor(), new IMEFVisitor() {
               @Override
               public void handleMetadata(Element mdata, int index) throws Exception {
                  md[index] = mdata;
               }

               //-----------------------------------------------------------------

               @Override
               public void handleMetadataFiles(File[] files, int index) throws Exception {
                  //md[index] = mdata;
               }

               @Override
               public void handleInfo(Element info, int index) throws Exception {
                  updateMetadata(ri, id, md[index], info, localRating);
                  publicFiles[index] = info.getChild("public");
               }

               //-----------------------------------------------------------------

               @Override
               public void handlePublicFile(String file, String changeDate, InputStream is,
                     int index) throws IOException {
                  updateFile(id, file, changeDate, is, publicFiles[index]);
               }

               //-----------------------------------------------------------------

               public void handlePrivateFile() {
               }

               @Override
               public void handleFeatureCat(Element md, int index) throws Exception {
                  // Feature Catalog not managed for harvesting
               }

               @Override
               public void handlePrivateFile(String file, String changeDate, InputStream is,
                     int index) throws IOException {
               }

            });
         } catch (Exception e) {
            //--- we ignore the exception here. Maybe the metadata has been removed just now
            result.unretrievable++;
         } finally {
            mefFile.delete();
         }
      }
   }

   //--------------------------------------------------------------------------

   private void updateMetadata(RecordInfo ri, String id, Element md, Element info,
         boolean localRating) throws Exception {
      String date = localUuids.getChangeDate(ri.uuid);

      if (!ri.isMoreRecentThan(date)) {
         log.debug("  - XML not changed for local metadata with uuid:" + ri.uuid);
         result.unchangedMetadata++;
      } else {
         log.debug("  - Updating local metadata with id=" + id);
         dataMan.updateMetadataExt(dbms, id, md, ri.changeDate);
         result.updatedMetadata++;
      }

      Element general = info.getChild("general");

      String popularity = general.getChildText("popularity");

      if (!localRating) {
         String rating = general.getChildText("rating");
         if (rating != null)
            dbms.execute("UPDATE Metadata SET rating=? WHERE id=?", new Integer(rating),
                  new Integer(id));
      }

      if (popularity != null)
         dbms.execute("UPDATE Metadata SET popularity=? WHERE id=?", new Integer(popularity),
               new Integer(id));

      Category category = getValidCategory(params.getCategories());

      // FIXME OpenWIS if null, no category assigned??
      if (category != null) {
         MetadataManager mm = new MetadataManager(dbms);
         mm.updateCategory(ri.uuid, category.getId());
      }

      dbms.commit();
      dataMan.indexMetadataGroup(dbms, ri.uuid, null);
   }

   //--------------------------------------------------------------------------
   //--- Public file update methods
   //--------------------------------------------------------------------------

   private void updateFile(String id, String file, String changeDate, InputStream is, Element files)
         throws IOException {
      if (files == null)
         log.debug("  - No 'public' element in info.xml. Cannot update public file :" + file);
      else {
         removeOldFile(id, files);
         updateChangedFile(id, file, changeDate, is);
      }
   }

   //--------------------------------------------------------------------------

   private void removeOldFile(String id, Element infoFiles) {
      File pubDir = new File(Lib.resource.getDir(context, "public", id));

      File files[] = pubDir.listFiles();

      if (files == null)
         log.error("  - Cannot scan directory for public files : " + pubDir.getAbsolutePath());

      else
         for (File file : files)
            if (!existsFile(file.getName(), infoFiles)) {
               log.debug("  - Removing old public file with name=" + file.getName());
               file.delete();
            }
   }

   //--------------------------------------------------------------------------

   private boolean existsFile(String fileName, Element files) {
      List list = files.getChildren("file");

      for (Object aList : list) {
         Element elem = (Element) aList;
         String name = elem.getAttributeValue("name");

         if (fileName.equals(name)) {
            return true;
         }
      }

      return false;
   }

   //--------------------------------------------------------------------------

   private void updateChangedFile(String id, String file, String changeDate, InputStream is)
         throws IOException {
      String pubDir = Lib.resource.getDir(context, "public", id);
      File locFile = new File(pubDir, file);

      ISODate locIsoDate = new ISODate(locFile.lastModified());
      ISODate remIsoDate = new ISODate(changeDate);

      if (!locFile.exists() || remIsoDate.sub(locIsoDate) > 0) {
         log.debug("  - Adding remote public file with name:" + file);

         FileOutputStream os = new FileOutputStream(locFile);
         BinaryFile.copy(is, os, false, true);
         locFile.setLastModified(remIsoDate.getSeconds() * 1000);
      } else {
         log.debug("  - Nothing to do to public file with name:" + file);
      }
   }

   //--------------------------------------------------------------------------
   //---
   //--- Private methods
   //---
   //--------------------------------------------------------------------------

   /** Return true if the uuid is present in the remote node */

   private boolean exists(Set<RecordInfo> records, String uuid) {
      for (RecordInfo ri : records)
         if (uuid.equals(ri.uuid))
            return true;

      return false;
   }

   //--------------------------------------------------------------------------

   private File retrieveMEF(String uuid) throws IOException {
      request.clearParams();
      request.addParam("uuid", uuid);
      request.addParam("format", "partial");

      request.setAddress("/" + params.servlet + "/srv/en/" + Geonet.Service.MEF_EXPORT);

      File tempFile = File.createTempFile("temp-", ".dat");
      request.executeLarge(tempFile);

      return tempFile;
   }

}

//=============================================================================

