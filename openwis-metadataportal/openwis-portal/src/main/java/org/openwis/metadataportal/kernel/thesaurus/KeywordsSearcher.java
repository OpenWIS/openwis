//===	Copyright (C) 2001-2005 Food and Agriculture Organization of the
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
//===	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: GeoNetwork@fao.org
//==============================================================================

package org.openwis.metadataportal.kernel.thesaurus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.kernel.Thesaurus;
import org.fao.geonet.kernel.ThesaurusManager;
import org.jdom.Element;
import org.openrdf.model.Value;
import org.openrdf.sesame.query.QueryResultsTable;
import org.openwis.metadataportal.services.thesaurus.dto.KeywordDTO;
import org.openwis.metadataportal.services.thesaurus.dto.SearchDTO;

public class KeywordsSearcher {
   private final ThesaurusManager _tm;

   private String _query;

   private String _lang;

   private List<KeywordDTO> _results = new ArrayList<KeywordDTO>();

   private int _maxResults = 10000;

   // --------------------------------------------------------------------------------
   // constructor
   public KeywordsSearcher(ThesaurusManager tm) {
      _tm = tm;
   }

   // --------------------------------------------------------------------------------
   public KeywordDTO searchById(String id, String sThesaurusName, String lang) throws Exception {

      _query = "SELECT prefLab, note, id, lowc, uppc "
            + " FROM {id} rdf:type {skos:Concept}; "
            + " skos:prefLabel {prefLab};"
            + " [skos:scopeNote {note} WHERE lang(note) LIKE \""
            + lang
            + "\"]; "
            + " [gml:BoundedBy {} gml:lowerCorner {lowc}]; "
            + " [gml:BoundedBy {} gml:upperCorner {uppc}] "
            + " WHERE lang(prefLab) LIKE \""
            + lang
            + "\" "
            + " AND id LIKE \""
            + id
            + "\" "
            + " IGNORE CASE "
            + " USING NAMESPACE skos=<http://www.w3.org/2004/02/skos/core#>, gml=<http://www.opengis.net/gml#> ";

      Thesaurus thesaurus = _tm.getThesaurusByName(sThesaurusName);

      // Perform request
      QueryResultsTable resultsTable = thesaurus.performRequest(_query);
      int rowCount = resultsTable.getRowCount();
      Integer idKeyword = 0;

      if (rowCount == 0) {
         return null;
      } else {
         // MUST be one because search by ID

         // preflab
         Value value = resultsTable.getValue(0, 0);
         String sValue = "";
         if (value != null) {
            sValue = value.toString();
         }

         //				 uri (= id in RDF file != id in list)
         Value uri = resultsTable.getValue(0, 2);
         String sUri = "";
         if (uri != null) {
            sUri = uri.toString();
         }

         KeywordDTO kb = new KeywordDTO(idKeyword, sValue, "", sUri, "", "", "", "",
               sThesaurusName, _lang);
         idKeyword++;

         return kb;
      }

   }

