package org.openwis.metadataportal.services.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

   /** The Constant UTC_TIME_ZONE. */
   public final static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

   /** The Constant DATE_TIME_PATTERN. */
   public final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   /** The thread local date format. */
   private static ThreadLocal<DateFormat> tlDateFormat = new ThreadLocal<DateFormat>();

   /**
    * Format the date.
    *
    * @param date the date
    * @return the formated date
    */
   public static String format(Date date) {
      DateFormat df = getThreadLocalDateFormat();
      return df.format(date);
   }

   /**
    * Parses the date.
    *
    * @param date the date
    * @return the parsed date
    * @throws ParseException the parse exception
    */
   public static Date parse(String date) throws ParseException {
      DateFormat df = getThreadLocalDateFormat();
      return df.parse(date);
   }

   /**
    * Gets the UTC date.
    *
    * @return the UTC date
    */
   public static Date getUTCDate() {
      return getUTCCalendar().getTime();
   }

   /**
    * Gets the UTC calendar.
    *
    * @return the UTC calendar
    */
   public static Calendar getUTCCalendar() {
      Calendar cal = Calendar.getInstance();
      cal.setTimeZone(UTC_TIME_ZONE);
      return cal;
   }

   /**
    * Gets the thread local date format.
    *
    * @return the thread local date format
    */
   private static DateFormat getThreadLocalDateFormat() {
      DateFormat result = null;
      synchronized (tlDateFormat) {
         result = tlDateFormat.get();
         if (result == null) {
            result = new SimpleDateFormat(DATE_TIME_PATTERN);
            result.setTimeZone(UTC_TIME_ZONE);
            tlDateFormat.set(result);
         }
      }
      return result;
   }

}
