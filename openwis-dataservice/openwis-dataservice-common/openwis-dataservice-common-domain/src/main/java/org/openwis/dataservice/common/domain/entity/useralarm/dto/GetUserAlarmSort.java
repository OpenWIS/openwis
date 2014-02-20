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
   REFERENCE_ID("ref_key"),
   MESSAGE("message");

   private final String sortFieldName;

   GetUserAlarmSort(String sortFieldName) {
      this.sortFieldName = sortFieldName;
   }

   public String sortFieldName() {
      return this.sortFieldName;
   }
};
