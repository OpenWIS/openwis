package org.openwis.metadataportal.services.system;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.system.dto.MaintenanceConfigurationDTO;

import java.sql.SQLException;

public class UpdateMaintenanceConfiguration implements Service {
    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        MaintenanceConfigurationDTO dto = JeevesJsonWrapper.read(params, MaintenanceConfigurationDTO.class);
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        SettingManager sm = gc.getSettingManager();
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        MaintenanceConfiguration maintenanceConfiguration = new MaintenanceConfiguration(sm);

        try {
            maintenanceConfiguration.update(dbms, dto);
        } catch (IllegalArgumentException | SQLException ex) {
            return JeevesJsonWrapper.send(new AcknowledgementDTO(false, ex.getMessage()));
        }
        return JeevesJsonWrapper.send(new AcknowledgementDTO(true));
    }
}
