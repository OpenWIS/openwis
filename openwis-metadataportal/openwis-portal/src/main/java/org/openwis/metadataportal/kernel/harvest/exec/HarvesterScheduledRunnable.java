/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.exec;

import java.text.MessageFormat;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.harvest.HarvestingTaskStatus;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvesterScheduledRunnable implements Runnable {

   private HarvesterThreadPoolExecutor pool;
   
   private HarvestingTask task;
   
   private ServiceContext context;

   /**
    * Default constructor.
    * Builds a HarvesterScheduleRunnable.
    * @param pool
    * @param harvestingTaskManager 
    */
   public HarvesterScheduledRunnable(HarvesterThreadPoolExecutor pool, HarvestingTask task, ServiceContext context) {
      super();
      this.pool = pool;
      this.task = task;
      this.context = context;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      Log.info(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
            "Harvesting task {0} has been triggered by Scheduler.", task.getId()));
      
      // refreshing task info
      refreshHarvestingTask();
      
      if(!pool.isRunning(task.getId())) {
         
         // Check the task is not disabled
         if (task.getStatus() != HarvestingTaskStatus.ACTIVE) {
            String msg = MessageFormat.format(
                  "Harvesting task {0} is not active, the scheduling will stop by failure",
                  task.getId());
            Log.warning(Geonet.HARVESTER_EXECUTOR, msg);
            throw new RuntimeException(msg);
         }
         
         Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
               "No Harvesting task {0} is running. Passing it to the execution pool.", task.getId()));
         pool.execute(new HarvesterRunnable(task, context));
      } else {
         Log.warning(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
               "A harvesting task {0} is already running. Ignoring it.", task.getId()));
         
      }
      Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
            "Exiting HarvesterScheduledRunnable.run() for harvesting task {0}.", task.getId()));
   }

   /**
    * Refresh the current task with info from db.
    */
   private void refreshHarvestingTask() {
      Log.info(Geonet.HARVESTER_EXECUTOR,
            MessageFormat.format("Refreshing Harvesting task {0} before execution", task.getId()));

      Dbms dbms;
      try {
         dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         HarvestingTaskManager harvestingTaskManager = new HarvestingTaskManager(dbms);
         // replace db task with current one
         this.task = harvestingTaskManager.getHarvestingTaskById(task.getId(), true);
      } catch (Exception e) {
         Log.error(Geonet.HARVESTER_EXECUTOR,
               MessageFormat.format("Unable to refresh harvesting task {0}.", task.getId()), e);
      }
   }
   
   public HarvestingTask getHarvestingTask() {
      return task;
   }

}
