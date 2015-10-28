package org.openwis.metadataportal.services.useralarms;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Element;
import org.openwis.dataservice.UserAlarmManagerWebService;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;
import org.openwis.metadataportal.services.useralarms.dto.AcknowledgeAlarmsDTO;

/**
 * Acknowledges an alarm for a user.  The user must own the alarm to have it acknowledged.
 *
 * @author lmika
 *
 */
public class Acknowledge implements Service {

   @Override
   public void init(String appPath, ServiceConfig params) throws Exception {
   }

   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      AcknowledgeAlarmsDTO acknowledgeAlarmsDTO = JeevesJsonWrapper.read(params, AcknowledgeAlarmsDTO.class);
      String userName = context.getUserSession().getUsername();

      UserAlarmManagerWebService userAlarmManager = DataServiceProvider.getUserAlarmManagerService();
      int count = userAlarmManager.acknowledgeAlarmsForUser(userName, acknowledgeAlarmsDTO.getAlarmIds());

      AcknowledgementDTO ackDTO = new AcknowledgementDTO(count == acknowledgeAlarmsDTO.getAlarmIds().size());

      return JeevesJsonWrapper.send(ackDTO);
   }

}
