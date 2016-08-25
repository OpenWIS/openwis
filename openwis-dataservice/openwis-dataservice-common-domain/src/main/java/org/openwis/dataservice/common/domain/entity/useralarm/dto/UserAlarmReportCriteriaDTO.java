package org.openwis.dataservice.common.domain.entity.useralarm.dto;

/**
 * DTO for the user alarm report.
 *
 * @author lmika
 *
 */
public class UserAlarmReportCriteriaDTO {
   private UserAlarmReportSort sortBy;
   private boolean sortAsc;
   private int offset;
   private int limit;

   public UserAlarmReportSort getSortBy() {
      return sortBy;
   }

   public void setSortBy(UserAlarmReportSort sortBy) {
      this.sortBy = sortBy;
   }

   public boolean isSortAsc() {
      return sortAsc;
   }

   public void setSortAsc(boolean sortAsc) {
      this.sortAsc = sortAsc;
   }

   public int getOffset() {
      return offset;
   }

   public void setOffset(int offset) {
      this.offset = offset;
   }

   public int getLimit() {
      return limit;
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }
}
