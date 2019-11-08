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

package org.fao.geonet.services.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jeeves.constants.Jeeves;
import jeeves.server.UserSession;
import jeeves.utils.Util;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;

/**
 *
 * Get default params info from session and/or request
 * and process text parameters.
 *
 * @author fxprunayre
 *
 */
public class SearchDefaults {
   /**
    * Default params for search
    */
   private static final Element DEFAULT_PARAMS = new Element(Jeeves.Elem.REQUEST);

   /**
    * Parameter name used to determine if session default should be included with the result parameters.
    * (see #BOM-13).
    */
   private static final String USE_SESSION_DEFAULTS = "useSessionDefaults";


   static {
      Map<String, String> map = new HashMap<String, String>();
      map.put(Geonet.SearchResult.RELATION, Geonet.SearchResult.Relation.OVERLAPS);
      map.put(Geonet.SearchResult.EXTENDED, Geonet.Text.OFF);
      map.put(Geonet.SearchResult.REMOTE, Geonet.Text.OFF);
      map.put(Geonet.SearchResult.TIMEOUT, "20");
      map.put(Geonet.SearchResult.HITS_PER_PAGE, "10");
      map.put(Geonet.SearchResult.SIMILARITY, ".8");
      map.put(Geonet.SearchResult.OUTPUT, Geonet.SearchResult.Output.FULL);
      map.put(Geonet.SearchResult.SORT_BY, Geonet.SearchResult.SortBy.RELEVANCE);
      map.put(Geonet.SearchResult.SORT_ORDER, "");
      map.put(Geonet.SearchResult.INTERMAP, Geonet.Text.ON);

      Element child;
      for (Entry<String, String> entry : map.entrySet()) {
         child = new Element(entry.getKey());
         child.setText(entry.getValue());
         DEFAULT_PARAMS.addContent(child);
      }
   }

   /**
    * Returns default values for the search parameters. If request params are
    * set, they're used. If parameters have changed in the user session, they
    * are read out here.
    *
    * @param srvContext the service context
    * @param request the request
    * @return the default search
    */
   public static Element getDefaultSearch(UserSession session, Element request) {
      Element result = new Element(Jeeves.Elem.REQUEST);

      // Add Request
      result = merge(result, request);

      // Add session
      if (session != null) {

         // If the session default parameters is missing or is set to 'true', 'on' or 'yes', merge the stored
         // session parameters with the other parameters.

         if (BooleanUtils.toBoolean(Util.getParam(request, USE_SESSION_DEFAULTS, Boolean.TRUE.toString()))) {
            Element sessionElement = (Element) session.getProperty(Geonet.Session.MAIN_SEARCH);
            // Set params in session for future use
            result = merge(result, sessionElement);
         }
      }

      // Add default params
      result = merge(result, DEFAULT_PARAMS);

      // Update session
      if (session != null) {
         session.setProperty(Geonet.Session.MAIN_SEARCH, result);
      }
      return result;
   }

   /**
    * Merge.
    *
    * @param left the left element
    * @param right the right element
    * @return the merged element
    */
   @SuppressWarnings("unchecked")
   private static Element merge(Element left, Element right) {
      Element result;
      if (right == null) {
         result = (Element) left.clone();
      } else if (left == null) {
         result = right;
      } else {
         String text;

         result = new Element(left.getName());
         text = left.getText();

         if (text != null) {
            result.setText(text);
         } else {
            result.setText(right.getText());
         }

         // Left Children
         List<Element> rightChildren = new ArrayList<Element>();
         Element rightChild;
         Element child;
         for (Element leftChild : (List<Element>) left.getChildren()) {
            rightChild = right.getChild(leftChild.getName());
            if (rightChild != null) {
               rightChildren.add(rightChild);
            }

            child = merge(leftChild, rightChild);
            result.addContent(child);
         }

         // Remaining right children
         for (Element righChild : (List<Element>) right.getChildren()) {
            if (!rightChildren.contains(righChild)) {
               result.addContent((Element) righChild.clone());
            }
         }
      }

      return result;
   }
}
