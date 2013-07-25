//=============================================================================
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

package org.openwis.metadataportal.services.thesaurus;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.ThesaurusManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.thesaurus.KeywordsSearcher;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.thesaurus.dto.SearchDTO;

/**
 * Returns a list of keywords given a list of thesaurus
 */

public class GetKeywords implements Service {
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   // --------------------------------------------------------------------------
   // ---
   // --- Service
   // ---
   // --------------------------------------------------------------------------

   public Element exec(Element params, ServiceContext context) throws Exception {
      SearchDTO searchDTO = JeevesJsonWrapper.read(params, SearchDTO.class);

      KeywordsSearcher searcher = null;

      // perform the search and save search result into session
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ThesaurusManager thesaurusMan = gc.getThesaurusManager();

      Log.debug("KeywordsManager", "Creating new keywords searcher");
      searcher = new KeywordsSearcher(thesaurusMan);
      searcher.search(context, searchDTO);
      searcher.sortResults("label");

      return JeevesJsonWrapper.send(searcher.getResults());
   }
}

// =============================================================================

