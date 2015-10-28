package org.openwis.management.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.management.entity.UserDisseminatedData;
import org.openwis.management.service.bean.UserDisseminatedDataResult;
import org.openwis.management.utils.DateTimeUtils;

/**
 * The Class DisseminatedDataStatisticsImplTestCase.
 */
@RunWith(Arquillian.class)
@Ignore
public class DisseminatedDataStatisticsImplTestCase extends ManagementServiceTest {

   /** The statistics. */
   @EJB
   private DisseminatedDataStatistics statisticsService;

   /**
    * Initialize the test.
    */
   @Before
   public void init() {
      // Initialize
   }

   /**
    * Test update user dissemination statistics.
    */
   @Test
   public void testUpdateUserExtractedData() {
      String user = "jdoe-123";
      long size;
      int nb;
      // Insert
      size = 10;
      nb = 1;
      UserDisseminatedData data;

      // nb: 1, vol: 10
      statisticsService.updateUserExtractedData(user,
            DateTimeUtils.format(DateTimeUtils.getUTCDate()), nb, size);
      data = statisticsService.getUserDisseminatedData(user,
            DateTimeUtils.format(DateTimeUtils.getUTCDate()));
      Assert.assertNotNull(data);
      Assert.assertEquals(1, data.getNbFiles().intValue());
      Assert.assertEquals(10, data.getSize().intValue());

      // nb: 2, vol: 20
      statisticsService.updateUserExtractedData(user,
            DateTimeUtils.format(DateTimeUtils.getUTCDate()), nb, size);
      data = statisticsService.getUserDisseminatedData(user,
            DateTimeUtils.format(DateTimeUtils.getUTCDate()));
      Assert.assertNotNull(data);
      Assert.assertEquals(2, data.getNbFiles().intValue());
      Assert.assertEquals(20, data.getSize().intValue());
   }

   /**
    * Test get user dissemination data.
    */
   @Test
   public void testGetUserDisseminationData() {
      String user = "user-1";
      UserDisseminatedData data = null;

      Calendar date;
      Calendar testDate;
      long size;
      int nb;
      date = DateTimeUtils.getUTCCalendar();
      testDate = DateTimeUtils.getUTCCalendar();
      testDate.clear();
      testDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      size = 10;
      nb = 1;

      // Test with no dissemination
      data = statisticsService.getUserDisseminatedData(user, DateTimeUtils.format(date.getTime()));
      Assert.assertNull(data);

      // Insert
      statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
            size);
      statisticsService.updateUserDisseminatedByToolData(user,
            DateTimeUtils.format(date.getTime()), nb, size);
      data = statisticsService.getUserDisseminatedData(user, DateTimeUtils.format(date.getTime()));
      Assert.assertNotNull(data);
      Assert.assertEquals(size, (long) data.getSize());
      Assert.assertEquals(nb, (int) data.getNbFiles());

      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.setTime(data.getDate());
      Assert.assertEquals(testDate, cal);

