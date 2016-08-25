/**
 *
 */
package org.openwis.dataservice.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Short Description goes here. <p>
 * Explanation goes here. <p>
 *
 * @author <a href="mailto:franck.foutou@vcs.de">Franck Foutou</a>
 */
public class ProductDateFilter extends FileNameInfoFilter {

   // -------------------------------------------------------------------------
   // Instance Variables
   // -------------------------------------------------------------------------

   // Time bounds
   private final Calendar lowerBounds;
   private final Calendar upperBounds;

   private static final String TIME_FORMAT_REGEX = "\\d{2}\\:\\d{2}[Z]";

   //   private final static String SHORT_TIME_FORMAT = "HH:mmZ";
   private final static TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

   private final static String UTC_TIMEZONE_CHAR = "Z";

   //   private final static char TIME_SEPARATOR_CHAR = ':';
   private final static char RANGE_SEPARATOR_CHAR = '/';

   private final Logger LOG = LoggerFactory.getLogger(ProductDateFilter.class);

   // -------------------------------------------------------------------------
   // Initialization
   // -------------------------------------------------------------------------

   /**
    * Default constructor. Builds a ExtractFromCacheImpl.DateFilter.
    *
    * @param startTime the first bounding time of the retrieval
    * @param endTime the second bounding time of the retrieval
    */
   public ProductDateFilter(final String expr) {
      Calendar[] timeBounds = parseTimeRange(expr);
      if (timeBounds == null || timeBounds.length < 2) {
         throw new IllegalArgumentException("Unable to parse time range: " + expr);
      }
      // check: period
      checkCalendars(timeBounds[0], timeBounds[1]);

      // init: bounds
      lowerBounds = timeBounds[0];
      upperBounds = timeBounds[1];
   }

   /**
    * Gets the lowerBounds.
    * @return the lowerBounds.
    */
   public final Calendar getLowerBounds() {
      return lowerBounds;
   }

   /**
    * Gets the upperBounds.
    * @return the upperBounds.
    */
   public final Calendar getUpperBounds() {
      return upperBounds;
   }

   // -------------------------------------------------------------------------
   // FileNameInfoFilter Impl.
   // -------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    *
    * @see FileNameInfoFilter#accept(String)
    */
   @Override
   public boolean accept(final String fileName) {
      // return value
      boolean accepted = false;

      // resolve file name info
      try {
         WMOFNC info = FileNameParser.parseFileName(fileName);
         if (info != null) {
            // compare product date
            Date productDate = info.getProductDate();

            Calendar calendar = createComparableCalendar(productDate);
            if (calendar != null) {

               // check time bounds
               if (checkTimeBounds(calendar, lowerBounds, true)) {
                  if (checkTimeBounds(calendar, upperBounds, false)) {
                     accepted = true;
                  }
               }
            }
         }
      }
      catch (ParseException e) {
         LOG.error(e.getMessage(), e);
      }

      // feedback
      return accepted;
   }

   // -------------------------------------------------------------------------
   // Utilities
   // -------------------------------------------------------------------------

   /**
    * Description goes here.
    */
   protected static Calendar createComparableCalendar(final Date date) {
      // return value
      Calendar calendar = null;

      if (date != null) {
         calendar = new GregorianCalendar(GMT_TIME_ZONE);
         calendar.setTime(date);

         // reset date fields
         calendar.set(0, 0, 0);
      }

      return calendar;
   }

   /**
    * Creates and returns an array containing the time bounds encoded in the given expression. <br>
    * The format of the time expression is: startTimeExpr/endTimeEprx, where date expressions are
    * according the ISO 8601 format: HH:mmZ
    *
    * @param timeRangeExpr the encoded time range to parse
    * @param the corresponding time bounds
    */
   protected static Calendar[] parseTimeRange(final String timeRangeExpr) {
      // fail fast
      if (timeRangeExpr == null || timeRangeExpr.trim().isEmpty()) {
         throw new IllegalArgumentException("Time range expression cannot be null or empty!");
      }

      // separator char
      String rangeExpr = timeRangeExpr.trim();
      int index = rangeExpr.indexOf(RANGE_SEPARATOR_CHAR);
      if (index <= 0 || index == rangeExpr.length() - 1) {
         throw new IllegalArgumentException("Invalid range expression specified: " + rangeExpr);
      }

      // lower bounds
      String lowerBoundExpr = rangeExpr.substring(0, index);
      Calendar lowerBounds = parseTimeFields(lowerBoundExpr);
      if (lowerBounds == null) {
         throw new IllegalArgumentException(
               "Invalid lower bound expression specified: " + lowerBoundExpr);
      }

      // upper bounds
      String upperBoundExpr = rangeExpr.substring(index + 1);
      Calendar upperBounds = parseTimeFields(upperBoundExpr);
      if (upperBounds == null) {
         throw new IllegalArgumentException(
               "Invalid upper bound expression specified: " + upperBoundExpr);
      }

      // time bounds
      Calendar[] timeBounds = {lowerBounds, upperBounds};
      return timeBounds;
   }


