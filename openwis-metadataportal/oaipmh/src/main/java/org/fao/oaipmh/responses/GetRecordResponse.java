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

package org.fao.oaipmh.responses;

import org.fao.oaipmh.OaiPmh;
import org.fao.oaipmh.requests.GetRecordRequest;
import org.jdom.Element;

//=============================================================================

public class GetRecordResponse extends AbstractResponse
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public GetRecordResponse() {}

	//---------------------------------------------------------------------------

	public GetRecordResponse(Element response)
	{
		super(response);
		build(response);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public Record getRecord() { return record; }

	//---------------------------------------------------------------------------

	public void setRecord(Record r)
	{
		record = r;
	}

	//---------------------------------------------------------------------------

	public Element toXml()
	{
		Element root = new Element(GetRecordRequest.VERB, OaiPmh.Namespaces.OAI_PMH);

		root.addContent(record.toXml());

		return root;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void build(Element response)
	{
		Element getRec = response.getChild("GetRecord", OaiPmh.Namespaces.OAI_PMH);
		Element record = getRec  .getChild("record",    OaiPmh.Namespaces.OAI_PMH);

		this.record = new Record(record);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private Record record;
}

//=============================================================================

