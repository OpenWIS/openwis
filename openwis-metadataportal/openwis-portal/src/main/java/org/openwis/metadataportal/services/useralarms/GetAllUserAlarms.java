package org.openwis.metadataportal.services.useralarms;

import jeeves.server.context.ServiceContext;

import org.openwis.datasource.server.service.impl.UserAlarmRequestType;

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
