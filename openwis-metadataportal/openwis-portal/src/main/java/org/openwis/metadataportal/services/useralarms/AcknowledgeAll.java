package org.openwis.metadataportal.services.useralarms;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.useralarms.UserAlarmManagerWebService;
import org.openwis.dataservice.useralarms.UserAlarmReferenceType;
import org.openwis.management.alert.AlarmEvent;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.useralarms.dto.AcknowledgeAllAlarmsDTO;

public class AcknowledgeAll implements Service {

   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      AcknowledgeAllAlarmsDTO acknowledgeAllAlarmsDTO = JeevesJsonWrapper.read(params, AcknowledgeAllAlarmsDTO.class);
      String userName = context.getUserSession().getUsername();

      UserAlarmReferenceType referenceType;
      if (acknowledgeAllAlarmsDTO.isSubscription()) {
         referenceType = UserAlarmReferenceType.SUBSCRIPTION;
      } else {
         referenceType = UserAlarmReferenceType.REQUEST;
      }

      UserAlarmManagerWebService userAlarmManager = DataServiceProvider.getUserAlarmManagerService();
      userAlarmManager.acknowledgeAllAlarmsForUserAndReferenceType(userName, referenceType);

      AcknowledgementDTO ackDTO = new AcknowledgementDTO(true);

      return JeevesJsonWrapper.send(ackDTO);
   }

}
