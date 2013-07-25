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

package org.fao.geonet.kernel.harvest.harvester.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jeeves.exceptions.BadXmlResponseEx;
import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.Privileges;
import org.fao.geonet.kernel.setting.SettingInfo;
import org.jdom.Element;
import org.jdom.Namespace;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================
/**
 * A fragment harvester used by other harvesters (THREDDS/Metadata Fragments)
 * to create metadata and/or sub-templates from metadata fragments they have
 * harvested
 *
**/

public class FragmentHarvester {

   private Logger log;

   private ServiceContext context;

   private Dbms dbms;

   private DataManager dataMan;

   private FragmentParams params;

   private String metadataGetService;

   private List metadataTemplateNamespaces = new ArrayList();

   private Element metadataTemplate;

   private List<Category> localCateg;

   private HarvestSummary harvestSummary;

   static private final Namespace xlink = Namespace.getNamespace("xlink",
         "http://www.w3.org/1999/xlink");

   /**
     * Constructor
     *
     * @param log
     * @param context		Jeeves context
     * @param dbms 			Database
     * @param params		Fragment harvesting configuration parameters
     *
     */
   public FragmentHarvester(Logger log, ServiceContext context, Dbms dbms, FragmentParams params) {
      this.log = log;
      this.context = context;
      this.dbms = dbms;
      this.params = params;

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      dataMan = gc.getDataManager();

      SettingInfo si = new SettingInfo(context);
      String siteUrl = si.getSiteUrl() + context.getBaseUrl();
      metadataGetService = siteUrl + "/srv/en/xml.metadata.get";

      if (params.templateId != null && !params.templateId.equals("0")) {
         loadTemplate();
      }
   }

   //---------------------------------------------------------------------------
   /**
    * Load metadata template to be used to generate metadata
    *
    */
   private void loadTemplate() {
      try {
         //--- Load template to be used to create metadata from fragments
         metadataTemplate = dataMan.getMetadataNoInfo(context, params.templateId);

         //--- Build a list of all Namespaces in the metadata document
         Namespace ns = metadataTemplate.getNamespace();
         if (ns != null) {
            metadataTemplateNamespaces.add(ns);
            metadataTemplateNamespaces.addAll(metadataTemplate.getAdditionalNamespaces());
         }
      } catch (Exception e) {
         log.error("Thrown Exception " + e + " opening template with id: " + params.templateId);
         e.printStackTrace();
      }
   }

   //---------------------------------------------------------------------------
   /**
    * Create subtemplates/metadata from fragments
    *
    * @param fragments      Fragments to use to create metadata/subtemplates
    *
    */
   @SuppressWarnings("unchecked")
   public HarvestSummary harvest(Element fragments) throws Exception {
      harvestSummary = new HarvestSummary();

      if (fragments == null || !fragments.getName().equals("records")) {
         throw new BadXmlResponseEx("<records> not found in response: \n"
               + Xml.getString(fragments));
      }

      //--- Loading categories and groups
      CategoryManager cm = new CategoryManager(dbms);
      localCateg = cm.getAllCategories();

      List<Element> recs = fragments.getChildren();

      for (Element rec : recs) {
         addFragments(rec.getChildren());
      }

      return harvestSummary;
   }

