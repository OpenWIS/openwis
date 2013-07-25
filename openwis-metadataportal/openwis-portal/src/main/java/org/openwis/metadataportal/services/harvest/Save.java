/**
 * 
 */
package org.openwis.metadataportal.services.harvest;

import java.util.ResourceBundle;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.availability.AvailabilityManager;
import org.openwis.metadataportal.kernel.availability.IAvailabilityManager;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.kernel.harvest.exec.HarvesterExecutorService;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.harvest.HarvestingTaskStatus;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Save implements Service {

   /** The bundle. */
   private ResourceBundle bundle;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      bundle = ResourceBundle.getBundle("openwisMessage");
   }
   
   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      HarvestingTaskManager harvesterManager = new HarvestingTaskManager(dbms);
      IAvailabilityManager availabilityManager = new AvailabilityManager(dbms);
      HarvesterExecutorService scheduler = HarvesterExecutorService.getInstance();

      HarvestingTask task = JeevesJsonWrapper.read(params, HarvestingTask.class);

      task.getConfiguration().put("authorUserName", context.getUserSession().getUsername());
      if (task.getBackup() != null) { //If backup, tests if backup mode is on to specify the appropriate status.
         //If local server is backuping deployment and this is a creation => Active by default.
         //If local server is backuping deployment and this is an edition => Do not change the status.
         if (availabilityManager.isLocalServerBackupingDeployment(task.getBackup().getName())) {
            if (task.getId() == null) {
               task.setStatus(HarvestingTaskStatus.ACTIVE);
            }
         } else {
            task.setStatus(HarvestingTaskStatus.SUSPENDED_BACKUP);
         }
      }

      if (task.getId() == null) {
         harvesterManager.createHarvestingTask(task);
      } else {
         
         // Updating harvesting task while running is not possible (concurrent access to configuration)
         if (scheduler.isRunning(task.getId())) {
            return JeevesJsonWrapper
                  .send(new AcknowledgementDTO(false, bundle.getString("Harvesting.running")));
         }
         
         scheduler.removeScheduledIfAny(task.getId());
         harvesterManager.updateHarvestingTask(task);
      }
      if (task.getRunMode().isRecurrent() && HarvestingTaskStatus.ACTIVE.equals(task.getStatus())) {
         // Schedule / Reschedule the task if any.
         harvesterManager.run(task.getId(), context);
      }

      return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
   }

}
