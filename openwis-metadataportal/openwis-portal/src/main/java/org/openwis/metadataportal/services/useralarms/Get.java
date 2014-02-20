package org.openwis.metadataportal.services.useralarms;

import java.util.Collections;
import java.util.List;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.useralarms.UserAlarm;
import org.openwis.dataservice.useralarms.UserAlarmManagerWebService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

public class Get implements Service {

   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   @Override
   public Element exec(Element params, ServiceContext ctx) throws Exception {
      String userName = ctx.getUserSession().getUsername();

      // Get the alarm service
      UserAlarmManagerWebService userAlarmManager = DataServiceProvider.getUserAlarmManagerService();
      if (userAlarmManager != null) {
         List<UserAlarm> userAlarms = userAlarmManager.getUserAlarmsForUser(userName);
         return JeevesJsonWrapper.send(userAlarms);
      } else {
         return JeevesJsonWrapper.send(Collections.emptyList());
      }
   }
}
