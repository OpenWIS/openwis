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

package org.fao.geonet.services.main;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.fao.geonet.kernel.search.ISearchManager.Searcher;
import org.fao.geonet.kernel.search.MetaSearcher;
import org.fao.geonet.services.util.SearchDefaults;
import org.jdom.Element;

//=============================================================================

/** main.search service. Perform a search
  */

public class Search implements Service {
   private ServiceConfig _config;

   //--------------------------------------------------------------------------
   //---
   //--- Init
   //---
   //--------------------------------------------------------------------------

   @Override
   public void init(String appPath, ServiceConfig config) throws Exception {
      _config = config;
   }

   //--------------------------------------------------------------------------
   //---
   //--- Service
   //---
   //--------------------------------------------------------------------------

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      UserSession session = context.getUserSession();

      ISearchManager searchMan = gc.getSearchmanager();

      Element elData = SearchDefaults.getDefaultSearch(session, params);
      String sRemote = elData.getChildText(Geonet.SearchResult.REMOTE);
      boolean remote = sRemote != null && sRemote.equals(Geonet.Text.ON);

      Element title = elData.getChild(Geonet.SearchResult.TITLE);
      Element abstr = elData.getChild(Geonet.SearchResult.ABSTRACT);
      Element any = elData.getChild(Geonet.SearchResult.ANY);
      Element phrase = elData.getChild(Geonet.SearchResult.PHRASE);
      Element or = elData.getChild(Geonet.SearchResult.OR);
      Element without = elData.getChild(Geonet.SearchResult.WITHOUT);
      Element all = elData.getChild(Geonet.SearchResult.ALL);

      // Parse bbox & assign to four *BL params
      Element bbox = elData.getChild(Geonet.SearchResult.BBOX);
      Element westBL = elData.getChild(Geonet.SearchResult.WEST_BL);
      Element southBL = elData.getChild(Geonet.SearchResult.SOUTH_BL);
      Element eastBL = elData.getChild(Geonet.SearchResult.EAST_BL);
      Element northBL = elData.getChild(Geonet.SearchResult.NORTH_BL);

      if (bbox != null && westBL == null && southBL == null && eastBL == null && northBL == null) {
         String bounds[] = bbox.getText().split(",");
         if (bounds.length == 4) {
            elData.addContent(new Element(Geonet.SearchResult.WEST_BL).addContent(bounds[0]));
            elData.addContent(new Element(Geonet.SearchResult.SOUTH_BL).addContent(bounds[1]));
            elData.addContent(new Element(Geonet.SearchResult.EAST_BL).addContent(bounds[2]));
            elData.addContent(new Element(Geonet.SearchResult.NORTH_BL).addContent(bounds[3]));
         }
      }

      // perform the search and save search query into session
      MetaSearcher searcher;

      context.info("Creating searchers");

      if (remote) {
         searcher = searchMan.newSearcher(Searcher.Z3950);
      } else {
         searcher = searchMan.newSearcher(Searcher.INDEX);
      }

      searcher.search(context, elData, _config);
      session.setProperty(Geonet.Session.SEARCH_RESULT, searcher);

      context.info("Getting summary");

      return searcher.getSummary();
   }
}

//=============================================================================

