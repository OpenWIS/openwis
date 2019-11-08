package org.openwis.metadataportal.services.useralarms.dto;

import java.util.List;

public class AcknowledgeAlarmsDTO {
   private List<Long> alarmIds;

   public List<Long> getAlarmIds() {
      return alarmIds;
   }

   public void setAlarmIds(List<Long> alarmIds) {
      this.alarmIds = alarmIds;
   }
}
