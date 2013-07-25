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
import org.fao.geonet.kernel.Thesaurus;
import org.fao.geonet.kernel.ThesaurusManager;
import org.jdom.Element;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.thesaurus.dto.KeywordDTO;
import org.openwis.metadataportal.services.thesaurus.dto.KeywordsDTO;

import java.io.File;
import java.util.Iterator;

//=============================================================================

/** Removes a thesaurus from the system.
  */

public class Delete implements Service {
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   //--------------------------------------------------------------------------
   //---
   //--- Service
   //---
   //--------------------------------------------------------------------------

   public Element exec(Element params, ServiceContext context) throws Exception {
      KeywordsDTO deleteList = JeevesJsonWrapper.read(params, KeywordsDTO.class);

      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      ThesaurusManager manager = gc.getThesaurusManager();

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);

      for (Iterator<KeywordDTO> iterator = deleteList.getKeywordListDTO().iterator(); iterator
            .hasNext();) {
         KeywordDTO keywordDTO = (KeywordDTO) iterator.next();
         Thesaurus thesaurus = manager.getThesaurusByName(keywordDTO.getThesaurus());
         File item = thesaurus.getFile();
         // Remove old file from thesaurus manager
         manager.remove(keywordDTO.getThesaurus());
         // Remove file
         if (item.exists()) {
            item.delete();
         }

         else {
            acknowledgementDTO = new AcknowledgementDTO(false, "Thesaurus not found --> "
                  + keywordDTO.getThesaurus());
            return JeevesJsonWrapper.send(acknowledgementDTO);
         }
      }

      return JeevesJsonWrapper.send(acknowledgementDTO);
   }
}

//=============================================================================

