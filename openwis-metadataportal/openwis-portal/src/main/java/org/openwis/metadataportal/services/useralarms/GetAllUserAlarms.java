package org.openwis.metadataportal.services.useralarms;

import jeeves.server.context.ServiceContext;

import org.openwis.dataservice.useralarms.UserAlarmReferenceType;

public class GetAllUserAlarms extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmReferenceType getReferenceType() {
      return null;
   }

   @Override
   protected String getUserName(ServiceContext ctx) {
      return null;
   }
}
