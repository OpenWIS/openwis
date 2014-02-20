package org.openwis.metadataportal.services.useralarms.dto;

import org.fao.geonet.util.ISODate;
import org.openwis.dataservice.useralarms.UserAlarm;

/**
 * DTO for user alarms.
 *
 * @author lmika
 *
 */
public class UserAlarmDTO {
   private long id;
   private String date;
   private String alarmType;
   private String message;

   public UserAlarmDTO() {
   }

   public UserAlarmDTO(UserAlarm alarm) {
      this.id = alarm.getId();
      this.date = new ISODate(alarm.getDateRaised().toGregorianCalendar().getTimeInMillis()).toString();
      this.alarmType = "Delivery error";
      this.message = alarm.getMessage();
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getAlarmType() {
      return alarmType;
   }

   public void setAlarmType(String alarmType) {
      this.alarmType = alarmType;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
