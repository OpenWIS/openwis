/**
 * 
 */
package org.openwis.metadataportal.services.template;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.metadata.ITemplateManager;
import org.openwis.metadataportal.kernel.metadata.TemplateManager;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SaveOrder implements Service {

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
   @SuppressWarnings("unchecked")
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      //Read from Ajax Request.

      List<String> templateUrns = JeevesJsonWrapper.read(params, List.class);

      List<Template> templates = Lists.transform(templateUrns, new Function<String, Template>() {
         @Override
         public Template apply(String input) {
            return new Template(input);
         }
      });
      
      GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

      ITemplateManager templateManager = new TemplateManager(dbms, gc.getDataManager(), gc.getSearchmanager());
      templateManager.updateDisplayOrder(templates);
      
      //Send Acknowledgement
      return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
   }

}
