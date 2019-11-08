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

package org.fao.oaipmh;

import org.jdom.Namespace;

//=============================================================================

public class OaiPmh
{
	//---------------------------------------------------------------------------
	//---
	//--- Constants
	//---
	//---------------------------------------------------------------------------

	public static final String SCHEMA_LOCATION = "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
	public static final String OAI_DC_SCHEMA_LOCATION = "http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd";
	public static final String ROOT_NAME       = "OAI-PMH";

   public static final String OAI_HARVESTER_LOGGER = "geonetwork.oai.provider";

	//---------------------------------------------------------------------------

	public static class Namespaces
	{
		public static final Namespace OAI_PMH= Namespace.getNamespace(          "http://www.openarchives.org/OAI/2.0/");
		public static final Namespace OAI_DC = Namespace.getNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
		public static final Namespace DC     = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
		public static final Namespace XSI    = Namespace.getNamespace("xsi",    "http://www.w3.org/2001/XMLSchema-instance");
	}
}

//=============================================================================

