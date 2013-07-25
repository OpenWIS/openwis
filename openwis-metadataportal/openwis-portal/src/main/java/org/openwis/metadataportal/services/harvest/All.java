/**
 * 
 */
package org.openwis.metadataportal.services.harvest;

import java.util.ArrayList;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.common.search.SearchCriteriaWrapper;
import org.openwis.metadataportal.common.search.SearchResultWrapper;
import org.openwis.metadataportal.common.search.SortDir;
import org.openwis.metadataportal.kernel.common.IMonitorable;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.kernel.harvest.exec.HarvesterExecutorService;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.common.json.MonitoringDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class All implements Service {

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      int start = Util.getParamAsInt(params, "start");
      int limit = Util.getParamAsInt(params, "limit");
      String sortColumn = Util.getParam(params, "sort", null);
      String sortDirection = Util.getParam(params, "dir", null);

      SearchCriteriaWrapper<Boolean, String> wrapper = new SearchCriteriaWrapper<Boolean, String>();
      wrapper.setCriteria(Util.getParam(params, "isSynchronization", false));
      wrapper.setStart(start);
      wrapper.setLimit(limit);
      if (sortColumn != null && sortDirection != null) {
         wrapper.setSort(sortColumn);
         wrapper.setDir(SortDir.valueOf(sortDirection));
      }

      HarvestingTaskManager harvesterManager = new HarvestingTaskManager(dbms);
      HarvesterExecutorService scheduler = HarvesterExecutorService.getInstance();

      SearchResultWrapper<HarvestingTask> allHarvestingTasks = harvesterManager
            .getAllHarvestingTasks(wrapper);
      List<MonitoringDTO<HarvestingTask>> rows = new ArrayList<MonitoringDTO<HarvestingTask>>();
      for(HarvestingTask h : allHarvestingTasks.getRows()) {
         MonitoringDTO<HarvestingTask> monitoredTask = new MonitoringDTO<HarvestingTask>(h);
         monitoredTask.setRunning(scheduler.isRunning(h.getId()));
         if(monitoredTask.isRunning()) {
            IMonitorable monitor = scheduler.monitor(h.getId());

            // OWT-366 - BEGIN
            // the progress information to display is no more a percent value but the number of processed item
            monitoredTask.setProgress((double) monitor.getProcessed());
            /*
            int total = (monitor.getTotal() > 0 ? monitor.getTotal() : 100);
            monitoredTask.setProgress(Math.floor(((float) monitor.getProcessed() / total) * 100));
            */
            // OWT-366 - END

         }
         rows.add(monitoredTask);
      }

      SearchResultWrapper<MonitoringDTO<HarvestingTask>> wrappedHarvestingTasks = new SearchResultWrapper<MonitoringDTO<HarvestingTask>>();
      wrappedHarvestingTasks.setTotal(allHarvestingTasks.getTotal());
      wrappedHarvestingTasks.setRows(rows);
      return JeevesJsonWrapper.send(wrappedHarvestingTasks);
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String arg0, ServiceConfig arg1) throws Exception {

   }

}
