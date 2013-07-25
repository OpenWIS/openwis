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

package org.fao.geonet.kernel.csw.services.getrecords;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.XPath;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.csw.common.ElementSetName;
import org.fao.geonet.csw.common.OutputSchema;
import org.fao.geonet.csw.common.ResultType;
import org.fao.geonet.csw.common.exceptions.CatalogException;
import org.fao.geonet.csw.common.exceptions.InvalidParameterValueEx;
import org.fao.geonet.csw.common.exceptions.NoApplicableCodeEx;
import org.fao.geonet.kernel.search.Pair;
import org.fao.geonet.kernel.search.SearchManagerFactory;
import org.fao.geonet.kernel.search.SortingInfo;
import org.jdom.Content;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.csw.services.getrecords.GenericCatalogSearcher;
import org.openwis.metadataportal.kernel.search.query.SearchResult;
import org.openwis.metadataportal.kernel.search.query.SearchResultDocument;

/**
 * The Class SearchController. <P>
 * Explanation goes here. <P>
 */
@SuppressWarnings("unchecked")
public class SearchController {

   /** The _searcher. */
   private final ICatalogSearcher _searcher;

   /**
    * Instantiates a new search controller.
    *
    * @param config the config
    * @param appPath the app path
    */
   @SuppressWarnings("rawtypes")
   public SearchController(ServiceConfig config, String appPath) {
      super();
      _searcher = new GenericCatalogSearcher(SearchManagerFactory.getQueryManagerFactory(config,
            appPath));
   }

   //---------------------------------------------------------------------------
   //---
   //--- Single public method to perform the general search tasks
   //---
   //---------------------------------------------------------------------------

   /**
    * Perform the general search tasks.
    *
    * @param context the context
    * @param startPos the start position
    * @param maxRecords the max records
    * @param resultType the result type
    * @param outSchema the out schema
    * @param setName the set name
    * @param filterExpr the filter expression
    * @param filterVersion the filter version
    * @param sort the sort
    * @param elemNames the element names
    * @param maxHitsFromSummary the max hits from summary
    * @return the pair
    * @throws CatalogException the catalog exception
    */
   public Pair<Element, Element> search(ServiceContext context, int startPos, int maxRecords,
         ResultType resultType, OutputSchema outSchema, ElementSetName setName, Element filterExpr,
         String filterVersion, SortingInfo sort, Set<String> elemNames, int maxHitsFromSummary)
         throws CatalogException {

      Element results = new Element("SearchResults", Csw.NAMESPACE_CSW);

      SearchResult searchResult = _searcher.search(context, filterExpr, filterVersion, sort,
            resultType, startPos, maxRecords, maxHitsFromSummary);

      List<SearchResultDocument> docs = searchResult.getDocuments();
      if ((resultType == ResultType.HITS || resultType == ResultType.RESULTS || resultType == ResultType.RESULTS_WITH_SUMMARY)
            && !docs.isEmpty()) {
         String id;
         Element md;
         for (SearchResultDocument doc : docs) {
            id = doc.getId();
            md = retrieveMetadata(context, id, setName, outSchema, elemNames, resultType);
            if (md == null) {
               context.warning("SearchController : Metadata not found or invalid schema : " + id);
            } else {
               results.addContent(md);
            }
         }
      }

      int hits = docs.size();
      int count = searchResult.getCount();

      results.setAttribute("numberOfRecordsMatched", String.valueOf(count));
      results.setAttribute("numberOfRecordsReturned", String.valueOf(hits));
      results.setAttribute("elementSet", setName.toString());

      if (count > hits) {
         results.setAttribute("nextRecord", hits + startPos + "");
      } else {
         results.setAttribute("nextRecord", "0");
      }

      Element summary = searchResult.toSummary(resultType);
      return Pair.read(summary, results);
   }

