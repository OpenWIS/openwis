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

package org.fao.geonet.guiservices.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.group.GroupManager;
import org.openwis.metadataportal.model.group.Group;

//=============================================================================

/** Service used to return all categories in the system
  */

public class GetLatestUpdated implements Service
{
	private int     _maxItems;
	private long    _timeBetweenUpdates;

	private Element _response;
	private long    _lastUpdateTime;

	//--------------------------------------------------------------------------
	//---
	//--- Init
	//---
	//--------------------------------------------------------------------------

	public void init(String appPath, ServiceConfig config) throws Exception
	{
		String sMaxItems           = config.getValue("maxItems",           "10");
		String sTimeBetweenUpdates = config.getValue("timeBetweenUpdates", "60");

		_maxItems           = Integer.parseInt(sMaxItems);
		_timeBetweenUpdates = Integer.parseInt(sTimeBetweenUpdates) * 1000;
	}

	//--------------------------------------------------------------------------
	//---
	//--- Service
	//---
	//--------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
    public Element exec(Element params, ServiceContext context) throws Exception
	{
		if (System.currentTimeMillis() > _lastUpdateTime + _timeBetweenUpdates) {

		    GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
		    Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

			UserSession us = context.getUserSession();
	        List<Group> groups = null;
	        GroupManager gm = new GroupManager(dbms);
	        
	        if (us.isAuthenticated()) {
	            if (us.getProfile().equals(Geonet.Profile.ADMINISTRATOR)) {
	                groups = gm.getAllGroups();
	            } else {
	                groups = gm.getAllUserGroups(us.getUserId());
	            }
	        } else {
	            groups = new ArrayList<Group>();
	            groups.add(new Group(-1, Geonet.Profile.GUEST));
	        }
			
			// only get public metadata (group 1: internet) viewable (operation O: view)

			// FIXME might be a specific OpenWIS service returning JSON data 
			// and calling the DataPolicyManager ... ??? Using StringBuffer...
			String query = "SELECT DISTINCT Metadata.id, Metadata.changeDate FROM Metadata, DataPolicy, OperationAllowed " +
					"WHERE Metadata.datapolicy=DataPolicy.id AND OperationAllowed.datapolicyId=DataPolicy.id " +
					"AND Metadata.isTemplate='n' AND OperationAllowed.operationId=0 AND (";
			
			String aux = "";

			for (Group group : groups) {
				aux += " OR groupId="+group.getId().toString();
			}

			query += aux.substring(4);
			query += ") ORDER BY changeDate DESC";

			Element result = dbms.select(query);

			_response = new Element("response");
			int numItems = 0;

			for (Iterator<Element> iter = result.getChildren().iterator(); iter.hasNext() && numItems++ < _maxItems; )
			{
				Element rec = iter.next();
				String  id = rec.getChildText("id");

				Element md = gc.getDataManager().getMetadata(context, id, false);
				_response.addContent(md);
			}
			_lastUpdateTime = System.currentTimeMillis();
		}
		return (Element)_response.clone();
	}
}

//=============================================================================

