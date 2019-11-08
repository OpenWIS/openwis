package org.openwis.metadataportal.services.useralarms;

import jeeves.server.context.ServiceContext;

import org.openwis.dataservice.UserAlarmRequestType;

public class GetRequests extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmRequestType getRequestType() {
      return UserAlarmRequestType.REQUEST;
   }

   @Override
   protected String getUserName(ServiceContext ctx) {
      return ctx.getUserSession().getUsername();
   }

}
