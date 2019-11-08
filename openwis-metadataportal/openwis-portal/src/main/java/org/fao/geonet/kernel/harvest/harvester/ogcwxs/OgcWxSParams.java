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

package org.fao.geonet.kernel.harvest.harvester.ogcwxs;

import jeeves.exceptions.BadInputEx;
import jeeves.utils.Util;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.harvester.AbstractParams;
import org.jdom.Element;

//=============================================================================

public class OgcWxSParams extends AbstractParams
{
	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public OgcWxSParams(DataManager dm)
	{
		super(dm);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Create : called when a new entry must be added. Reads values from the
	//---          provided entry, providing default values
	//---
	//---------------------------------------------------------------------------

	public void create(Element node) throws BadInputEx
	{
		super.create(node);

		Element site = node.getChild("site");
		Element opt  = node.getChild("options");

		url       		= Util.getParam(site, "url",  "");
		icon      		= Util.getParam(site, "icon", "");
		ogctype   		= Util.getParam(site, "ogctype", "");
		lang  	  		= Util.getParam(opt, "lang",  "");
		topic  	  		= Util.getParam(opt, "topic",  "");
		createThumbnails= Util.getParam(opt, "createThumbnails",  false);
		useLayer  		= Util.getParam(opt, "useLayer",  false);
		useLayerMd		= Util.getParam(opt, "useLayerMd",  false);
		datasetCategory	= Util.getParam(opt, "datasetCategory",  "");
	}

	//---------------------------------------------------------------------------
	//---
	//--- Update : called when an entry has changed and variables must be updated
	//---
	//---------------------------------------------------------------------------

	public void update(Element node) throws BadInputEx
	{
		super.update(node);

		Element site = node.getChild("site");
		Element opt  = node.getChild("options");

		url       		= Util.getParam(site,  "url",  url);
		icon      		= Util.getParam(site,  "icon", icon);
		ogctype  		= Util.getParam(site,  "ogctype", ogctype);
		
		lang  	  		= Util.getParam(opt, "lang",  lang);
		topic  	  		= Util.getParam(opt, "topic",  topic);
		createThumbnails= Util.getParam(opt, "createThumbnails",  createThumbnails);
		useLayer  		= Util.getParam(opt, "useLayer",  useLayer);
		useLayerMd		= Util.getParam(opt, "useLayerMd",  useLayerMd);
		datasetCategory = Util.getParam(opt, "datasetCategory",  datasetCategory);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Other API methods
	//---
	//---------------------------------------------------------------------------

	public OgcWxSParams copy()
	{
		OgcWxSParams copy = new OgcWxSParams(dm);
		copyTo(copy);

		copy.url  				= url;
		copy.icon 				= icon;
		copy.ogctype		 	= ogctype;
		copy.lang 				= lang;
		copy.topic 				= topic;
		copy.createThumbnails 	= createThumbnails;
		copy.useLayer 			= useLayer;
		copy.useLayerMd 		= useLayerMd;
		copy.datasetCategory    = datasetCategory;
		return copy;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	public String url;
	public String icon;
	public String ogctype;
	public String lang;
	public String topic;
	public String crs = "epsg:4326";
	public boolean createThumbnails;
	public boolean useLayer;
	public boolean useLayerMd;
	public String datasetCategory;
}

//=============================================================================


