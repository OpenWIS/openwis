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

package org.fao.geonet.kernel.harvest;

import jeeves.exceptions.BadInputEx;
import jeeves.exceptions.JeevesException;
import jeeves.exceptions.MissingParameterEx;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.server.resources.ResourceManager;
import jeeves.utils.Log;
import jeeves.utils.Xml;
import org.fao.geonet.constants.Edit;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.harvest.Common.OperResult;
import org.fao.geonet.kernel.harvest.harvester.AbstractHarvester;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;

import java.sql.SQLException;
import java.util.HashMap;

//=============================================================================

public class HarvestManager
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public HarvestManager(ServiceContext context, SettingManager sm, DataManager dm) throws Exception
	{
		this.context = context;

		xslPath    = context.getAppPath() + Geonet.Path.STYLESHEETS+ "/xml/harvesting/";
		settingMan = sm;
		dataMan    = dm;

		AbstractHarvester.staticInit(context);

		Element entries = settingMan.get("harvesting", -1).getChild("children");

		if (entries != null)
			for (Object o : entries.getChildren())
			{
				Element node = transform((Element) o);
				String  type = node.getAttributeValue("type");

				AbstractHarvester ah = AbstractHarvester.create(type, context, sm, dm);
				ah.init(node);
				hmHarvesters.put(ah.getID(), ah);
				hmHarvestLookup.put(ah.getParams().uuid, ah);
			}
	}

	//---------------------------------------------------------------------------

	private Element transform(Element node) throws Exception
	{
		String type = node.getChildText("value");

		node = (Element) node.clone();

		return Xml.transform(node, xslPath + type +".xsl");
	}

	//---------------------------------------------------------------------------

	protected void finalize()
	{
		for (AbstractHarvester ah : hmHarvesters.values())
			ah.shutdown();
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public Element get(String id) throws Exception
	{
		Element result = (id == null)
									? settingMan.get("harvesting", -1)
									: settingMan.get("harvesting/id:"+id, -1);

		if (result == null)
			return null;

		if (id != null)
		{
			result = transform(result);
			addInfo(result);
		}

		else
		{
			Element nodes = result.getChild("children");

			result = new Element("nodes");

			if (nodes != null)
				for (Object o : nodes.getChildren())
				{
					Element node = transform((Element) o);
					addInfo(node);
					result.addContent(node);
				}
		}

		return result;
	}

	//---------------------------------------------------------------------------

	public String add(Dbms dbms, Element node) throws JeevesException, SQLException
	{
		Log.debug(Geonet.HARVEST_MAN, "Adding harvesting node : \n"+ Xml.getString(node));

		String type = node.getAttributeValue("type");

		AbstractHarvester ah = AbstractHarvester.create(type, context, settingMan, dataMan);

		ah.add(dbms, node);
		hmHarvesters.put(ah.getID(), ah);
		hmHarvestLookup.put(ah.getParams().uuid, ah);
		Log.debug(Geonet.HARVEST_MAN, "Added node with id : \n"+ ah.getID());

		return ah.getID();
	}

	//---------------------------------------------------------------------------

	public synchronized boolean update(Dbms dbms, Element node) throws BadInputEx, SQLException
	{
		Log.debug(Geonet.HARVEST_MAN, "Updating harvesting node : \n"+ Xml.getString(node));

		String id = node.getAttributeValue("id");

		if (id == null)
			throw new MissingParameterEx("attribute:id", node);

		AbstractHarvester ah = hmHarvesters.get(id);

		if (ah == null)
			return false;

		ah.update(dbms, node);
		return true;
	}

	//---------------------------------------------------------------------------
	/** This method must be synchronized because it cannot run if we are updating some entries */

	public synchronized OperResult remove(Dbms dbms, String id) throws Exception
	{
		Log.debug(Geonet.HARVEST_MAN, "Removing harvesting with id : "+ id);

		AbstractHarvester ah = hmHarvesters.get(id);

		if (ah == null)
			return OperResult.NOT_FOUND;

		hmHarvestLookup.remove(ah.getParams().uuid);
		ah.destroy(dbms);
		hmHarvesters.remove(id);
		settingMan.remove(dbms, "harvesting/id:"+id);

		return OperResult.OK;
	}

	//---------------------------------------------------------------------------

	public OperResult start(Dbms dbms, String id) throws SQLException
	{
		Log.debug(Geonet.HARVEST_MAN, "Starting harvesting with id : "+ id);

		AbstractHarvester ah = hmHarvesters.get(id);

		if (ah == null)
			return OperResult.NOT_FOUND;

		return ah.start(dbms);
	}

	//---------------------------------------------------------------------------

	public OperResult stop(Dbms dbms, String id) throws SQLException
	{
		Log.debug(Geonet.HARVEST_MAN, "Stopping harvesting with id : "+ id);

		AbstractHarvester ah = hmHarvesters.get(id);

		if (ah == null)
			return OperResult.NOT_FOUND;

		return ah.stop(dbms);
	}

	//---------------------------------------------------------------------------

	public OperResult run(Dbms dbms, String id) throws SQLException {
		Log.debug(Geonet.HARVEST_MAN, "Running harvesting with id : "+ id);

		AbstractHarvester ah = hmHarvesters.get(id);

		if (ah == null)
			return OperResult.NOT_FOUND;

		return ah.run(dbms);
	}

	//---------------------------------------------------------------------------

	public OperResult invoke(ResourceManager resourceManager, String id)
	{
		Log.debug(Geonet.HARVEST_MAN, "Invoking harvester with id : "+ id);

		AbstractHarvester ah = hmHarvesters.get(id);

		if (ah == null)
			return OperResult.NOT_FOUND;

		return ah.invoke(resourceManager);
	}

	//---------------------------------------------------------------------------

	public Element getHarvestInfo(String harvestUuid, String id, String uuid)
	{
		Element info = new Element(Edit.Info.Elem.HARVEST_INFO);

		AbstractHarvester ah = hmHarvestLookup.get(harvestUuid);

		if (ah != null)
			ah.addHarvestInfo(info, id, uuid);

		return info;
	}

	//---------------------------------------------------------------------------

	public AbstractHarvester getHarvester(String harvestUuid)
	{
		return hmHarvestLookup.get(harvestUuid);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//---------------------------------------------------------------------------

	private void addInfo(Element node)
	{
		String id = node.getAttributeValue("id");
		hmHarvesters.get(id).addInfo(node);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Vars
	//---
	//---------------------------------------------------------------------------

	private String         xslPath;
	private SettingManager settingMan;
	private DataManager    dataMan;
	private ServiceContext context;

	private HashMap<String, AbstractHarvester> hmHarvesters   = new HashMap<String, AbstractHarvester>();
	private HashMap<String, AbstractHarvester> hmHarvestLookup= new HashMap<String, AbstractHarvester>();
}

//=============================================================================

