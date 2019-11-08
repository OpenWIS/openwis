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

import java.util.Iterator;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.Thesaurus;
import org.fao.geonet.kernel.ThesaurusManager;
import org.jdom.Element;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.thesaurus.dto.BroadNarrListDTO;
import org.openwis.metadataportal.services.thesaurus.dto.KeywordDTO;

//=============================================================================

/**
 * Update the information of a thesaurus
 */

public class UpdateElement implements Service {
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   // --------------------------------------------------------------------------
   // ---
   // --- Service
   // ---
   // --------------------------------------------------------------------------

   public Element exec(Element params, ServiceContext context) throws Exception {
      KeywordDTO updateKeywordDTO = JeevesJsonWrapper.read(params, KeywordDTO.class);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

      String thesaurusRef = updateKeywordDTO.getThesaurus();
      String namespace = updateKeywordDTO.getNameSpaceCode();
      String oldid = updateKeywordDTO.getRelativeCode();
      String newid = String.valueOf(updateKeywordDTO.getId());
      String thesaType = updateKeywordDTO.getType();
      String prefLab = updateKeywordDTO.getValue();
      String lang = context.getLanguage();
      String definition = updateKeywordDTO.getDefinition();
      BroadNarrListDTO broadNarrListDTO = updateKeywordDTO.getBroadNarrListDTO();
      BroadNarrListDTO delBroadNarrListDTO = updateKeywordDTO.getDelBroadNarrListDTO();

      ThesaurusManager manager = gc.getThesaurusManager();
      Thesaurus thesaurus = manager.getThesaurusByName(thesaurusRef);

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);

      if (!(oldid.equals(newid))) {
         if (thesaurus.isFreeCode(namespace, newid)) {
            thesaurus.updateCode(namespace, oldid, newid);
         } else {
            acknowledgementDTO = new AcknowledgementDTO(false,
                  "Code value already exists in thesaurus");
            return JeevesJsonWrapper.send(acknowledgementDTO);
         }
      }

      if (thesaType.equals("place")) {
         String east = updateKeywordDTO.getCoordEast();
         String west = updateKeywordDTO.getCoordWest();
         String south = updateKeywordDTO.getCoordSouth();
         String north = updateKeywordDTO.getCoordNorth();
         thesaurus.updateElement(namespace, newid, prefLab, definition, east, west, south, north,
               lang);
      } else {
         thesaurus.updateElement(namespace, newid, prefLab, definition, lang);
      }

      if (delBroadNarrListDTO != null && delBroadNarrListDTO.getKeywordListDTO() != null && !delBroadNarrListDTO.getKeywordListDTO().isEmpty()) {
         for (Iterator iterator = delBroadNarrListDTO.getKeywordListDTO().iterator(); iterator.hasNext();) {
            KeywordDTO narrower = (KeywordDTO) iterator.next();
            namespace = narrower.getNameSpaceCode();
            if (namespace == null || namespace.trim().equals("")) {
               namespace = "#";
            }
            thesaurus.removeBnElement(namespace, newid, narrower.getRelativeCode());
         }
      }

     

      if (broadNarrListDTO != null && broadNarrListDTO.getKeywordListDTO() != null && !broadNarrListDTO.getKeywordListDTO().isEmpty()) {
         for (Iterator iterator = broadNarrListDTO.getKeywordListDTO().iterator(); iterator
               .hasNext();) {
            KeywordDTO narrower = (KeywordDTO) iterator.next();
            namespace = narrower.getNameSpaceCode();
            if (namespace == null || namespace.trim().equals("")) {
               namespace = "#";
            }
            thesaurus.updateBnElement(namespace, newid, narrower.getRelativeCode());
         }
      }

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }
}

// =============================================================================