   public void searchParents(String id, String sThesaurusName, String lang) throws Exception {
      String _lang = lang;
      _query = "SELECT distinct prefLab, note, id, lowc, uppc " //, n "
            + " FROM {id} rdf:type {skos:Concept}; "
            + " skos:prefLabel {prefLab};"
            + " [skos:broader {broad}]; "
            //+ " [skos:narrower {n}]; "
            + " [skos:scopeNote {note} WHERE lang(note) LIKE \""
            + _lang
            + "\"]; "
            + " [gml:BoundedBy {} gml:lowerCorner {lowc}]; "
            + " [gml:BoundedBy {} gml:upperCorner {uppc}] "
            + " WHERE lang(prefLab) LIKE \""
            + _lang
            + "\""
            + " AND broad = NULL"
            + " USING NAMESPACE skos=<http://www.w3.org/2004/02/skos/core#>, gml=<http://www.opengis.net/gml#> ";
      // For each thesaurus, search for keywords in _results
      _results = new ArrayList<KeywordDTO>();
      Integer idKeyword = 0;
      Thesaurus thesaurus = _tm.getThesaurusByName(sThesaurusName);

      // Perform request
      QueryResultsTable resultsTable = thesaurus.performRequest(_query);

      int rowCount = resultsTable.getRowCount();

      for (int row = 0; row < rowCount; row++) {
         // preflab
         Value value = resultsTable.getValue(row, 0);
         String sValue = "";
         if (value != null) {
            sValue = value.toString();
         }
         // definition
         Value definition = resultsTable.getValue(row, 1);
         String sDefinition = "";
         if (definition != null) {
            sDefinition = definition.toString();
         }
         // uri (= id in RDF file != id in list)
         Value uri = resultsTable.getValue(row, 2);
         String sUri = "";
         if (uri != null) {
            sUri = uri.toString();
         }

         Value lowCorner = resultsTable.getValue(row, 3);
         Value upperCorner = resultsTable.getValue(row, 4);

         String sUpperCorner;
         String sLowCorner;

         String sEast = "";
         String sSouth = "";
         String sWest = "";
         String sNorth = "";

         // lowcorner
         if (lowCorner != null) {
            sLowCorner = lowCorner.toString();
            sWest = sLowCorner.substring(0, sLowCorner.indexOf(' ')).trim();
            sSouth = sLowCorner.substring(sLowCorner.indexOf(' ')).trim();
         }

         // uppercorner
         if (upperCorner != null) {
            sUpperCorner = upperCorner.toString();
            sEast = sUpperCorner.substring(0, sUpperCorner.indexOf(' ')).trim();
            sNorth = sUpperCorner.substring(sUpperCorner.indexOf(' ')).trim();
         }

         KeywordDTO kb = new KeywordDTO(idKeyword, sValue, sDefinition, sUri, sEast, sWest, sSouth,
               sNorth, sThesaurusName, _lang);
         _results.add(kb);
         idKeyword++;
      }
   }

   public void searchChildren(ServiceContext srvContext, Element params) throws Exception {
      String id = Util.getParam(params, "id");
      String sThesaurusName = Util.getParam(params, "thesaurus");

      String _lang = srvContext.getLanguage();

      searchChildren(id, sThesaurusName, _lang);
   }

   public void searchChildren(String id, String sThesaurusName, String _lang) throws Exception {

      Thesaurus thesaurus = _tm.getThesaurusByName(sThesaurusName);
      _results = new ArrayList<KeywordDTO>();

      String _query = "SELECT prefLab, note, id, n "
            + " from {id} rdf:type {skos:Concept};"
            + " skos:prefLabel {prefLab};"
            + " [skos:broader {b}];"
            + " [skos:narrower {n}];"
            + " [skos:scopeNote {note} WHERE lang(note) LIKE \""
            + _lang
            + "\"] "
            + " WHERE lang(prefLab) LIKE \""
            + _lang
            + "\""
            + " AND b LIKE \"*"
            + id
            + "\""
            + " IGNORE CASE "
            + " USING NAMESPACE skos=<http://www.w3.org/2004/02/skos/core#>, gml=<http://www.opengis.net/gml#> ";

      // Perform request
      QueryResultsTable resultsTable = thesaurus.performRequest(_query);

      int rowCount = resultsTable.getRowCount();
      Integer idKeyword = 0;

      Set<String> labels = new HashSet<String>();

      for (int row = 0; row < rowCount; row++) {

         // preflab
         Value value = resultsTable.getValue(row, 0);
         String sValue = "";
         if (value != null) {
            sValue = value.toString();
         }
         // skip duplicate
         if (labels.contains(sValue)) {
            continue;
         }
         else
         {
            labels.add(sValue);
         }
         //        uri (= id in RDF file != id in list)
         Value uri = resultsTable.getValue(row, 2);
         String sUri = "";
         if (uri != null) {
            sUri = uri.toString();
         }

         Value leaf = resultsTable.getValue(row, 3);
         boolean isLeaf = false;
         if (leaf == null) {
            isLeaf = true;
         }

         KeywordDTO kb = new KeywordDTO(idKeyword, sValue, "", sUri, "", "", "", "",
               sThesaurusName, _lang, isLeaf);
         _results.add(kb);
         idKeyword++;
      }
   }

