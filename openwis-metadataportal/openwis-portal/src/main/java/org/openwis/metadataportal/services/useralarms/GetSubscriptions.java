package org.openwis.metadataportal.services.useralarms;

import java.util.List;

import org.openwis.dataservice.ProcessedRequest;
import org.openwis.dataservice.ProcessedRequestService;
import org.openwis.dataservice.useralarms.UserAlarm;
import org.openwis.dataservice.useralarms.UserAlarmReferenceType;
import org.openwis.metadataportal.kernel.external.DataServiceProvider;
import org.openwis.metadataportal.services.request.dto.follow.AdhocDTO;
import org.openwis.metadataportal.services.useralarms.dto.RequestUserAlarmDTO;
import org.openwis.metadataportal.services.useralarms.dto.UserAlarmDTO;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class GetSubscriptions extends AbstractUserAlarmGetService {

   @Override
   protected UserAlarmReferenceType getReferenceType() {
      return UserAlarmReferenceType.SUBSCRIPTION;
   }

   @Override
   protected List<UserAlarmDTO> convertToDtos(List<UserAlarm> userAlarms) {

      final ProcessedRequestService prs = DataServiceProvider.getProcessedRequestService();

      return Lists.transform(userAlarms, new Function<UserAlarm, UserAlarmDTO>() {
         public UserAlarmDTO apply(UserAlarm userAlarm) {

            // TODO: This is a RMI call, which is expensive.  Replace this with a call which accepts
            // a batch of process request IDs.

            ProcessedRequest processedRequest = prs.getProcessedRequest(userAlarm.getId());
            AdhocDTO dto = AdhocDTO.adhocProcessedRequestToDTO(processedRequest);

            return new RequestUserAlarmDTO(userAlarm, dto);
         }

         public boolean equals(Object obj) {
            return (this == obj);
         }
      });
   }
}
