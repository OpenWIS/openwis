/**
 * 
 */
package org.openwis.dataservice.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openwis.dataservice.common.util.DateTimeUtils;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class TestDateUtils {

   /** The Constant CHECK_DATE. */
   private static final String[] CHECK_DATE = {"2010-11-25T01:25:55Z", "2008-06-04T02:05:28Z",
         "1970-01-01T00:00:00Z"};

   /** The Constant CHECK_DATE_MILIS. */
   private static final long[] CHECK_DATE_MILIS = {1290648355000L, 1212545128000L, 0L};

   /** The Constant WRONG_FORMAT_DATE. */
   private static final String WRONG_FORMAT_DATE = "2010-11-25T01:25:55CET";

   /**
    * Run the String formatUTC(long) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_1() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++], DateTimeUtils.formatUTC(miliseconds));
      }

   }

   /**
    * Run the String formatUTC(Date) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_2() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++], DateTimeUtils.formatUTC(new Date(miliseconds)));
      }
   }

   /**
    * Run the String formatUTC(long,String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_3() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++],
               DateTimeUtils.formatUTC(new Date(miliseconds), DateTimeUtils.DATE_TIME_PATTERN));
      }
   }

   /**
    * Run the String formatUTC(long,Locale) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_4() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++],
               DateTimeUtils.formatUTC(miliseconds, Locale.ENGLISH));
      }
   }

   /**
    * Run the String formatUTC(Date,String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_5() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++],
               DateTimeUtils.formatUTC(new Date(miliseconds), DateTimeUtils.DATE_TIME_PATTERN));
      }
   }

   /**
    * Run the String formatUTC(Date,Locale) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_6() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++],
               DateTimeUtils.formatUTC(new Date(miliseconds), Locale.ENGLISH));
      }
   }

   /**
    * Run the String formatUTC(long,String,Locale) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_7() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++], DateTimeUtils.formatUTC(new Date(miliseconds),
               DateTimeUtils.DATE_TIME_PATTERN, Locale.ENGLISH));
      }
   }

   /**
    * Run the String formatUTC(Date,String,Locale) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testFormatUTC_8() throws Exception {
      int index = 0;
      for (long miliseconds : CHECK_DATE_MILIS) {
         Assert.assertEquals(CHECK_DATE[index++], DateTimeUtils.formatUTC(miliseconds,
               DateTimeUtils.DATE_TIME_PATTERN, Locale.ENGLISH));
      }
   }

   /**
    * Run the String formatUTC(Date,String,Locale) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = NullPointerException.class)
   public void testFormatUTC_9() throws Exception {
      DateTimeUtils.formatUTC(null, DateTimeUtils.DATE_TIME_PATTERN, Locale.ENGLISH);
   }

   /**
    * Run the String formatUTC(Date,String,Locale) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = NullPointerException.class)
   public void testFormatUTC_10() throws Exception {
      DateTimeUtils.formatUTC(new Date(CHECK_DATE_MILIS[0]), null, Locale.ENGLISH);
   }

   /**
    * Run the String formatUTC(Date,String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = NullPointerException.class)
   public void testFormatUTC_11() throws Exception {
      String pattern = null;
      DateTimeUtils.formatUTC(new Date(CHECK_DATE_MILIS[0]), pattern);
   }

   /**
    * Run the String formatUTC(Date,String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = NullPointerException.class)
   public void testFormatUTC_12() throws Exception {
      DateTimeUtils.formatUTC(null, DateTimeUtils.DATE_TIME_PATTERN);
   }

   /**
    * Run the Calendar getUTCCalendar() method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testGetUTCCalendar() throws Exception {
      Calendar result = DateTimeUtils.getUTCCalendar();

      // add additional test code here
      Assert.assertNotNull(result);
   }

   /**
    * Run the Calendar getUTCCalendar(String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testGetUTCCalendar_2() throws Exception {
      int index = 0;
      for (String date : CHECK_DATE) {
         Calendar result = DateTimeUtils.getUTCCalendar(date);
         Assert.assertEquals(CHECK_DATE_MILIS[index++], result.getTimeInMillis());
      }
   }

   /**
    * Run the Calendar getUTCCalendar(String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = java.text.ParseException.class)
   public void testGetUTCCalendar_3() throws Exception {
      Calendar result = DateTimeUtils.getUTCCalendar(WRONG_FORMAT_DATE);
      // add additional test code here
      assertNotNull(result);
   }

   /**
    * Run the int getUTCField(Date,int) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testGetUTCField_1() throws Exception {
      Date date = new Date(CHECK_DATE_MILIS[0]);
      // add additional test code here
      assertEquals(2010, DateTimeUtils.getUTCField(date, Calendar.YEAR));
      assertEquals(10, DateTimeUtils.getUTCField(date, Calendar.MONTH));
      assertEquals(25, DateTimeUtils.getUTCField(date, Calendar.DAY_OF_MONTH));
   }

   /**
    * Run the int getUTCField(Date,int) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = NullPointerException.class)
   public void testGetUTCField_2() throws Exception {
      DateTimeUtils.getUTCField(null, Calendar.YEAR);
   }

   /**
    * Run the int getUTCHour(Date) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testGetUTCHour_1() throws Exception {
      Date date = new Date(CHECK_DATE_MILIS[0]);
      assertEquals(1, DateTimeUtils.getUTCHour(date));
      date = new Date(CHECK_DATE_MILIS[1]);
      assertEquals(2, DateTimeUtils.getUTCHour(date));
      date = new Date(CHECK_DATE_MILIS[2]);
      assertEquals(0, DateTimeUtils.getUTCHour(date));
   }

   /**
    * Run the int getUTCMinutes(Date) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testGetUTCMinutes_1() throws Exception {
      Date date = new Date(CHECK_DATE_MILIS[0]);
      assertEquals(25, DateTimeUtils.getUTCMinutes(date));
      date = new Date(CHECK_DATE_MILIS[1]);
      assertEquals(05, DateTimeUtils.getUTCMinutes(date));
      date = new Date(CHECK_DATE_MILIS[2]);
      assertEquals(0, DateTimeUtils.getUTCMinutes(date));
   }

   /**
    * Run the int getUTCSecondes(Date) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testGetUTCSecondes_1() throws Exception {
      Date date = new Date(CHECK_DATE_MILIS[0]);
      assertEquals(55, DateTimeUtils.getUTCSeconds(date));
      date = new Date(CHECK_DATE_MILIS[1]);
      assertEquals(28, DateTimeUtils.getUTCSeconds(date));
      date = new Date(CHECK_DATE_MILIS[2]);
      assertEquals(0, DateTimeUtils.getUTCSeconds(date));
   }

   /**
    * Run the Date getUTCTime() method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test
   public void testGetUTCTime_1() throws Exception {
      Date result = DateTimeUtils.getUTCTime();
      // add additional test code here
      assertNotNull(result);
   }

   /**
    * Run the Date parseDateTime(String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   public void testParseDateTime_1() throws Exception {
      int index = 0;
      for (String date : CHECK_DATE) {
         Date result = DateTimeUtils.parseDateTime(date);
         Assert.assertEquals(CHECK_DATE_MILIS[index++], result.getTime());
      }
   }

   /**
    * Run the Date parseDateTime(String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = java.text.ParseException.class)
   public void testParseDateTime_2() throws Exception {
      Date result = DateTimeUtils.parseDateTime(WRONG_FORMAT_DATE);
      assertNotNull(result);
   }

   /**
    * Run the Date parseDateTime(String) method test.
    *
    * @throws Exception the exception
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Test(expected = NullPointerException.class)
   public void testParseDateTime_3() throws Exception {
      Date result = DateTimeUtils.parseDateTime(null);
      assertNotNull(result);
   }

   /**
    * Perform pre-test initialization.
    *
    * @throws Exception
    *         if the initialization fails for some reason
    *
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @Before
   public void setUp() throws Exception {
      // add additional set up code here
   }

   /**
    * Perform post-test clean-up.
    *
    * @throws Exception
    *         if the clean-up fails for some reason
    *
    * @generatedBy CodePro at 25/11/10 14:00
    */
   @After
   public void tearDown() throws Exception {
      // Add additional tear down code here
   }

}
