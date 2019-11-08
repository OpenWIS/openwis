/**
 * 
 */
package org.openwis.metadataportal.services.template;

import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.model.metadata.Template;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * List all templates. <P>
 * Explanation goes here. <P>
 * 
 */
public class All implements Service {

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
    	
    	GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        ISearchManager searchMan = gc.getSearchmanager();

        List<Template> templates = searchMan.getAllTemplates();

        return JeevesJsonWrapper.send(templates);
    }
}
