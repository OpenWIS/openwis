//=============================================================================
//===	Copyright (C) 2009 Swistopo
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

package org.fao.geonet.kernel.harvest.harvester.z3950;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.UUIDMapper;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.ISearchManager.Searcher;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.MetadataManager;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================

class Harvester {

   private UUIDMapper localUuids;

   private final DataManager dataMan;

   private final ISearchManager searchMan;

   private final Logger log;

   private final Dbms dbms;

   private final Z3950Params params;

   private ServiceContext context;

   private List<Category> localCateg;

   /**
    * Default constructor.
    * Builds a Harvester.
    * @param log
    * @param context
    * @param dbms
    * @param params
    */
   public Harvester(Logger log, ServiceContext context, Dbms dbms, Z3950Params params) {
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      this.context = context;
      this.log = log;
      searchMan = gc.getSearchmanager();
      dataMan = gc.getDataManager();
      this.context = context;
      this.dbms = dbms;
      this.params = params;
   }

   // ---------------------------------------------------------------------------
   // ---
   // --- API methods
   // ---
   // ---------------------------------------------------------------------------

   public Z3950Result harvest() throws Exception {

      log.info("Retrieving remote metadata information:" + params.uuid);

      Z3950Result result = new Z3950Result();

      // --- Clean all before harvest : Remove/Add mechanism
      localUuids = new UUIDMapper(dbms, params.uuid);

      // --- remove old metadata
      for (String uuid : localUuids.getUUIDs()) {
         log.debug("  - Removing old metadata before update with uuid: " + uuid);
         dataMan.deleteMetadataByURN(dbms, uuid);
         result.locallyRemoved++;
      }

      if (result.locallyRemoved > 0)
         dbms.commit();

      // --- Search remote node
      MetaSearcher s = searchMan.newSearcher(Searcher.Z3950);

      ServiceConfig config = new ServiceConfig();

      Element request = new Element("request");

      // --- Z39.50 servers from harvest params
      for (String id : params.getRepositories()) {
         request.addContent(new Element(Geonet.SearchResult.SERVERS).setText(id));
      }

      // --- Z39.50 query from harvest params
      request.addContent(new Element(Geonet.SearchResult.ZQUERY).setText(params.query));

      // --- don't want any html presentations here
      request.addContent(new Element(Geonet.SearchResult.SERVERHTML).setText("off"));

      // --- do the search
      s.search(context, request, config);

      if (s.getSize() == 0) {
         throw new Exception("Search failed or returned 0 results");
      }

      // -- process the hits in groups of 100
      int numberOfHits = Math.min(Integer.parseInt(params.maximumHits), s.getSize());
      int groupSize = 100;

      // --- return only maximum hits as directed by the harvest params
      int nrGroups = (numberOfHits / groupSize) + 1;
      for (int i = 1; i < nrGroups; i++) {
         int lower = ((i - 1) * groupSize) + 1;
         int upper = (i * groupSize);
         request.addContent(new Element("from").setText("" + lower));
         request.addContent(new Element("to").setText("" + upper));

         // --- Loading results
         Element results = s.present(context, request, config);

         // --- Loading categories
         CategoryManager cm = new CategoryManager(dbms);
         localCateg = cm.getAllCategories();

         // --- Store records
         List<Element> list = results.getChildren();

         log.debug("There are " + list.size() + " children in the results");

         boolean transformIt = false;
         String thisXslt = context.getAppPath() + Geonet.Path.IMPORT_STYLESHEETS + "/";
         if (!params.importXslt.equals("none")) {
            thisXslt = thisXslt + params.importXslt;
            transformIt = true;
         }

         // --- For each record....
         for (Element md : list) {
            md = (Element) md.clone();

            if (md.getQualifiedName().equals("summary"))
               continue;

            // -- Remove existing geonet:info children as for example
            // -- GeoNetwork Z39.50 server return when full mode
            // -- an extra element with server info not needed
            // -- once harvested
            md.removeChildren(Edit.RootChild.INFO, Edit.NAMESPACE);

            // validate it here if requested
            if (params.validate) {
               try {
                  Xml.validate(md);
               } catch (Exception e) {
                  System.out.println("Cannot validate XML, ignoring. Error was: " + e.getMessage());
                  result.doesNotValidate++;
                  continue; // skip this one
               }
            }

            // transform using importxslt if not none
            if (transformIt) {
               try {
                  md = Xml.transform(md, thisXslt);
               } catch (Exception e) {
                  System.out
                        .println("Cannot transform XML, ignoring. Error was: " + e.getMessage());
                  result.badFormat++;
                  continue; // skip this one
               }
            }

            // detect schema, extract uuid and add
            String schema = dataMan.autodetectSchema(md);
            if (schema == null) {
               log.warning("Skipping metadata with unknown schema.");
               result.unknownSchema++;
               continue;
            }

            String uuid = dataMan.extractUUID(schema, md);
            if (uuid == null || uuid.equals("")) {
               log.warning("Skipping metadata due to failure extracting uuid (uuid null or empty).");
               result.unretrievable++;
               continue;
            }

            log.info("  - Adding metadata for services with " + uuid);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = new Date();

            String id = dataMan.insertMetadataExt(dbms, schema, md, params.uuid, df.format(date),
                  df.format(date), uuid, null);

            int iId = Integer.parseInt(id);

            Category category = getValidCategory(params.getCategories());

            // FIXME OpenWIS if null, no category assigned??
            if (category != null) {
               MetadataManager mm = new MetadataManager(dbms);
               mm.updateCategory(uuid, category.getId());
            }

            dataMan.setTemplateExt(dbms, uuid, "n", null);
            dataMan.setHarvestedExt(dbms, uuid, params.uuid, params.name);

            dbms.commit();
            dataMan.indexMetadata(dbms, uuid, null);

            result.addedMetadata++;

         }
      }

      return result;
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

// =============================================================================

