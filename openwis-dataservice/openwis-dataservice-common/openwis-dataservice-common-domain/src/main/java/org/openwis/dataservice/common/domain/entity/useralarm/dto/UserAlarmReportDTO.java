package org.openwis.dataservice.common.domain.entity.useralarm.dto;

/**
 * A DTO used to report on the number of user alarms for a particular user.
 *
 * @author lmika
 *
 */
public class UserAlarmReportDTO {
   private String userId;
   private int alarms;

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
    * The number of alarms this user has.
    */
   public int getAlarms() {
      return alarms;
   }

   public void setAlarms(int alarms) {
      this.alarms = alarms;
   }

   /**
    * Creates a new instance of a UserAlarmReportDTO.
    * @param userId
    * @param alarmCount
    * @return
    */
   public static UserAlarmReportDTO createInstance(String userId, int alarmCount) {
      UserAlarmReportDTO dto = new UserAlarmReportDTO();
      dto.setUserId(userId);
      dto.setAlarms(alarmCount);
      return dto;
   }
}
