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

import jeeves.resources.dbms.Dbms;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;

//=============================================================================

/** Create a mapping remote ID -> local ID / change date. Retrieves all metadata
  * of a given siteID and puts them into an hashmap.
  */

public class UUIDMapper
{
	private HashMap<String, String> hmUuidDate 		 = new HashMap<String, String>();
	private HashMap<String, String> hmUuidId   		 = new HashMap<String, String>();
	private HashMap<String, String> hmUuidTemplate = new HashMap<String, String>();

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public UUIDMapper(Dbms dbms, String harvestUuid) throws Exception
	{
		String query = "SELECT id, uuid, changeDate, isTemplate FROM Metadata WHERE harvestUuid=?";

		List idsList = dbms.select(query, harvestUuid).getChildren();

        for (Object anIdsList : idsList) {
            Element record = (Element) anIdsList;

            String id = record.getChildText("id");
            String uuid = record.getChildText("uuid");
            String date = record.getChildText("changedate");
            String isTemplate = record.getChildText("istemplate");

            hmUuidDate.put(uuid, date);
            hmUuidId.put(uuid, id);
            hmUuidTemplate.put(uuid, isTemplate);
        }
	}

	//--------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//--------------------------------------------------------------------------

	public String getTemplate(String uuid) { return hmUuidTemplate.get(uuid); }

	//--------------------------------------------------------------------------
	
	public String getChangeDate(String uuid) { return hmUuidDate.get(uuid); }

	//--------------------------------------------------------------------------

	public String getID(String uuid) { return hmUuidId.get(uuid); }

	//--------------------------------------------------------------------------

	public Iterable<String> getUUIDs() { return hmUuidDate.keySet(); }
}

//=============================================================================

