package org.openwis.dataservice.common.domain.entity.useralarm.dto;

/**
 * Enum for the sort direction for the get user alarm service.
 *
 * @author lmika
 *
 */
public enum GetUserAlarmSort {
   DATE("date"),
   CATEGORY("category"),
   USER_ID("user_id"),
   ALARM_TYPE("req_type"),
   REQUEST_ID("req_id"),
   MESSAGE("message");

   private final String sortFieldName;

   GetUserAlarmSort(String sortFieldName) {
      this.sortFieldName = sortFieldName;
   }

   public String sortFieldName() {
      return this.sortFieldName;
   }
};