      // Insert
      statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
            size);
      statisticsService.updateUserDisseminatedByToolData(user,
            DateTimeUtils.format(date.getTime()), nb, size);
      data = statisticsService.getUserDisseminatedData(user, DateTimeUtils.format(date.getTime()));
      Assert.assertNotNull(data);
      Assert.assertEquals(2 * size, (long) data.getSize());
      Assert.assertEquals(2 * nb, (int) data.getNbFiles());

      Assert.assertEquals(testDate, cal);
   }

   /**
    * Test get users dissemination data.
    */
   @Test
   public void testGetUsersDisseminationData() {
      String[] users = new String[] {"user-3", "user-4"};
      UserDisseminatedData data = null;

      Calendar date;
      Calendar testDate;
      long size;
      int nb;
      date = DateTimeUtils.getUTCCalendar();
      date.add(Calendar.MONTH, -1);

      testDate = DateTimeUtils.getUTCCalendar();
      testDate.clear();
      testDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      size = 10;
      nb = 1;

      // Test with no dissemination
      for (String user : users) {
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNull(data);

         // Insert
         statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
               size);
         statisticsService.updateUserDisseminatedByToolData(user,
               DateTimeUtils.format(date.getTime()), nb, size);
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNotNull(data);
         Assert.assertEquals(size, (long) data.getSize());
         Assert.assertEquals(nb, (int) data.getNbFiles());
         Assert.assertEquals(size, (long) data.getDissToolSize());
         Assert.assertEquals(nb, (int) data.getDissToolNbFiles());

         Calendar cal = DateTimeUtils.getUTCCalendar();
         cal.setTime(data.getDate());
         Assert.assertEquals(testDate, cal);

         // Insert
         statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
               size);
         statisticsService.updateUserDisseminatedByToolData(user,
               DateTimeUtils.format(date.getTime()), nb, size);
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNotNull(data);
         Assert.assertEquals(2 * size, (long) data.getSize());
         Assert.assertEquals(2 * nb, (int) data.getNbFiles());
         Assert.assertEquals(2 * size, (long) data.getDissToolSize());
         Assert.assertEquals(2 * nb, (int) data.getDissToolNbFiles());

         Assert.assertEquals(testDate, cal);
      }
   }

   /**
    * Test get user dissemination data between date.
    */
   @Test
   public void testGetUserDisseminationDataBetweenDate() {
      String user = "user-2";
      UserDisseminatedData data = null;

      Calendar date;
      Calendar startDate;
      long size;
      int nb;
      date = DateTimeUtils.getUTCCalendar();
      startDate = DateTimeUtils.getUTCCalendar();
      startDate.clear();
      startDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      size = 10;
      nb = 1;

      // add 8 days
      for (int i = 0; i < 7; i++) {
         statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
               size);
         statisticsService.updateUserDisseminatedByToolData(user,
               DateTimeUtils.format(date.getTime()), nb, size);
         date.add(Calendar.DAY_OF_MONTH, 1);
      }

      // check all data
      data = statisticsService.getUserDisseminatedDataInInterval(user,
            DateTimeUtils.format(startDate.getTime()), DateTimeUtils.format(date.getTime()));
      Assert.assertNotNull(data);
      Assert.assertEquals(7 * size, (long) data.getSize());
      Assert.assertEquals(7 * nb, (int) data.getNbFiles());
      Assert.assertEquals(7 * size, (long) data.getDissToolSize());
      Assert.assertEquals(7 * nb, (int) data.getDissToolNbFiles());

      // Check on 6 days
      date.add(Calendar.DAY_OF_MONTH, -2);
      data = statisticsService.getUserDisseminatedDataInInterval(user,
            DateTimeUtils.format(startDate.getTime()), DateTimeUtils.format(date.getTime()));
      Assert.assertNotNull(data);
      Assert.assertEquals(6 * size, (long) data.getSize());
      Assert.assertEquals(6 * nb, (int) data.getNbFiles());
      Assert.assertEquals(6 * size, (long) data.getDissToolSize());
      Assert.assertEquals(6 * nb, (int) data.getDissToolNbFiles());
   }

   /**
    * Test get users dissemination data.
    */
   @Test
   public void testGetDisseminationData() {
      String[] users = new String[] {"x-user-3", "x-user-4"};
      UserDisseminatedData data = null;

      Calendar date;
      Calendar testDate;
      long size;
      int nb;
      date = DateTimeUtils.getUTCCalendar();
      date.add(Calendar.MONTH, -1);

      testDate = DateTimeUtils.getUTCCalendar();
      testDate.clear();
      testDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      size = 10;
      nb = 1;

      // Test with no dissemination
      for (String user : users) {
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNull(data);

         // Insert
         statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
               size);
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNotNull(data);
         Assert.assertEquals(size, (long) data.getSize());
         Assert.assertEquals(nb, (int) data.getNbFiles());

         Calendar cal = DateTimeUtils.getUTCCalendar();
         cal.setTime(data.getDate());
         Assert.assertEquals(testDate, cal);
      }

      // Retrieve total
      data = statisticsService.getDisseminatedData(DateTimeUtils.format(date.getTime()));
      Assert.assertNotNull(data);
      Assert.assertTrue(users.length * size <= data.getSize());
      Assert.assertTrue(users.length * nb <= data.getNbFiles());
   }

   /**
    * Test get users dissemination data.
    */
   @Test
   public void testGetDisseminationData2() {
      String[] users = new String[] {"x-user-5", "x-user-6"};
      UserDisseminatedData data = null;

      Calendar date;
      Calendar testDate;
      long size;
      int nb;
      date = DateTimeUtils.getUTCCalendar();
      date.add(Calendar.YEAR, -2);

      testDate = DateTimeUtils.getUTCCalendar();
      testDate.clear();
      testDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      size = 10;
      nb = 1;

      // Test with no dissemination
      for (String user : users) {
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNull(data);

         // Insert
         statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
               size);
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNotNull(data);
         Assert.assertEquals(size, (long) data.getSize());
         Assert.assertEquals(nb, (int) data.getNbFiles());

         Calendar cal = DateTimeUtils.getUTCCalendar();
         cal.setTime(data.getDate());
         Assert.assertEquals(testDate, cal);
      }

      // Retrieve total
      List<UserDisseminatedData> lst = statisticsService.getUsersDisseminatedData(
            new HashSet<String>(Arrays.asList(users)), DateTimeUtils.format(date.getTime()));
      data = statisticsService.getDisseminatedData(DateTimeUtils.format(date.getTime()));
      Assert.assertNotNull(lst);
      Assert.assertEquals(users.length, lst.size());
   }

   /**
    * Test get users dissemination data.
    */
   @Test
   public void testGetDisseminationData3() {
      String[] users = new String[] {"xx-user-7", "xx-user-8", "plop"};
      UserDisseminatedData data = null;

      Calendar date;
      Calendar testDate;
      long size;
      int nb;
      date = DateTimeUtils.getUTCCalendar();
      date.add(Calendar.YEAR, -3);

      testDate = DateTimeUtils.getUTCCalendar();
      testDate.clear();
      testDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      size = 10;
      nb = 1;

      // Test with no dissemination
      for (String user : users) {
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNull(data);

         // Insert
         statisticsService.updateUserExtractedData(user, DateTimeUtils.format(date.getTime()), nb,
               size);
         data = statisticsService.getUserDisseminatedData(user,
               DateTimeUtils.format(date.getTime()));
         Assert.assertNotNull(data);
         Assert.assertEquals(size, (long) data.getSize());
         Assert.assertEquals(nb, (int) data.getNbFiles());

         Calendar cal = DateTimeUtils.getUTCCalendar();
         cal.setTime(data.getDate());
         Assert.assertEquals(testDate, cal);
      }

      // Retrieve total

      UserDisseminatedDataResult result = statisticsService.getUsersDisseminatedDataByUser(
            "xx-user", 0, 20, null, null);
      data = statisticsService.getDisseminatedData(DateTimeUtils.format(date.getTime()));
      Assert.assertNotNull(result);
      Assert.assertEquals(users.length - 1, result.getCount());
      Assert.assertEquals(users.length - 1, result.getList().size());
   }

   /**
    * Test get users dissemination data.
    */
   @Test
   public void testGetDisseminationDataInInterval() {
      String[] users = new String[] {"user-5", "user-6"};
      UserDisseminatedData data = null;

      Calendar date;
      Calendar testDate;
      long size;
      int nb;
      date = DateTimeUtils.getUTCCalendar();
      date.add(Calendar.MONTH, -2);

      testDate = DateTimeUtils.getUTCCalendar();
      testDate.clear();
      testDate.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      size = 10;
      nb = 1;

      // Test with no dissemination
      int days = 5;
      Calendar current;
      current = DateTimeUtils.getUTCCalendar();
      current.clear();
      current.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH));
      for (int i = 0; i < days; i++) {
         for (String user : users) {
            data = statisticsService.getUserDisseminatedData(user,
                  DateTimeUtils.format(current.getTime()));
            Assert.assertNull(data);
            // Insert
            statisticsService.updateUserExtractedData(user,
                  DateTimeUtils.format(current.getTime()), nb, size);
            data = statisticsService.getUserDisseminatedData(user,
                  DateTimeUtils.format(current.getTime()));
            Assert.assertNotNull(data);
            Assert.assertEquals(size, (long) data.getSize());
            Assert.assertEquals(nb, (int) data.getNbFiles());
         }
         current.add(Calendar.DATE, 1);
      }

      // open interval on lower bound
      date.add(Calendar.DATE, -1);
      
      // Retrieve total
      UserDisseminatedDataResult result = statisticsService.getDisseminatedDataInInterval(
            DateTimeUtils.format(date.getTime()), DateTimeUtils.format(current.getTime()), 0, 10,
            null, null);
      Assert.assertNotNull(result);
      Assert.assertEquals(days, result.getList().size());
      Assert.assertEquals(days, result.getCount());
      for (UserDisseminatedData udd : result.getList()) {
         Assert.assertEquals(users.length * size, (long) udd.getSize());
         Assert.assertEquals(users.length * nb, (int) udd.getNbFiles());
      }
   }

}
