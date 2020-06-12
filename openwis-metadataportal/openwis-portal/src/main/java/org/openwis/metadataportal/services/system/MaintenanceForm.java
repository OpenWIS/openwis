package org.openwis.metadataportal.services.system;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.system.dto.MaintenanceConfigurationDTO;

import javax.ejb.Local;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MaintenanceForm implements Service {
    private final String START_DATE_KEY = "system/maintenance/start_date";

    private final String END_DATE_KEY = "system/maintenance/end_date";

    private final String ENABLED_KEY = "system/maintenance/enabled";

    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        MaintenanceConfigurationDTO dto = new MaintenanceConfigurationDTO();

        try {
            String sStartDate = gc.getSettingManager().getValue(START_DATE_KEY);
            dto.setStartDate(sStartDate);

            String sEndDate = gc.getSettingManager().getValue(END_DATE_KEY);
            dto.setEndDate(sEndDate);

            String sEnabled = gc.getSettingManager().getValue(ENABLED_KEY);
            dto.setEnabled(!sEnabled.isEmpty() && Boolean.parseBoolean(sEnabled));
            return JeevesJsonWrapper.send(dto);
        } catch (NullPointerException e) {
            return JeevesJsonWrapper.send(new MaintenanceConfigurationDTO());
        }
    }
}
