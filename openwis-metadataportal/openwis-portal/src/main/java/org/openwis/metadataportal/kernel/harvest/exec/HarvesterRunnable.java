/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.exec;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.MetadataServiceAlerts;
import org.openwis.metadataportal.kernel.common.IMonitorable;
import org.openwis.metadataportal.kernel.external.ManagementServiceProvider;
import org.openwis.metadataportal.kernel.harvest.AbstractHarvester;
import org.openwis.metadataportal.kernel.harvest.CatalogReportHelper;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.metadata.MetadataAlignerError;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.services.catalog.CatalogStatUpdateHelper;
import org.openwis.metadataportal.services.login.LoginConstants;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class HarvesterRunnable implements Runnable {

   private HarvestingTask harvestingTask;

   private ServiceContext context;

   private AbstractHarvester harvester;

   /**
    * Default constructor.
    * Builds a HarvesterThread.
    * @param harvestingTask
    */
   public HarvesterRunnable(HarvestingTask harvestingTask, ServiceContext context) {
      super();
      this.harvestingTask = harvestingTask;
      this.context = context;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      Log.info(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
            "Running harvesting task {0} ({1}).", harvestingTask.getName(), harvestingTask.getId()));
      MetadataAlignerResult result = null;

      try {
         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

         //Update last run
         HarvestingTaskManager harvestingTaskManager = new HarvestingTaskManager(dbms);
         Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
               "Updating last run date for harvesting task {0} ({1}).", harvestingTask.getName(),
               harvestingTask.getId()));
         harvestingTaskManager.updateLastRunDate(harvestingTask.getId());
         dbms.commit();

         harvester = HarvesterFactory.createHarvester(harvestingTask.getType(), context, dbms);
         Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
               "Lauching harvesting task {0} ({1}) from harvester.", harvestingTask.getName(),
               harvestingTask.getId()));
         result = harvester.harvest(harvestingTask);

         if (result != null) {
            Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
                  "Harvesting task {0} ({1}) completed. Updating last results.",
                  harvestingTask.getName(), harvestingTask.getId()));
            harvestingTaskManager.updateHarvestingTaskResult(harvestingTask.getId(), result);
            Log.debug(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
                  "Last results of harvesting task {0} ({1}) have been updated.",
                  harvestingTask.getName(), harvestingTask.getId()));

            CatalogStatUpdateHelper.updateStatOnHarvesting(harvestingTask, result.getTotal(),
                  result.getVolume());
            
         	// create a report that store harvesting tasks urn added, updated, and removed
             String lastRunDate = harvestingTaskManager.getLastRunDate(harvestingTask.getId());
             CatalogReportHelper.createReport(harvestingTask.getId(), harvestingTask.getName(), 
             		lastRunDate, result.getUrnAdded(), result.getUrnUpdated(), result.getUrnRemoved());
         } else {
            result = new MetadataAlignerResult();
            result.setDate(new Date());
            result.getErrors().add(
                  new MetadataAlignerError(harvestingTask.getUuid(), MessageFormat.format(
                        "No result available for harvesting task {0} ({1}).",
                        harvestingTask.getName(), harvestingTask.getId())));
            harvestingTaskManager.updateHarvestingTaskResult(harvestingTask.getId(), result);
            Log.warning(Geonet.HARVESTER_EXECUTOR, MessageFormat.format(
                  "No result available for harvesting task {0} ({1}).", harvestingTask.getName(),
                  harvestingTask.getId()));
         }

         //Close connection.
         context.getResourceManager().close();

         // Raise alert
         AlertService alertService = ManagementServiceProvider.getAlertService();
         if (alertService == null) {
            Log.error(LoginConstants.LOG,
                  "Could not get hold of the AlertService. No alert was passed!");
            return;
         }

         String source = "harvesting-" + harvestingTask.getName();
         String location = "Catalogue";
         String eventId;
         if (harvestingTask.isSynchronizationTask()) {
            eventId = MetadataServiceAlerts.SYNCHRONISATION_TASK.getKey();
         } else {
            eventId = MetadataServiceAlerts.HARVESTING_TASK_COMPLETED.getKey();
         }

         List<Object> arguments = new ArrayList<Object>();
         arguments.add(MessageFormat.format("Harvesting task completed {0} ({1}).",
               harvestingTask.getName(), harvestingTask.getId()));
         alertService.raiseEvent(source, location, null, eventId, arguments);

         //Finish harvesting.
         Log.info(
               Geonet.HARVESTER_EXECUTOR,
               MessageFormat.format("Harvesting task completed {0} ({1}).",
                     harvestingTask.getName(), harvestingTask.getId()));
      } catch (Exception e) {
         //Alarm.
         Log.error(
               Geonet.HARVESTER_EXECUTOR,
               MessageFormat.format("Error during harvesting task {0} ({1}) : {2}",
                     harvestingTask.getName(), harvestingTask.getId(), e.getMessage()), e);

         //Abort connection.
         try {
            context.getResourceManager().abort();
         } catch (Exception e1) {
            //NOOP
         }

         doFailReport(result, e);
      }
   }

   /**
    * Description goes here.
    * @param result
    * @param e
    */
   private void doFailReport(MetadataAlignerResult result, Exception e) {
      if (result == null) {
         result = new MetadataAlignerResult();
         result.setDate(new Date());
         result.setFail(true);
      }

      // Raise an alarm
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null) {
         Log.error(LoginConstants.LOG,
               "Could not get hold of the AlertService. No alert was passed!");
         return;
      }

      String source = "harvesting-" + harvestingTask.getName();
      String location = "Catalogue";
      String eventId;
      if (harvestingTask.isSynchronizationTask()) {
         eventId = MetadataServiceAlerts.SYNCHRONISATION_TASK_FAILED.getKey();
      } else {
         eventId = MetadataServiceAlerts.HARVESTING_TASK_FAILED.getKey();
      }
      List<Object> arguments = new ArrayList<Object>();
      arguments.add(harvestingTask.getName());
      arguments.add(harvestingTask.getId());
      arguments.add(e.getMessage());
      alertService.raiseEvent(source, location, null, eventId, arguments);

      // TEST
      //      int i = 1;
      //      while (i < 101) {
      //         result.getErrors().add(
      //               new MetadataAlignerError("", MessageFormat.format(
      //                     "Num " + i + " : " + "Error during harvesting task {0} ({1}) : {2}", harvestingTask.getName(),
      //                     harvestingTask.getId(), e.getMessage())));
      //         i++;
      //      }
      result.getErrors().add(
            new MetadataAlignerError("", MessageFormat.format(
                  "Error during harvesting task {0} ({1}) : {2}", harvestingTask.getName(),
                  harvestingTask.getId(), e.getMessage())));
      try {
         //Update last run
         Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
         HarvestingTaskManager harvestingTaskManager = new HarvestingTaskManager(dbms);
         harvestingTaskManager.updateHarvestingTaskResult(harvestingTask.getId(), result);
         //Close connection.
         context.getResourceManager().close();

      } catch (Exception e2) {
         Log.error(
               Geonet.HARVESTER_EXECUTOR,
               MessageFormat.format("Error during harvesting task {0} ({1}) : {2}",
                     harvestingTask.getName(), harvestingTask.getId()), e2);
         //Abort connection.
         try {
            context.getResourceManager().abort();
         } catch (Exception e1) {
            //NOOP
         }
         //NOOP
      }
   }

   /**
    * Gets the harvestingTask.
    * @return the harvestingTask.
    */
   public HarvestingTask getHarvestingTask() {
      return harvestingTask;
   }

   /**
    * Description goes here.
    * @return
    */
   public IMonitorable monitor() {
      return harvester;
   }
}
