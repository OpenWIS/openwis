/**
 * 
 */
package org.openwis.metadataportal.services.metadata;

import java.util.List;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.stylesheet.StyleSheetManager;
import org.openwis.metadataportal.model.styleSheet.Stylesheet;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class StyleSheetAll implements Service {

   private String appPath;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      this.appPath = appPath;
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
      
      StyleSheetManager sm = new StyleSheetManager(dbms);
      List<Stylesheet> stylesheet = sm.getAllStyleSheet(appPath);
      return JeevesJsonWrapper.send(stylesheet);
   }

}
