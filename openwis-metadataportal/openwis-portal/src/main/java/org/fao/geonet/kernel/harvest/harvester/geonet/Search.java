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

package org.fao.geonet.kernel.harvest.harvester.geonet;

import jeeves.exceptions.BadParameterEx;
import jeeves.utils.Util;
import org.fao.geonet.lib.Lib;
import org.jdom.Element;

//=============================================================================

class Search
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public Search() {}

	//---------------------------------------------------------------------------

	public Search(Element search) throws BadParameterEx
	{
		freeText = Util.getParam(search, "freeText", "");
		title    = Util.getParam(search, "title",    "");
		abstrac  = Util.getParam(search, "abstract", "");
		keywords = Util.getParam(search, "keywords", "");
		digital  = Util.getParam(search, "digital",  false);
		hardcopy = Util.getParam(search, "hardcopy", false);

		Element source = search.getChild("source");

		sourceUuid = Util.getParam(source, "uuid", "");
		sourceName = Util.getParam(source, "name", "");
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public Search copy()
	{
		Search s = new Search();

		s.freeText  = freeText;
		s.title     = title;
		s.abstrac   = abstrac;
		s.keywords  = keywords;
		s.digital   = digital;
		s.hardcopy  = hardcopy;
		s.sourceUuid= sourceUuid;
		s.sourceName= sourceName;

		return s;
	}

	//---------------------------------------------------------------------------

	public Element createRequest()
	{
		Element req = new Element("request");

		add(req, "any",      freeText);
		add(req, "title",    title);
		add(req, "abstract", abstrac);
		add(req, "themekey", keywords);
		add(req, "siteId",   sourceUuid);

		if (digital)
			Lib.element.add(req, "digital", "on");

		if (hardcopy)
			Lib.element.add(req, "paper", "on");

		return req;
	}

	//---------------------------------------------------------------------------

	public static Search createEmptySearch() throws BadParameterEx
	{
		return new Search(new Element("search"));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void add(Element req, String name, String value)
	{
		if (value.length() != 0)
			req.addContent(new Element(name).setText(value));
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	public String  freeText;
	public String  title;
	public String  abstrac;
	public String  keywords;
	public boolean digital;
	public boolean hardcopy;
	public String  sourceUuid;
	public String  sourceName;
}

//=============================================================================


