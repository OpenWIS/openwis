package org.openwis.metadataportal.services.useralarms;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.useralarms.UserAlarmManagerWebService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.useralarms.dto.AcknowledgeAlarmsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcknowledgeAll implements Service {

   private static Logger log = LoggerFactory.getLogger(AcknowledgeAll.class);

   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
      // TODO Auto-generated method stub
   }

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      String userName = context.getUserSession().getUsername();

      UserAlarmManagerWebService userAlarmManager = DataServiceProvider.getUserAlarmManagerService();
      userAlarmManager.acknowledgeAllAlarmsForUser(userName);

      AcknowledgementDTO ackDTO = new AcknowledgementDTO(true);

      return JeevesJsonWrapper.send(ackDTO);
   }

}