   /**
    * Retrieve metadata from the database.
    * Conversion between metadata record and output schema are defined
    * in xml/csw/schemas/ directory.
    *
    * @param context the context
    * @param id the id
    * @param setName the set name
    * @param outSchema the out schema
    * @param elemNames the elem names
    * @param resultType the result type
    * @return The XML metadata record if the record could be converted to
    * the required output schema. Null if no conversion available for
    * the schema (eg. fgdc record could not be converted to ISO).
    * @throws CatalogException the catalog exception
    */
   public static Element retrieveMetadata(ServiceContext context, String id,
         ElementSetName setName, OutputSchema outSchema, Set<String> elemNames,
         ResultType resultType) throws CatalogException {
      try {
         //--- get metadata from DB
         GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
         Element res = gc.getDataManager().getMetadata(context, id, false);

         if (res == null)
            return null;

         String schema = res.getChild(Edit.RootChild.INFO, Edit.NAMESPACE).getChildText(
               Edit.Info.Elem.SCHEMA);

         String FS = File.separator;

         // --- transform iso19115 record to iso19139
         // --- If this occur user should probably migrate the catalogue from iso19115 to iso19139.
         // --- But sometimes you could harvest remote node in iso19115 and make them available through CSW
         if ("iso19115".equals(schema)) {
            String stylesheet = MessageFormat.format(
                  "{0}xsl{1}conversion{1}import{1}ISO19115-to-ISO19139.xsl", context.getAppPath(),
                  FS);
            res = Xml.transform(res, stylesheet);
            schema = "iso19139";
         }

         //--- skip metadata with wrong schemas

         if ("fgdc-std".equals(schema) || "dublin-core".equals(schema))
            if (outSchema != OutputSchema.OGC_CORE)
               return null;

         //--- apply stylesheet according to setName and schema

         String prefix;
         switch (outSchema) {
         case OGC_CORE:
            prefix = "ogc";
            break;
         case ISO_PROFILE:
            prefix = "iso";
            break;
         default:
            // FIXME ISO PROFIL : Use declared primeNS in current node.
            prefix = "fra";
            if (!schema.contains("iso19139")) {
               // FIXME : should we return null or an exception in that case and which exception
               throw new InvalidParameterValueEx("outputSchema not supported for metadata " + id
                     + " schema.", schema);
            }
            break;
         }

         String schemaDir = MessageFormat.format("{0}xml{2}csw{2}schemas{2}{1}{2}",
               context.getAppPath(), schema, FS);
         String styleSheet = MessageFormat.format("{0}{1}-{2}.xsl", schemaDir, prefix, setName);

         Map<String, String> params = new HashMap<String, String>();
         params.put("lang", context.getLanguage());
         params.put("displayInfo", resultType == ResultType.RESULTS_WITH_SUMMARY ? "true" : "false");

         res = Xml.transform(res, styleSheet, params);

         //--- if the client has specified some ElementNames, then we search for them
         //--- if they are in anything else other that csw:Record, if csw:Record
         //--- remove only the unwanted ones

         if (elemNames != null) {
            if (outSchema != OutputSchema.OGC_CORE) {
               Element frags = (Element) res.clone();
               frags.removeContent();
               for (String s : elemNames) {
                  try {
                     Content o = (Content) XPath.getElement(res, s);
                     if (o != null) {
                        frags.addContent((Content) o.clone());
                     }
                  } catch (Exception e) {
                     Log.error(Geonet.CSW_SEARCH, "Error into building search result", e);
                     throw new InvalidParameterValueEx("elementName has invalid XPath : " + s,
                           e.getMessage());
                  }
               }
               res = frags;
            } else {
               removeElements(res, elemNames);
            }
         }
         return res;
      } catch (Exception e) {
         Log.error(Geonet.CSW_SEARCH, "Error while getting metadata with id : " + id, e);
         throw new NoApplicableCodeEx("Raised exception while getting metadata :" + e);
      }
   }

   /**
    * Removes the elements.
    *
    * @param md the md
    * @param elemNames the elem names
    */
   private static void removeElements(Element md, Set<String> elemNames) {
      Iterator<Element> i = ((List<Element>) md.getChildren()).iterator();
      Element elem;
      while (i.hasNext()) {
         elem = i.next();

         if (!FieldMapper.match(elem, elemNames)) {
            i.remove();
         }
      }
   }
}
