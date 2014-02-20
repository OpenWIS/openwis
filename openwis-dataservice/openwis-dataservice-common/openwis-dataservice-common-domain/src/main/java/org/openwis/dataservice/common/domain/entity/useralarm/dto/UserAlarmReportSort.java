package org.openwis.dataservice.common.domain.entity.useralarm.dto;

public enum UserAlarmReportSort {
   USER_ID("user_id"),
   REQUEST_COUNT("request_cnt"),
   SUBSCRIPTION_COUNT("subscription_cnt"),
   TOTAL_COUNT("total_cnt");

   private final String sortFieldName;

   private UserAlarmReportSort(String sortFieldName) {
      this.sortFieldName = sortFieldName;
   }

   public String getSortFieldName() {
      return this.sortFieldName;
   }
}
