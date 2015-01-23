package org.openwis.metadataportal.services.useralarms;

import org.jdom.Element;
import org.openwis.dataservice.useralarms.UserAlarmManagerWebService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.useralarms.dto.AcknowledgeAlarmsDTO;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

public class DeleteAll implements Service {

   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      // TODO Auto-generated method stub

   }

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      UserAlarmManagerWebService userAlarmManager = DataServiceProvider.getUserAlarmManagerService();
      int count = userAlarmManager.deleteAllAlarms();

      AcknowledgementDTO ackDTO = new AcknowledgementDTO(true);

      return JeevesJsonWrapper.send(ackDTO);
   }

}
