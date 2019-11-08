package org.openwis.metadataportal.services.useralarms;

import jeeves.server.context.ServiceContext;

import org.openwis.dataservice.UserAlarmRequestType;

public class GetAllUserAlarms extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmRequestType getRequestType() {
      return null;
   }

   @Override
   protected String getUserName(ServiceContext ctx) {
      return null;
   }
}
