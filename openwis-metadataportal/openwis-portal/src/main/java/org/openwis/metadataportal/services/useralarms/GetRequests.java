package org.openwis.metadataportal.services.useralarms;

import org.openwis.dataservice.useralarms.UserAlarmReferenceType;

public class GetRequests extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmReferenceType getReferenceType() {
      return UserAlarmReferenceType.REQUEST;
   }

}
