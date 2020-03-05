package org.openwis.metadataportal.services.user;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.user.dto.UserActions;
import org.openwis.metadataportal.services.user.dto.UserLogDTO;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Report implements Service {
    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

        String query = "SELECT * from user_log;";
        List<Element> elements = dbms.select(query).getChildren();
        if (elements.size() == 0) {
            return null;
        }

        List<UserLogDTO> results = new ArrayList<>();
       for (Element element: elements) {
           UserLogDTO log = new UserLogDTO();
           log.setId(Util.getParamAsInt(element,"id"));
           log.setDate(toTimestamp(Util.getParam(element,"date")));
           log.setAction(UserActions.valueOf(StringUtils.upperCase(Util.getParam(element, "action"))));
           log.setAttribute(Util.getParam(element,"attribute", ""));
           log.setUsername(Util.getParam(element,"username"));
           log.setActioner(Util.getParam(element,"actioner"));
           results.add(log);
       }

        return JeevesJsonWrapper.send(results);
    }

    private Timestamp toTimestamp(String value) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = LocalDateTime.parse(value).atZone(zoneId);
        return Timestamp.valueOf(zdt.toLocalDateTime());
    }
}
