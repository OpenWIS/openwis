package org.openwis.management.service;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openwis.management.entity.ReplicatedData;
import org.openwis.management.utils.DateTimeUtils;

/**
 * The Class ReplicatedDataStatisticsImplTestCase.
 */
@RunWith(Arquillian.class)
@Ignore
public class ReplicatedDataStatisticsImplTestCase extends ManagementServiceTest {

   /** The statistics. */
   @EJB
   private ReplicatedDataStatistics statisticsService;

   /**
    * Initialize the test.
    */
   @Before
   public void init() {
      // Initialize
   }

   /**
    * Test update replicated data statistics.
    */
   @Test
   public void testUpdateReplicatedData() {
	  String source = "Source";
      long size;

      // Insert
      size = 10;
      ReplicatedData data;

      // vol: 10
      statisticsService.updateReplicatedData(source, DateTimeUtils.format(DateTimeUtils.getUTCDate()), size);
      data = statisticsService.getReplicatedDataFromSource(source, DateTimeUtils.format(DateTimeUtils.getUTCDate()));
      
      Assert.assertNotNull(data);
      Assert.assertEquals(10, data.getSize().intValue());

      // vol: 20
      statisticsService.updateReplicatedData(source, DateTimeUtils.format(DateTimeUtils.getUTCDate()), size);
      data = statisticsService.getReplicatedDataFromSource(source, DateTimeUtils.format(DateTimeUtils.getUTCDate()));

      Assert.assertNotNull(data);
      Assert.assertEquals(20, data.getSize().intValue());
   }

   /**
    * Test get replicated data from source.
    */
   @Test
   public void testGetReplicatedDataFromSource() {
	  String source = "Source-1";

	  Calendar cal = DateTimeUtils.getUTCCalendar();
	  cal.add(Calendar.MONTH, -1);
	  Calendar current = DateTimeUtils.getUTCCalendar();
	  current.setTimeInMillis(cal.getTimeInMillis());
 
	  ReplicatedData data;

	  data = statisticsService.getReplicatedDataFromSource(source, DateTimeUtils.format(current));
      Assert.assertNull(data);

      statisticsService.updateReplicatedData(source, DateTimeUtils.format(current), 10);
      data = statisticsService.getReplicatedDataFromSource(source, DateTimeUtils.format(current));
      Assert.assertNotNull(data);
      Assert.assertEquals(10, data.getSize().intValue());
   }

   /**
    * Test get ingested data from source in interval.
    */
   @Test
   public void testGetReplicatedDataFromSourceInInterval() {
	  String source = "Source-2";
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -1);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
    	  statisticsService.updateReplicatedData(source, DateTimeUtils.format(current), 10);
    	  current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      ReplicatedData data = statisticsService.getReplicatedDataFromSourceInInterval(
            source, DateTimeUtils.format(cal), DateTimeUtils.format(current));
      Assert.assertNotNull(data);
      Assert.assertEquals(days * 10, data.getSize().intValue());
   }

   /**
    * Test get replicated data per day.
    */
   @Test
   public void testGetReplicatedData() {
	  String source3 = "Source-3";
	  String source4 = "Source-4";
	  
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -2);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
    	  statisticsService.updateReplicatedData(source3, DateTimeUtils.format(current), 10);
    	  statisticsService.updateReplicatedData(source4, DateTimeUtils.format(current), 10);
      }

      // Checks
      ReplicatedData data = statisticsService.getReplicatedData(DateTimeUtils.format(current));
      Assert.assertNotNull(data);
      Assert.assertEquals(days * 2 * 10, data.getSize().intValue());
   }

   /**
    * Test get overall replicated data in interval.
    */
   @Test
   public void testGetReplicatedDataInInterval() {
	  String source5 = "Source-5";
	  String source6 = "Source-6";
      Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -3);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
    	  statisticsService.updateReplicatedData(source5, DateTimeUtils.format(current), 10);
    	  statisticsService.updateReplicatedData(source6, DateTimeUtils.format(current), 10);
    	  current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      ReplicatedData data = statisticsService.getReplicatedDataInInterval(
            DateTimeUtils.format(cal), DateTimeUtils.format(current));
      Assert.assertNotNull(data);
      Assert.assertEquals(days * 2 * 10, data.getSize().intValue());
   }
   
   /**
    * Test get replicated data statistics.
    */
   @Test
   public void testGetReplicatedDataStatistics() {
	  String source = "Source-7";

	  Calendar cal = DateTimeUtils.getUTCCalendar();
      cal.add(Calendar.YEAR, -4);
      Calendar current = DateTimeUtils.getUTCCalendar();
      current.setTimeInMillis(cal.getTimeInMillis());

      // Feed
      int days = 5;
      for (int i = 0; i < days; i++) {
    	  statisticsService.updateReplicatedData(source, DateTimeUtils.format(current), 10);
    	  current.add(Calendar.DAY_OF_MONTH, 1);
      }

      // Checks
      List<ReplicatedData> list = statisticsService.getReplicatedDataStatistics(0,5,null,null);
      Assert.assertNotNull(list);
      Assert.assertEquals(days, list.size());
   }
}
