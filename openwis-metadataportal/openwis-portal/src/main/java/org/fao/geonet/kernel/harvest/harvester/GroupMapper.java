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

package org.fao.geonet.kernel.harvest.harvester;

import java.util.HashMap;
import java.util.List;

import jeeves.resources.dbms.Dbms;

import org.jdom.Element;

//=============================================================================

/**
 * Loads all groups from the database and create a mapping. <P>
 * (group name) -> (group ID) <P>
 * 
 * @deprecated
 * 
 */
public class GroupMapper
{
	/**
	 * Default constructor.
	 * Builds a GroupMapper.
	 * @param dbms
	 * @throws Exception
	 */
	public GroupMapper(Dbms dbms) throws Exception
	{
		String query = "SELECT id, name FROM Groups";

		List idsList = dbms.select(query).getChildren();

        for (Object anIdsList : idsList) {
            Element record = (Element) anIdsList;

            String id = record.getChildText("id");
            String name = record.getChildText("name");

            add(name, id);
        }
	}

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public void add(String name, String id)
	{
		hmNameId.put(name, id);
		hmIdName.put(id, name);
	}

	//--------------------------------------------------------------------------

	/** Given a group name returns its id */

	public String getID(String name) { return hmNameId.get(name); }

	public String getName(String id) { return hmIdName.get(id); }

	//--------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//--------------------------------------------------------------------------

	private HashMap<String, String> hmNameId = new HashMap<String, String>();
	private HashMap<String, String> hmIdName = new HashMap<String, String>();
}

//=============================================================================

