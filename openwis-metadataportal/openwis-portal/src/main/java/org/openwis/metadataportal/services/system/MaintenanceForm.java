package org.openwis.metadataportal.services.system;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

public class MaintenanceForm implements Service {


    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        MaintenanceConfiguration maintenanceConfiguration = new MaintenanceConfiguration(gc.getSettingManager());
        return JeevesJsonWrapper.send(maintenanceConfiguration.getDTO());
    }
}
