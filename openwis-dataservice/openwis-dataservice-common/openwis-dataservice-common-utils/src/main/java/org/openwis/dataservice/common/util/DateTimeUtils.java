/**
 *
 */
package org.openwis.dataservice.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Useful Methods for date.
 *
 * @author AKKA
 */
public final class DateTimeUtils {

   /** The Constant UTC_TIME_ZONE. */
   public final static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

   /** The Constant DATE_PATTERN. */
   public final static String DATE_PATTERN = "yyyy-MM-dd";

   /** The Constant TIME_PATTERN. */
   public final static String TIME_PATTERN = "HH:mm:ss'Z'";

   /** The Constant DATE_TIME_PATTERN. */
   public final static String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   /**
    * Default constructor.
    * Builds a DateTimeUtils.
    */
   private DateTimeUtils() {
      super();
   }

   /**
    * Retrieve an UTC date.
    *
    * @return the uTC time
    */
   public static Date getUTCTime() {
      Calendar cal = Calendar.getInstance(UTC_TIME_ZONE);
      cal.add(Calendar.MILLISECOND, -cal.get(Calendar.DST_OFFSET) - cal.get(Calendar.ZONE_OFFSET));
      return cal.getTime();
   }

   /**
    * Retrieve an UTC Calendar.
    *
    * @return the uTC calendar
    */
   public static Calendar getUTCCalendar() {
      return Calendar.getInstance(UTC_TIME_ZONE);
   }

   /**
    * Retrieve a field of the date using the UTC time zone.
    *
    * @param date the date
    * @param field the field
    * @return the uTC field
    */
   public static int getUTCField(Date date, int field) {

      if (date == null) {
         throw new NullPointerException("Provided date is null !");
      }

      Calendar calendar = getUTCCalendar();
      calendar.setTime(date);
      return calendar.get(field);
   }

   /**
    * <p>
    * Formats a date/time into default pattern using the UTC time zone.
    * </p>
    *
    * @param millis the date to format expressed in milliseconds
    * @return the formatted date
    */
   public static String formatUTC(long millis) {
      return formatUTC(millis, DATE_TIME_PATTERN);
   }

   /**
    * <p>
    * Formats a date/time into default pattern using the UTC time zone.
    * </p>
    *
    * @param date the date
    * @return the formatted date
    */
   public static String formatUTC(Date date) {
      return formatUTC(date, DATE_TIME_PATTERN);
   }

   /**
    * <p>
    * Formats a date/time into default date pattern using the UTC time zone.
    * </p>
    *
    * @param date the date
    * @param locale the locale to use, may be <code>null</code>
    * @return the formatted date
    */
   public static String formatUTC(Date date, Locale locale) {
      return formatUTC(date, DATE_TIME_PATTERN, locale);
   }

   /**
    * <p>
    * Formats a date/time into a specific pattern using the UTC time zone.
    * </p>
    *
    * @param millis
    *            the date to format expressed in milliseconds
    * @param pattern
    *            the pattern to use to format the date
    * @return the formatted date
    */
   public static String formatUTC(long millis, String pattern) {
      return DateFormatUtils.format(new Date(millis), pattern, UTC_TIME_ZONE);
   }

   /**
    * <p>
    * Formats a date/time into a specific pattern using the UTC time zone.
    * </p>
    *
    * @param date
    *            the date to format
    * @param pattern
    *            the pattern to use to format the date
    * @return the formatted date
    */
   public static String formatUTC(Date date, String pattern) {
      if ((date == null) || (pattern == null)) {
         throw new NullPointerException("Provided patameter is null");
      }
      return DateFormatUtils.format(date, pattern, UTC_TIME_ZONE);
   }

   /**
    * <p>
    * Formats a date/time into a specific pattern using the UTC time zone.
    * </p>
    *
    * @param millis
    *            the date to format expressed in milliseconds
    * @param pattern
    *            the pattern to use to format the date
    * @param locale
    *            the locale to use, may be <code>null</code>
    * @return the formatted date
    */
   public static String formatUTC(long millis, String pattern, Locale locale) {
      return DateFormatUtils.format(millis, pattern, UTC_TIME_ZONE, locale);
   }

   /**
    * <p>
    * Formats a date/time into default date pattern using the UTC time zone.
    * </p>
    *
    * @param millis the date to format expressed in milliseconds
    * @param locale the locale to use, may be <code>null</code>
    * @return the formatted date
    */
   public static String formatUTC(long millis, Locale locale) {
      return formatUTC(millis, DATE_TIME_PATTERN, locale);
   }

   /**
    * <p>
    * Formats a date/time into a specific pattern using the UTC time zone.
    * </p>
    *
    * @param date
    *            the date to format
    * @param pattern
    *            the pattern to use to format the date
    * @param locale
    *            the locale to use, may be <code>null</code>
    * @return the formatted date
    */
   public static String formatUTC(Date date, String pattern, Locale locale) {
      if ((date == null) || (pattern == null)) {
         throw new NullPointerException("Provided patameter is null ");
      }
      return DateFormatUtils.format(date, pattern, UTC_TIME_ZONE, locale);
   }

   /**
    * Retrieve the hour of the date using the UTC time zone.
    *
    * @param date the date
    * @return the uTC hour
    */
   public static int getUTCHour(Date date) {
      return getUTCField(date, Calendar.HOUR_OF_DAY);
   }

   /**
    * Retrieve the minutes of the date using the UTC time zone.
    *
    * @param date the date
    * @return the uTC minutes
    */
   public static int getUTCMinutes(Date date) {
      return getUTCField(date, Calendar.MINUTE);
   }

   /**
    * Retrieve the seconds of the date using the UTC time zone.
    *
    * @param date the date
    * @return the uTC seconds
    */
   public static int getUTCSeconds(Date date) {
      return getUTCField(date, Calendar.SECOND);
   }

   /**
    * Description goes here.
    *
    * @param date the date
    * @return the date
    * @throws ParseException the parse exception
    */
   public static Date parseDateTime(String date) throws ParseException {
      if (date == null) {
         throw new NullPointerException("Provided date is null");
      }

      Date parsedDate = null;
      DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN,
            Locale.ENGLISH);
      dateFormat.setTimeZone(UTC_TIME_ZONE);
      
      try {
         parsedDate = dateFormat.parse(date);
         //         if (!sdf.getTimeZone().hasSameRules(DateUtils.UTC_TIME_ZONE)) {
         //            throw new Exception("The given date <" + date + "> is not in UTC time zone ");
         //         }
      } catch (ParseException e) {
         throw new ParseException("Impossible to parse the given string <" + date
               + "> into UTC date ", e.getErrorOffset());
      }

      return parsedDate;
   }

   /**
    * Description goes here.
    *
    * @param date the date
    * @return the uTC calendar
    * @throws Exception the exception
    */
   public static Calendar getUTCCalendar(String date) throws Exception {
      Date parsedDate = parseDateTime(date);
      Calendar c = getUTCCalendar();
      c.setTime(parsedDate);
      return c;
   }

}
