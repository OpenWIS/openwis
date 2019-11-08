/**
 * 
 */
package org.openwis.metadataportal.services.catalog;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.catalog.CatalogManager;
import org.openwis.metadataportal.model.catalog.Catalog;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Get implements Service {

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
         
    	Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        CatalogManager cm = new CatalogManager(dbms);    
        Catalog catalog = cm.getCatalog();

        return JeevesJsonWrapper.send(catalog);
    }

}