   public void search(ServiceContext srvContext, SearchDTO searchDTO) throws Exception {
      // Get params from request and set default
      String sKeyword = searchDTO.getKeyword();

      // Get max results number or set default one.
      _maxResults = Integer.parseInt(searchDTO.getMaxResults());

      // Type of search
      int pTypeSearch;
      if (searchDTO.getTypeSearch() != null) { // if param pTypeSearch not here
         pTypeSearch = Integer.parseInt(searchDTO.getTypeSearch());

         // Thesaurus to search in
         List listThesauri = new Vector<Element>();
         // Type of thesaurus to search in
         String pTypeThesaurus = null;
         //		if (params.getChild("pType") != null)							// if param pTypeSearch not here
         //			pTypeThesaurus = Util.getParam(params, "pType");
         //
         //		boolean bAll = true;
         //
         //		if (params.getChild("pThesauri") != null){							// if param pThesauri not here
         //			listThesauri = params.getChildren("pThesauri");
         //			bAll = false;
         //
         //			// Check empty child and remove empty ones.
         //			for (Iterator<Element> it = listThesauri.iterator(); it.hasNext();) {
         //				Element th = it.next();
         //				if ("".equals(th.getTextTrim()))
         //					it.remove();
         //			}
         //
         //			if (listThesauri.size() == 0)
         //				bAll = true;
         //		}
         //
         //		//	If no thesaurus search in all.
         //		if (bAll){
         //			Hashtable<String, Thesaurus> tt = _tm.getThesauriTable();
         //
         //			Enumeration<String> e = tt.keys();
         //			boolean add = true;
         //		    while (e.hasMoreElements())										// Fill the list with all thesauri available
         //		    {
         //		    	Thesaurus thesaurus = tt.get(e.nextElement());
         //		    	if (pTypeThesaurus != null){
         //                    add = thesaurus.getDname().equals(pTypeThesaurus);
         //		    	}
         //
         //		    	if (add){
         //		    		Element el = new Element("pThesauri");
         //			    	el.addContent(thesaurus.getKey());
         //			    	listThesauri.add(el);
         //		    	}
         //		    }
         //		}

         String thesori = searchDTO.getThesauri();

         // Keyword to look for
         if (!sKeyword.equals("")) {

            // FIXME : Where to search ? only on term having GUI language or in all ?
            // Should be
            // - look for a term in all language
            // - get prefLab in GUI lang
            // This will cause multilingual metadata search quite complex !!
            // Quid index and thesaurus ?

            String _lang = srvContext.getLanguage();
            _query = "SELECT prefLab, note, id, lowc, uppc "
                  + " FROM {id} rdf:type {skos:Concept}; " + " skos:prefLabel {prefLab};"
                  + " [skos:scopeNote {note} WHERE lang(note) LIKE \"" + _lang + "\"]; "
                  + " [gml:BoundedBy {} gml:lowerCorner {lowc}]; "
                  + " [gml:BoundedBy {} gml:upperCorner {uppc}] " + " WHERE lang(prefLab) LIKE \""
                  + _lang + "\"" + " AND prefLab LIKE ";

            switch (pTypeSearch) {
            case 0: // Start with
               _query += "\"" + sKeyword + "*\" ";
               break;
            case 1: // contains
               _query += "\"*" + sKeyword + "*\" ";
               break;
            case 2: // exact match
               _query += "\"" + sKeyword + "\" ";
               break;
            default:
               break;
            }
            _query += " IGNORE CASE "
                  + " LIMIT "
                  + _maxResults
                  + " USING NAMESPACE skos=<http://www.w3.org/2004/02/skos/core#>, gml=<http://www.opengis.net/gml#> ";

         }

         // For each thesaurus, search for keywords in _results
         _results = new ArrayList<KeywordDTO>();
         Integer idKeyword = 0;

         //            for (Object aListThesauri : listThesauri) {             // Search in all Thesaurus if none selected
         //                Element el = (Element) aListThesauri;
         //                String sThesaurusName = el.getTextTrim();

         Thesaurus thesaurus = _tm.getThesaurusByName(thesori);

         // Perform request
         QueryResultsTable resultsTable = thesaurus.performRequest(_query);

         int rowCount = resultsTable.getRowCount();

         for (int row = 0; row < rowCount; row++) {
            // preflab
            Value value = resultsTable.getValue(row, 0);
            String sValue = "";
            if (value != null) {
               sValue = value.toString();
            }
            // definition
            Value definition = resultsTable.getValue(row, 1);
            String sDefinition = "";
            if (definition != null) {
               sDefinition = definition.toString();
            }
            // uri (= id in RDF file != id in list)
            Value uri = resultsTable.getValue(row, 2);
            String sUri = "";
            if (uri != null) {
               sUri = uri.toString();
            }

            Value lowCorner = resultsTable.getValue(row, 3);
            Value upperCorner = resultsTable.getValue(row, 4);

            String sUpperCorner;
            String sLowCorner;

            String sEast = "";
            String sSouth = "";
            String sWest = "";
            String sNorth = "";

            // lowcorner
            if (lowCorner != null) {
               sLowCorner = lowCorner.toString();
               sWest = sLowCorner.substring(0, sLowCorner.indexOf(' ')).trim();
               sSouth = sLowCorner.substring(sLowCorner.indexOf(' ')).trim();
            }

            // uppercorner
            if (upperCorner != null) {
               sUpperCorner = upperCorner.toString();
               sEast = sUpperCorner.substring(0, sUpperCorner.indexOf(' ')).trim();
               sNorth = sUpperCorner.substring(sUpperCorner.indexOf(' ')).trim();
            }

            KeywordDTO kb = new KeywordDTO(idKeyword, sValue, sDefinition, sUri, sEast, sWest,
                  sSouth, sNorth, thesori, _lang);
            _results.add(kb);
            idKeyword++;
         }
         //            }
      }
   }

