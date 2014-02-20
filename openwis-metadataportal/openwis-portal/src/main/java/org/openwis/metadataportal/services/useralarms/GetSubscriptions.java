package org.openwis.metadataportal.services.useralarms;

import jeeves.server.context.ServiceContext;

import org.openwis.dataservice.useralarms.UserAlarmReferenceType;

public class GetSubscriptions extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmReferenceType getReferenceType() {
      return UserAlarmReferenceType.SUBSCRIPTION;
   }

   @Override
   protected String getUserName(ServiceContext ctx) {
      return ctx.getUserSession().getUsername();
   }
}
