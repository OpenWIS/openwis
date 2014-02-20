package org.openwis.dataservice.common.domain.entity.useralarm.dto;

import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmReferenceType;

/**
 * Criteria for the get user alarm request.
 *
 * @author lmika
 *
 */
public class GetUserAlarmCriteriaDTO {
   private String username;
   private UserAlarmReferenceType referenceType;
   private GetUserAlarmSort sortColumn;
   private boolean sortAscending;
   private int offset;
   private int limit;

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public UserAlarmReferenceType getReferenceType() {
      return referenceType;
   }

   public void setReferenceType(UserAlarmReferenceType referenceType) {
      this.referenceType = referenceType;
   }

   public GetUserAlarmSort getSortColumn() {
      return sortColumn;
   }

   public void setSortColumn(GetUserAlarmSort sortColumn) {
      this.sortColumn = sortColumn;
   }

   public boolean isSortAscending() {
      return sortAscending;
   }

   public void setSortAscending(boolean sortAscending) {
      this.sortAscending = sortAscending;
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
