/**
 * 
 */
package org.openwis.metadataportal.services.metadata;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.stylesheet.StyleSheetManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.styleSheet.Stylesheet;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metadata.dto.BatchImportMetadataFormDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class BatchImportForm implements Service {

   private String appPath;

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String appPath, ServiceConfig serviceConfig) throws Exception {
       this.appPath = appPath;
    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        //--- retrieve style sheet
        StyleSheetManager sm = new StyleSheetManager(dbms);
        List<Stylesheet> stylesheet = sm.getAllStyleSheet(appPath);

        //-- Retrieve categories.
        CategoryManager cm = new CategoryManager(dbms);
        List<Category> categories = cm.getAllCategories();

        BatchImportMetadataFormDTO dto = new BatchImportMetadataFormDTO();
        dto.setStyleSheet(stylesheet);
        dto.setCategories(categories);
        return JeevesJsonWrapper.send(dto);
    }

}
