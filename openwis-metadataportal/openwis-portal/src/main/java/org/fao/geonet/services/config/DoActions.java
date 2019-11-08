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

package org.fao.geonet.services.config;

import jeeves.constants.Jeeves;
import jeeves.exceptions.OperationAbortedEx;
import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.setting.SettingInfo;
import org.jdom.Element;

//=============================================================================

public class DoActions implements Service
{
	//--------------------------------------------------------------------------
	//---
	//--- Init
	//---
	//--------------------------------------------------------------------------

	@Override
   public void init(String appPath, ServiceConfig params) throws Exception {}

	//--------------------------------------------------------------------------
	//---
	//--- Service
	//---
	//--------------------------------------------------------------------------

	@Override
   public Element exec(Element params, ServiceContext context) throws Exception
	{

		if (params.getText().equals("ok")) {
			doActions(context);
		} else {
			// else what? Exceptions don't get this far so must be "ok" response
			throw new OperationAbortedEx("DoActions received unexpected request: "+Xml.getString(params));
		}

		return new Element(Jeeves.Elem.RESPONSE).setText("ok");
	}

	//--------------------------------------------------------------------------
	//---
	//--- doActions - do any immediate actions resulting from changes to settings
	//---
	//--------------------------------------------------------------------------

	// do any immediate actions resulting from changes to settings
	private void doActions(ServiceContext context) throws Exception {
		GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		DataManager		dataMan = gc.getDataManager();
		SettingInfo si = new SettingInfo(gc.getSettingManager());

		try {
			if (si.getIndexOptimizerSchedulerEnabled()) {
				dataMan.rescheduleOptimizer(si.getIndexOptimizerSchedulerAt(), si.getIndexOptimizerSchedulerInterval());
			} else {
				dataMan.disableOptimizer();
			}
		} catch (Exception e) {
			e.printStackTrace();
         throw new OperationAbortedEx("Parameters saved but cannot restart Index Optimizer: "
               + e.getMessage());
		}

		// should also restart the Z server?
	}

}

//=============================================================================

