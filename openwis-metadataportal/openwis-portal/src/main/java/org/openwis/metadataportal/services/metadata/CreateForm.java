/**
 * 
 */
package org.openwis.metadataportal.services.metadata;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.kernel.datapolicy.DataPolicyManager;
import org.openwis.metadataportal.kernel.datapolicy.IDataPolicyManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.datapolicy.DataPolicy;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.metadata.dto.CreateMetadataFormDTO;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CreateForm implements Service {
    
    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String appPath, ServiceConfig serviceConfig) throws Exception {

    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        
        UserSession session = context.getUserSession();

        IDataPolicyManager dpm = new DataPolicyManager(dbms);
        ISearchManager searchManager = gc.getSearchmanager();

        //--- retrieve data policies
        List<DataPolicy> dataPolicies = null;
        if (Geonet.Profile.ADMINISTRATOR.equals(session.getProfile())) {
            dataPolicies = dpm.getAllDataPolicies(false, false);
        } else {
            dataPolicies = dpm.getAllUserDataPolicies(session.getUsername(), false, false);
        }
        
        //-- Retrieve templates.
        List<Template> templates = searchManager.getAllTemplates();

        //-- Retrieve categories.
        CategoryManager cm = new CategoryManager(dbms);
        List<Category> categories = cm.getAllCategories();

        CreateMetadataFormDTO dto = new CreateMetadataFormDTO();
        dto.setDataPolicies(dataPolicies);
        dto.setTemplates(templates);
        dto.setCategories(categories);
        return JeevesJsonWrapper.send(dto);
    }

}