   protected static Calendar parseTimeFields(final String timeExpr) {
      // fail fast
      if (timeExpr == null || timeExpr.trim().isEmpty()) {
         throw new IllegalArgumentException("Time expression cannot be null or empty!");
      }

      // check time expression
      String expr = timeExpr.trim();
      if (!expr.matches(TIME_FORMAT_REGEX)) {
         throw new IllegalArgumentException("Invalid time expression specified: " + timeExpr);
      }
      if (expr.endsWith(UTC_TIMEZONE_CHAR)) {
         expr = expr.substring(0, expr.length() - 1);
      }

      // return value
      Calendar calendar = null;

      // time interval
      try{
         String value = expr.substring(0, 2);
         int hourOfDay = Integer.parseInt(value);
         if (hourOfDay < 0 || hourOfDay > 23) {
            throw new IllegalArgumentException("Invalid hour field in time expression specified: " + value);
         }

         value = expr.substring(3);
         int minute = Integer.parseInt(value);
         if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Invalid minute field in time expression specified: " + value);
         }

         calendar = new GregorianCalendar(GMT_TIME_ZONE);
         calendar.set(0, 0, 0, hourOfDay, minute);
      }
      catch (Exception e) {
         throw new IllegalArgumentException("Failed to parse time fields: " + e.getMessage());
      }

      return calendar;
   }

   /**
    * Default constructor. Builds a ExtractFromCacheImpl.DateFilter.
    *
    * @param lowerBounds the first bounding time of the retrieval
    * @param upperBounds the second bounding time of the retrieval
    */
   private static void checkCalendars(final Calendar lowerBounds, final Calendar upperBounds) {
      // check arguments
      if (lowerBounds == null) {
         throw new IllegalArgumentException("Invalid lower time bounds!");
      }
      if (upperBounds == null) {
         throw new IllegalArgumentException("Invalid upper time bounds!");
      }

      // check values
      int startTime = lowerBounds.get(Calendar.HOUR_OF_DAY);
      int endTime = upperBounds.get(Calendar.HOUR_OF_DAY);
      if (startTime < 0) {
         throw new IllegalArgumentException(
            "Invalid time range specified: start time lower than 0!");
      }
      if (endTime < startTime) {
         throw new IllegalArgumentException(
            "Invalid time range specified: start time greater than end time!");
      }
   }

   /**
    * Default constructor. Builds a ExtractFromCacheImpl.DateFilter.
    *
    * @param lowerBounds the first bounding time of the retrieval
    * @param upperBounds the second bounding time of the retrieval
    */
   private static boolean checkTimeBounds(final Calendar calendar, final Calendar limit, final boolean lowerBounds) {
      boolean inRange = false;

      // time fields
      int minute = calendar.get(Calendar.MINUTE);
      int minuteRef = limit.get(Calendar.MINUTE);
      int minuteDiff = minute - minuteRef;

      int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
      int hourOfDayRef = limit.get(Calendar.HOUR_OF_DAY);
      int hourOfDayDiff = hourOfDay - hourOfDayRef;

      // lower bounds: limit <= calendar
      if (lowerBounds) {
         inRange = hourOfDayDiff >= 0;
         if (inRange) {
            if (hourOfDayDiff == 0) {
               inRange = minuteDiff >= 0;
            }
         }
      }
      // upper bounds: calendar <= limit
      else {
         inRange = hourOfDayDiff <= 0;
         if (inRange) {
            if (hourOfDayDiff == 0) {
               inRange = minuteDiff <= 0;
            }
         }
      }

      return inRange;

   }

}
