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

import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.oaipmh.Lib;
import org.fao.geonet.kernel.oaipmh.ResumptionTokenCache;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.oaipmh.requests.ListIdentifiersRequest;
import org.fao.oaipmh.requests.TokenListRequest;
import org.fao.oaipmh.responses.Header;
import org.fao.oaipmh.responses.ListIdentifiersResponse;
import org.fao.oaipmh.responses.ListResponse;
import org.fao.oaipmh.util.ISODate;
import org.fao.oaipmh.util.SearchResult;
import org.jdom.Element;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.metadata.DeletedMetadataManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.metadata.DeletedMetadata;

//=============================================================================

/**
 * ListIdentifiers operation. <P>
 */
public class ListIdentifiers extends AbstractTokenLister
{
	/**
	 * Default constructor.
	 * Builds a ListIdentifiers.
	 * @param cache
	 * @param sm
	 */
	public ListIdentifiers(ResumptionTokenCache cache, SettingManager sm) {
		super(cache, sm);
	}

	/**
	 * {@inheritDoc}
	 * @see org.fao.geonet.kernel.oaipmh.services.AbstractTokenLister#getVerb()
	 */
	public String getVerb() { return ListIdentifiersRequest.VERB; }

	/**
	 * {@inheritDoc}
	 * @see org.fao.geonet.kernel.oaipmh.services.AbstractTokenLister#processRequest(org.fao.oaipmh.requests.TokenListRequest, int, org.fao.oaipmh.util.SearchResult, jeeves.server.context.ServiceContext)
	 */
	public ListResponse processRequest(TokenListRequest req, int pos, SearchResult result, ServiceContext context, Category category) throws Exception  {

		//--- loop to retrieve metadata		
		ListIdentifiersResponse res = new ListIdentifiersResponse();
		
		// Get a DBMS for the request
		Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
		
		// Get an application path
		String appPath = context.getAppPath();

		int num = 0;

		while (num<OpenwisMetadataPortalConfig.getInt(ConfigurationConstants.OAI_MAX_RECORDS) && pos < result.getUrns().size())
		{
			String urn = result.getUrns().get(pos);

			Header h = buildHeader(dbms, urn, result.prefix, appPath, category);

			if (h != null) {
				res.addHeader(h);
				num++;
			}

			pos++;
		}

		return res;

	}

	/**
	 * Build a header for ListIdentifiers response.
	 * @param dbms
	 * @param urn
	 * @param prefix
	 * @param appPath
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
   private static Header buildHeader(Dbms dbms, String urn, String prefix, String appPath, Category category) throws Exception
	{
	   // Check if it's a deleted metadata or metadata removed from a category
	   DeletedMetadata dm = new DeletedMetadataManager(dbms).getDeletedMetadataByUrn(urn, category);
      if (dm != null) {
         return buildHeaderFromDeletedMetadata(dbms, dm, prefix, appPath);
      }
	   
		String query = "SELECT schemaId, localimportdate FROM Metadata WHERE uuid=?";
		List<Element> list = dbms.select(query, urn).getChildren();
		if (list.isEmpty()) {
		   return null;
		}
		
		// Build the header from metadata
		Element rec = list.get(0);
		String schema     = rec.getChildText("schemaid");
		String localImportDate = rec.getChildText("localimportdate");

		// Try to disseminate format if not by schema then by conversion
		if (!prefix.equals(schema)) {
			if (!Lib.existsConverter(schema, appPath, prefix)) {
				return null;
			}
		}

		// Build the header and set some infos
		Header h = new Header();
		h.setIdentifier(urn);
		h.setDateStamp(new ISODate(localImportDate));

		// Add the category (here called sets)
		String categoryName = null;
		if (category == null || category.getId() == null) {
         CategoryManager cm = new CategoryManager(dbms);
         Category cat = cm.getCategoryByMetadataUrn(urn);
         if (cat != null) {
            categoryName = cat.getName();
         }
		} else {
		   // the category is given by the request itself
		   categoryName = category.getName();
		}
		h.addSet(categoryName);

		return h;
	}
	
}

//=============================================================================

