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

package org.fao.geonet.kernel.oaipmh.services;

import java.text.MessageFormat;
import java.util.Map;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.oaipmh.OaiPmhService;
import org.fao.oaipmh.exceptions.BadResumptionTokenException;
import org.fao.oaipmh.requests.AbstractRequest;
import org.fao.oaipmh.requests.ListSetsRequest;
import org.fao.oaipmh.responses.AbstractResponse;
import org.fao.oaipmh.responses.ListSetsResponse;
import org.fao.oaipmh.responses.SetInfo;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================

public class ListSets implements OaiPmhService {
   public String getVerb() {
      return ListSetsRequest.VERB;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Service
   //---
   //---------------------------------------------------------------------------

   public AbstractResponse execute(AbstractRequest request, ServiceContext context)
         throws Exception {
      ListSetsRequest req = (ListSetsRequest) request;
      ListSetsResponse res = new ListSetsResponse();

      //--- we don't provide streaming for sets

      if (req.getResumptionToken() != null)
         throw new BadResumptionTokenException(req.getResumptionToken());

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      CategoryManager cm = new CategoryManager(dbms);
      Map<Category, Integer> categs = cm.getAllCategoriesAndMetadataCount();
      for (Category cat : categs.keySet()) {
         int count = categs.get(cat);
         res.addSet(new SetInfo(cat.getName(), cat.getName(), MessageFormat.format(
               "This set contains {0} records.", count)));
      }
      
      return res;
   }
}

//=============================================================================

