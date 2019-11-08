package org.openwis.metadataportal.services.useralarms;

import jeeves.server.context.ServiceContext;

import org.openwis.dataservice.UserAlarmRequestType;

public class GetSubscriptions extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmRequestType getRequestType() {
      return UserAlarmRequestType.SUBSCRIPTION;
   }

   @Override
   protected String getUserName(ServiceContext ctx) {
      return ctx.getUserSession().getUsername();
   }
}
