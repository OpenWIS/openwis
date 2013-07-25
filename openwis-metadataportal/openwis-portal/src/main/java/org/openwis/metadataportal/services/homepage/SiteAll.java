/**
 * 
 */
package org.openwis.metadataportal.services.homepage;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.site.SiteManager;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SiteAll implements Service {

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
     */
    @Override
    public Element exec(Element arg0, ServiceContext context) throws Exception {
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
        GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        SettingManager sm =gc.getSettingManager();

        SiteManager siteManager = new SiteManager(dbms, sm);

        return JeevesJsonWrapper.send(siteManager.getAllSites());
    }

    /**
     * {@inheritDoc}
     * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
     */
    @Override
    public void init(String arg0, ServiceConfig arg1) throws Exception {

    }

}
