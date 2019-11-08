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

package org.fao.geonet.kernel.oaipmh;

import java.util.HashMap;
import java.util.Map;

import jeeves.constants.Jeeves;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.oaipmh.services.GetRecord;
import org.fao.geonet.kernel.oaipmh.services.Identify;
import org.fao.geonet.kernel.oaipmh.services.ListIdentifiers;
import org.fao.geonet.kernel.oaipmh.services.ListMetadataFormats;
import org.fao.geonet.kernel.oaipmh.services.ListRecords;
import org.fao.geonet.kernel.oaipmh.services.ListSets;
import org.fao.geonet.kernel.setting.SettingInfo;
import org.fao.geonet.kernel.setting.SettingManager;
import org.fao.oaipmh.exceptions.BadArgumentException;
import org.fao.oaipmh.exceptions.OaiPmhException;
import org.fao.oaipmh.requests.AbstractRequest;
import org.fao.oaipmh.responses.AbstractResponse;
import org.fao.oaipmh.server.OaiPmhFactory;
import org.fao.oaipmh.util.Lib;
import org.jdom.Element;

//=============================================================================

public class OaiPmhDispatcher
{
	
	public static final int MODE_MODIFIDATE = 2;
	public static final int MODE_TEMPEXTEND = 1;
	
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public OaiPmhDispatcher(SettingManager sm)
	{
		ResumptionTokenCache cache = new ResumptionTokenCache(sm);
		
		register(new GetRecord());
		register(new Identify());
		register(new ListIdentifiers(cache, sm));
		register(new ListMetadataFormats());
		register(new ListRecords(cache, sm));
		register(new ListSets());
	}

	//---------------------------------------------------------------------------

	private void register(OaiPmhService s)
	{
		hmServices.put(s.getVerb(), s);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public Element dispatch(Element request, ServiceContext context)
	{
		Element response = dispatchI(request, context);
		validateResponse(context, response);

		return response;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private method
	//---
	//---------------------------------------------------------------------------

	private Element dispatchI(Element request, ServiceContext context)
	{
		String url = null;

		Map<String, String> params = null;

		SettingInfo si = new SettingInfo(context);

		try
		{
			url    = si.getSiteUrl() + context.getBaseUrl() +"/"+ Jeeves.Prefix.SERVICE +"/en/"+ context.getService();
			params = OaiPmhFactory.extractParams(request);

			AbstractRequest  req = OaiPmhFactory.parse(params);
			OaiPmhService    srv = hmServices.get(req.getVerb());
			AbstractResponse res = srv.execute(req, context);

			Element response = res.toXml();

			return Lib.createOaiRoot(url, params, response);
		}

		catch(OaiPmhException e)
		{
			return OaiPmhException.marshal(e, url, params);
		}

		catch(Exception e)
		{
			context.info("Exception stack trace : \n"+ Util.getStackTrace(e));

			//--- we should use another exception type but we don't have a specific
			//--- type to handle internal errors

			BadArgumentException ex = new BadArgumentException("Internal error : "+ e.getMessage());

			return OaiPmhException.marshal(ex, url, params);
		}
	}

	//---------------------------------------------------------------------------

	private void validateResponse(ServiceContext context, Element response)
	{
		String schema = context.getAppPath() + Geonet.SchemaPath.OAI_PMH;

		context.debug("Validating against : "+ schema);

		try
		{
			Xml.validate(schema, response);
		}
		catch (Exception e)
		{
			context.warning("OAI-PMH response does not validate : "+e.getMessage());
		}
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private HashMap<String, OaiPmhService> hmServices = new HashMap<String, OaiPmhService>();
}

//=============================================================================

