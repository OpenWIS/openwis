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
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.ThesaurusManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.thesaurus.KeywordsSearcher;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.thesaurus.dto.EditElementDTO;
import org.openwis.metadataportal.services.thesaurus.dto.BroadNarrListDTO;
import org.openwis.metadataportal.services.thesaurus.dto.KeywordDTO;

import java.util.ArrayList;

//=============================================================================

/**
 * For editing : adds a tag to a thesaurus. Access is restricted
 */

public class EditElement implements Service {
	public void init(String appPath, ServiceConfig params) throws Exception {
	}

	// --------------------------------------------------------------------------
	// ---
	// --- Service
	// ---
	// --------------------------------------------------------------------------

	public Element exec(Element params, ServiceContext context)
			throws Exception {
	   KeywordDTO keywordDTO = JeevesJsonWrapper.read(params, KeywordDTO.class);

		EditElementDTO editElementDTO = new EditElementDTO();
		editElementDTO.setKeywordRef(keywordDTO);

		GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ThesaurusManager thesaurusMan = gc.getThesaurusManager();
      KeywordsSearcher searcherBNR = new KeywordsSearcher(thesaurusMan);

      String ref      = keywordDTO.getThesaurus();
      String lang    = context.getLanguage();
		// Only if consult (ie. external thesaurus) search for related concept
//		if (!keywordDTO.isEdition()){
		   String uri     = keywordDTO.getRelativeCode();
			ArrayList<String> reqType = new ArrayList<String>();
			reqType.add("broader");
			reqType.add("narrower");
			reqType.add("related");

			for (int i = 0; i <= reqType.size() - 1; i++) {
				searcherBNR.searchBN(uri, ref, reqType.get(i), lang);
			
				searcherBNR.sortResults("label");
				String type;
				
				if(reqType.get(i).equals("broader"))		// If looking for broader search concept in a narrower element
					type = "narrower";
				else if(reqType.get(i).equals("narrower"))
					type = "broader";
				else 
					type = "related";
				
				BroadNarrListDTO broadNarrListDTO = new BroadNarrListDTO();
				broadNarrListDTO.setKeywordType(type);
				broadNarrListDTO.setKeywordListDTO(searcherBNR.getResults());
				editElementDTO.getBroadNarrListDTO().add(broadNarrListDTO);
			}
			
			searcherBNR = null;
//		}
//		else
//		{
//		   // All element
//		   searcherBNR.searchAll(ref, lang);
//       searcherBNR.sortResults("label");
//       editElementDTO.setAllKeywordsDTO(searcherBNR.getResults());
//		   editElementDTO.setEditElementLegendDTO(new ArrayList<EditElementLegendDTO>());
//		}

		return JeevesJsonWrapper.send(editElementDTO);
	}
}
