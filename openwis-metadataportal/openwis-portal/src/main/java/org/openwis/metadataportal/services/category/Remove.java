/**
 * 
 */
package org.openwis.metadataportal.services.category;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.services.category.dto.CategoriesDTO;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

import com.google.common.base.Joiner;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Remove implements Service {
   
   /** The bundle. */
   private static ResourceBundle bundle = ResourceBundle.getBundle("openwisMessage");;


   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {

   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      //Read from Ajax Request.

      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      CategoriesDTO dto = JeevesJsonWrapper.read(params, CategoriesDTO.class);

      CategoryManager cm = new CategoryManager(dbms);
      HarvestingTaskManager harvManager = new HarvestingTaskManager(dbms);

      AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO(true);
      ArrayList<String> referencedHarvestingTasks = new ArrayList<String>();
      ArrayList<String> nomEmptyCategories = new ArrayList<String>();
      for (Category category : dto.getCategories()) {
         List<HarvestingTask> harvestingTasks = harvManager.findHarvestingTaskByCategory(category.getId());
         if (harvestingTasks.size() > 0) {
            ArrayList<String> taskNames = new ArrayList<String>();
            for (HarvestingTask harvestingTask : harvestingTasks) {
               taskNames.add(harvestingTask.getName());
            }
            String catMsg = category.getName() + " (" + Joiner.on(",").join(taskNames) + ")";
            referencedHarvestingTasks.add(catMsg);
         } else {
            if (cm.countMdInCategory(category) == 0) {
               cm.removeCategory(category);
            } else {
               nomEmptyCategories.add(category.getName());
            }
         }
      }

      if (referencedHarvestingTasks.size() > 0) {
         String msg = MessageFormat.format(bundle.getString("RemoveCategory.failHarvesting"), Joiner.on(",").join(referencedHarvestingTasks));
         acknowledgementDTO = new AcknowledgementDTO(false, msg);
      } else if (nomEmptyCategories.size() > 0) {
         String msg = MessageFormat.format(bundle.getString("RemoveCategory.fail"), Joiner.on(",").join(nomEmptyCategories));
         acknowledgementDTO = new AcknowledgementDTO(false, msg);
      }
      
      //Send Acknowledgement
      return JeevesJsonWrapper.send(acknowledgementDTO);
   }

}
