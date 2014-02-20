package org.openwis.dataservice.common.domain.entity.useralarm.dto;

/**
 * A DTO used to report on the number of user alarms for a particular user.
 *
 * @author lmika
 *
 */
public class UserAlarmReportDTO {
   private String userId;
   private int requestCount;
   private int subscriptionCount;
   private int totalCount;

   /**
    * The user ID this alarm report is for.
    */
   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   /**
    * The number of alarms this user has which relate to adhoc requests.
    */
   public int getRequestCount() {
      return requestCount;
   }

   public void setRequestCount(int alarms) {
      this.requestCount = alarms;
   }

   /**
    * The number of alarms this user has which relate to subscriptions.
    */
   public int getSubscriptionCount() {
      return subscriptionCount;
   }

   public void setSubscriptionCount(int subscriptionAlarms) {
      this.subscriptionCount = subscriptionAlarms;
   }

   /**
    * The total number of alarms the user has.
    */
   public int getTotalCount() {
      return totalCount;
   }

   public void setTotalCount(int totalAlarms) {
      this.totalCount = totalAlarms;
   }

   /**
    * Creates a new instance of a UserAlarmReportDTO.
    * @param userId
    * @param alarmCount
    * @return
    */
   public static UserAlarmReportDTO createInstance(String userId, int requestCount, int subscriptionCount, int totalCount) {
      UserAlarmReportDTO dto = new UserAlarmReportDTO();
      dto.setUserId(userId);
      dto.setRequestCount(requestCount);
      dto.setSubscriptionCount(subscriptionCount);
      dto.setTotalCount(totalCount);
      return dto;
   }
}
