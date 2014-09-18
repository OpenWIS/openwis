package org.openwis.metadataportal.services.harvest;

import java.io.File;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.BinaryFile;
import jeeves.utils.Util;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.harvest.CatalogReportHelper;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.model.harvest.HarvestingTask;

public class LastReportFile implements Service {

	/**
	 * {@inheritDoc}
	 * 
	 * @see jeeves.interfaces.Service#init(java.lang.String,
	 *      jeeves.server.ServiceConfig)
	 */
	@Override
	public void init(String appPath, ServiceConfig params) throws Exception {

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see jeeves.interfaces.Service#exec(org.jdom.Element,
	 *      jeeves.server.context.ServiceContext)
	 */
	@Override
	public Element exec(Element params, ServiceContext context)
			throws Exception {
		Element result = null;
		Integer id = Util.getParamAsInt(params, "id");

		if (id != null) {
			Dbms dbms = (Dbms) context.getResourceManager().open(
					Geonet.Res.MAIN_DB);

			HarvestingTaskManager manager = new HarvestingTaskManager(dbms);
			HarvestingTask task = manager.getHarvestingTaskById(id, true);

			if (task != null) {
				String lastRunDate = manager.getLastRunDate(task.getId());

				File lastReportFile = CatalogReportHelper.getReportFile(
						task.getId(), task.getName(), lastRunDate);

				if (lastReportFile.exists()) {
					result = BinaryFile.encode(200,
							lastReportFile.getAbsolutePath());

				} else {
					throw new IllegalArgumentException("File not found --> "
							+ lastReportFile.getAbsolutePath());
				}
			}
		}

		return result;
	}

}