   public void searchBN(ServiceContext srvContext, Element params, String request) throws Exception {
      // TODO : Add geonetinfo elements.
      String id = Util.getParam(params, "id");
      String sThesaurusName = Util.getParam(params, "thesaurus");

      String _lang = srvContext.getLanguage();

      searchBN(id, sThesaurusName, request, _lang);
   }

   public void searchBN(String id, String sThesaurusName, String request, String _lang)
         throws Exception {

      Thesaurus thesaurus = _tm.getThesaurusByName(sThesaurusName);
      _results = new ArrayList<KeywordDTO>();

      String _query = "SELECT prefLab, note, id "
            + " from {id} rdf:type {skos:Concept};"
            + " skos:prefLabel {prefLab};"
            + " [skos:"
            + request
            + " {b}];"
            + " [skos:scopeNote {note} WHERE lang(note) LIKE \""
            + _lang
            + "\"] "
            + " WHERE lang(prefLab) LIKE \""
            + _lang
            + "\""
            + " AND b LIKE \"*"
            + id
            + "\""
            + " IGNORE CASE "
            + " USING NAMESPACE skos=<http://www.w3.org/2004/02/skos/core#>, gml=<http://www.opengis.net/gml#> ";

      //	Perform request
      QueryResultsTable resultsTable = thesaurus.performRequest(_query);

      int rowCount = resultsTable.getRowCount();
      Integer idKeyword = 0;

      for (int row = 0; row < rowCount; row++) {

         // preflab
         Value value = resultsTable.getValue(row, 0);
         String sValue = "";
         if (value != null) {
            sValue = value.toString();
         }

         //			 uri (= id in RDF file != id in list)
         Value uri = resultsTable.getValue(row, 2);
         String sUri = "";
         if (uri != null) {
            sUri = uri.toString();
         }

         KeywordDTO kb = new KeywordDTO(idKeyword, sValue, "", sUri, "", "", "", "",
               sThesaurusName, _lang);
         _results.add(kb);
         idKeyword++;
      }
   }

