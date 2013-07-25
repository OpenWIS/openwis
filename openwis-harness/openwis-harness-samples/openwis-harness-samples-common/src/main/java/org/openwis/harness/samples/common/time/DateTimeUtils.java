package org.openwis.harness.samples.common.time;

/**
 * 
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * The Class DateTimeUtils. <P>
 * Explanation goes here. <P>
 */
public final class DateTimeUtils {

   /** The Constant DATE_PATTERN. */
   private final static String DATE_PATTERN = "yyyy-MM-dd";

   /** The Constant TIME_PATTERN. */
   private final static String TIME_PATTERN = "HH:mm'Z'";

   /** The Constant DATE_TIME_PATTERN. */
   private final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   /** The Constant DATE_FORMAT. */
   private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_PATTERN,
         Locale.ENGLISH);

   /** The Constant DATE_FORMAT. */
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

   /** The Constant TIME_FORMAT. */
   private static final DateFormat TIME_FORMAT = new SimpleDateFormat(TIME_PATTERN, Locale.ENGLISH);

   /**
    * Default constructor.
    * Builds a DateTimeUtils.
    */
   private DateTimeUtils() {
      super();
   }

   /**
    * Parses the date time.
    *
    * @param datetime the datetime
    * @return the calendar
    * @throws ParseException the parse exception
    */
   public static Calendar parseDateTime(String datetime) throws ParseException {
      return parse(DATE_TIME_FORMAT, datetime);
   }

   /**
    * Format date time.
    *
    * @param cal the cal
    * @return the string
    */
   public static String formatDateTime(Calendar cal) {
      return format(DATE_TIME_FORMAT, cal);
   }

   /**
    * Parses the date.
    *
    * @param date the date
    * @return the calendar
    * @throws ParseException the parse exception
    */
   public static Calendar parseDate(String date) throws ParseException {
      return parse(DATE_FORMAT, date);
   }

   /**
    * Format date.
    *
    * @param date the date
    * @return the string
    */
   public static String formatDate(Calendar date) {
      return format(DATE_FORMAT, date);
   }

   /**
    * Gets the minutes.
    *
    * @param time the time
    * @return the minutes
    * @throws ParseException the parse exception
    */
   public static int getMinutes(String time) throws ParseException {
      Calendar cal = parse(TIME_FORMAT, time);
      return 60 * cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE);
   }

   /**
    * Format time.
    *
    * @param hour the hour
    * @param min the minute
    * @return the string
    */
   public static String formatTime(int hour, int min) {
      Calendar cal = getCalendarUTC();
      cal.clear();
      cal.add(Calendar.HOUR, hour);
      cal.add(Calendar.MINUTE, min);
      return format(TIME_FORMAT, cal);
   }

   /**
    * Parses the date.
    * Warning the synchronize is mandatory to avoid ThreadSafe issue with SimpleDateFormat.
    * @param dateTimeFormat the date time format
    * @param date the date
    * @return the calendar
    * @throws ParseException the parse exception
    */
   private static synchronized Calendar parse(DateFormat dateTimeFormat, String date)
         throws ParseException {
      Calendar result = getCalendarUTC();
      result.clear();
      result.setTime(dateTimeFormat.parse(date));
      return result;
   }

   /**
    * Format.
    * Warning the synchronize is mandatory to avoid ThreadSafe issue with SimpleDateFormat.
    * @param dateFormat the date format
    * @param cal the calendar
    * @return the string
    */
   private static synchronized String format(DateFormat dateFormat, Calendar cal) {
      return dateFormat.format(cal.getTime());
   }

   /**
    * Get the UTC calendar.
    * @return the calendar
    */
   public static Calendar getCalendarUTC() {
      return Calendar.getInstance();
   }

   /**
    * Gets the copy.
    *
    * @param from the from
    * @param fields the fields
    * @return the copy
    */
   public static Calendar getCopy(Calendar from, int... fields) {
      Calendar cal = Calendar.getInstance();
      cal.clear();
      if (from != null && fields != null) {
         for (int field : fields) {
            cal.set(field, from.get(field));
         }
      }
      return cal;
   }

}