   //---------------------------------------------------------------------------
   /**
     * Add subtemplates and/or metadata using fragments and metadata template
     *
     * @param fragments		List of fragments to add to GeoNetwork database
     *
     */
   private void addFragments(List<Element> fragments) throws Exception {

      Element templateCopy = null;

      if (metadataTemplate != null) {
         templateCopy = (Element) metadataTemplate.clone();
      }

      boolean matchFragment = true;

      for (Element fragment : fragments) {

         // get the id and title from the fragment to match/use in any template
         String title = fragment.getAttributeValue("title");
         String matchId = fragment.getAttributeValue("id");

         if (matchId == null || matchId.equals("")) {
            log.error("Fragment won't be matched because no id attribute "
                  + Xml.getString(fragment));
            matchFragment = false;
         }

         // get the metadata fragment from the fragment container
         Element md = (Element) fragment.getChildren().get(0);

         String schema = dataMan.autodetectSchema(md); // e.g. iso19139;

         if (schema == null) {
            log.warning("Skipping metadata with unknown schema.");
            harvestSummary.fragmentsUnknownSchema++;
         } else {
            if (params.createSubtemplates) {
               String uuid = fragment.getAttributeValue("uuid");
               if (uuid == null || uuid.equals("")) {
                  uuid = UUID.randomUUID().toString();
                  log.warning("  - Metadata fragment did not have uuid! Fragment XML is "
                        + Xml.getString(md));
               }
               log.info("  - Adding metadata fragment with " + uuid + " schema is set to " + schema
                     + " XML is " + Xml.getString(md));
               DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
               Date date = new Date();

               // Category may be null
               Category category = getValidCategory(params.categories);

               String id = dataMan.insertHarvestedMetadata(dbms, schema, md, params.uuid, df
                     .format(date), df.format(date), uuid, null, null,
                     category != null ? category.getId() : null);

               int iId = Integer.parseInt(id);

               dataMan.setTemplateExt(dbms, uuid, "s", null);
               dataMan.setHarvestedExt(dbms, uuid, params.uuid, params.url);

               dataMan.indexMetadataGroup(dbms, uuid, null);

               dbms.commit();
               harvestSummary.fragmentsAdded++;

               if (metadataTemplate != null && matchFragment) {
                  insertLinkToFragmentIntoTemplate(templateCopy, matchId, uuid, title);
               }
            } else {
               insertFragmentIntoMetadata(templateCopy, matchId, md);
            }
         }
      }

      harvestSummary.fragmentsReturned += fragments.size();

      if (metadataTemplate != null && matchFragment) {
         // now add any record built from template with linked in fragments
         log.info("	- Attempting to insert metadata record with link");
         DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         Date date = new Date();
         String recUuid = UUID.randomUUID().toString();
         String templateSchema = dataMan.autodetectSchema(templateCopy);
         templateCopy = dataMan.setUUID(templateSchema, recUuid, templateCopy);

         List<String> categId = new ArrayList<String>();
         categId.add(params.isoCategory);
         // Category may be null
         Category category = getValidCategory(categId);

         String id = dataMan.insertHarvestedMetadata(dbms, templateSchema, templateCopy,
               params.uuid, df.format(date), df.format(date), recUuid, null, null,
               category != null ? category.getId() : null);

         int iId = Integer.parseInt(id);

         log.info("	- Set harvested");

         dataMan.setHarvestedExt(dbms, recUuid, params.uuid, params.url);

         dataMan.indexMetadataGroup(dbms, recUuid, null);

         log.info("	- Commit " + id);
         dbms.commit();
         harvestSummary.recordsBuilt++;
      }
   }

   //---------------------------------------------------------------------------
   /**
     * Insert Link to Fragment - replace all instances of matchId to the uuid
     * of the fragment
     *
     * @param templateCopy		Copy of the template for fragment links
     * @param matchId		Id used in template to place fragment
     * @param uuid 			uuid of the fragment inserted into GeoNetwork db
     *
     */
   @SuppressWarnings("unchecked")
   private void insertLinkToFragmentIntoTemplate(Element templateCopy, String matchId, String uuid,
         String title) throws Exception {

      // find all elements that have an attribute id with the matchId
      log.info("Attempting to search metadata for " + matchId);
      List<Element> elems = (List<Element>) Xml.selectNodes(templateCopy, "*//*[@id='" + matchId
            + "']", metadataTemplateNamespaces);

      // for each of these elements...
      for (Element elem : elems) {
         log.info("Element found " + Xml.getString(elem));

         // add uuidref attribute to link to fragment
         elem.setAttribute("uuidref", uuid);
         elem.setAttribute("href", metadataGetService + "?uuid=" + uuid, xlink);
         elem.setAttribute("show", "replace", xlink);
         if (title != null)
            elem.setAttribute("title", title, xlink);
      }

      if (elems.size() > 0)
         harvestSummary.fragmentsMatched++;

      log.info("Template with metadata links is\n" + Xml.getString(templateCopy));
   }

   //---------------------------------------------------------------------------
   /**
     * Insert Fragment - replace all instances of matchId with the fragment
     *
     * @param metadata		Copy of the template into which to insert fragments
     * @param matchId		Id used in template to place fragment
     * @param fragment		Fragment to insert
     *
     */
   @SuppressWarnings("unchecked")
   private void insertFragmentIntoMetadata(Element metadata, String matchId, Element fragment)
         throws Exception {
      // find all elements that have an attribute id with the matchId

      log.info("Attempting to search metadata for " + matchId);
      List<Element> elems = (List<Element>) Xml.selectNodes(metadata,
            "*//*[@id='" + matchId + "']", metadataTemplateNamespaces);

      // for each of these elements...

      for (Element elem : elems) {
         log.info("Element found " + Xml.getString(elem));
         //replace current element with fragment
         Element parent = elem.getParentElement();
         parent.setContent(parent.indexOf(elem), (Element) fragment.clone());
      }

      if (elems.size() > 0) {
         harvestSummary.fragmentsMatched++;
      }

      log.info("Generated metadata is\n" + Xml.getString(metadata));
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

   static public class FragmentParams {
      public String url;

      public String uuid;

      public String templateId;

      public String isoCategory;

      public Boolean createSubtemplates;

      public Iterable<Privileges> privileges;

      public List<String> categories;
   }

   public class HarvestSummary {
      public int fragmentsMatched;

      public int recordsBuilt;

      public int fragmentsReturned;

      public int fragmentsAdded;

      public int fragmentsUnknownSchema;
   }
}
