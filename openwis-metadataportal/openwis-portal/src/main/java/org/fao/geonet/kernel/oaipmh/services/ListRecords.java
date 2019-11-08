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

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.oaipmh.ResumptionTokenCache;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.oaipmh.exceptions.CannotDisseminateFormatException;
import org.fao.oaipmh.exceptions.IdDoesNotExistException;
import org.fao.oaipmh.requests.ListRecordsRequest;
import org.fao.oaipmh.requests.TokenListRequest;
import org.fao.oaipmh.responses.ListRecordsResponse;
import org.fao.oaipmh.responses.Record;
import org.fao.oaipmh.util.SearchResult;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.model.category.Category;

//=============================================================================

/**
 * ListRecords operation. <P>
 */
public class ListRecords extends AbstractTokenLister {

   /**
    * Default constructor.
    * Builds a ListRecords.
    * @param cache
    * @param sm
    */
   public ListRecords(ResumptionTokenCache cache, SettingManager sm) {
      super(cache, sm);
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.oaipmh.services.AbstractTokenLister#getVerb()
    */
   public String getVerb() {
      return ListRecordsRequest.VERB;
   }

   /**
    * {@inheritDoc}
    * @see org.fao.geonet.kernel.oaipmh.services.AbstractTokenLister#processRequest(org.fao.oaipmh.requests.TokenListRequest, int, org.fao.oaipmh.util.SearchResult, jeeves.server.context.ServiceContext)
    */
   public ListRecordsResponse processRequest(TokenListRequest req, int pos, SearchResult result,
         ServiceContext context, Category category) throws Exception {

      int num = 0;
      ListRecordsResponse res = new ListRecordsResponse();

      // Get a DBMS for the request
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      // Loop to retrieve metadata

      while (num < OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.OAI_MAX_RECORDS) && pos < result.getUrns().size()) {
         String urn = result.getUrns().get(pos);

         Record r = buildRecord(context, dbms, urn, result.prefix, category);

         // FIXME possible bug if record is null. We should flag the metadata as invalid record 
         // in order to take it into account in the resumption token. 
         if (r != null) {
            res.addRecord(r);
            num++;
         }

         pos++;
      }

      return res;

   }
   
   
   /**
    * Build a record for the given metadata URN.
    * 
    * @param context
    * @param dbms
    * @param urn
    * @param prefix
    * @return
    * @throws Exception
    */
   private Record buildRecord(ServiceContext context, Dbms dbms, String urn, String prefix, Category category)
         throws Exception {

      // have to catch exceptions and return null because this function can
      // be called several times for a list of MD records
      // and we do not want to stop because of one error
      try {
         return GetRecord.buildRecord(context, dbms, urn, prefix, category);
      } catch (IdDoesNotExistException e) {
         return null;
      } catch (CannotDisseminateFormatException e2) {
         return null;
      } catch (Exception e3) {
         throw e3;
      }
   }
}

//=============================================================================