   public void findEnclosedGeoKeyword(String sKeywordCode) {
      _query = "SELECT prefLab, note, id, lowc, uppc "
            + " FROM {id} rdf:type {skos:Concept}; "
            + " skos:prefLabel {prefLab};"
            + " [skos:scopeNote {note} WHERE lang(note) LIKE \""
            + _lang
            + "\"]; "
            + " [gml:BoundedBy {} gml:lowerCorner {lowc}]; "
            + " [gml:BoundedBy {} gml:upperCorner {uppc}] "
            + " WHERE lang(prefLab) LIKE \""
            + _lang
            + "\""
            + " AND prefLab LIKE \""
            + sKeywordCode
            + "*\" "
            + " USING NAMESPACE skos=<http://www.w3.org/2004/02/skos/core#>, gml=<http://www.opengis.net/gml#> ";
   }

   public int getNbResults() {
      return _results.size();
   }

   public void sortResults(String tri) {
      if ("label".equals(tri)) {
         // sort by label
         Collections.sort(_results, new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
               final KeywordDTO kw1 = (KeywordDTO) o1;
               final KeywordDTO kw2 = (KeywordDTO) o2;
               return kw1.getValue().compareToIgnoreCase(kw2.getValue());
            }
         });
      }
      if ("definition".equals(tri)) {
         // sort by def
         Collections.sort(_results, new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
               final KeywordDTO kw1 = (KeywordDTO) o1;
               final KeywordDTO kw2 = (KeywordDTO) o2;
               return kw1.getDefinition().compareToIgnoreCase(kw2.getDefinition());
            }
         });
      }
   }

   public List<KeywordDTO> getResults() throws Exception {

      List<KeywordDTO> keywordList = new ArrayList<KeywordDTO>();

      int nbResults = (getNbResults() <= _maxResults ? getNbResults() : _maxResults);

      //for (int i = from; i <= to; i++) {
      for (int i = 0; i <= nbResults - 1; i++) {
         KeywordDTO kb = _results.get(i);
         keywordList.add(kb);
         //			Element elKeyword = new Element("keyword");
         //			Element elSelected = new Element("selected");
         //			// TODO : Add Thesaurus name
         //
         //			if (kb.isSelected()) {
         //				elSelected.addContent("true");
         //			} else {
         //				elSelected.addContent("false");
         //			}
         //			Element elId = new Element("id");
         //			elId.addContent(Integer.toString(kb.getId()));
         //			Element elValue = new Element("value");
         //			elValue.addContent(kb.getValue());
         //			Element elDefiniton = new Element("definition");
         //			elDefiniton.addContent(kb.getDefinition());
         //			Element elTh = new Element("thesaurus");
         //			elTh.addContent(kb.getThesaurus());
         //			Element elUri = new Element("uri");
         //			elUri.addContent(kb.getCode());
         //
         //			addBbox(kb, elKeyword);
         //
         //			elKeyword.addContent(elSelected);
         //			elKeyword.addContent(elId);
         //			elKeyword.addContent(elValue);
         //			elKeyword.addContent(elDefiniton);
         //			elKeyword.addContent(elTh);
         //			elKeyword.addContent(elUri);
         //			elDescKeys.addContent(elKeyword);
      }

      return keywordList;
   }

   //	public void selectUnselectKeywords(Element params) {
   //		List listIdKeywordsSelected = params.getChildren("pIdKeyword");
   //        for (Object aListIdKeywordsSelected : listIdKeywordsSelected) {
   //            Element el = (Element) aListIdKeywordsSelected;
   //            int keywordId = Integer.decode(el.getTextTrim());
   //            for (KeywordDTO _result : _results) {
   //               if (( _result).getId() == keywordId) {
   //                  ( _result)
   //                          .setSelected(!( _result)
   //                                  .isSelected());
   //              }
   //            }
   //        }
   //	}

   /**
    * @return an element describing the list of selected keywords
    */
   //	public Element getSelectedKeywords() {
   //		Element elDescKeys = new Element("descKeys");
   //		int nbSelectedKeywords = 0;
   //		for (int i = 0; i < this.getNbResults(); i++) {
   //			KeywordDTO kb = _results.get(i);
   //			if (kb.isSelected()) {
   //				Element elKeyword = new Element("keyword");
   //				// keyword type
   //				String thesaurusType = kb.getThesaurus();
   //				thesaurusType = thesaurusType.replace('.', '-');
   //				thesaurusType =  thesaurusType.split("-")[1];
   //				elKeyword.setAttribute("type", thesaurusType);
   //				Element elValue = new Element("value");
   //				elValue.addContent(kb.getValue());
   //				Element elCode = new Element("code");
   //				//String code=kb.getRelativeCode();
   //				//code = code.split("#")[1];
   //				//elCode.addContent(code);
   //				addBbox(kb, elKeyword);
   //				elKeyword.addContent(elCode);
   //				elKeyword.addContent(elValue);
   //				elDescKeys.addContent(elKeyword);
   //				nbSelectedKeywords++;
   //			}
   //		}
   //		Element elNbTot = new Element("nbtot");
   //		elNbTot.addContent(Integer.toString(nbSelectedKeywords));
   //		elDescKeys.addContent(elNbTot);
   //
   //		return elDescKeys;
   //	}

   /**
    * Add bounding box of keyword if one available.
    *
    * @param kb	The keyword to analyze.
    * @param elKeyword	The XML fragment to update.
    */
   private void addBbox(KeywordDTO kb, Element elKeyword) {
      if (kb.getCoordEast() != null && kb.getCoordWest() != null && kb.getCoordSouth() != null
            && kb.getCoordNorth() != null && !kb.getCoordEast().equals("")
            && !kb.getCoordWest().equals("") && !kb.getCoordSouth().equals("")
            && !kb.getCoordNorth().equals("")) {
         Element elBbox = new Element("geo");
         Element elEast = new Element("east");
         elEast.addContent(kb.getCoordEast());
         Element elWest = new Element("west");
         elWest.addContent(kb.getCoordWest());
         Element elSouth = new Element("south");
         elSouth.addContent(kb.getCoordSouth());
         Element elNorth = new Element("north");
         elNorth.addContent(kb.getCoordNorth());
         elBbox.addContent(elEast);
         elBbox.addContent(elWest);
         elBbox.addContent(elSouth);
         elBbox.addContent(elNorth);
         elKeyword.addContent(elBbox);
      }
   }

   /**
    * @return a collection of descKeys element describing the list of selected keywords
    */
   //	public ArrayList getSelectedKeywordsInDescKeys() {
   //		ArrayList<KeywordDTO> listSelectedKeywords = new ArrayList<KeywordDTO>();
   //		ArrayList listElDescKeys = new ArrayList();
   //
   //		// Get all selected keywords
   //		for (int i=0; i<this.getNbResults(); i++){
   //			KeywordDTO kb = _results.get(i);
   //			if (kb.isSelected()) {
   //				listSelectedKeywords.add(kb);
   //			}
   //		}
   //
   //		// Sort keywords
   //		Collections.sort(listSelectedKeywords, new Comparator() {
   //			// Compare
   //			public int compare(final Object o1, final Object o2) {
   //				final KeywordDTO kw1 = (KeywordDTO) o1;
   //				final KeywordDTO kw2 = (KeywordDTO) o2;
   //				return kw1.getThesaurus().compareToIgnoreCase(kw2.getThesaurus());
   //			}
   //		});
   //
   //		String thesaurusName ="";
   //		Element elDescKeys = null;
   //		Element elKeyTyp = null;
   //		Element elKeyTypCd = null;
   //		Element elThesaName = null;
   //		Element elResTitle = null;
   //		Element elResRefDate = null;
   //		Element elRefDate = null;
   //		Element elRefDateType = null;
   //		Element elDateTypCd = null;
   //
   //        for (KeywordDTO kb : listSelectedKeywords) {
   //            if (!thesaurusName.equals(kb.getThesaurus())) {
   //                if (elDescKeys != null) {
   //                    elKeyTyp.addContent(elKeyTypCd);
   //                    elDescKeys.addContent(elKeyTyp);
   //                    elRefDateType.addContent(elDateTypCd);
   //                    elResRefDate.addContent(elRefDateType);
   //                    elResRefDate.addContent(elRefDate);
   //                    elThesaName.addContent(elResTitle);
   //                    elThesaName.addContent(elResRefDate);
   //                    elDescKeys.addContent(elThesaName);
   //                    listElDescKeys.add(elDescKeys.clone());
   //                }
   //                elDescKeys = new Element("descKeys");
   //                String thesaurusType = kb.getThesaurus();
   //                thesaurusType = thesaurusType.replace('.', '-');
   //                thesaurusType = thesaurusType.split("-")[1];
   //                elKeyTyp = new Element("keyTyp");
   //                elKeyTypCd = new Element("KeyTypCd");
   //                elKeyTypCd.setAttribute("value", thesaurusType);
   //                elThesaName = new Element("thesaName");
   //                elResTitle = new Element("resTitle");
   //                elResTitle.addContent(kb.getThesaurus());
   //                elResRefDate = new Element("resRefDate");
   //                elRefDate = new Element("refDate");
   //                elRefDateType = new Element("refDateType");
   //                elDateTypCd = new Element("DateTypCd");
   //                elDateTypCd.setAttribute("value", "nill");
   //
   //                thesaurusName = kb.getThesaurus();
   //            }
   //            Element elKeyword = new Element("keyword");
   //            elKeyword.addContent(kb.getValue());
   //            if (elDescKeys != null) {
   //                elDescKeys.addContent(elKeyword);
   //            }
   //        }
   //		// add last item
   //		if (elDescKeys!=null){
   //            elKeyTyp.addContent(elKeyTypCd);
   //            elDescKeys.addContent(elKeyTyp);
   //			elRefDateType.addContent(elDateTypCd);
   //			elResRefDate.addContent(elRefDateType);
   //			elResRefDate.addContent(elRefDate);
   //			elThesaName.addContent(elResTitle);
   //			elThesaName.addContent(elResRefDate);
   //			elDescKeys.addContent(elThesaName);
   //			listElDescKeys.add(elDescKeys.clone());
   //		}
   //		return listElDescKeys;
   //	}

   //	public List<KeywordDTO> getSelectedKeywordsInList() {
   //		ArrayList<KeywordDTO> keywords = new ArrayList<KeywordDTO>();
   //		for (int i = 0; i < this.getNbResults(); i++) {
   //			KeywordDTO kb = _results.get(i);
   //			if (kb.isSelected()) {
   //					keywords.add(kb);
   //				}
   //			}
   //		return keywords;
   //	}

   public KeywordDTO existsResult(String id) {
      KeywordDTO keyword = null;
      for (int i = 0; i < getNbResults(); i++) {
         KeywordDTO kb = _results.get(i);
         if (kb.getId() == Integer.parseInt(id)) {
            keyword = kb;
            break;
         }
      }
      return keyword;
   }

}
