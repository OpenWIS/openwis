//=============================================================================
//===	Copyright (C) 2009 Swisstopo
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

package org.fao.geonet.kernel.harvest.harvester.z3950;

import jeeves.exceptions.BadInputEx;
import jeeves.interfaces.Logger;
import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.server.resources.ResourceManager;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.harvest.harvester.AbstractHarvester;
import org.fao.geonet.kernel.harvest.harvester.AbstractParams;
import org.fao.geonet.lib.Lib;
import org.jdom.Element;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;


/**
 * {@link Z3950Harvester} needs to be configured in xml/repositories.xml.tem in
 * order to be used by the harvesting process.
 * 
 * @author fxprunayre
 * @author sppigot 
 */
public class Z3950Harvester extends AbstractHarvester {

	public static void init(ServiceContext context) throws Exception {
	}

	public String getType() {
		return "z3950";
	}

	protected void doInit(Element node) throws BadInputEx {
		params = new Z3950Params(dataMan);
		params.create(node);
	}

	protected void doDestroy(Dbms dbms) throws SQLException {
		File icon = new File(context.getAppPath() + "images/logos", params.uuid
				+ ".gif");

		icon.delete();
		Lib.sources.delete(dbms, params.uuid);
	}

	protected String doAdd(Dbms dbms, Element node) throws BadInputEx,
			SQLException {
		params = new Z3950Params(dataMan);

		// --- retrieve/initialize information
		params.create(node);

		// --- force the creation of a new uuid
		params.uuid = UUID.randomUUID().toString();

		String id = settingMan.add(dbms, "harvesting", "node", getType());

		storeNode(dbms, params, "id:" + id);
		Lib.sources.update(dbms, params.uuid, params.name, true);
		Lib.sources.copyLogo(context, "/images/harvesting/" + params.icon,
				params.uuid);

		return id;
	}

	protected void doUpdate(Dbms dbms, String id, Element node)
			throws BadInputEx, SQLException {
		Z3950Params copy = params.copy();

		// --- update variables
		copy.update(node);

		String path = "harvesting/id:" + id;

		settingMan.removeChildren(dbms, path);

		// --- update database
		storeNode(dbms, copy, path);

		// --- we update a copy first because if there is an exception CswParams
		// --- could be half updated and so it could be in an inconsistent state

		Lib.sources.update(dbms, copy.uuid, copy.name, true);
		Lib.sources.copyLogo(context, "/images/harvesting/" + copy.icon,
				copy.uuid);

		params = copy;
	}

	protected void storeNodeExtra(Dbms dbms, AbstractParams p, String path,
			String siteId, String optionsId) throws SQLException {
		Z3950Params params = (Z3950Params) p;

		settingMan.add(dbms, "id:" + siteId, "icon", params.icon);
		settingMan.add(dbms, "id:" + siteId, "query", params.query);

		storeRepositories(dbms, "id:" + siteId, params);
	}

	private void storeRepositories(Dbms dbms, String path, Z3950Params params) throws SQLException {
		String repoId = settingMan.add(dbms, path, "repositories", "");
		for (String id : params.getRepositories()) {
			settingMan.add(dbms, "id:"+ repoId, "repository", id);
		}
	}

	public AbstractParams getParams() {
		return params;
	}

	protected void doAddInfo(Element node) {
		// --- if the harvesting is not started yet, we don't have any info

		if (result == null)
			return;

		// --- ok, add proper info

		Element info = node.getChild("info");
		Element res = new Element("result");

		// --- put here harvesting information after it has been executed

		add(res, "total", result.totalMetadata);
		add(res, "added", result.addedMetadata);
		add(res, "updated", result.updatedMetadata);
		add(res, "unchanged", result.unchangedMetadata);
		add(res, "unknownSchema", result.unknownSchema);
		add(res, "removed", result.locallyRemoved);
		add(res, "unretrievable", result.unretrievable);
		add(res, "badFormat", result.badFormat);
		add(res, "doesNotValidate", result.doesNotValidate);

		info.addContent(res);
	}

	protected void doHarvest(Logger log, ResourceManager rm) throws Exception {
		Dbms dbms = (Dbms) rm.open(Geonet.Res.MAIN_DB);

		Harvester h = new Harvester(log, context, dbms, params);
		result = h.harvest();
	}

	private Z3950Params params;
	private Z3950Result result;
}

// =============================================================================

class Z3950Result {
	public int totalMetadata;
	public int addedMetadata;
	public int updatedMetadata;
	public int unchangedMetadata;
	public int locallyRemoved;
	public int unknownSchema;
	public int unretrievable;
	public int badFormat;
	public int doesNotValidate;
}

// =============================================================================

