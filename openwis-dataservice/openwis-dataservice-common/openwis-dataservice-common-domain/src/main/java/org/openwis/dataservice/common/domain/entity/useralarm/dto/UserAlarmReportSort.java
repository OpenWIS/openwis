package org.openwis.dataservice.common.domain.entity.useralarm.dto;

public enum UserAlarmReportSort {
   USER("1"),
   ALARMS("2");

   private final String sortFieldName;

   private UserAlarmReportSort(String sortFieldName) {
      this.sortFieldName = sortFieldName;
   }

   public String getSortFieldName() {
      return this.sortFieldName;
   }
}
