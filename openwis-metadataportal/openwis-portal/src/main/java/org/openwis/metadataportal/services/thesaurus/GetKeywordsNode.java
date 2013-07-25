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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.ThesaurusManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.thesaurus.KeywordsSearcher;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.thesaurus.dto.KeywordDTO;
import org.openwis.metadataportal.services.thesaurus.dto.NodeDTO;

//=============================================================================

/**
 * 
 * Retrieve Node.
 * 
 * @author mcoudert
 *
 */
public class GetKeywordsNode implements Service {
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   // --------------------------------------------------------------------------
   // ---
   // --- Service
   // ---
   // --------------------------------------------------------------------------

   public Element exec(Element params, ServiceContext context) throws Exception {
      String nodeId = Util.getParam(params, "nodeId");
      //String nodeUri = Util.getParam(params, "nodeUri");
      String thesRef = Util.getParam(params, "thesRef");

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ThesaurusManager thesaurusMan = gc.getThesaurusManager();
      KeywordsSearcher searcherBNR = new KeywordsSearcher(thesaurusMan);
      List<NodeDTO> list = new ArrayList<NodeDTO>();
      List<KeywordDTO> keyWords = new ArrayList<KeywordDTO>();
      // Node is root
      if (nodeId.equals("0")) {
         searcherBNR.searchParents("", thesRef, context.getLanguage());
         keyWords = searcherBNR.getResults();
      }
      else
      {
         String code = Util.getParam(params, "code");
         searcherBNR.searchChildren(code, thesRef, context.getLanguage());
         searcherBNR.sortResults("label");
         keyWords = searcherBNR.getResults();
      }
      for (Iterator<KeywordDTO> iterator = keyWords.iterator(); iterator.hasNext();) {
         KeywordDTO keywordDTO = (KeywordDTO) iterator.next();
         NodeDTO nodeDto = new NodeDTO();
         nodeDto.setKeyword(keywordDTO);
         list.add(nodeDto);
      }

      return JeevesJsonWrapper.send(list);
   }
}
