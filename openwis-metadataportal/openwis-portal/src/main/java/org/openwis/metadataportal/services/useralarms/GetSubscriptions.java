package org.openwis.metadataportal.services.useralarms;

import org.openwis.dataservice.useralarms.UserAlarmReferenceType;

public class GetSubscriptions extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmReferenceType getReferenceType() {
      return UserAlarmReferenceType.SUBSCRIPTION;
   }
}
